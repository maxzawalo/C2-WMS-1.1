package maxzawalo.c2.full.analitics.debt;

import java.util.ArrayList;
import java.util.List;

public class ContractorDebt {
	public List<DebtPeriod> periods = new ArrayList<>();
	public double balance = 0;
	public double overdueBalance = 0;

	public ContractorDebt() {
		periods.add(new DebtPeriod(0, 8));
		periods.add(new DebtPeriod(8, 16));
		periods.add(new DebtPeriod(16, 31));
		periods.add(new DebtPeriod(31, 61));
		periods.add(new DebtPeriod(61, 91));
		periods.add(new DebtPeriod(91, 0));
	}

	@Override
	public String toString() {
		// System.out.println("========= Периоды
		// =========================");
		for (DebtPeriod p : periods) {
			System.out.println(p.toReportCaption() + "\t" + p.sum);
		}

		System.out.println("balance=" + balance);
		System.out.println("overdueBalance=" + overdueBalance);
		// System.out.println("всего проводок: " + all.size());
		return "";
	}
}