package maxzawalo.c2.base.ui.pc.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import maxzawalo.c2.base.ui.pc.model.BOTableModel;

public class MultiLineCellRenderer extends JTextArea implements TableCellRenderer {
	BOTableModel model;

	public MultiLineCellRenderer() {
		setLineWrap(true);
		setWrapStyleWord(true);
		setOpaque(true);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		if (model == null)
			model = ((BOTableModel) table.getModel());

		boolean fuzzy = (model.getValue(row, "fuzzy") != null && (boolean) model.getValue(row, "fuzzy"));

		if (isSelected) {
			setForeground(table.getSelectionForeground());
			setBackground(table.getSelectionBackground());
		} else {
			setForeground(table.getForeground());
			setBackground(table.getBackground());
		}
		if(fuzzy)
			setForeground(Color.GRAY);

		setFont(table.getFont());
		if (hasFocus) {
			setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
			if (table.isCellEditable(row, column)) {
				setForeground(UIManager.getColor("Table.focusCellForeground"));
				setBackground(UIManager.getColor("Table.focusCellBackground"));
			}
		} else {
			setBorder(new EmptyBorder(1, 2, 1, 2));
		}
		String text = ((value == null) ? "" : value.toString());
		setToolTipText(text);
		setText(text);
		return this;
	}
}