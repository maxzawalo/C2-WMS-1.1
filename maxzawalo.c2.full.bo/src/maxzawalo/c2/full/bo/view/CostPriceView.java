package maxzawalo.c2.full.bo.view;

import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.annotation.BoField;

public class CostPriceView {

	@BoField(caption = "Дата док-та")
	@DatabaseField
	public String doc_date;

	@BoField(caption = "Код док-та")
	@DatabaseField
	public String doc_code;

	@BoField(caption = "Тип док-та")
	@DatabaseField
	public String doc_type;

	@BoField(caption = "Номенклатура")
	@DatabaseField
	public String product;

	@BoField(caption = "Кол-во")
	@DatabaseField
	public String count;

	@BoField(caption = "Себестоимость")
	@DatabaseField
	public String cost_price;

	@BoField(caption = "Цена")
	@DatabaseField
	public String price;

	@Override
	public String toString() {
		return doc_type + " " + doc_date + " " + doc_code;
	}
}