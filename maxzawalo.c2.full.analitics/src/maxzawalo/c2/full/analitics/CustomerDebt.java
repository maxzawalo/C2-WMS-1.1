package maxzawalo.c2.full.analitics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JLabel;

import maxzawalo.c2.base.interfaces.MapSortT;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.base.utils.ListUtils;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.data.factory.catalogue.ContractorFactory;
import maxzawalo.c2.full.analitics.debt.ContractorDebt;
import maxzawalo.c2.full.analitics.debt.Debt;
import maxzawalo.c2.full.analitics.debt.DebtPeriod;
import maxzawalo.c2.full.bo.registry.RegistryAccounting;
import maxzawalo.c2.full.data.factory.registry.RegistryAccountingFactory;

public class CustomerDebt {

	public static List<Integer> align = Arrays.asList(new Integer[] { JLabel.LEFT, JLabel.RIGHT, JLabel.RIGHT,
			JLabel.RIGHT, JLabel.RIGHT, JLabel.RIGHT, JLabel.RIGHT, JLabel.RIGHT, JLabel.RIGHT });

	static MapSortT MapSortByValue = new MapSortT<Integer, Double>() {
		@Override
		public int Do(Object o1, Object o2, boolean desc) {
			return ((Comparable<Double>) ((Map.Entry<Integer, ContractorDebt>) (o2)).getValue().balance)
					.compareTo(((Map.Entry<Integer, ContractorDebt>) (o1)).getValue().balance);
		}
	};

	public static ContractorDebt CalcByContractor(String acc, Date reportDate, List<RegistryAccounting> all,
			int contractorId) {
		List<Debt> debts = new ArrayList<>();
		ContractorDebt cDebt = new ContractorDebt();

		double bablo = 0;
		//
		for (RegistryAccounting ra : all) {
			ra.reg_date = Format.beginOfDay(ra.reg_date);

			// фильтруем по Контрагенту из общего списка
			if (!((ra.DtAccount.startsWith(acc) && ra.DtSubcount1.id == contractorId)
					|| (ra.KtAccount.startsWith(acc) && ra.KtSubcount1.id == contractorId)))
				continue;

			if (ra.DtAccount.startsWith(acc) && ra.DtSubcount1.id == contractorId) {
				cDebt.balance += ra.sum;
				// if (ra.RegMeta.equals("Реализация товаров и услуг"))
				// debt += ra.sum;
				Debt debt = new Debt();
				debt.date = ra.reg_date;
				debt.sum = ra.sum;
				debt.balance = ra.sum;
				debt.days = 0;
				debts.add(debt);

			} else {
				cDebt.balance -= ra.sum;
				bablo += ra.sum;
			}

			cDebt.balance = Format.defaultRound(cDebt.balance);
			// System.out.println(Format.Show(ra.reg_date) + "\t" + ra.sum +
			// "\t" + ra.RegMeta);
		}

		// Проплачиваем по очереди
		for (Debt d : debts) {
			if (bablo != 0) {
				if (d.balance != 0) {
					double b = Math.min(d.balance, bablo);
					d.balance = Format.defaultRound(d.balance - b);
					bablo = Format.defaultRound(bablo - b);
				}
			} else
				break;
		}

		// System.out.println("----------------");
		// Вычисляем дни просрочки
		for (Debt d : debts) {
			if (d.balance != 0)
				d.days = ((reportDate.getTime() - d.date.getTime()) / (24 * 60 * 60 * 1000));

			if (d.days > 30)
				cDebt.overdueBalance = Format.defaultRound(cDebt.overdueBalance + d.balance);
			// System.out.println(Format.Show(d.date) + "\t" + d.sum + "\t" +
			// d.balance + "\t" + d.days);
		}

		// for (Debt d : debts)
		// System.out.println(d);

		// Свертка дней просрочки в периоды
		for (Debt d : debts)
			if (d.days != 0) {
				for (DebtPeriod p : cDebt.periods) {
					if ((d.days >= p.fromDays) && ((p.toDays == 0) || (d.days < p.toDays))) {
						p.sum = Format.defaultRound(p.sum + d.balance);
						break;
					}
					// // Разрывы в периодах цен - берем со следующего периода
					// (себе в минус)
					// if (cost_price < ta.fromSum)
					// break;
				}
			}

		return cDebt;
	}

	public static List<List<String>> Create(Date reportDate) {
		return Create(reportDate, null);
	}

	public static List<List<String>> Create(Date reportDate, Contractor contractor) {
		List<List<String>> matrix = new ArrayList<>();

		String acc = "62";
		List<RegistryAccounting> all = new RegistryAccountingFactory().GetByAccount(new Date(0), reportDate, acc,
				RegistryAccountingFactory.CustomerDebtFilter());

		Set<Integer> ids = new HashSet<>();

		if (contractor == null) {
			for (RegistryAccounting ra : all) {
				if (ra.DtAccount.startsWith(acc))
					ids.add(ra.DtSubcount1.id);
				else if (ra.KtAccount.startsWith(acc))
					ids.add(ra.KtSubcount1.id);
			}
		} else
			ids.add(contractor.id);

		System.out.println(ids);

		Map<Integer, ContractorDebt> debts = new HashMap<>();
		for (int id : ids)
			debts.put(id, CalcByContractor(acc, reportDate, all, id));

		debts = ListUtils.sortMapByValue(debts, MapSortByValue);

		List<String> row = new ArrayList<>();
		row.add("Покупатель");
		row.add("Общая задолженность");
		row.add("В т.ч. просроченная задолженность");

		for (Entry<Integer, ContractorDebt> entry : debts.entrySet()) {
			for (DebtPeriod p : entry.getValue().periods) {
				row.add(p.toReportCaption());
			}
			break;
		}
		matrix.add(row);

		ContractorDebt total = new ContractorDebt();
		double balance = 0;
		double overdueBalance = 0;

		for (Entry<Integer, ContractorDebt> entry : debts.entrySet()) {
			ContractorDebt item = entry.getValue();
			if (item.balance <= 0)
				break;

			balance += item.balance;
			overdueBalance += item.overdueBalance;

			System.out.println("========= " + new ContractorFactory().GetById(entry.getKey()).name + "\t" + item);
			row = new ArrayList<>();
			Contractor c = new ContractorFactory().GetById(entry.getKey());
			c = new ContractorFactory().LoadContactInfo(c);

			String link = "<a href='/report/CustomerDebt_Transactions?contractor=" + c.id + "' target='_blank'>"
					+ c.name + "</a>";
			row.add(link + "</br>" + "<i>" + c.phone + "</i>");

			row.add(Format.Show(item.balance));
			row.add(item.overdueBalance == 0 ? "" : Format.Show(item.overdueBalance));

			for (int i = 0; i < item.periods.size(); i++) {
				DebtPeriod p = item.periods.get(i);
				total.periods.get(i).sum += p.sum;
				row.add(p.sum == 0 ? "" : Format.Show(p.sum));
			}
			matrix.add(row);
		}

		row = new ArrayList<>();
		row.add("Итого");
		row.add(Format.Show(balance));
		row.add(Format.Show(overdueBalance));
		for (DebtPeriod p : total.periods)
			row.add(p.sum == 0 ? "" : Format.Show(p.sum));
		matrix.add(row);

		return matrix;
	}

	public static List<RegistryAccounting> GetTransactions(int contractorId) {
		String acc = "62";
		Date reportDate = new Date();
		List<RegistryAccounting> all = new RegistryAccountingFactory().GetByAccount(new Date(0), reportDate, acc, "");
		List<RegistryAccounting> filtered = new ArrayList<>();
		for (RegistryAccounting ra : all) {
			// фильтруем по Контрагенту из общего списка
			if (!((ra.DtAccount.startsWith(acc) && ra.DtSubcount1.id == contractorId)
					|| (ra.KtAccount.startsWith(acc) && ra.KtSubcount1.id == contractorId)))
				continue;
			filtered.add(ra);
		}
		return filtered;
	}
}