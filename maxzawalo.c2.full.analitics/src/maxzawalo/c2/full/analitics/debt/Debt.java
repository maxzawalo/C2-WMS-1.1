package maxzawalo.c2.full.analitics.debt;

import java.util.Date;

import maxzawalo.c2.base.utils.Format;

public class Debt {
	public Date date;
	public double sum = 0;
	public double balance = 0;
	public double days = 0;

	@Override
	public String toString() {
		return Format.Show(date) + "\t" + sum + "\t" + balance + "\t" + days;
	}
}