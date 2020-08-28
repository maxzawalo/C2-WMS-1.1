package maxzawalo.c2.full.bo.document.order;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import maxzawalo.c2.free.bo.LotOfProduct;
import maxzawalo.c2.free.bo.store.StoreTP;

public class OrderTablePart {

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = false)
	public LotOfProduct lotOfProduct;

	@DatabaseTable(tableName = "order_tp_product")
	public static class Product extends StoreTP<Product> {
	}

	@DatabaseTable(tableName = "order_tp_service")
	public static class Service extends StoreTP<Service> {
	}

	@DatabaseTable(tableName = "order_tp_equipment")
	public static class Equipment extends StoreTP<Equipment> {
	}
}