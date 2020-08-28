package maxzawalo.c2.free.bo;

import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.CatalogueBO;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.free.bo.document.invoice.Invoice;

public class Price extends CatalogueBO<Price> {

	@DatabaseField(columnName = "price")
	public double price = 0;

	@DatabaseField(columnName = "total")
	public double total = 0;

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true)
	public Product product;

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true)
	public Invoice invoice;

	//TODO: проработать связку
	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = false)
	public BO barcode;

	@DatabaseField(defaultValue = PriceState.Новый + "")
	public int price_state = PriceState.Новый;

	public Price() {
	}

	@Override
	public String toString() {
		// TODO: product name
		// return "(" + ((this.barcode != null) ? this.barcode.getCode() :
		// "___") + ")"
		// + ((this.product != null) ? this.product.name : "Товар") + "\t\n" +
		// price + "р. " + total + "р.";
		return ((this.product != null) ? this.product.name : "") + "|" + price + "|" + total + "";
	}

	@Override
	public void Check() {
		super.Check();

		if (total == 0) {
			total = price;
			// TODO:var NDS
			price = Format.defaultRound(total * 100 / (100 + Settings.defaultVat));
		}
	}
}