package maxzawalo.c2.full.bo.document.write_off_product;

import com.j256.ormlite.table.DatabaseTable;

import maxzawalo.c2.free.bo.store.StoreTP;

public class WriteOffProductTablePart {

	@DatabaseTable(tableName = "writeoffproduct_tp_product")
	public static class Product extends StoreTP<Product> {
	}

	@DatabaseTable(tableName = "writeoffproduct_tp_service")
	public static class Service extends StoreTP<Service> {
	}

	@DatabaseTable(tableName = "writeoffproduct_tp_equipment")
	public static class Equipment extends StoreTP<Equipment> {
	}
}