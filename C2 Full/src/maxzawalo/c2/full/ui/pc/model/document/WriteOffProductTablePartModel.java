package maxzawalo.c2.full.ui.pc.model.document;

import javax.swing.JLabel;

import maxzawalo.c2.base.ui.pc.model.ColumnSettings;
import maxzawalo.c2.free.bo.store.StoreTP;
import maxzawalo.c2.free.ui.pc.model.document.StoreTPModel;
import maxzawalo.c2.full.bo.document.write_off_product.WriteOffProductTablePart;

public class WriteOffProductTablePartModel extends StoreTPModel<WriteOffProductTablePart.Product> {

	public WriteOffProductTablePartModel() {
		ColumnSettings column = AddVisibleColumns();
		column.name = StoreTP.fields.PRODUCT.replace("_id", "");
		column.horizontalAlignment = JLabel.LEFT;

		column = AddVisibleColumns();
		column.format = "0.000";
		column.name = StoreTP.fields.COUNT;

		AddPriceDiscountOffColumn();

		column = AddVisibleColumns();
		column.name = StoreTP.fields.PRICE;

		column = AddVisibleColumns();
		column.name = StoreTP.fields.SUM;

		column = AddVisibleColumns();
		column.name = StoreTP.fields.RATE_VAT;

		column = AddVisibleColumns();
		column.name = StoreTP.fields.SUM_VAT;

		AddDiscountColumn();

		column = AddVisibleColumns();
		column.name = StoreTP.fields.TOTAL;

		setColumnCaptions();
	}
}