package maxzawalo.c2.full.data.factory.catalogue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import maxzawalo.c2.base.data.DbHelper;
import maxzawalo.c2.base.data.GenericRowMapper;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.base.utils.Global;
import maxzawalo.c2.base.utils.Profiler;
import maxzawalo.c2.free.bo.LotOfProduct;
import maxzawalo.c2.free.bo.Price;
import maxzawalo.c2.free.bo.PriceState;
import maxzawalo.c2.free.bo.Product;
import maxzawalo.c2.free.bo.Store;
import maxzawalo.c2.free.data.factory.catalogue.LotOfProductFactoryFree;
import maxzawalo.c2.free.data.factory.catalogue.PriceFactory;
import maxzawalo.c2.free.data.factory.catalogue.UnitsFactory;

public class LotOfProductFactoryFull extends LotOfProductFactoryFree {

	public LotOfProductFactoryFull() {
		typeBO = LotOfProduct.class;
		mapper = new GenericRowMapper(typeBO);
	}

	public boolean ClearPriceState(List<LotOfProduct> selected) {
		PriceFactory factory = new PriceFactory();
		boolean res = true;
		for (LotOfProduct lot : selected) {
			Price p = factory.GetById(lot.price_bo.id);
			if (p != null)
				res &= factory.UpdateState(p, PriceState.Новый);
		}
		return res;
	}

	public List<LotOfProduct> Ge4TP(Product product, Store store, Date docDate) {
		Profiler profiler = new Profiler();
		profiler.Start("Lot.Ge4TP");

		Dao<LotOfProduct, Integer> lotDao = DbHelper.geDaos(typeBO);
		List<LotOfProduct> list = new ArrayList<>();

		String sql = "(select";
		sql += " IFNULL(reg.lotOfProduct_id, 0) as `id`,";
		sql += " reg.reserve,";
		sql += " IFNULL(reg.product_id, 0) as `product_id`,";
		sql += " IFNULL(lot.doc_id, 0) as `doc_id`,";
		sql += " IFNULL(lot.doc_type, 0) as `doc_type`,";
		sql += " IFNULL(lot.cost_price, 0) as `cost_price`,";
		sql += " IFNULL(lot.price_bo_id, 0) as `price_bo_id`,";
		sql += " IFNULL(reg.price, 0) as `price`,";
		sql += " IFNULL(reg.count, 0) as `count`,";
		sql += " IFNULL(reg.reg_date, 0) as `reg_date`";
		// sql += " IFNULL(p.`name`, '') as `product_name`";
		sql += " from `registry_product` reg";
		// sql += " join (select * from product where `sync_flag` = 0 and
		// is_group = false AND deleted=false "
		// + ItemFilter(searchData, parent, selectChildGroups) + " LIMIT " +
		// currentPage * pageSize + ","
		// + pageSize + ") p ON reg.`product_id` = p.`id` ";
		sql += " join lotofproduct lot ON reg.lotOfProduct_id = lot.id";
		// TODO: and lot.`product_id`=" + product.id;???
		sql += " WHERE reg.deleted=false AND lot.deleted=false and reg.`product_id`=" + product.id;
		if (store != null)
			sql += " AND reg.store_id=" + store.id;
		sql += " AND reg.reg_date <=" + Format.endOfDay(docDate).getTime();
		sql += ")";
		sql += " ORDER BY lot.id, reg.reg_date";

		// TODO: filter
		try {
			System.out.println(sql);

			{
				list = new ArrayList<>();
				mapper.level = 0;// GenericRowMapper.get(LotOfProduct.class)
				list = lotDao.queryRaw(sql, mapper).getResults();

				// Только что введенные позиции в Номенклатуру
				int lot_id = -10;
				for (LotOfProduct lot : list)
					if (lot.id == 0)
						lot.id = lot_id--;

				// Подгружаем документы и ед.изм.
				for (LotOfProduct lot : list) {
					// lot.doc =
					// FactoryByRegTypeFree.Create(lot.doc_type).GetById(lot.doc.id);
					lot.doc = getDoc(lot);
					lot.product.units = new UnitsFactory().GetById(lot.product.units.id);
				}
				// Агрегация
				list = SumGroupBalace(list);// проверяем

				for (LotOfProduct lot : list)
					if (lot.product == null)
						log.ERROR("Ge4TP", "lot.product == null");
					else if (lot.product.id == 0)
						log.ERROR("Ge4TP", "lot.product.id == 0");
			}
			profiler.Stop("Lot.Ge4TP");
		} catch (Exception e) {
			profiler.Stop("Lot.Ge4TP");
			log.ERROR("Ge4TP", e);
		}

		profiler.PrintElapsed("Lot.Ge4TP");

		return list;
	}

	@Override
	public List GetByPeriod(Date startDate, Date endDate) {
		if (Global.InMemoryDb) {
			return new ArrayList<>();
		} else {
			try {
				QueryBuilder<LotOfProduct, Integer> builder = getQueryBuilder();
				Where<LotOfProduct, Integer> where = builder.where();
				NonDeletedFilter(where);

				String sql = where.getStatement();
				// sql += " and GetDocDateByType(doc_type, doc_id) >= " +
				// startDate.getTime();
				sql += " and GetDocDateByType(doc_type, doc_id) <= " + endDate.getTime();
				where = builder.where();
				where.raw(sql);
				System.out.println(builder.prepareStatementString());
				return builder.query();
			} catch (Exception e) {
				log.ERROR("GetByPeriod", e);
			}
		}
		return new ArrayList<>();
	}

}