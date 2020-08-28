package maxzawalo.c2.full.ui.pc.model.document;

import maxzawalo.c2.free.ui.pc.model.document.StoreTPModel;
import maxzawalo.c2.full.bo.document.cashvoucher.CashVoucherTablePart;

public class CashVoucherTablePartModel extends StoreTPModel<CashVoucherTablePart.Product> {

	public CashVoucherTablePartModel() {
		AddProductColumn();
		AddCountColumn();
		AddPriceDiscountOffColumn();
		AddPriceColumn();
		AddSumColumn();
		AddRateVatColumn();
		AddSumVatColumn();
		AddDiscountColumn();
		AddTotalColumn();

		setColumnCaptions();
	}
}