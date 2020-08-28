package maxzawalo.c2.free.data.json.adapter;

import maxzawalo.c2.free.bo.document.bill.Bill;
import maxzawalo.c2.free.bo.document.bill.BillTablePart;
import maxzawalo.c2.free.data.json.StoreTPAdapter;

public class BillTablePartAdapter {

	public static class Product extends StoreTPAdapter<BillTablePart.Product, Bill> {
	}

	public static class Service extends StoreTPAdapter<BillTablePart.Service, Bill> {
	}

	public static class Equipment extends StoreTPAdapter<BillTablePart.Equipment, Bill> {
	}
}