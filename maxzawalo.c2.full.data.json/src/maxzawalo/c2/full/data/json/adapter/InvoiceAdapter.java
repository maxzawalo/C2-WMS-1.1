package maxzawalo.c2.full.data.json.adapter;

import maxzawalo.c2.free.bo.document.invoice.Invoice;
import maxzawalo.c2.free.data.json.StoreDocAdapter;

public class InvoiceAdapter extends StoreDocAdapter<Invoice> {

	public InvoiceAdapter() {
		replaces.add(new ReplacedField("Доставка", "delivery"));
	}
}