package maxzawalo.c2.base.ui.pc.listener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import maxzawalo.c2.base.ui.pc.controls.TableWithState;
import maxzawalo.c2.base.utils.Logger;

public class SColumnListener extends MouseAdapter implements TableColumnModelListener {

	protected Logger log = Logger.getLogger(this.getClass().getSimpleName());

	private int oldIndex = -1;
	private int newIndex = -1;
	private boolean dragging = false;

	private boolean resizing = false;
	private int resizingColumn = -1;
	private int oldWidth = -1;

	TableWithState table;

	public SColumnListener(TableWithState table) {
		this.table = table;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// capture start of resize
		if (e.getSource() instanceof JTableHeader) {
			TableColumn tc = ((JTableHeader) e.getSource()).getResizingColumn();
			if (tc != null) {
				resizing = true;
				resizingColumn = tc.getModelIndex();
				oldWidth = tc.getPreferredWidth();
			} else {
				resizingColumn = -1;
				oldWidth = -1;
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// column moved
		if (dragging && oldIndex != newIndex) {
			// model.columnMoved(oldIndex, newIndex);
			// TODO:
			log.INFO("mouseReleased", "column moved: " + oldIndex + " -> " + newIndex);
		}
		dragging = false;
		oldIndex = -1;
		newIndex = -1;

		// column resized
		if (resizing) {
			if (e.getSource() instanceof JTableHeader) {
				TableColumnModel columnModel = ((JTableHeader) e.getSource()).getColumnModel();
				TableColumn tc = columnModel.getColumn(resizingColumn);
				if (tc != null) {
					int newWidth = tc.getPreferredWidth();
					if (newWidth != oldWidth) {
						table.SaveTableSettings(columnModel);
						log.INFO("mouseReleased", "column resized: " + resizingColumn + " -> " + newWidth);
					}
				}
			}
		}
		resizing = false;
		resizingColumn = -1;
		oldWidth = -1;
	}

	@Override
	public void columnAdded(TableColumnModelEvent e) {
	}

	@Override
	public void columnRemoved(TableColumnModelEvent e) {
	}

	@Override
	public void columnMoved(TableColumnModelEvent e) {
		// capture dragging
		dragging = true;
		if (oldIndex == -1) {
			oldIndex = e.getFromIndex();
		}

		newIndex = e.getToIndex();
	}

	@Override
	public void columnMarginChanged(ChangeEvent e) {
	}

	@Override
	public void columnSelectionChanged(ListSelectionEvent e) {
	}
}