package maxzawalo.c2.free.bo;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.bo.SlaveCatalogueBO;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.free.bo.document.bill.Bill;
import maxzawalo.c2.free.bo.enums.ContractType;

@BoField(caption = "Договор", type1C = "Справочники.ДоговорыКонтрагентов")
public class Contract extends SlaveCatalogueBO<Contract, Contractor> {

	public static class fields {
		public static final String DOC_DATE = "DocDate";
		public static final String NUMBER = "number";
		public static final String DOC_CURRENCY = "doc_currency_id";
		public static final String CONTRACT_TYPE = "contract_type_id";
		public static final String IS_BILL = "is_bill";
		public static final String BILL = "bill_id";
		public static final String RETURN_WITH_SIGN = "return_with_sign";
	}

	@BoField(caption = "Дата", fieldName1C = "Дата")
	@DatabaseField(index = true, dataType = DataType.DATE_LONG, columnName = Contract.fields.DOC_DATE)
	public Date DocDate = new Date();

	@BoField(caption = "Номер", fieldName1C = "Номер")
	@DatabaseField(width = 100, columnName = Contract.fields.NUMBER)
	public String number = "";

	
	@BoField(caption = "Валюта", fieldName1C = "ВалютаВзаиморасчетов")
	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, columnName = Contract.fields.DOC_CURRENCY)
	public Currency doc_currency = Settings.mainCurrency;

	@BoField(caption = "Вид договора", fieldName1C = "ВидДоговора")
	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = false, columnName = Contract.fields.CONTRACT_TYPE)
	public ContractType contract_type;

	@DatabaseField(index = true, columnName = Contract.fields.IS_BILL)
	public boolean is_bill = false;

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, columnName = Contract.fields.BILL)
	public Bill bill;

	@DatabaseField(index = true, columnName = Contract.fields.RETURN_WITH_SIGN)
	public boolean return_with_sign = false;

	public Contract() {
		// TODO:
		// // Добавляем загрузку foreignAutoRefresh = false
		// mapper.nonSkipForeign.put("contract_type", ContractType.class);
	}

	@Override
	public String toString() {
		// Договор №23
		// без договора
		// В счет зарплаты
		// счет-фактура 40
		// б/н
		// Договор №Счет №17-03-3927 от 21.03.2017

		String prefix = "Договор №";
		if (number.toLowerCase().contains("счет"))
			prefix = "";

		if (is_bill) {
			prefix = "";
			if (bill != null) {
				number = "Счет " + bill.code;
				return number + " от " + Format.Show(DocDate);
			}
		}
		number = CleanNumber(this.number);
		return prefix + number + (Format.beginOfDay(DocDate).getTime() == Format.GetDate("01.01.0001").getTime() ? ""
				: " от " + Format.Show(DocDate));
	}

	public static String CleanNumber(String number) {
		return number.replace("Договор", "").replace("№", "").trim();
	}

	// @Override
	// protected void Check() {
	// // if (id == 0)
	// name = "" + this; // "Договор №" + number + " от " +
	// // Format.Show(DocDate);
	// }
}