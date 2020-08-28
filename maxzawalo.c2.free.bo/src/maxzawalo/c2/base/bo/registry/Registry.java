package maxzawalo.c2.base.bo.registry;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.DocumentBO;

public class Registry<T> extends BO<T> {

	public static class fields {
		public static final String REG_TYPE = "reg_type";
		public static final String REG_ID = "reg_id";
		public static final String REG_DATE = "reg_date";
	}

	public int regRoundPlaces = 3;

	@DatabaseField(index = true)
	public int reg_type = 0;

	@DatabaseField(index = true)
	public int reg_id = 0;

	/**
	 * reg_date нужно для выборки баланса - партий в нужной последовательности если
	 * сменится дата док - Id партии останется прежней, а сча сортируем по нему
	 */
	@BoField(caption = "Дата")
	@DatabaseField(index = true, dataType = DataType.DATE_LONG, columnName = fields.REG_DATE)
	public Date reg_date = new Date();

	public void SetRegIdAndType(T item) {
		((Registry) item).reg_type = reg_type;
		((Registry) item).reg_id = reg_id;
		((Registry) item).reg_date = reg_date;
	}

	public void setRegistrator(DocumentBO doc) {
		reg_type = doc.reg_type;
		reg_id = doc.id;
		reg_date = doc.DocDate;
	}

	public String getRegistratorKey() {
		return reg_type + "_" + reg_id;
	}
}