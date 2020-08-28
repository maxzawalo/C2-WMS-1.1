package maxzawalo.c2.free.ui.pc.control;


import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.TableCellRenderer;

import maxzawalo.c2.base.ui.pc.renderer.MultiLineCellRenderer;
import maxzawalo.c2.free.ui.pc.model.catalogue.LotOfProductModel;

public class LotProductMergedTableUI extends BasicTableUI {

	public int[] mergedCol = { 0, 1 };
	LotOfProductModel tableModel;
	List<Integer> selectedGroup = new ArrayList<>();

	public void paint(Graphics g, JComponent c) {

		if (!(table.getModel() instanceof LotOfProductModel))
			return;// Заглушка для WindowBuilder

		tableModel = (LotOfProductModel) table.getModel();
		Rectangle oldClipBounds = g.getClipBounds();
		Rectangle clipBounds = new Rectangle(oldClipBounds);
		int tableWidth = table.getColumnModel().getTotalColumnWidth();
		clipBounds.width = Math.min(clipBounds.width, tableWidth);
		g.setClip(clipBounds);

		int firstIndex = 0;// table.rowAtPoint(new Point(0, clipBounds.y));
		int lastIndex = table.getRowCount() - 1;

		Rectangle rowRect = new Rectangle(0, 0, tableWidth, table.getRowHeight() + table.getRowMargin());
		rowRect.y = firstIndex * rowRect.height;

		for (int index = firstIndex; index <= lastIndex; index++) {
			// if (rowRect.intersects(clipBounds)) //TODO:
			{
				// System.out.println(); // debug
				// System.out.print("" + index +": "); // row
				paintRow(g, index, false);
			}
			rowRect.y += rowRect.height;
		}

		// Находим выделенную группу
		selectedGroup.clear();
		if (table.getSelectedRow() >= 0) {

			// Движемся вниз до начала следующей группы
			int row = table.getSelectedRow() + 1;
			for (; row < lastIndex; row++)
				if (tableModel.groups.get(row) != 0)
					break;

			row--;
			for (; row >= 0; row--) {
				selectedGroup.add(row);
				if (tableModel.groups.get(row) != 0)
					break;
			}
		}

		// Обновляем merged
		firstIndex = 0;
		for (int index = firstIndex; index <= lastIndex; index++) {
			// if (rowRect.intersects(clipBounds)) //TODO:
			{
				// System.out.println(); // debug
				// System.out.print("" + index +": "); // row
				// if (tableModel.groups.get(index) != 0)
				paintRow(g, index, true);
			}
			rowRect.y += rowRect.height;
		}
		g.setClip(oldClipBounds);
	}

	private void paintRow(Graphics g, int row, boolean repaintMerged) {
		Rectangle rect = g.getClipBounds();
		boolean drawn = false;
		int numColumns = table.getColumnCount();
		// int column = 0;
		if (repaintMerged) {
			// column = mergedCol[1];
			numColumns = mergedCol[1] + 1;
		}

		for (int column = 0; column < numColumns; column++) {
			if (!repaintMerged && isMergedCol(column))
				continue;

			Rectangle cellRect = table.getCellRect(row, column, true);
			// if (cellRect.intersects(rect))
			{
				drawn = true;
				int count = 1;
				if (tableModel.groups.size() != 0)
					count = tableModel.groups.get(row);
				// if (count != 0)
				paintCell(g, cellRect, row, column, count, repaintMerged);
			}
			// else {
			// if (drawn)
			// break;
			// }
		}
	}

	public boolean isMergedCol(int column) {
		for (int i = 0; i < mergedCol.length; i++)
			if (mergedCol[i] == column)
				return true;
		return false;
	}

	private void paintCell(Graphics g, Rectangle cellRect, int row, int column, int mergedRowCount, boolean selected) {
		int spacingHeight = table.getRowMargin();
		int spacingWidth = table.getColumnModel().getColumnMargin();

		int height = cellRect.height;

		if (isMergedCol(column))
			height *= mergedRowCount;

		if (height != 0) {
			Color c = g.getColor();
			g.setColor(table.getGridColor());
			g.drawRect(cellRect.x, cellRect.y, cellRect.width - 1, height - 1);
			g.setColor(c);

			cellRect.setBounds(cellRect.x + spacingWidth / 2, cellRect.y + spacingHeight / 2,
					cellRect.width - spacingWidth, height - spacingHeight);

			// if (table.isEditing() && table.getEditingRow() == row &&
			// table.getEditingColumn() == column) {
			// Component component = table.getEditorComponent();
			// component.setBounds(cellRect);
			// component.validate();
			// } else
			{
				TableCellRenderer renderer = table.getCellRenderer(row, column);
				if (isMergedCol(column))
					renderer = new MultiLineCellRenderer();

				Component component = table.prepareRenderer(renderer, row, column);

				if (component.getParent() == null) {
					rendererPane.add(component);
				}

				if (isMergedCol(column)) {
					Color backColor = table.getBackground();

					if (selectedGroup.contains(row)) {
						backColor = table.getSelectionBackground();
					}
					component.setBackground(backColor);
				}

				rendererPane.paintComponent(g, component, table, cellRect.x, cellRect.y, cellRect.width,
						cellRect.height, true);

			}
		}
	}
}