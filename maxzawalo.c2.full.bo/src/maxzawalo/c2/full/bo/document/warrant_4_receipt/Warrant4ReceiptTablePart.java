package maxzawalo.c2.full.bo.document.warrant_4_receipt;

import com.j256.ormlite.table.DatabaseTable;

import maxzawalo.c2.free.bo.store.StoreTP;

public class Warrant4ReceiptTablePart {
	@DatabaseTable(tableName = "warrant4receipt_tp_product")
	public static class Product extends StoreTP<Product> {
	}

	@DatabaseTable(tableName = "warrant4receipt_tp_service")
	public static class Service extends StoreTP<Service> {
	}

	@DatabaseTable(tableName = "warrant4receipt_tp_equipment")
	public static class Equipment extends StoreTP<Equipment> {
	}
}