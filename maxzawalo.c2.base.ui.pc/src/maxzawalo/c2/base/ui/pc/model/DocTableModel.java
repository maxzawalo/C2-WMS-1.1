package maxzawalo.c2.base.ui.pc.model;

import javax.swing.JLabel;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.ui.pc.renderer.DateCellRenderer;
import maxzawalo.c2.base.ui.pc.renderer.DocStateRenderer;
import maxzawalo.c2.base.ui.pc.renderer.LockStateRenderer;

public class DocTableModel<T> extends BOTableModel<T> {

	public DocTableModel() {
		ColumnSettings column = AddVisibleColumns();
		column.name = BO.fields.LOCKED_BY;
		column.caption = " ";
		column.renderer = new LockStateRenderer();
		column.horizontalAlignment = JLabel.CENTER;
		column.to_string_js = "locked_by_render";

		column = AddVisibleColumns();
		column.name = BO.fields.DOC_STATE;
		column.renderer = new DocStateRenderer();
		column.horizontalAlignment = JLabel.CENTER;
		column.to_string_js = "doc_state_render";

		column = AddVisibleColumns();
		column.name = DocumentBO.fields.COMMENT;

		column = AddVisibleColumns();
		column.name = DocumentBO.fields.DOC_DATE;
		column.caption = "Дата";
		column.renderer = new DateCellRenderer();
		column.horizontalAlignment = JLabel.LEFT;
		column.to_string_js = "date";
	}
}