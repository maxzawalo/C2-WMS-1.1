package maxzawalo.c2.free.bo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.bo.SlaveCatalogueBO;

@BoField(caption = "Банковский счет", type1C = "Справочники.БанковскиеСчета")
@DatabaseTable(tableName = "bank_account")
public class BankAccount extends SlaveCatalogueBO<BankAccount, Contractor> {
	public static class fields {
		public static final String NUMBER = "number";
	}

	@BoField(caption = "Номер счета", fieldName1C = "НомерСчета")
	@DatabaseField(width = 34, columnName = fields.NUMBER)
	public String number = "";

	@Override
	public String toString() {
		return number;
	}
	// INSERT INTO `bank_account` VALUES
	// ('BY06BELB30121432460080226000',29,NULL,NULL,NULL,NULL,1,'00-000095',NULL,NULL,NULL,0,'7f9f0860-6156-11e7-b01f-38d5471ab5e6',0,0,0,'\'');
}