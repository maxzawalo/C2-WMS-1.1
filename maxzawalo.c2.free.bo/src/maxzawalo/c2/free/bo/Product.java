package maxzawalo.c2.free.bo;

import javax.xml.bind.annotation.XmlType;

import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.bo.CatalogueBO;
import maxzawalo.c2.base.bo.User;

/**
 * Created by Max on 10.03.2017.
 */

@BoField(caption = "Номенклатура", type1C = "Справочники.Номенклатура")
@XmlType(name = "product1")
public class Product extends CatalogueBO<Product> {
	public static class fields {
		// public static final String PRICE = "price";
		public static final String COUNT = "count";
		public static final String UNITS = "units_id";
		public static final String ADDITION = "addition";
		public static final String WEB_CAT = "web_cat";

		//calc
		public static final String TURNOVER_COUNT = "TurnoverCount";
	}

	// @DatabaseField(columnName = Product.fields.PRICE)
	// public double price = 0;

	// @DatabaseField(columnName = Product.fields.COUNT)
	// public double count = 1;

	@BoField(caption = "Единица измерения", fieldName1C = "ЕдиницаИзмерения")
	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, columnName = Product.fields.UNITS)
	public Units units = Settings.mainUnits;

	@BoField(caption = "Наценка", fieldName1C = "Наценка")
	@DatabaseField(columnName = Product.fields.ADDITION)
	public double addition = 0;

	@BoField(caption = "web_cat")
	@DatabaseField(width = 250, columnName = fields.WEB_CAT)
	public String web_cat = "";

	public Product() {
	}

	@Override
	public String toString() {
		// return super.toString() + " " + ((units != null) ? units.name : "");
		if (!User.current.isSimple()) {
			return name + ((addition == 0) ? "" : " - " + addition + " %");
		}
		return name;
	}
}