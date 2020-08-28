package maxzawalo.c2.free.bo.registry;

import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.registry.Registry;
import maxzawalo.c2.base.utils.Console;

public class AccAcc<T> extends Registry<T> {
	public static class fields {
		public static final String SubCount1 = "sub_count_1_id";
		public static final String SubCount2 = "sub_count_2_id";
		public static final String SubCount3 = "sub_count_3_id";
		public static final String SubCount4 = "sub_count_4_id";
		public static final String SubCount5 = "sub_count_5_id";
		public static final String SUM = "sum";
		public static final String COUNT = "count";
		public static final String IS_DEBIT = "is_debit";
		public static final String TRANSACTION_NUM = "transaction_num";
	}

	protected boolean active = false;

	public boolean isActive() {
		return active;
	}

	protected boolean passive = false;
	protected boolean currency = false;
	protected boolean off_balance = false;
	protected boolean quantitative = false;

	public boolean isQuantitative() {
		return quantitative;
	}

	protected Class typeSubCount1;
	protected Class typeSubCount2;
	protected Class typeSubCount3;
	protected Class typeSubCount4;
	protected Class typeSubCount5;

	@DatabaseField(index = true, columnName = fields.TRANSACTION_NUM)
	public int transaction_num = 0;

	@DatabaseField(index = true, columnName = fields.IS_DEBIT)
	public boolean is_debit = true;

	@DatabaseField(columnName = fields.SUM, defaultValue = "0")
	public double sum = 0;

	@DatabaseField(columnName = fields.COUNT, defaultValue = "0")
	public double count = 0;

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, columnName = fields.SubCount1)
	public BO SubCount1;

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, columnName = fields.SubCount2)
	public BO SubCount2;

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, columnName = fields.SubCount3)
	public BO SubCount3;

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, columnName = fields.SubCount4)
	public BO SubCount4;

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, columnName = fields.SubCount5)
	public BO SubCount5;

	public AccAcc() {
	}

	public void toDebit(double sum) throws Exception {
		if (quantitative)
			throw new Exception(code + " счет количественный");
		if (sum == 0) {
			Console.I().WARN(getClass(), "toDebit", "Сумма = 0");
		}
		this.is_debit = true;
		this.sum = sum;
		this.count = 0;
	}

	public void toDebit(double sum, double count) throws Exception {
		if (!quantitative)
			throw new Exception(code + " счет не количественный");
		if (sum == 0) {
			Console.I().WARN(getClass(), "toDebit", "Сумма = 0");
		}
		this.is_debit = true;
		this.sum = sum;
		this.count = count;
	}

	public void toCredit(double sum) throws Exception {
		if (quantitative)
			throw new Exception(code + " счет количественный");
		if (sum == 0) {
			Console.I().WARN(getClass(), "toCredit", "Сумма = 0");
		}
		this.is_debit = false;
		this.sum = sum;
		this.count = 0;
	}

	public void toCredit(double sum, double count) throws Exception {
		if (!quantitative)
			throw new Exception(code + " счет не количественный");
		if (sum == 0) {
			Console.I().WARN(getClass(), "toCredit", "Сумма = 0");
		}
		this.is_debit = false;
		this.sum = sum;
		this.count = count;
	}

	@Override
	public String toString() {
		return // (is_debit ? "Дт" : "Кт") +
		// "\t" + //
		code + "\t" + //
				sum + "\t" + (quantitative ? count : " ");
	}

	public String Print() {
		return code + "\t" + //
				sum + "\t" + (quantitative ? count : " ") + "\t" + SubCount1;
	}
}