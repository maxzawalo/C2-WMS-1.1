package maxzawalo.c2.base.ui.pc.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;

public class RedZeroAndMinusCellRenderer extends CustomCellRenderer {
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		if (Double.parseDouble((String) value) <= 0) {
			cellComponent.setForeground(Color.RED);
			Font font = cellComponent.getFont();
			cellComponent.setFont(new Font(font.getFontName(), Font.BOLD, font.getSize()));
		} else
			cellComponent.setForeground(table.getForeground());

		return cellComponent;
	}
}