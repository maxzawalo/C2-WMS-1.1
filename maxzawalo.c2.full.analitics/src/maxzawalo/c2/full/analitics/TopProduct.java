package maxzawalo.c2.full.analitics;

import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.annotation.BoField;

public class TopProduct extends View<TopProduct> {
	@BoField(caption = "Наименование")
	@DatabaseField
	public String name = "";

	@BoField(caption = "Кол-во")
	@DatabaseField
	public double count = 0;

	@BoField(caption = "Ед.Изм.")
	@DatabaseField
	public String units = "";
}