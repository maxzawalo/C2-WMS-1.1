package maxzawalo.c2.full.bo.view;

import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.annotation.BoField;

public class RemainingStockView {

	@BoField(caption = "Код")
	@DatabaseField
	public String code;

	@BoField(caption = "Наименование")
	@DatabaseField
	public String name;

	@BoField(caption = "Количество")
	@DatabaseField
	public double count;
}