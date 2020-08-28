package maxzawalo.c2.free.bo;

import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.bo.SlaveCatalogueBO;

@BoField(caption = "Контакт", type1C = "ТабЧасть.КонтактнаяИнформация")
public class ContactInfo extends SlaveCatalogueBO<ContactInfo, Contractor> {
	public static class fields {
		public static final String CONTACT_TYPE = "contact_type";
	}
	
	@BoField(fieldName1C = "Вид", caption = "")
	@DatabaseField(index = true, width = 50, columnName = fields.CONTACT_TYPE)
	public String contact_type = "";

	public ContactInfo() {
	}

	@Override
	public String toString() {
		// return super.toString() + " " + ((units != null) ? units.name : "");

		return name + " " + contact_type;
	}

	@Override
	public boolean HasNoCode() {
		return true;
	}
}