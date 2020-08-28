package maxzawalo.c2.free.bo.document.writeoffmoney;

import java.util.List;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.bo.registry.RegType;
import maxzawalo.c2.free.bo.BankAccount;
import maxzawalo.c2.free.bo.bank.BankDocBO;
import maxzawalo.c2.free.bo.enums.WriteOffMoneyType;

@BoField(caption = "Списание с расчетного счета", type1C = "Документы.СписаниеСРасчетногоСчета")
@DatabaseTable(tableName = "writeoffmoney")
public class WriteOffMoney extends BankDocBO<WriteOffMoney> {

	public static class fields {
		public static final String PAYMENT_DETAILS = "payment_details";
		public static final String WRITEOFFMONEY_TYPE = "writeoffmoney_type_id";
	}

	@BoField(caption = "Вид операции", fieldName1C = "ВидОперации")
	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = false, columnName = fields.WRITEOFFMONEY_TYPE)
	public WriteOffMoneyType writeoffmoney_type;

	@BoField(caption = "Назначение платежа", fieldName1C = "НазначениеПлатежа")
	@DatabaseField(index = true, width = 1000, columnName = fields.PAYMENT_DETAILS)
	public String payment_details = "";

	public WriteOffMoney() {
		reg_type = RegType.WriteOffMoney;
	}

	@Override
	protected void setTpTypes() {
		itemPaymentT = WriteOffMoneyTablePart.Payment.class;
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