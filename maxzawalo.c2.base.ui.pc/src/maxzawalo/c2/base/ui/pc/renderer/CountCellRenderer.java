package maxzawalo.c2.base.ui.pc.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;

public class CountCellRenderer extends CustomCellRenderer {

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		// Такая конструкция уже учитывает column.format = "0.000";
		if (!isReserve && (Double.parseDouble(value.toString()) == 0))
			cellComponent.setForeground(Color.GRAY);
		// else
		// cellComponent.setForeground(table.getForeground());
		// } else if (row == 1) {
		// cellComponent.setBackground(Color.GRAY);
		// } else {
		// cellComponent.setBackground(Color.CYAN);
		// }
		return cellComponent;
	}
}