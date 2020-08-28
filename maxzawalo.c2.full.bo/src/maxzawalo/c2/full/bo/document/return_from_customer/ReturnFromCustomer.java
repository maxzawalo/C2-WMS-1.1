package maxzawalo.c2.full.bo.document.return_from_customer;

import java.util.Date;
import java.util.List;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.bo.registry.RegType;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.free.bo.store.StoreDocBO;

@BoField(caption = "Возврат от покупателя")
public class ReturnFromCustomer extends StoreDocBO<ReturnFromCustomer> {

	public static class fields {
		public static final String IN_FORM_NUMBER = "in_form_number";
		public static final String IN_FORM_DATE = "in_form_date";
	}

	@BoField(caption = "Входящий номер")
	@DatabaseField(index = true, width = 30, columnName = ReturnFromCustomer.fields.IN_FORM_NUMBER)
	public String in_form_number = "";

	@DatabaseField(index = true, dataType = DataType.DATE_LONG, columnName = ReturnFromCustomer.fields.IN_FORM_DATE)
	public Date in_form_date = new Date();

	public ReturnFromCustomer() {
		reg_type = RegType.ReturnFromCustomer;
	}

	@Override
	protected void setTpTypes() {
		itemProductT = ReturnFromCustomerTablePart.Product.class;
		itemServiceT = ReturnFromCustomerTablePart.Service.class;
		itemEquipmentT = ReturnFromCustomerTablePart.Equipment.class;
	}

	@Override
	public String toString() {
		return "Возврат от покупателя " + Format.Show(this.DocDate) + " " + this.code + " " + this.id;
	}

	@Override
	protected List<String> getExceptFields() {
		List<String> exceptFields = super.getExceptFields();
		exceptFields.add(ReturnFromCustomer.fields.IN_FORM_NUMBER);
		exceptFields.add(ReturnFromCustomer.fields.IN_FORM_DATE);
		// exceptFields.add("delivery");

		return exceptFields;
	}
}