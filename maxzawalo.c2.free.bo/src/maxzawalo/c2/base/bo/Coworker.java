package maxzawalo.c2.base.bo;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.annotation.BoField;

public class Coworker extends CatalogueBO<Coworker> {
	public static class fields {
		public static final String POSITION = "position";
		public static final String PASSPORT_BATCH = "passport_batch";
		public static final String PASSPORT_NUMBER = "passport_number";
		public static final String PASSPORT_ISSUED_BY = "passport_issued_by";
		public static final String PASSPORT_ISSUED_DATE = "passport_issued_date";
	}

	@BoField(caption = "Должность")
	@DatabaseField(index = true, width = 50, columnName = Coworker.fields.POSITION)
	public String position;

	@BoField(caption = "Серия паспорта")
	@DatabaseField(index = true, width = 2, columnName = Coworker.fields.PASSPORT_BATCH)
	public String passport_batch;

	@BoField(caption = "Номер паспорта")
	@DatabaseField(index = true, width = 7, columnName = Coworker.fields.PASSPORT_NUMBER)
	public String passport_number;

	@BoField(caption = "Кем выдан паспорт")
	@DatabaseField(index = true, width = 100, columnName = Coworker.fields.PASSPORT_ISSUED_BY)
	public String passport_issued_by;

	@BoField(caption = "Дата выдачи паспорта")
	@DatabaseField(index = true, dataType = DataType.DATE_LONG, columnName = Coworker.fields.PASSPORT_ISSUED_DATE)
	public Date passport_issued_date = new Date();

	@Override
	public String toString() {
		return this.name;
	}

	public String toReport() {
		return position + ", " + toReportShort();
	}

	public String toReportShort() {
		if (name.trim().equals(""))
			return "";
		String[] fio = name.split(" ");
		return fio[0] + " " + fio[1].charAt(0) + ". " + fio[2].charAt(0) + ".";
	}

	public String toReportFullName() {
		return position + ", " + name;
	}
}