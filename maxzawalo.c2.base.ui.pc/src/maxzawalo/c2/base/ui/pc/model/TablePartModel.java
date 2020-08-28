package maxzawalo.c2.base.ui.pc.model;

import java.awt.Rectangle;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.TablePartItem;
import maxzawalo.c2.base.ui.pc.controls.PopupPanelTable;

public class TablePartModel<Item> extends BOEditTableModel<Item> {

	public TablePartModel() {
		ColumnSettings column = AddVisibleColumns();
		column.name = TablePartItem.fields.POS;
		// column.horizontalAlignment = JLabel.LEFT;
	}

	protected boolean IsVisColumn(int col, String colName) {
		colName = colName.replace("_id", "");
		return visibleColumns.get(col).name.equals(colName);
	}

	protected Class GetVisColumnClass(int col, String colName) {
		// colName = colName.replace("_id", "");
		// visibleColumns.get(col).name.equals(colName);
		return BO.class;
	}

	protected void EditBoCell(int row, int col, String colName) {
		Rectangle r = table.getCellRect(row, col, true);
		((PopupPanelTable) table).ShowMenu(GetVisColumnClass(col, colName), (BO) getValueAt(row, col), r.width, r.y,
				row, col);
	}
}