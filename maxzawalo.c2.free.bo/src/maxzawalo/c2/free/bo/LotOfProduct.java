package maxzawalo.c2.free.bo;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.bo.CatalogueBO;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.free.bo.document.invoice.Invoice;
import maxzawalo.c2.free.bo.store.StoreDocBO;

//TODO: если в поиске 0 - сообщение о фильтрации по группе - повысить иерархию
@BoField(caption = "Партия", type1C = "Документы.Партия")
public class LotOfProduct extends CatalogueBO<LotOfProduct> {
	public static final String InMemoryGroupTransactionKey = "LotOfProduct.InMemoryGroupTransaction";
	
	public static class fields {
		public static final String COST_PRICE = "cost_price";
		public static final String PRICE = "price";
		public static final String COUNT = "count";
		public static final String RESERVE = "reserve";
		public static final String PRODUCT = "product_id";
		public static final String GROUP = "group";
		public static final String UNITS = "units";
		public static final String LOT = "lot";
		public static final String PRICE_WITH_VAT = "price_with_vat";
		public static final String CALC_PRICE = "calc_price";
		public static final String ADD = "add";
		public static final String FROM_LOT_ID = "from_lot_id";
		public static final String DOC = "doc_id";
		public static final String PRICE_BO = "price_bo_id";
		public static final String DOC_TYPE = "doc_type";
	}

	// @SerializedName("contractor_id")
	@BoField(caption = "Номенклатура")
	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, columnName = LotOfProduct.fields.PRODUCT)
	public Product product;

	@BoField(caption = "Приходный документ")
	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = false, columnName = LotOfProduct.fields.DOC)
	public StoreDocBO doc;

	@DatabaseField(index = true, columnName = LotOfProduct.fields.DOC_TYPE)
	public int doc_type;

	@BoField(caption = "Ценник")
	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, columnName = LotOfProduct.fields.PRICE_BO)
	public Price price_bo;

	@BoField(caption = "Себестоимость")
	@DatabaseField(columnName = LotOfProduct.fields.COST_PRICE)
	public double cost_price = 0;

	@BoField(caption = "Цена")
	@DatabaseField(columnName = LotOfProduct.fields.PRICE)
	public double price = 0;

	@BoField(caption = "Количество")
	@DatabaseField(columnName = LotOfProduct.fields.COUNT)
	public double count = 0;

	@BoField(caption = "Резерв")
	@DatabaseField(columnName = LotOfProduct.fields.RESERVE)
	public boolean reserve = false;
//	
//
//	/**
//	 * Для сортировки FIFO итп при агрегации баланса
//	 */
//	public Date doc_date;
	

	public LotOfProduct() {
	}

	@Override
	public String toString() {
		// return "" + product.id + "|" + invoice + "|" + price + "|" + count;
		return Format.Show("dd.MM.yy", doc.DocDate) + "|" + id;
	}

	public String print() {
		// return "" + product.id + "|" + invoice + "|" + price + "|" + count;
		return Format.Show("dd.MM.yy", doc.DocDate) + "|" + Leading2Str(Format.Show(count, 3), " ", 8) + "|"
				+ Leading2Str(Format.Show(cost_price), " ", 8) + "|" + Leading2Str(Format.Show(price), " ", 8);
	}

	String Leading2Str(Object obj, String symbol, int max) {
		String data = "" + obj;
		int length = data.length();
		for (int i = 0; i < max - length; i++)
			data = symbol + data;

		return data;
	}

	public boolean getDelivery() {
		boolean delivery = true;
		if (doc instanceof Invoice)
			delivery = ((Invoice) doc).delivery;
		return delivery;
	}

	@Override
	public Object getCalcField(String name) {
		// TODO:
		switch (name) {
		case LotOfProduct.fields.UNITS:
			return (product.units == null ? "" : product.units.name);
		case LotOfProduct.fields.PRICE_WITH_VAT:
			return Format.defaultRound(price * (100 + Settings.defaultVat) / 100);
		// case LotOfProduct.fields.CALC_PRICE:
		// return TradeAddition.CalcAddition(product, cost_price,
		// getDelivery());
		case LotOfProduct.fields.ADD:
			return Format.roundDouble(100 * (price / cost_price - 1), 0);
		// return TradeAddition.getAddition(product, cost_price,
		// getDelivery());
		case LotOfProduct.fields.GROUP:
			String value = (String) calcFields.get(name);
			// if (value == null) {
			// value = "";
			// Product parent1 = new Product().getParent(product);
			// if (parent1 != null) {
			// Product parent2 = new Product().getParent(parent1);
			// value = parent1.name;
			// if (parent2 != null)
			// value = parent2.name + "|" + value;
			// }
			// calcFields.put(name, value);
			// }

			return value;
		case LotOfProduct.fields.LOT:
			if (doc == null)
				return "";
			return doc.asLot();
		default:
			return super.getCalcField(name);
		}
	}

	@Override
	public Class<?> ReplaceType() {
		return Product.class;
	}

	@Override
	public boolean HasNoCode() {
		return true;
	}
}