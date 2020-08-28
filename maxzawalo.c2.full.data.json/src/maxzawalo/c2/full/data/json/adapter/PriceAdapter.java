package maxzawalo.c2.full.data.json.adapter;

import maxzawalo.c2.free.bo.Price;
import maxzawalo.c2.free.data.json.CatalogueAdapter;

public class PriceAdapter extends CatalogueAdapter<Price> {

	public PriceAdapter() {
		replaces.add(new ReplacedField("НоменклатураУИ", "product"));
		replaces.add(new ReplacedField("Цена", "price"));
		replaces.add(new ReplacedField("ЦенаСНДС", "total"));

		replaces.add(new ReplacedField("НакладнаяУИ", "invoice"));
	}

//	@Override
//	protected void Deserialize(JsonReader reader, String fieldname) throws IOException {
//		if ("invoice".equals(fieldname)) {
//			JsonToken token = reader.peek();
//			String uuid = reader.nextString();
//
//			obj.invoice = new Invoice();
//			// obj.invoice.ForSync();
//			obj.invoice = obj.invoice.GetByUUID(uuid);
//		} else {
//			super.Deserialize(reader, fieldname);
//		}
//	}
}