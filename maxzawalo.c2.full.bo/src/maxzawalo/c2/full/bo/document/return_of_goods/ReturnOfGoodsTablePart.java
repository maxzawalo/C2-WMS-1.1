package maxzawalo.c2.full.bo.document.return_of_goods;

import com.j256.ormlite.table.DatabaseTable;

import maxzawalo.c2.free.bo.store.StoreTP;

public class ReturnOfGoodsTablePart {
	@DatabaseTable(tableName = "returnofgoods_tp_product")
	public static class Product extends StoreTP<Product> {
	}

	@DatabaseTable(tableName = "returnofgoods_tp_service")
	public static class Service extends StoreTP<Service> {
	}

	@DatabaseTable(tableName = "returnofgoods_tp_equipment")
	public static class Equipment extends StoreTP<Equipment> {
	}
}