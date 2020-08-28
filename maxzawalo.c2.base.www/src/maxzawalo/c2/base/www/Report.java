package maxzawalo.c2.base.www;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.data.factory.CatalogueFactory;
import maxzawalo.c2.base.utils.FileUtils;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.base.utils.Logger;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.bo.Product;
import maxzawalo.c2.free.bo.Store;
import maxzawalo.c2.free.bo.registry.RegistryProduct;
import maxzawalo.c2.free.data.factory.catalogue.ContractorFactory;
import maxzawalo.c2.free.data.factory.catalogue.ProductFactory;
import maxzawalo.c2.free.data.factory.registry.RegistryProductFactory;
import maxzawalo.c2.free.reporter.HtmlReporter;
import maxzawalo.c2.full.analitics.CustomerDebt;
import maxzawalo.c2.full.analitics.MajorContributorReport;
import maxzawalo.c2.full.analitics.TopProduct;
import maxzawalo.c2.full.analitics.TransactionByDocPeriod;
import maxzawalo.c2.full.bo.registry.RegistryAccounting;
import maxzawalo.c2.full.bo.view.ContractorTransactionView;
import maxzawalo.c2.full.bo.view.OldProductView;
import maxzawalo.c2.full.bo.view.RemainingStockView;
import maxzawalo.c2.full.bo.view.TransactionView;
import maxzawalo.c2.full.data.factory.view.OldProductViewFactory;
import maxzawalo.c2.full.data.factory.view.RemainingStockViewFactory;
import maxzawalo.c2.full.data.factory.view.TransactionViewFactory;

//TODO: вынести в  free.reporter
public class Report {
	static Logger log = Logger.getLogger(Report.class);

	// TODO: безопасность, роли (пустая выдача)
	public static String Get(String api, Map<String, String> q) throws Exception {
		String data = "";
		if (api.equals("OldProductView")) {
			data = OldProductView(q);
		} else if (api.equals("ContractorTransactionView")) {
			data = ContractorTransactionView(q);
		} else if (api.equals("CustomerDebtView")) {
			data = CustomerDebtView(q);
		} else if (api.equals("CustomerDebt_Transactions")) {
			data = CustomerDebt_Transactions(q);
		} else if (api.equals("TransactionView")) {
			data = TransactionView(q);
		} else if (api.equals("RemainingStockView")) {
			data = RemainingStockView(q);
		} else if (api.equals("MajorContributorReport")) {
			data = MajorContributorReport(q);
		} else if (api.equals("MajorContributorReport_Transactions")) {
			data = MajorContributorReport_Transactions(q);
		} else if (api.equals("TopProductByPrice")) {
			data = TopProductByPrice(q);
		} else if (api.equals("TopProduct")) {
			data = TopProduct(q);
		} else if (api.equals("ProfitReport")) {
			data = ProfitReport(q);
		} else if (api.equals("ProfitReportCache")) {
			data = ProfitReportCache(q);
		}

		// Сохраняем на диск
		FileUtils.Text2File(FileUtils.GetReportDir() + api + "_" + System.currentTimeMillis() + ".html", data, false);
		return data;
	}

	protected static String CustomerDebt_Transactions(Map<String, String> q) {
		// TODO: toDate filter
		String data;
		// TODO: action in full version
		int contractor = Integer.parseInt(q.get("contractor"));
		data = HtmlReporter.Create(RegistryAccounting.class, CustomerDebt.GetTransactions(contractor), "Проводки для отчета 'Задолженность покупателей по срокам долга'",
				new ContractorFactory().GetById(contractor).name, "");
		return data;
	}

	protected static String CustomerDebtView(Map<String, String> q) {
		String data;
		// TODO: action in full version
		Date toDate = new Date(Long.parseLong(q.get("toDate")));
		data = HtmlReporter.Create(CustomerDebt.Create(toDate), CustomerDebt.align, "customer_debt", "Задолженность покупателей по срокам долга", Format.Show(toDate), "");
		return data;
	}

	protected static String ContractorTransactionView(Map<String, String> q) {
		String data;
		long fromDate = Long.parseLong(q.get("fromDate"));
		long toDate = Long.parseLong(q.get("toDate"));
		Contractor contractor = new ContractorFactory().GetById(Integer.parseInt(q.get("contractor")));
		data = HtmlReporter.Create(ContractorTransactionView.class, new TransactionViewFactory().getByContractor(new Date(fromDate), new Date(toDate), contractor), "Отгрузка по Контрагенту",
				contractor.name, "с " + Format.Show(new Date(fromDate)) + " по " + Format.Show(new Date(toDate)));
		return data;
	}

	protected static String OldProductView(Map<String, String> q) {
		String data;
		int days = Integer.parseInt(q.get("days"));
		data = HtmlReporter.Create(OldProductView.class, new OldProductViewFactory().get(null, days), "Залежалый товар", "Дней лежания (min): " + days, "");
		return data;
	}

	protected static String TransactionView(Map<String, String> q) throws UnsupportedEncodingException {
		String data;
		Date fromDate = new Date(Long.parseLong(q.get("fromDate")));
		Date toDate = new Date(Long.parseLong(q.get("toDate")));
		String searchData = java.net.URLDecoder.decode(q.get("searchData"), "UTF-8");
		Store store = (Store) ProcessBO((BO) new CatalogueFactory<>().Create(Store.class).GetById(TryParseInt(q, "store", 0)));
		Product p = new ProductFactory().GetById(Integer.parseInt(q.get("product")));
		p = (Product) ProcessBO(p);
		data = HtmlReporter.Create(TransactionView.class, new TransactionViewFactory().get(fromDate, toDate, store, p, null, searchData), "Проводки/движение",
				(store == null ? "" : "" + store) + "</br>" + searchData, "с " + Format.Show(fromDate) + " по " + Format.Show(toDate));
		return data;
	}

	public static int TryParseInt(Map<String, String> q, String param, int defVal) {
		try {
			return Integer.parseInt(q.get(param));
		} catch (Exception e) {
			log.ERROR("TryParseInt", e);
		}

		return defVal;
	}

	protected static String RemainingStockView(Map<String, String> q) {
		String data;
		Store store = (Store) ProcessBO((BO) new CatalogueFactory<>().Create(Store.class).GetById(TryParseInt(q, "store", 0)));
		Product parent = new ProductFactory().GetById(Integer.parseInt(q.get("parent")));
		parent = (Product) ProcessBO(parent);
		List<RemainingStockView> list = new RemainingStockViewFactory().get(store, parent);
		data = HtmlReporter.Create(RemainingStockView.class, list, "Остатки на складе", (store == null ? "" : "" + store), (parent == null ? "" : "" + parent));
		return data;
	}

	protected static String MajorContributorReport_Transactions(Map<String, String> q) {
		String data;
		Date fromDate = new Date(Long.parseLong(q.get("fromDate")));
		Date toDate = new Date(Long.parseLong(q.get("toDate")));
		Contractor contractor = new ContractorFactory().GetById(Integer.parseInt(q.get("contractor")));

		data = HtmlReporter.Create(RegistryAccounting.class, MajorContributorReport.getTransactions(fromDate, toDate, contractor), "Проводки для отчета 'Крупнейшие плательщики'", contractor.name,
				"с " + Format.Show(fromDate) + " по " + Format.Show(toDate));
		return data;
	}

	protected static String TopProduct(Map<String, String> q) {
		String data;
		Date fromDate = new Date(Long.parseLong(q.get("fromDate")));
		Date toDate = new Date(Long.parseLong(q.get("toDate")));
		Store store = (Store) ProcessBO((BO) new CatalogueFactory<>().Create(Store.class).GetById(TryParseInt(q, "store", 0)));
		List<TopProduct> list = new TransactionByDocPeriod().TopProduct(fromDate, toDate, store, 0);

		for (TopProduct tp : list)
			tp.name = LinkTransactionView(fromDate, toDate, store, new ProductFactory().GetById(tp.id));

		data = HtmlReporter.Create(TopProduct.class, list, "Топ продаж за период (кол-во)", (store == null ? "" : "" + store), "с " + Format.Show(fromDate) + " по " + Format.Show(toDate));
		return data;
	}

	protected static String TopProductByPrice(Map<String, String> q) throws Exception {
		String data;
		Date fromDate = new Date(Long.parseLong(q.get("fromDate")));
		Date toDate = new Date(Long.parseLong(q.get("toDate")));
		Store store = (Store) ProcessBO((BO) new CatalogueFactory<>().Create(Store.class).GetById(TryParseInt(q, "store", 0)));
		// TODO FIFO?
		List<RegistryProduct> turnoverBySum = new RegistryProductFactory().TurnoverBySum(store, fromDate, toDate);

		List<List<String>> matrix = new ArrayList<>();
		List<String> cols = new ArrayList<>();
		cols.add("Номенклатура");
		cols.add("Сумма, руб");
		cols.add("Кол-во");
		matrix.add(cols);

		for (RegistryProduct rp : turnoverBySum) {
			rp.product = new ProductFactory().GetById(rp.product.id);
			cols = new ArrayList<>();
			String link = LinkTransactionView(fromDate, toDate, store, rp.product);

			cols.add(link);
			cols.add("" + rp.price);
			cols.add("" + rp.count);
			matrix.add(cols);
		}
		data = HtmlReporter.Create(matrix, "Топ продаж за период (Цена)", "Топ продаж за период (Цена)", (store == null ? "" : "" + store),
				"с " + Format.Show(fromDate) + " по " + Format.Show(toDate));
		return data;
	}

	protected static String MajorContributorReport(Map<String, String> q) {
		String data;
		// http://localhost:9194/report/MajorContributorReport?fromDate=1514754000000&toDate=1519506000000
		Date fromDate = new Date(Long.parseLong(q.get("fromDate")));
		Date toDate = new Date(Long.parseLong(q.get("toDate")));
		List<Entry<Contractor, Double>> list = MajorContributorReport.get(fromDate, toDate);
		List<List<String>> matrix = new ArrayList<>();

		List<String> cols = new ArrayList<>();
		cols.add("Контрагент");
		cols.add("Сумма, руб");
		matrix.add(cols);

		for (Entry<Contractor, Double> item : list) {
			cols = new ArrayList<>();
			String link = "<a href='/report/MajorContributorReport_Transactions?fromDate=" + fromDate.getTime() + "&toDate=" + toDate.getTime() + "&contractor=" + item.getKey().id
					+ "' target='_blank'>" + item.getKey().name + "</a>";
			cols.add(link);
			cols.add(item.getValue() + "");
			matrix.add(cols);
		}

		cols = new ArrayList<>();
		cols.add("Итого:");
		double sum = list.stream().map(Entry::getValue).collect(Collectors.summingDouble(i -> i));
		cols.add("" + sum);
		matrix.add(cols);

		data = HtmlReporter.Create(matrix, "Крупнейшие плательщики", "Крупнейшие плательщики", "", "с " + Format.Show(fromDate) + " по " + Format.Show(toDate));
		return data;
	}

	public static String ProfitReport(Map<String, String> q) {
		// String data = "";
		Date fromDate = new Date(Long.parseLong(q.get("fromDate")));
		Date toDate = new Date(Long.parseLong(q.get("toDate")));

		List<Entry<Contractor, Double>> list = MajorContributorReport.ProfitReport(fromDate, toDate);
		List<List<String>> matrix = new ArrayList<>();

		List<String> cols = new ArrayList<>();
		cols.add("Контрагент");
		cols.add("Сумма, руб");
		matrix.add(cols);

		for (Entry<Contractor, Double> item : list) {
			cols = new ArrayList<>();
			String link = item.getKey().name;
			// "<a href='/report/MajorContributorReport_Transactions?fromDate="
			// + fromDate.getTime()
			// + "&toDate=" + toDate.getTime() + "&contractor=" +
			// item.getKey().id + "' target='_blank'>"
			// + item.getKey().name + "</a>";
			cols.add(link);
			cols.add(item.getValue() + "");
			matrix.add(cols);
		}

		cols = new ArrayList<>();
		cols.add("Итого:");
		double sum = Format.defaultRound(list.stream().map(Entry::getValue).collect(Collectors.summingDouble(i -> i)));
		cols.add("" + sum);
		matrix.add(cols);

		return HtmlReporter.Create(matrix, "Прибыль по отгрузке", "Прибыль по отгрузке", "", "с " + Format.Show(fromDate) + " по " + Format.Show(toDate));
	}

	private static String ProfitReportCache(Map<String, String> q) {
		Date fromDate = new Date(Long.parseLong(q.get("fromDate")));
		Date toDate = new Date(Long.parseLong(q.get("toDate")));

		List<Entry<Date, Double>> list = MajorContributorReport.ProfitReportCache(fromDate, toDate);
		List<List<String>> matrix = new ArrayList<>();

		List<String> cols = new ArrayList<>();
		cols.add("Дата");
		cols.add("Сумма, руб");
		matrix.add(cols);

		for (Entry<Date, Double> item : list) {
			cols = new ArrayList<>();
			cols.add(Format.Show(item.getKey()));
			cols.add(item.getValue() + "");
			matrix.add(cols);
		}

		cols = new ArrayList<>();
		cols.add("Всего:");
		double sum = Format.defaultRound(list.stream().map(Entry::getValue).collect(Collectors.summingDouble(i -> i)));
		cols.add("" + sum);
		matrix.add(cols);

		return HtmlReporter.Create(matrix, "Прибыль по кассе", "Прибыль по кассе", "", "с " + Format.Show(fromDate) + " по " + Format.Show(toDate));
	}

	protected static String LinkTransactionView(Date fromDate, Date toDate, Store store, Product p) {
		return "<a href='/report/TransactionView?fromDate=" + fromDate.getTime() + "&toDate=" + toDate.getTime() + "&store=" + (store == null ? "0" : "" + store.id) + "&product=" + p.id
				+ "&searchData=' target='_blank'>" + p.name + "</a>";
	}

	protected static BO ProcessBO(BO p) {
		if (p != null && p.id == 0)
			p = null;
		return p;
	}
}