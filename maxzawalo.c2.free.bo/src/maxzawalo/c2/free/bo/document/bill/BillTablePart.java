package maxzawalo.c2.free.bo.document.bill;

import com.j256.ormlite.table.DatabaseTable;

import maxzawalo.c2.free.bo.store.StoreTP;

public class BillTablePart {

//	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = false)
//	public LotOfProduct lotOfProduct;

	@DatabaseTable(tableName = "bill_tp_product")
	public static class Product extends StoreTP<Product> {
	}

	@DatabaseTable(tableName = "bill_tp_service")
	public static class Service extends StoreTP<Service> {
	}

	@DatabaseTable(tableName = "bill_tp_equipment")
	public static class Equipment extends StoreTP<Equipment> {
	}
}