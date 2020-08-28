package maxzawalo.c2.full.bo.registry;

import javax.swing.JLabel;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.registry.Registry;
import maxzawalo.c2.base.utils.Format;

@DatabaseTable(tableName = "registry_accounting")
public class RegistryAccounting extends Registry<RegistryAccounting> {
	public static class fields {
		// calc
		public static final String SALDO = "saldo";

	}

	@BoField(caption = "Дт Счет")
	@DatabaseField(index = true, width = 10)
	public String DtAccount = "";

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = false, canBeNull = false)
	public BO DtSubcount1 = new BO();

	@BoField(caption = "Кт Счет")
	@DatabaseField(index = true, width = 10)
	public String KtAccount = "";

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = false, canBeNull = false)
	public BO KtSubcount1 = new BO();

	@BoField(caption = "Сумма", horizontalAlignment = JLabel.RIGHT)
	@DatabaseField
	public double sum = 0;

	@BoField(caption = "Описание", horizontalAlignment = JLabel.RIGHT)
	@DatabaseField(index = true, width = 100)
	public String RegMeta = "";

	public Integer getKtSubcount1Id() {
		return KtSubcount1.id;
	}

	@Override
	public String toString() {
		return Format.Show(reg_date) + "\t" + RegMeta + "\t" + sum + "\t" + DtAccount + "|" + KtAccount;
	}
}