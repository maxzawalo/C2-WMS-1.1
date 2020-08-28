package maxzawalo.c2.free.data.json.adapter;

import maxzawalo.c2.free.bo.Product;
import maxzawalo.c2.free.data.json.CatalogueAdapter;

public class ProductAdapter extends CatalogueAdapter<Product> {
	public ProductAdapter() {
		replaces.add(new ReplacedField("Наценка", Product.fields.ADDITION));
		replaces.add(new ReplacedField("ЕдиницаИзмеренияУИ", Product.fields.UNITS));
	}

	// @Override
	// protected void Serialize(JsonWriter writer, BO bo) {
	// super.Serialize(writer, bo);
	// Product p = (Product) bo;
	// try {
	// writer.name(Product.fields.UNITS.replace("_id", ""));
	// gson.toJson(gson.toJsonTree(p.units), writer);
	// } catch (Exception e) {
	// log.ERROR("Serialize", e);
	// }
	// }

	// @Override
	// protected void Deserialize(JsonReader reader, String fieldname) throws
	// IOException {
	// if (fieldname.equals(Product.fields.UNITS.replace("_id", ""))) {
	// obj.units = ReadBONullable(reader, Units.class);
	// } else
	// super.Deserialize(reader, fieldname);
	// }
}