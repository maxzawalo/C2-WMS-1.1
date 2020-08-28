package maxzawalo.c2.free.bo.registry;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.registry.Registry;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.bo.LotOfProduct;
import maxzawalo.c2.free.bo.Product;
import maxzawalo.c2.free.bo.Settings;
import maxzawalo.c2.free.bo.Store;

@DatabaseTable(tableName = "registry_product")
public class RegistryProduct extends Registry<RegistryProduct> {
	public static class fields {
		public static final String STORE = "store_id";
		public static final String CONTRACTOR = "contractor_id";
		public static final String RESERVE = "reserve";
		public static final String PRODUCT = "product_id";
		public static final String LOT = "lotofproduct_id";
	}

	@BoField(caption = "Номенклатура")
	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = false, canBeNull = false)
	public Product product;

	@BoField(caption = "Партия")
	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = false, canBeNull = false)
	public LotOfProduct lotOfProduct;

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = false, columnName = RegistryProduct.fields.STORE)
	public Store store = Settings.mainStore;


	@BoField(caption = "Себестоимость")
	@DatabaseField
	public double cost_price = 0;
	
	@BoField(caption = "Цена")
	@DatabaseField
	public double price = 0;// ?? фиксируем?

	@BoField(caption = "Количество")
	@DatabaseField
	public double count = 0;

	@DatabaseField
	public boolean reserve = false;

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = false, columnName = RegistryProduct.fields.CONTRACTOR)
	public Contractor contractor;

	@Override
	public Object getCalcField(String name) {
		switch (name) {
		case "units":
			// Услуги без ед.изм.
			return (product.units == null ? "" : product.units.name);
		case "cost_price":
			return lotOfProduct.cost_price;
		// TODO:
		// case "calc_price":
		// return TradeAddition.CalcAddition(product, lotOfProduct.cost_price,
		// lotOfProduct.getDelivery());
		// case "add":
		// return TradeAddition.getAddition(product, lotOfProduct.cost_price,
		// lotOfProduct.getDelivery());
		case "doc_add":
			if (lotOfProduct.cost_price == 0)
				return 0;
			return Format.defaultRound((price / lotOfProduct.cost_price - 1) * 100);
		case "price_bo":
			return lotOfProduct.price_bo;
		default:
			return super.getCalcField(name);
		}
	}

	@Override
	public String toString() {
		return product + "|" + count + "|" + price;
	}

	public double getCount() {
		return count;
	}

	public String dimensionGroupingByKey() {
		if (this.store == null)// || this.product == null || this.lotOfProduct
								// == null)
		{
			System.out.print("this.store == null");
		}

		return ((this.store == null ? "" : this.store.id) + "-" + this.product.id + "-" + this.lotOfProduct.id);
	}
}