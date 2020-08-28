package maxzawalo.c2.full.analitics.debt;

public class DebtPeriod {
	public int fromDays = 0;
	public int toDays = 0;
	public double sum = 0;

	public DebtPeriod(int fromDays, int toDays) {
		this.fromDays = fromDays;
		this.toDays = toDays;
	}

	public String toReportCaption() {
		if (fromDays == 0)
			return "До " + (toDays - 1) + " дней";
		if (toDays == 0)
			return "Свыше " + (fromDays - 1) + " дней";
		return "От " + fromDays + " до " + (toDays - 1) + " дней";
	}
}