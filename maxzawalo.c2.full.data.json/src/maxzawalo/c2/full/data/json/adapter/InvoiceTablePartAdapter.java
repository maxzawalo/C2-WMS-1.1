package maxzawalo.c2.full.data.json.adapter;

import maxzawalo.c2.free.bo.document.invoice.Invoice;
import maxzawalo.c2.free.bo.document.invoice.InvoiceTablePart;
import maxzawalo.c2.free.data.json.StoreTPAdapter;

public class InvoiceTablePartAdapter {

	public static class Product extends StoreTPAdapter<InvoiceTablePart.Product, Invoice> {

	}

	public static class Service extends StoreTPAdapter<InvoiceTablePart.Service, Invoice> {

	}

	public static class Equipment extends StoreTPAdapter<InvoiceTablePart.Equipment, Invoice> {

	}
}