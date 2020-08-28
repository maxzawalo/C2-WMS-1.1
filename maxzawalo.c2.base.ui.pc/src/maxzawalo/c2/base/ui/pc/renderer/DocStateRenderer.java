package maxzawalo.c2.base.ui.pc.renderer;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;

import maxzawalo.c2.base.ui.pc.common.UI;

public class DocStateRenderer extends CustomCellRenderer {

	ImageIcon imgDeleted = UI.getDeletedIcon(16, 16);
	ImageIcon imgCommited = UI.getCommitedIcon(18, 22);

	public static BufferedImage convertToBufferedImage(Image image) {
		BufferedImage newImage = new BufferedImage(image.getWidth(null), image.getHeight(null),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = newImage.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return newImage;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		JLabel label = new JLabel();
		label.setOpaque(true);
		if (isSelected)
			label.setBackground(table.getSelectionBackground());
		else
			label.setBackground(table.getBackground());

		if (value != null) {
			label.setHorizontalAlignment(JLabel.CENTER);
			if (value.equals("v")) {
				label.setIcon(imgCommited);
				label.setToolTipText("Проведен");
			} else if (value.equals("x")) {
				label.setIcon(imgDeleted);
				label.setToolTipText("Удален");
			}
			// else
			// label.setIcon(new ImageIcon());
		}

		return label;
	}
}