package maxzawalo.c2.base.ui.pc.renderer;

import java.awt.Component;
import java.awt.FontMetrics;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

import maxzawalo.c2.base.utils.StringUtils;

public class MultiLineHeaderRenderer extends JList implements TableCellRenderer {
	public MultiLineHeaderRenderer() {
		setOpaque(true);
		setForeground(UIManager.getColor("TableHeader.foreground"));
		setBackground(UIManager.getColor("TableHeader.background"));
		setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		ListCellRenderer renderer = getCellRenderer();
		((JLabel) renderer).setHorizontalAlignment(JLabel.CENTER);
		setCellRenderer(renderer);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		setFont(table.getFont());

		String text = (value == null) ? "" : value.toString();
		setToolTipText(text);

		FontMetrics fm = this.getFontMetrics(this.getFont());
		int width = table.getColumnModel().getColumn(column).getWidth();
		List<String> textList = StringUtils.wrap(text, fm, width);
		Vector v = new Vector();
		for (String line : textList)
			v.addElement(line);

		setListData(v);
		return this;
	}
}