package maxzawalo.c2.full.data.json.adapter;

import maxzawalo.c2.free.data.json.StoreTPAdapter;
import maxzawalo.c2.full.bo.document.cashvoucher.CashVoucher;
import maxzawalo.c2.full.bo.document.cashvoucher.CashVoucherTablePart;

public class CashVoucherTablePartAdapter {
	public static class Product extends StoreTPAdapter<CashVoucherTablePart.Product, CashVoucher> {
	}

	public static class Service extends StoreTPAdapter<CashVoucherTablePart.Service, CashVoucher> {
	}

	public static class Equipment extends StoreTPAdapter<CashVoucherTablePart.Equipment, CashVoucher> {
	}
}