package maxzawalo.c2.full.ui.pc.model.document;

import javax.swing.JLabel;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.ui.pc.model.BOTableModel;
import maxzawalo.c2.base.ui.pc.model.ColumnSettings;
import maxzawalo.c2.base.ui.pc.renderer.DateCellRenderer;
import maxzawalo.c2.base.ui.pc.renderer.DocStateRenderer;
import maxzawalo.c2.base.ui.pc.renderer.LockStateRenderer;
import maxzawalo.c2.free.bo.store.StoreDocBO;
import maxzawalo.c2.full.bo.document.write_off_product.WriteOffProduct;

public class WriteOffProductTableModel extends BOTableModel<WriteOffProduct> {

	public WriteOffProductTableModel() {
		ColumnSettings column = AddVisibleColumns();
		column.name = BO.fields.LOCKED_BY.replace("_id", "");
		column.caption = " ";
		column.renderer = new LockStateRenderer();
		column.horizontalAlignment = JLabel.CENTER;

		column = AddVisibleColumns();
		column.name = BO.fields.DOC_STATE;
		column.renderer = new DocStateRenderer();
		column.horizontalAlignment = JLabel.LEFT;

		column = AddVisibleColumns();
		column.name = DocumentBO.fields.DOC_DATE;
		column.renderer = new DateCellRenderer();
		column.horizontalAlignment = JLabel.LEFT;

		column = AddVisibleColumns();
		column.caption = "Сумма";
		column.name = StoreDocBO.fields.ShowTotalSum;

		column = AddVisibleColumns();
		column.caption = "НДС";
		column.name = StoreDocBO.fields.ShowTotalVat;

		// column = AddVisibleColumns();
		// column.name = "doc_currency";

		column = AddVisibleColumns();
		column.name = BO.fields.CODE;

		column = AddVisibleColumns();
		column.name = BO.fields.ID;

		setColumnCaptions();
	}
}