package maxzawalo.c2.full.data.factory.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.full.bo.registry.RegistryAccounting;
import maxzawalo.c2.full.bo.view.ReconciliationReport;
import maxzawalo.c2.full.data.factory.registry.RegistryAccountingFactory;

public class ReconciliationReportFactory {
	public static ReconciliationReport Create(Contractor contractor, Date fromDate, Date reportDate) {
		fromDate = Format.beginOfDay(fromDate);
		String filter = " and ((DtAccount like '#acc%' and DtSubcount1_id=#contractorId) ";
		filter += " or (KtAccount like '#acc%' and KtSubcount1_id=#contractorId)) ";
		filter = filter.replace("#contractorId", "" + contractor.id);
		filter += RegistryAccountingFactory.CustomerDebtFilter();
		String acc = "62";
		List<RegistryAccounting> all = new RegistryAccountingFactory().GetByAccount(new Date(0), reportDate, acc,
				filter);

		double saldo = 0;

		double turnoverDebet = 0;
		double turnoverKredit = 0;

		List<RegistryAccounting> convolution = new ArrayList<>();
		RegistryAccounting base = new RegistryAccounting();
		base.RegMeta = "Сальдо на начало периода";
		convolution.add(base);

		// Свертка по документам и датам
		for (RegistryAccounting ra : all) {
			ra.reg_date = Format.beginOfDay(ra.reg_date);

			if (ra.DtAccount.startsWith(acc) && ra.DtSubcount1.id == contractor.id) {
				saldo += ra.sum;
				if (!BeforeStartDate(fromDate, ra))
					turnoverDebet += ra.sum;
				ra.RegMeta = "Продажа";
			} else {
				saldo -= ra.sum;
				if (!BeforeStartDate(fromDate, ra))
					turnoverKredit += ra.sum;
				ra.RegMeta = "Оплата";
			}
			saldo = Format.defaultRound(saldo);

			if (BeforeStartDate(fromDate, ra)) {
				base.reg_date = ra.reg_date;
				base.sum = saldo;
			} else {
				if (base.reg_date.equals(ra.reg_date) && ra.RegMeta.equals(base.RegMeta)) {
					base.sum += ra.sum;
					base.sum = Format.defaultRound(base.sum);
				} else {
					base = ra;
					convolution.add(base);
				}
			}
			base.calcFields.put(RegistryAccounting.fields.SALDO, saldo);
		}

		for (RegistryAccounting ra : convolution) {
			System.out.print(ra);
			System.out.println("|" + ra.calcFields.get(RegistryAccounting.fields.SALDO));
		}

		ReconciliationReport report = new ReconciliationReport();
		report.contractor = contractor;
		report.DocDate = reportDate;
		report.fromDate = fromDate;
		report.startSaldo = convolution.get(0).sum;
		convolution.remove(0);
		report.endSaldo = saldo;
		report.convolution = convolution;

		report.turnoverDebet = turnoverDebet;
		report.turnoverKredit = turnoverKredit;
		return report;
	}

	protected static boolean BeforeStartDate(Date fromDate, RegistryAccounting ra) {
		return ra.reg_date.getTime() < fromDate.getTime();
	}
}