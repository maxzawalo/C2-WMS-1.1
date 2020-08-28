package maxzawalo.c2.full.ui.pc.model.document;

import maxzawalo.c2.free.ui.pc.model.document.StoreDocTableModel;
import maxzawalo.c2.full.bo.document.cashvoucher.CashVoucher;

public class CashVoucherTableModel extends StoreDocTableModel<CashVoucher> {

	public CashVoucherTableModel() {
		// ColumnSettings column = AddVisibleColumns();
		// column.name = BO.fields.LOCKED_BY.replace("_id", "");
		// column.caption = " ";
		// column.renderer = new LockStateRenderer();
		// column.horizontalAlignment = JLabel.CENTER;
		//
		// column = AddVisibleColumns();
		// column.name = BO.fields.DOC_STATE;
		// column.renderer = new DocStateRenderer();
		// column.horizontalAlignment = JLabel.LEFT;
		//
		// column = AddVisibleColumns();
		// column.name = DocumentBO.fields.DOC_DATE;
		// column.renderer = new DateCellRenderer();
		// column.horizontalAlignment = JLabel.LEFT;
		//
		// column = AddVisibleColumns();
		// column.caption = "Сумма";
		// column.name = StoreDocBO.fields.ShowTotalSum;
		//
		// column = AddVisibleColumns();
		// column.caption = "НДС";
		// column.name = StoreDocBO.fields.ShowTotalVat;
		//
		// // column = AddVisibleColumns();
		// // column.name = "doc_currency";
		//
		// column = AddVisibleColumns();
		// column.name = BO.fields.CODE;
		//
		// column = AddVisibleColumns();
		// column.name = BO.fields.ID;
		//
		// setColumnCaptions();
	}
}