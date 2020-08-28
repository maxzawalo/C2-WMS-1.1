package maxzawalo.c2.free.bo.document.deliverynote;

import com.j256.ormlite.table.DatabaseTable;

import maxzawalo.c2.free.bo.store.StoreTP;

public class DeliveryNoteTablePart {

	@DatabaseTable(tableName = "deliverynote_tp_product")
	public static class Product extends StoreTP<Product> {

	}

	@DatabaseTable(tableName = "deliverynote_tp_service")
	public static class Service extends StoreTP<Service> {
	}

	@DatabaseTable(tableName = "deliverynote_tp_equipment")
	public static class Equipment extends StoreTP<Equipment> {
	}

}