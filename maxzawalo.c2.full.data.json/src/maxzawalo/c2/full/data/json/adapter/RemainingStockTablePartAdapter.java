package maxzawalo.c2.full.data.json.adapter;

import maxzawalo.c2.free.data.json.StoreTPAdapter;
import maxzawalo.c2.full.bo.document.remaining_stock.RemainingStock;
import maxzawalo.c2.full.bo.document.remaining_stock.RemainingStockTablePart;

public class RemainingStockTablePartAdapter {

	public static class Product extends StoreTPAdapter<RemainingStockTablePart.Product, RemainingStock> {
	}

	public static class Service extends StoreTPAdapter<RemainingStockTablePart.Service, RemainingStock> {
	}

	public static class Equipment extends StoreTPAdapter<RemainingStockTablePart.Equipment, RemainingStock> {
	}
}