package maxzawalo.c2.free.bo.document.receiptmoney;

import java.util.List;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.bo.registry.RegType;
import maxzawalo.c2.free.bo.bank.BankDocBO;
import maxzawalo.c2.free.bo.enums.ReceiptMoneyType;

@BoField(caption = "Поступление на расчетный счет", type1C = "Документы.ПоступлениеНаРасчетныйСчет")
@DatabaseTable(tableName = "receiptmoney")
public class ReceiptMoney extends BankDocBO<ReceiptMoney> {

	public static class fields {
		public static final String RECEIPT_MONEY_TYPE = "receipt_money_type_id";
		public static final String PAYMENT_DETAILS = "payment_details";
	}

	@BoField(fieldName1C = "ВидОперации", caption = "Вид операции")
	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = false, columnName = ReceiptMoney.fields.RECEIPT_MONEY_TYPE)
	public ReceiptMoneyType receipt_money_type;

	@BoField(caption = "НазначениеПлатежа", fieldName1C = "НазначениеПлатежа")
	@DatabaseField(index = true, width = 1000, columnName = ReceiptMoney.fields.PAYMENT_DETAILS)
	public String payment_details = "";

	public ReceiptMoney() {
		reg_type = RegType.ReceiptMoney;
	}

	@Override
	protected void setTpTypes() {
		itemPaymentT = ReceiptMoneyTablePart.Payment.class;
	}

//	@Override
//	public void setUsedRegistries() {
//		// RegistryProduct transaction = new RegistryProduct();
//		// usedRegistries.add(transaction);
//	}

	@Override
	protected List<String> getExceptFields() {
		List<String> exceptFields = super.getExceptFields();
		exceptFields.add(BankDocBO.fields.IN_NUMBER);
		exceptFields.add(BankDocBO.fields.IN_DATE);
		// exceptFields.add(ReceiptMoney.fields.DELIVERY);

		return exceptFields;
	}
}