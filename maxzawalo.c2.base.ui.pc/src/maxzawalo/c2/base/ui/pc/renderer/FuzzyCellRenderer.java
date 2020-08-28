package maxzawalo.c2.base.ui.pc.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;

public class FuzzyCellRenderer extends CustomCellRenderer {

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		if (fuzzy)
			cellComponent.setForeground(Color.GRAY);

		return cellComponent;
	}
}