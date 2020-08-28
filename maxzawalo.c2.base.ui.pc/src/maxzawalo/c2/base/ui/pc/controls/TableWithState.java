package maxzawalo.c2.base.ui.pc.controls;

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.TablePartItem;
import maxzawalo.c2.base.ui.pc.model.BOTableModel;
import maxzawalo.c2.base.utils.FileUtils;
import maxzawalo.c2.base.utils.Logger;
import maxzawalo.c2.base.utils.StringUtils;

public class TableWithState extends JTable {

	protected Logger log = Logger.getLogger(this.getClass().getSimpleName());


	public JPopupMenu rowPopup;
	public JMenuItem editRowPopupItem;

	protected int btnSize = 0;

	public TableWithState() {
		this(null);
	}

	public TableWithState(TableModel model) {
		super(model);

		setRowSelectionAllowed(true);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		rowPopup = new JPopupMenu();
		editRowPopupItem = new JMenuItem("Редактировать");
		rowPopup.add(editRowPopupItem);

		addMouseListener(new MouseAdapter() {
			final int defaultDismissTimeout = ToolTipManager.sharedInstance().getDismissDelay();
			// 2 minutes
			final int dismissDelayMinutes = (int) TimeUnit.MINUTES.toMillis(2);

			@Override
			public void mouseEntered(MouseEvent me) {
				ToolTipManager.sharedInstance().setDismissDelay(dismissDelayMinutes);
			}

			@Override
			public void mouseExited(MouseEvent me) {
				ToolTipManager.sharedInstance().setDismissDelay(defaultDismissTimeout);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					JTable source = (JTable) e.getSource();
					int row = source.rowAtPoint(e.getPoint());
					int column = source.columnAtPoint(e.getPoint());

					if (!source.isRowSelected(row))
						source.changeSelection(row, column, false, false);

					rowPopup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		btnSize = UIManager.getInt("Table.rowHeight") - 3;


	}

	public BO selectedBO;
	protected TablePartItem selectedTP;
	protected int startLotPopupY = 0;

	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		Component c = super.prepareRenderer(renderer, row, column);
		if (c instanceof JComponent) {
			if (isBigToolTipColumn(column)) {
				JComponent jc = (JComponent) c;

				FontMetrics fm = this.getFontMetrics(this.getFont());
				int width = 400;// etColumnModel().getColumn(column).getWidth();
				List<String> lines = StringUtils.wrap(getValueAt(row, column) + "", fm, width);

				String text = "";
				for (String line : lines)
					text += line + "<br>";
				String html = "<html><p><font color=\"#000\" " + "size=\"6\">" + text + "</font></p></html>";
				jc.setToolTipText(html);
			}
		}
		return c;
	}

	protected boolean isBigToolTipColumn(int column) {
		return false;
	}

	public String settingsKey = "table";

	public void SaveTableSettings(TableColumnModel columnModel) {
		String columns = "";
		columnsWidth = new String[columnModel.getColumnCount()];
		for (int col = 0; col < columnModel.getColumnCount(); col++) {
			columnsWidth[col] = "" + columnModel.getColumn(col).getPreferredWidth();
			columns += columnsWidth[col] + " ";
		}

		FileUtils.Text2File(FileUtils.GetSettingsDir() + settingsKey + ".settings", columns, false);
	}

	protected String[] columnsWidth;

	public void LoadTableSettings() {
		try {
			if (columnsWidth != null)
				return;

			String data = FileUtils.readFileAsString(FileUtils.GetSettingsDir() + settingsKey + ".settings");
			if (data.equals(""))
				return;
			columnsWidth = data.split(" ");
			for (int col = 0; col < Math.min(columnModel.getColumnCount(), columnsWidth.length); col++)
				if (((BOTableModel) getModel()).getVisibleColumn(col).addCol)
					columnModel.getColumn(col).setPreferredWidth(0);
				else
					columnModel.getColumn(col).setPreferredWidth(Integer.parseInt(columnsWidth[col]));

		} catch (Exception e) {
			log.ERROR("LoadTableSettings", e);
		}
	}

	public void Refresh() {
		revalidate();
		repaint();
	}
}