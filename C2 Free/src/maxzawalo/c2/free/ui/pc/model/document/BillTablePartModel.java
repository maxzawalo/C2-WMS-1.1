package maxzawalo.c2.free.ui.pc.model.document;

import maxzawalo.c2.base.ui.pc.model.ColumnSettings;
import maxzawalo.c2.free.bo.document.bill.BillTablePart;

public class BillTablePartModel extends StoreTPModel<BillTablePart.Product> {

	public BillTablePartModel() {
		ColumnSettings column = AddProductColumn();

		column = AddCountColumn();
		column.format = "0.000";

		AddPriceDiscountOffColumn();

		column = AddPriceColumn();

		column = AddSumColumn();

		column = AddRateVatColumn();

		column = AddSumVatColumn();

		AddDiscountColumn();

		column = AddTotalColumn();

		setColumnCaptions();
	}
}