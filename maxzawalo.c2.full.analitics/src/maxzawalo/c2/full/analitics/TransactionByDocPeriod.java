package maxzawalo.c2.full.analitics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.j256.ormlite.dao.Dao;

import maxzawalo.c2.base.bo.CatalogueBO;
import maxzawalo.c2.base.data.DbHelper;
import maxzawalo.c2.base.data.GenericRowMapper;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.base.utils.Logger;
import maxzawalo.c2.free.bo.Settings;
import maxzawalo.c2.free.bo.Store;
import maxzawalo.c2.free.bo.document.deliverynote.DeliveryNote;
import maxzawalo.c2.free.bo.registry.RegistryProduct;
import maxzawalo.c2.free.bo.store.StoreDocBO;
import maxzawalo.c2.free.data.factory.document.StoreDocFactory;
import maxzawalo.c2.full.bo.document.cashvoucher.CashVoucher;

public class TransactionByDocPeriod {
	static Logger log = Logger.getLogger(TransactionByDocPeriod.class);

	public static List<DateNumber> name(Date fromDate, Date toDate, StoreDocBO doc) {
		try {
			List<DateNumber> all = new ArrayList<>();
			fromDate = Format.beginOfDay(fromDate);
			toDate = Format.endOfDay(toDate);

			Map<Date, Double> group = new HashMap<>();

			List<StoreDocBO> list = GetDocuments(fromDate, toDate, doc);

			for (Object obj : list) {
				StoreDocBO d = (StoreDocBO) obj;
				Date date = Format.beginOfDay(d.DocDate);
				Double value = group.get(date);
				if (value == null)
					group.put(date, d.total);
				else
					group.put(date, d.total + value);
			}

			Date currentDate = fromDate;
			while (currentDate.getTime() < toDate.getTime()) {
				// group.put(currentDate, 0d);
				DateNumber dateNumber = new DateNumber();
				dateNumber.date = currentDate;
				dateNumber.number = group.get(currentDate);
				if (dateNumber.number == null) {
					// Выходные и дни без движения
					dateNumber.number = 0d;
					System.out.println("");
				}
				all.add(dateNumber);
				currentDate = Format.AddDay(currentDate, 1);
			}
			return all;

			// RegistryProduct registry = new RegistryProduct();
			// registry.reg_type = RegType.DeliveryNote;
			// List<RegistryProduct> entries =
			// registry.SelectEntriesByPeriod(fromDate, toDate);

		} catch (Exception e) {
			log.ERROR("GetByPeriod", e);
		}

		return new ArrayList<>();
	}

	public static List<StoreDocBO> GetDocuments(Date fromDate, Date toDate, StoreDocBO doc) {
		List<StoreDocBO> list = new StoreDocFactory<>().Create(doc.getClass()).GetByPeriod(fromDate, toDate, true);
		// Внутреннее перемещение итп удаляем
		list.removeIf(d -> d.contractor != null && d.contractor.id == Settings.myFirm.id);
		return list;
	}

	public static List<TopProduct> TopProduct(Date fromDate, Date toDate, Store store, int top) {
		fromDate = Format.beginOfDay(fromDate);
		toDate = Format.endOfDay(toDate);
		try {
			int[] docs = new int[] { new DeliveryNote().reg_type, new CashVoucher().reg_type };
			String docTypes = Arrays.toString(docs).replaceAll("\\[|\\]", "");

			Dao<TopProduct, Integer> lotDao = DbHelper.geDaos(TopProduct.class);
			String sql = "select ";
			sql += " rp.product_id `id`, ";
			sql += " p." + CatalogueBO.fields.NAME + ", ";
			sql += " abs(sum(rp.count)) `count`, ";
			sql += " u." + CatalogueBO.fields.NAME + " `units` ";
			sql += " from registry_product rp ";
			sql += " join product p on rp.product_id=p.id ";
			sql += " join units u on p.units_id = u.id ";
			sql += " where ";
			sql += " rp.deleted = false and rp.sync_flag = 0 and ";
			if (store != null)
				sql += " rp." + RegistryProduct.fields.STORE + "=" + store.id + " and ";
			sql += " rp.reg_type in (" + docTypes + ") and ";
			sql += " rp." + RegistryProduct.fields.CONTRACTOR + "<>" + Settings.myFirm.id + " and ";
			sql += " rp.reg_date>= " + fromDate.getTime() + " and ";
			sql += " rp.reg_date<= " + toDate.getTime() + " group by product_id ";
			sql += " order by `count` desc ";

			if (top != 0)
				sql += " limit 0, " + top;

			System.out.println(sql);
			return lotDao.queryRaw(sql, GenericRowMapper.get(TopProduct.class)).getResults();
		} catch (Exception e) {
			log.ERROR("Top20Product", e);
		}
		return new ArrayList<>();
	}
}