package maxzawalo.c2.full.analitics;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import maxzawalo.c2.base.data.factory.TablePartItemFactory;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.bo.document.deliverynote.DeliveryNote;
import maxzawalo.c2.free.bo.document.deliverynote.DeliveryNoteTablePart;
import maxzawalo.c2.free.bo.store.StoreDocBO;
import maxzawalo.c2.free.bo.store.StoreTP;
import maxzawalo.c2.free.data.factory.catalogue.ContractorFactory;
import maxzawalo.c2.full.bo.document.cashvoucher.CashVoucher;
import maxzawalo.c2.full.bo.document.cashvoucher.CashVoucherTablePart;
import maxzawalo.c2.full.bo.registry.RegistryAccounting;
import maxzawalo.c2.full.data.factory.registry.RegistryAccountingFactory;

public class MajorContributorReport {
	public static List<Entry<Contractor, Double>> get(Date fromDate, Date toDate) {
		String acc = "51";
		// Исключить расходные от Контрагента
		List<RegistryAccounting> all = new RegistryAccountingFactory().GetByAccount(fromDate, toDate, acc,
				RegistryAccountingFactory.ContributorFilter());

		List<Entry<Contractor, Double>> group = new ArrayList<>();
		all.stream()
				.collect(Collectors.groupingBy(RegistryAccounting::getKtSubcount1Id,
						Collectors.summingDouble(r -> ((RegistryAccounting) r).sum)))
				.entrySet().stream()
				.map(item -> new AbstractMap.SimpleEntry(new ContractorFactory().GetById(item.getKey()),
						Format.defaultRound(item.getValue())))
				.forEach(i -> group.add(i));
		// .collect(Collectors.toList());

		Collections.sort(group, (g2, g1) -> g1.getValue().compareTo(g2.getValue()));

		group.forEach((item) -> System.out.println(item));
		return group;
	}

	public static List<RegistryAccounting> getTransactions(Date fromDate, Date toDate, Contractor contractor) {
		String acc = "51";
		// Исключить расходные от Контрагента
		List<RegistryAccounting> all = new RegistryAccountingFactory().GetByAccount(fromDate, toDate, acc,
				RegistryAccountingFactory.ContributorFilter());
		all.removeIf(r -> r.KtSubcount1.id != contractor.id);
		return all;
	}

	public static List<Entry<Contractor, Double>> ProfitReport(Date fromDate, Date toDate) {
		List<StoreTP> list = ((TablePartItemFactory) new TablePartItemFactory()
				.Create(DeliveryNoteTablePart.Product.class)).GetByPeriod(DeliveryNote.class, fromDate, toDate);

		Map<Integer, Double> map = new HashMap<>();
		for (StoreDocBO doc : TransactionByDocPeriod.GetDocuments(fromDate, toDate, new DeliveryNote())) {
			double sum = 0;
			if (map.containsKey(doc.contractor.id))
				sum = map.get(doc.contractor.id);

			for (StoreTP tp : list.stream().filter(tp -> tp.doc == doc.id).collect(Collectors.toList()))
				sum += (tp.price - tp.price_discount_off) * tp.count;

			sum = Format.defaultRound(sum);
			map.put(doc.contractor.id, sum);
		}

		List<Entry<Contractor, Double>> group = new ArrayList<>();
		map.entrySet().stream().map(
				item -> new AbstractMap.SimpleEntry(new ContractorFactory().GetById(item.getKey()), item.getValue()))
				.forEach(i -> group.add(i));
		// .collect(Collectors.toList());

		group.removeIf(g -> g.getValue() == 0);

		Collections.sort(group, (g2, g1) -> g1.getValue().compareTo(g2.getValue()));

		group.forEach((item) -> System.out.println(item));
		return group;
	}

	public static List<Entry<Date, Double>> ProfitReportCache(Date fromDate, Date toDate) {
		List<StoreTP> list = ((TablePartItemFactory) new TablePartItemFactory()
				.Create(CashVoucherTablePart.Product.class)).GetByPeriod(CashVoucher.class, fromDate, toDate);

		Map<Long, Double> map = new HashMap<>();
		for (StoreDocBO doc : TransactionByDocPeriod.GetDocuments(fromDate, toDate, new CashVoucher())) {
			double sum = 0;
			Long time = Format.beginOfDay(doc.DocDate).getTime();
			if (map.containsKey(time))
				sum = map.get(time);

			for (StoreTP tp : list.stream().filter(tp -> tp.doc == doc.id).collect(Collectors.toList()))
				sum += (tp.price - tp.price_discount_off) * tp.count;

			sum = Format.defaultRound(sum);
			map.put(time, sum);
		}

		List<Entry<Date, Double>> group = new ArrayList<>();
		map.entrySet().stream().map(item -> new AbstractMap.SimpleEntry(new Date(item.getKey()), item.getValue()))
				.forEach(i -> group.add(i));
		// .collect(Collectors.toList());

		group.removeIf(g -> g.getValue() == 0);

		Collections.sort(group, (g1, g2) -> ((Long) g1.getKey().getTime()).compareTo(((Long) g2.getKey().getTime())));

		group.forEach((item) -> System.out.println(item));
		return group;
	}
}