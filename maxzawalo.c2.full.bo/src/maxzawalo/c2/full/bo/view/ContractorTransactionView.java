package maxzawalo.c2.full.bo.view;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.free.bo.Product;
import maxzawalo.c2.free.bo.Units;

public class ContractorTransactionView {
	@BoField(caption = "Номенклатура")
	// @DatabaseField
	public String product_name = "";

	@BoField(caption = "Номенклатура код")
	// @DatabaseField
	public String product_code = "";

	// @DatabaseField(index = true, foreign = true, foreignAutoRefresh = true,
	// columnName = "product_id")
	public Product product;

	@BoField(caption = "Кол-во (в проводках)")
	// @DatabaseField
	public double reg_count;

	@BoField(caption = "Ед. изм.")
	// @DatabaseField
	public Units units;

	@BoField(caption = "Себестоимость средняя(в проводках)")
	// @DatabaseField
	public double cost_price;

	@BoField(caption = "Цена средняя(в проводках)")
	// @DatabaseField
	public double reg_price;
}