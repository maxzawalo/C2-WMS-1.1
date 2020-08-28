package maxzawalo.c2.full.bo.document.remaining_stock;

import com.j256.ormlite.table.DatabaseTable;

import maxzawalo.c2.free.bo.store.StoreTP;

public class RemainingStockTablePart {

	@DatabaseTable(tableName = "remaining_stock_tp_product")
	public static class Product extends StoreTP<Product> {
	}

	@DatabaseTable(tableName = "remaining_stock_tp_service")
	public static class Service extends StoreTP<Service> {
	}

	@DatabaseTable(tableName = "remaining_stock_tp_equipment")
	public static class Equipment extends StoreTP<Equipment> {
	}

}