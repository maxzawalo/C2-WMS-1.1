package maxzawalo.c2.full.data.json.adapter;

import maxzawalo.c2.free.data.json.CatalogueAdapter;
import maxzawalo.c2.full.bo.TradeAddition;

public class TradeAdditionAdapter extends CatalogueAdapter<TradeAddition> {
	public TradeAdditionAdapter() {
		replaces.add(new ReplacedField("СуммаОт", TradeAddition.fields.FROM_SUM));
		replaces.add(new ReplacedField("СуммаДо", TradeAddition.fields.TO_SUM));
		replaces.add(new ReplacedField("Процент", TradeAddition.fields.PERCENT));
		replaces.add(new ReplacedField("ПроцентДоставка", TradeAddition.fields.PERCENT_DELIVERY));
	}
}