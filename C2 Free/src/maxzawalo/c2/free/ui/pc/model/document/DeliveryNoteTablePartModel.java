package maxzawalo.c2.free.ui.pc.model.document;

import maxzawalo.c2.base.ui.pc.model.ColumnSettings;
import maxzawalo.c2.free.bo.document.deliverynote.DeliveryNoteTablePart;

public class DeliveryNoteTablePartModel extends StoreTPModel<DeliveryNoteTablePart.Product> {
	
	public DeliveryNoteTablePartModel() {
		ColumnSettings column = AddProductColumn();

		column = AddCountColumn();

		column = AddUnitsColumn();

		column = AddPriceDiscountOffColumn();
		
		column = AddPriceColumn();

		column = AddSumColumn();

		column = AddRateVatColumn();

		column = AddSumVatColumn();

		column = AddDiscountColumn();

		column = AddTotalColumn();

		setColumnCaptions();
	}
}