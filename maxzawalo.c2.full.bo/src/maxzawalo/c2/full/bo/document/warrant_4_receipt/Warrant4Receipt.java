package maxzawalo.c2.full.bo.document.warrant_4_receipt;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.bo.Coworker;
import maxzawalo.c2.base.bo.registry.RegType;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.free.bo.store.StoreDocBO;

@BoField(caption = "Доверенность")
public class Warrant4Receipt extends StoreDocBO<Warrant4Receipt> {
	public static class fields {
		public static final String END_DATE = "end_date";
		public static final String COWORKER = "coworker_id";
	}

	@DatabaseField(index = true, dataType = DataType.DATE_LONG, columnName = Warrant4Receipt.fields.END_DATE)
	public Date end_date = new Date();

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, columnName = Warrant4Receipt.fields.COWORKER)
	public Coworker coworker;

	public Warrant4Receipt() {
		reg_type = RegType.Warrant4Receipt;
	}

	@Override
	protected void setTpTypes() {
		itemProductT = Warrant4ReceiptTablePart.Product.class;
		itemServiceT = Warrant4ReceiptTablePart.Service.class;
		itemEquipmentT = Warrant4ReceiptTablePart.Equipment.class;
	}

	@Override
	public String toString() {
		return "Доверенность " + Format.Show(this.DocDate) + " " + this.code + " " + this.id;
	}
}