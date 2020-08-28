package maxzawalo.c2.free.bo;

import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.bo.CatalogueBO;

@BoField(caption = "Склад", type1C = "Справочники.Склады")
public class Store extends CatalogueBO<Store> {
	public static class fields {
		public static final String ADDRESS = "address";
	}

	// TODO: тип склада, тип цен, МОЛ
	@BoField(caption = "Адрес")
	@DatabaseField(index = true, width = 100, columnName = fields.ADDRESS)
	public String address = "";

	@Override
	public String toString() {
		return name;
	}
}