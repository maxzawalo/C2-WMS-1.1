package maxzawalo.c2.full.data.factory.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.j256.ormlite.dao.Dao;

import maxzawalo.c2.base.bo.registry.RegType;
import maxzawalo.c2.base.data.DbHelper;
import maxzawalo.c2.base.data.GenericRowMapper;
import maxzawalo.c2.base.data.factory.FactoryBO;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.base.utils.Profiler;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.bo.Product;
import maxzawalo.c2.free.bo.Store;
import maxzawalo.c2.free.bo.registry.RegistryProduct;
import maxzawalo.c2.free.data.factory.catalogue.ProductFactory;
import maxzawalo.c2.free.data.factory.catalogue.UnitsFactory;
import maxzawalo.c2.full.bo.view.ContractorTransactionView;
import maxzawalo.c2.full.bo.view.TransactionView;

public class TransactionViewFactory extends FactoryBO<TransactionView> {

	public static List<TransactionView> get(Date fromDate, Date toDate, Store store, Product product,
			Contractor contractor, String searchData) {
		List<TransactionView> list = new ArrayList<>();
		// boolean groupTop = true;

		String sql = "";
		// if (groupTop)
		// sql += " select * from (";
		sql += " select";
		sql += " rp.reg_date `doc_date`,";
		// sql += " GetDocCodeByType(rp.reg_type,rp.reg_id) `doc_code`,";
		sql += " rp.reg_id `reg_id`,";
		sql += " rp.reg_type `reg_type`,";// GetDocNameByType
		if (product == null || product.is_group) {
			// Неточный поиск/Группа
			if (product == null)
				product = new Product();
			sql += " rp.product_id `product_id`,";
		}
		sql += " IFNULL(rp.contractor_id, 0) `contractor_id`,";
		// sql += " IFNULL(c.name,'') `contractor`,";
		// sql += " GetDocContractorByType(rp.reg_type,rp.reg_id)
		// `contractor_id`,";
		// if (groupTop)
		// sql += " abs(sum(ROUND(rp.count,3))) `reg_count`,";
		// else
		sql += " ROUND(rp.count,3) `reg_count`,";
		sql += " ROUND(lot.cost_price,2) `lot_cost_price`,";
		sql += " ROUND(lot.price,2) `lot_price`,";
		sql += " ROUND(rp.price,2) `reg_price`,";
		sql += " (CASE";
		sql += " WHEN lot.price <> rp.price THEN (select '+')";
		sql += " ELSE (select '')";
		sql += "END) `is_price_diff`";

		sql += " from registry_product rp";
		if (product.is_group)
			sql += " join product p on rp.product_id = p.id";

		// sql += " left join product p on rp.product_id = p.id";
		sql += " left join lotofproduct lot on rp.lotOfProduct_id = lot.id";
		// sql += " left join contractor c on
		// GetDocContractorByType(rp.reg_type,rp.reg_id) = c.id";

		sql += " where not rp.deleted and rp.sync_flag = 0 ";
		if (store != null)
			sql += " and rp." + RegistryProduct.fields.STORE + " = " + store.id;
		if (contractor != null)
			sql += " and rp.contractor_id = " + contractor.id;
		sql += " and rp.reg_date>=" + Format.beginOfDay(fromDate).getTime();
		sql += " and rp.reg_date<=" + Format.endOfDay(toDate).getTime();
		// if (product != null) {
		if (product.is_group)
			sql += new ProductFactory().ItemFilter(new Product(), product, true, "p.", "");
		else {
			if (product.id == 0)
				// TODO: "rp.product_" что то надо делать
				sql += new ProductFactory().ItemFilter(product, null, true, "rp.product_", searchData);
			else
				sql += " and rp.product_id = " + product.id;
		}

		// if (groupTop) {
		// sql += " group by rp.product_id ";
		// sql += " ) t ";
		// sql += " order by t.reg_count desc, t.reg_price desc;";
		// }

		try {
			Dao<TransactionView, Integer> boDao = DbHelper.geDaos(TransactionView.class);
			// QueryBuilder<TransactionView, Integer> builder =
			// boDao.queryBuilder();
			System.out.println(sql);
			Profiler p = new Profiler();
			p.Start("TransactionViewGet");
			list = boDao.queryRaw(sql, GenericRowMapper.get(TransactionView.class)).getResults();

			if (contractor == null)
				// Сортируем если выборка не по Контрагенту
				Collections.sort(list,
						(TransactionView tw1, TransactionView tw2) -> tw1.doc_date.compareTo(tw2.doc_date));

			System.out.println(list.size());
			for (TransactionView tw : list) {
				// TODO: Ссылка на документ - переход - по doc_type(doc_id ведь
				// есть)
				String href = "/DocForm.html?class=" + RegType.GetDocClass(Integer.parseInt(tw.reg_type)) + "&id="
						+ tw.reg_id;
				tw.reg_type = "<a href='" + href + "' target='_blank'>" + RegType.ToText(Integer.parseInt(tw.reg_type))
						+ "</a>";
				tw.date = Format.Show(new Date(tw.doc_date));
				tw.contr = (tw.contractor == null ? "" : tw.contractor.name);
				tw.product_code = (tw.product == null ? "" : tw.product.code);
				// TODO: Ссылка на Номенклатуру - переход
				tw.product_name = (tw.product == null ? "" : tw.product.name);
			}

			p.Stop("TransactionViewGet");
			p.PrintElapsed("TransactionViewGet");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

	public static List<ContractorTransactionView> getByContractor(Date fromDate, Date toDate, Contractor contractor) {
		// TODO: учеть что покупатель м.б. поставщиком
		List<TransactionView> list = get(fromDate, toDate, null, null, contractor, "");
		// List<TransactionView> transform =
		// list.stream().collect(Collectors.groupingBy(p ->
		// p.product.id)).entrySet()
		// .stream()
		// .map(e -> e.getValue().stream()
		// .reduce((f1, f2) -> new TransactionView(f1.product, f1.reg_count +
		// f2.reg_count)))
		// .map(f -> f.get())
		// .collect(Collectors.toList());

		Map<Integer, List<TransactionView>> transform = list.stream().collect(Collectors.groupingBy(p -> p.product.id));
		List<ContractorTransactionView> report = new ArrayList<>();
		for (Integer id : transform.keySet()) {
			List<TransactionView> group = transform.get(id);
			ContractorTransactionView item = new ContractorTransactionView();
			item.product = group.get(0).product;
			item.product_name = group.get(0).product.name;
			item.product_code = group.get(0).product.code;
			item.units = new UnitsFactory().GetById(group.get(0).product.units.id);
			// integers.values().stream().mapToInt(i -> i).reduce(0, (x,y) ->
			// x+y);
			item.reg_count = Format.countRound(Math.abs(group.stream().mapToDouble(t -> t.reg_count).sum()));
			item.reg_price = Format
					.defaultRound(Math.abs(group.stream().mapToDouble(t -> t.reg_price).average().getAsDouble()));
			item.cost_price = Format
					.defaultRound(Math.abs(group.stream().mapToDouble(t -> t.lot_cost_price).average().getAsDouble()));
			report.add(item);
		}
		report.sort(Comparator.comparing((ContractorTransactionView t) -> t.reg_count).reversed()
				.thenComparing(t -> t.reg_price));

		return report;
	}
}