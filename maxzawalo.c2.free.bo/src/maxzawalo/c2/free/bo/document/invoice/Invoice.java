package maxzawalo.c2.free.bo.document.invoice;

import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.bo.registry.RegType;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.free.bo.registry.RegistryProduct;
import maxzawalo.c2.free.bo.store.StoreDocBO;

@XmlRootElement(name = "issuance", namespace = "http://www.w3schools.com")
@XmlType(name = "issuance")
@XmlAccessorType(XmlAccessType.FIELD)
@BoField(caption = "Приходная накладная")
public class Invoice extends StoreDocBO<Invoice> {

	public static class fields {
		public static final String IN_FORM_NUMBER = "in_form_number";
		public static final String IN_FORM_DATE = "in_form_date";
		public static final String DELIVERY = "delivery";
	}

	@BoField(caption = "Входящий номер")
	@DatabaseField(index = true, width = 30, columnName = Invoice.fields.IN_FORM_NUMBER)
	public String in_form_number = "";

	@DatabaseField(index = true, dataType = DataType.DATE_LONG, columnName = Invoice.fields.IN_FORM_DATE)
	public Date in_form_date = new Date();

	@DatabaseField(index = true, columnName = Invoice.fields.DELIVERY)
	public boolean delivery = false;

	// @DatabaseField
	// public double total_1c = 0;//для тестирования округления

	public Invoice() {
		reg_type = RegType.Invoice;
	}

	@Override
	protected void setTpTypes() {
		itemProductT = InvoiceTablePart.Product.class;
		itemServiceT = InvoiceTablePart.Service.class;
		itemEquipmentT = InvoiceTablePart.Equipment.class;
	}

//	@Override
//	public void setUsedRegistries() {
//		AddUsedRegistry(new RegistryProduct());
//	}

	

//	@Override
//	public String toString() {
//		super.toString()
//		return "Приход " + Format.Show(this.DocDate) + " " + this.code + " " + this.id;
//	}

	@Override
	protected List<String> getExceptFields() {
		List<String> exceptFields = super.getExceptFields();
		exceptFields.add(Invoice.fields.IN_FORM_NUMBER);
		exceptFields.add(Invoice.fields.IN_FORM_DATE);
		exceptFields.add(Invoice.fields.DELIVERY);

		return exceptFields;
	}
}