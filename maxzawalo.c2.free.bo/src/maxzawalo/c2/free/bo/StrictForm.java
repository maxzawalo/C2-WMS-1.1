package maxzawalo.c2.free.bo;

import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.bo.CatalogueBO;

public class StrictForm extends CatalogueBO<StrictForm> {

	public static class fields {
		public static final String REG_TYPE = "reg_type";
		public static final String REG_ID = "reg_id";
		public static final String FORM_NUMBER = "form_number";
		public static final String FORM_BATCH = "form_batch";
		public static final String FORM_TYPE_CODE = "form_type_code";
		public static final String FORM_TYPE_NAME = "form_type_name";
		public static final String WRITE_OFF_TYPE = "write_off_type";
	}

	@DatabaseField(index = true, columnName = StrictForm.fields.REG_TYPE)
	public int reg_type = 0;

	@DatabaseField(index = true, columnName = StrictForm.fields.REG_ID)
	public int reg_id = 0;

	// strict reporting forms
	// TODO: @Json(caption = "НомерБланка") - для адаптера
	@BoField(caption = "НомерБланка")
	@DatabaseField(index = true, width = 15, columnName = StrictForm.fields.FORM_NUMBER)
	public String form_number = "";

	@BoField(caption = "Cерия")
	@DatabaseField(index = true, width = 5, columnName = StrictForm.fields.FORM_BATCH)
	public String form_batch = "";

	@BoField(caption = "ТипБСОКод")
	@DatabaseField(index = true, width = 5, columnName = StrictForm.fields.FORM_TYPE_CODE)
	public String form_type_code = "";

	@BoField(caption = "ТипБСОНаименование")
	@DatabaseField(index = true, width = 10, columnName = StrictForm.fields.FORM_TYPE_NAME)
	public String form_type_name = "";

	@BoField(caption = "Тип списания")
	@DatabaseField(index = true, width = 15, columnName = StrictForm.fields.WRITE_OFF_TYPE)
	public String write_off_type = "";
 
	@Override
	public boolean HasNoCode() {
		return true;
	}
}