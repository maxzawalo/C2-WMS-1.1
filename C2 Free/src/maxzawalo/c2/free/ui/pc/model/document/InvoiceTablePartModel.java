package maxzawalo.c2.free.ui.pc.model.document;

import maxzawalo.c2.base.ui.pc.model.ColumnSettings;
import maxzawalo.c2.free.bo.document.invoice.InvoiceTablePart;

public class InvoiceTablePartModel extends StoreTPModel<InvoiceTablePart.Product> {

	public InvoiceTablePartModel() {
		ColumnSettings column = AddProductColumn();

		column = AddCountColumn();

		column = AddPriceColumn();

		column = AddSumColumn();

		column = AddRateVatColumn();
		column.format = "0.000000";

		column = AddSumVatColumn();

		column = AddTotalColumn();

		setColumnCaptions();
	}
}