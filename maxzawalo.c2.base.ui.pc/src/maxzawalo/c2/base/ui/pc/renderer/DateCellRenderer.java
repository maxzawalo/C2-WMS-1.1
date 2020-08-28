package maxzawalo.c2.base.ui.pc.renderer;

import java.awt.Component;
import java.util.Date;

import javax.swing.JTable;

import maxzawalo.c2.base.utils.Format;

public class DateCellRenderer extends CustomCellRenderer {
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		if (value instanceof Date) {
			value = Format.Show((Date) value);
		}
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
}