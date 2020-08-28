package maxzawalo.c2.free.bo.document.writeoffmoney;

import com.j256.ormlite.table.DatabaseTable;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.free.bo.bank.BankTP;

public class WriteOffMoneyTablePart {
	@BoField(caption = "РасшифровкаПлатежа", type1C = "ТабЧастьДок.СписаниеСРасчетногоСчета.РасшифровкаПлатежа")
	@DatabaseTable(tableName = "writeoffmoney_tp_payment")
	public static class Payment extends BankTP<Payment> {
//		public static class fields {
//			public static final String BILL = "bill_id";
//			public static final String SERVICE_SUM = "service_sum";
//		}
//
//		@BoField(caption = "Счет", fieldName1C = "СчетНаОплату")
//		@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, columnName = fields.BILL)
//		public Bill bill;
//
//		@BoField(caption = "Сумма услуг", fieldName1C = "СуммаУслуг")
//		@DatabaseField(columnName = fields.SERVICE_SUM)
//		public double service_sum = 0;
	}
}