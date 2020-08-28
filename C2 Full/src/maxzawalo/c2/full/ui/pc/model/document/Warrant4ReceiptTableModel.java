package maxzawalo.c2.full.ui.pc.model.document;

import javax.swing.JLabel;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.ui.pc.model.BOTableModel;
import maxzawalo.c2.base.ui.pc.model.ColumnSettings;
import maxzawalo.c2.base.ui.pc.renderer.DateCellRenderer;
import maxzawalo.c2.base.ui.pc.renderer.DocStateRenderer;
import maxzawalo.c2.base.ui.pc.renderer.LockStateRenderer;
import maxzawalo.c2.full.bo.document.warrant_4_receipt.Warrant4Receipt;

public class Warrant4ReceiptTableModel extends BOTableModel<Warrant4Receipt> {

	public Warrant4ReceiptTableModel() {
		ColumnSettings column = AddVisibleColumns();
		column.name = BO.fields.LOCKED_BY.replace("_id", "");
		column.caption = " ";
		column.renderer = new LockStateRenderer();
		column.horizontalAlignment = JLabel.CENTER;

		column = AddVisibleColumns();
		column.name = BO.fields.DOC_STATE;
		column.renderer = new DocStateRenderer();
		column.horizontalAlignment = JLabel.CENTER;

		column = AddVisibleColumns();
		column.name = DocumentBO.fields.COMMENT;

		column = AddVisibleColumns();
		column.name = DocumentBO.fields.DOC_DATE;
		column.caption = "Дата";
		column.renderer = new DateCellRenderer();
		column.horizontalAlignment = JLabel.LEFT;

		column = AddVisibleColumns();
		column.name = "contractor";
		column.caption = "Контрагент";
		column.horizontalAlignment = JLabel.LEFT;

		column = AddVisibleColumns();
		column.caption = "Дата действия";
		column.name = "end_date";
		column.renderer = new DateCellRenderer();

		column = AddVisibleColumns();
		column.caption = "Подотчетное лицо";
		column.name = "coworker";

		column = AddVisibleColumns();
		column.name = BO.fields.CODE;

		column = AddVisibleColumns();
		column.name = BO.fields.ID;

		setColumnCaptions();
	}
}