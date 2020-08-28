package maxzawalo.c2.free.bo;

import java.util.List;

import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.annotation.JsonField;
import maxzawalo.c2.base.bo.CatalogueBO;

@BoField(caption = "Контрагент", type1C = "Справочники.Контрагенты")
public class Contractor extends CatalogueBO<Contractor> {

	public static class fields {
		public static final String UNP = "unp";
		public static final String MAIN_CONTRACT = "main_contract_id";

		public static final String DISCOUNT = "discount";
		public static final String IS_RESIDENT = "is_resident";
		public static final String IS_INDIVIDUAL = "is_individual";

		public static final String LEGAL_ADDRESS = "legal_address";
		public static final String PHONE = "phone";
		public static final String FAX = "fax";
		public static final String CONTACT_INFO = "contact_info";
	}

	@BoField(caption = "УНП", fieldName1C = "ИНН")
	@DatabaseField(width = 10, columnName = Contractor.fields.UNP)
	public String unp = "";

	@BoField(fieldName1C = "ОсновнойДоговорКонтрагента", caption = "Основной договор")
	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, columnName = Contractor.fields.MAIN_CONTRACT)
	public Contract main_contract;

	@JsonField(columnName = Contractor.fields.LEGAL_ADDRESS)
	public String legal_address = "";

	@JsonField(columnName = Contractor.fields.PHONE)
	public String phone = "";

	@JsonField(columnName = Contractor.fields.FAX)
	public String fax = "";

	@DatabaseField(width = 100)
	public String main_bank_name = "";

	@DatabaseField(width = 28)
	public String main_bank_acccount = "";

	@BoField(caption = "Резидент РБ", fieldName1C = "РезидентРБ")
	@DatabaseField(columnName = Contractor.fields.IS_RESIDENT)
	public boolean is_resident = true;

	@DatabaseField(columnName = Contractor.fields.IS_INDIVIDUAL)
	public boolean is_individual = false;

	@BoField(caption = "Скидка, %")
	@DatabaseField(columnName = Contractor.fields.DISCOUNT)
	public double discount = 0;

	// @JsonField(columnName = Contractor.fields.CONTACT_INFO)
	public List<ContactInfo> contactInfo;

	public Contractor() {
		// this.name = "Контрагент " + System.currentTimeMillis();
	}

	public String getMainContract() {
		if (main_contract != null)
			return "" + main_contract;

		return "Основной договор";

	}

	@Override
	public String toString() {
		// return "(" + this.id + ")" + this.getCode() + " " + this.name + "\n"
		// + this.unp;
		return this.name;
	}

	// public String FullName() {
	// return this.name ;// + "(" + this.unp + ")";
	// }
}
