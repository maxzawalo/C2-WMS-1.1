package maxzawalo.c2.base.ui.pc.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import maxzawalo.c2.base.ui.pc.model.BOTableModel;

public class CustomCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 6703872492730589499L;
	public int fontStyle = Font.PLAIN; // Font.ITALIC | Font.BOLD;
	protected BOTableModel model;
	protected boolean isReserve = false;
	protected boolean fuzzy = false;

	public CustomCellRenderer() {
		System.out.println("На www CustomCellRenderer не нужен");
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		cellComponent.setFont(cellComponent.getFont().deriveFont(fontStyle));
		if (model == null)
			model = ((BOTableModel) table.getModel());
		Object reserve = model.getValueAt(row, model.getColNumByVisibleColumns("reserve"));
		isReserve = (reserve != null && (boolean) reserve);

		Object fuzzy = model.getValue(row, "fuzzy");
		this.fuzzy = (fuzzy != null && (boolean) fuzzy);

		if (isReserve)
			cellComponent.setForeground(Color.LIGHT_GRAY);
		else
			cellComponent.setForeground(table.getForeground());

		Object bad = model.getValue(row, "bad");
		if (bad != null && (boolean) bad) {
			if (isSelected)
				cellComponent.setBackground(Color.decode("#db5252"));
			else
				cellComponent.setBackground(Color.PINK);
		} else {
			if (isSelected)
				cellComponent.setBackground(table.getSelectionBackground());
			else
				cellComponent.setBackground(table.getBackground());
		}

		return cellComponent;
	}
}