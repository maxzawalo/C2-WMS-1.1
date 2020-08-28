package maxzawalo.c2.free.bo.bank;

import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.bo.TablePartItem;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.free.bo.Contract;

public class BankTP<Item> extends TablePartItem<Item> {

	public static class fields {
		public static final String SUM = "sum";
		public static final String RATE_VAT = "rateVat";
		public static final String SUM_VAT = "sumVat";
		public static final String CONTRACT = "contract_id";
	}

	@BoField(caption = "Договор", fieldName1C = "ДоговорКонтрагента")
	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, columnName = BankTP.fields.CONTRACT)
	public Contract contract;

	@BoField(caption = "Сумма", fieldName1C = "СуммаПлатежа")
	@DatabaseField(columnName = BankTP.fields.SUM)
	public double sum = 0;

	// ПеречислениеСсылка.СтавкиНДС
	@BoField(caption = "Ставка НДС", fieldName1C = "СтавкаНДС")
	@DatabaseField(columnName = BankTP.fields.RATE_VAT)
	// TODO:Settings.defaultVat;
	public double rateVat = 20;

	@BoField(caption = "Сумма НДС", fieldName1C = "СуммаНДС")
	@DatabaseField(columnName = BankTP.fields.SUM_VAT)
	public double sumVat = 0;

	public BankTP() {
	}

	@Override
	public void Calc(String fieldName) {
		System.out.println("Calc " + fieldName);
		CalcSumVat();
	}

	public void CalcSumVat() {
		sumVat = Format.defaultRound(sum * rateVat / (100 + rateVat));
	}

}