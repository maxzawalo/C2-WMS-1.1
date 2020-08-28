package maxzawalo.c2.free.data.json.adapter;

import maxzawalo.c2.free.bo.LotOfProduct;
import maxzawalo.c2.free.data.json.CatalogueAdapter;

public class LotOfProductAdapter extends CatalogueAdapter<LotOfProduct> {
	public LotOfProductAdapter() {
		// replaces.add(new ReplacedField("НаименованиеПолное", "full_name"));
	}

	@Override
	protected boolean IsSkipField(String fieldName) {
		return (super.IsSkipField(fieldName) || fieldName.equals(LotOfProduct.fields.DOC.replace("_id", ""))
				|| fieldName.equals(LotOfProduct.fields.PRICE_BO.replace("_id", "")));
	}
}