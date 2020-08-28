package maxzawalo.c2.full.bo.document.cashvoucher;

import com.j256.ormlite.table.DatabaseTable;

import maxzawalo.c2.free.bo.store.StoreTP;

public class CashVoucherTablePart {

	@DatabaseTable(tableName = "cashvoucher_tp_product")
	public static class Product extends StoreTP<Product> {
		public Product() {
			sum_contains_vat = true;
			nalichka = true;
		}
	}

	@DatabaseTable(tableName = "cashvoucher_tp_service")
	public static class Service extends StoreTP<Service> {
		public Service() {
			sum_contains_vat = true;
			nalichka = true;
		}
	}

	@DatabaseTable(tableName = "cashvoucher_tp_equipment")
	public static class Equipment extends StoreTP<Equipment> {
		public Equipment() {
			sum_contains_vat = true;
			nalichka = true;
		}
	}
}