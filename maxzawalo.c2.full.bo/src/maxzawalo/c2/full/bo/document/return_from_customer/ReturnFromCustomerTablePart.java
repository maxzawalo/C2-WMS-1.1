package maxzawalo.c2.full.bo.document.return_from_customer;

import com.j256.ormlite.table.DatabaseTable;

import maxzawalo.c2.free.bo.store.StoreTP;

public class ReturnFromCustomerTablePart {
	@DatabaseTable(tableName = "returnfromcustomer_tp_product")
	public static class Product extends StoreTP<Product> {
	}

	@DatabaseTable(tableName = "returnfromcustomer_tp_service")
	public static class Service extends StoreTP<Service> {
	}

	@DatabaseTable(tableName = "returnfromcustomer_tp_equipment")
	public static class Equipment extends StoreTP<Equipment> {
	}

}