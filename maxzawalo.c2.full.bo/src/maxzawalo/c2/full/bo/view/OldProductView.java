package maxzawalo.c2.full.bo.view;

import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.free.bo.Product;

public class OldProductView {

	@BoField(caption = "Дата прихода")
	@DatabaseField
	public String date;

	// @DatabaseField(index = true, dataType = DataType.DATE_LONG, columnName =
	// "doc_date")
	@DatabaseField(index = true, columnName = "doc_date")
	public Long doc_date;

	@BoField(caption = "Номенклатура id")
	@DatabaseField
	public String product_id = "";

	@BoField(caption = "Номенклатура код")
	@DatabaseField
	public String product_code = "";

	@BoField(caption = "Наименование")
	@DatabaseField
	public String product_name = "";

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, columnName = "product_id")
	public Product product;

	@BoField(caption = "Остаток")
	@DatabaseField
	public double count;

	@BoField(caption = "Ед.изм.")
	@DatabaseField
	public String product_units = "";

	@BoField(caption = "Дней лежания")
	@DatabaseField
	public int days;

	@BoField(caption = "Есть картинки")
	@DatabaseField
	public String has_image = "";
}