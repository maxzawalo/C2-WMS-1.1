package maxzawalo.c2.free.bo;

import java.util.Date;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.bo.CatalogueBO;

@BoField(caption = "Валюта", type1C = "Справочники.Валюты")
public class Currency extends CatalogueBO<Currency> {
	// @DatabaseField
	public double rate = 1;

	// @DatabaseField
	public double scale = 1;

	// @DatabaseField
	public Date date;

	@Override
	public String toString() {
		return name;
	}
}