package maxzawalo.c2.full.bo.view;

import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.bo.Product;

public class TransactionView {

	@BoField(caption = "Дата док-та")
	@DatabaseField
	public String date;

	// @DatabaseField(index = true, dataType = DataType.DATE_LONG, columnName =
	// "doc_date")
	@DatabaseField(index = true, columnName = "doc_date")
	public Long doc_date;

//	@BoField(caption = "Код док-та")
	// @DatabaseField
	public String doc_code;

	@BoField(caption = "Тип док-та")
	@DatabaseField
	public String reg_type;

//	@BoField(caption = "Id док-та")
	@DatabaseField
	public String reg_id;

	@BoField(caption = "Контрагент")
	@DatabaseField
	public String contr = "";

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, columnName = "contractor_id")
	public Contractor contractor;

	@BoField(caption = "Номенклатура")
	@DatabaseField
	public String product_name = "";

	@BoField(caption = "Номенклатура код")
	@DatabaseField
	public String product_code = "";

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, columnName = "product_id")
	public Product product;

	@BoField(caption = "Кол-во (в проводках)")
	@DatabaseField
	public double reg_count;

	@BoField(caption = "Себестоимость(в партии)")
	@DatabaseField
	public double lot_cost_price;

	@BoField(caption = "Цена(в партии)")
	@DatabaseField
	public double lot_price;

	@BoField(caption = "Цена(в проводках)")
	@DatabaseField
	public double reg_price;

	@BoField(caption = "Отличается цена")
	@DatabaseField
	public String is_price_diff;
}