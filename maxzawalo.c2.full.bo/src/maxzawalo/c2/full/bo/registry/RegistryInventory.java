package maxzawalo.c2.full.bo.registry;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.bo.registry.Registry;
import maxzawalo.c2.free.bo.Product;
import maxzawalo.c2.free.bo.Settings;
import maxzawalo.c2.free.bo.Store;

@DatabaseTable(tableName = "registry_inventory")
public class RegistryInventory extends Registry<RegistryInventory> {
	@BoField(caption = "Номенклатура")
	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = false, canBeNull = false)
	public Product product;

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = false)
	public Store store = Settings.mainStore;

	@BoField(caption = "Количество план")
	@DatabaseField
	public double count = 0;

	@BoField(caption = "Количество факт")
	@DatabaseField
	public double real_count = 0;

	// @Override
	// public Object getCalcField(String name) {
	// switch (name) {
	// case "units":
	// return product.units.name;
	// case "cost_price":
	// return lotOfProduct.cost_price;
	// case "calc_price":
	// return TradeAddition.CalcAddition(product, lotOfProduct.cost_price,
	// lotOfProduct.getDelivery());
	// case "add":
	// return TradeAddition.getAddition(product, lotOfProduct.cost_price,
	// lotOfProduct.getDelivery());
	// case "doc_add":
	// return Format.defaultRound((price / lotOfProduct.cost_price - 1) * 100);
	// case "price_bo":
	// return lotOfProduct.price_bo;
	// default:
	// return super.getCalcField(name);
	// }
	// }
}