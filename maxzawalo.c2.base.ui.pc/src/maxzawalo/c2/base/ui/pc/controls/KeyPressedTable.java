package maxzawalo.c2.base.ui.pc.controls;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.base.data.factory.FactoryBO;
import maxzawalo.c2.base.interfaces.ActionC2;
import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.ui.pc.listener.SColumnListener;
import maxzawalo.c2.base.ui.pc.model.BOTableModel;
import maxzawalo.c2.base.ui.pc.model.ColumnSettings;
import maxzawalo.c2.base.ui.pc.renderer.CustomCellRenderer;
import maxzawalo.c2.base.ui.pc.renderer.MultiLineHeaderRenderer;
import maxzawalo.c2.base.utils.Console;

public class KeyPressedTable extends TableWithState {

	boolean markDelete = false;

	public boolean doDeleteQuestion = false;
	public boolean readonly = false;

	public Action beforeDeleteRowAction;
	public List<ActionC2> afterDeleteRowAction = new ArrayList<>();
	public Action deleteRowAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent evt) {
			if (readonly)
				return;
			UI.Start(KeyPressedTable.this);
			// Для LotOfProductListForm.returnToSelectList
			if (beforeDeleteRowAction != null)
				beforeDeleteRowAction.actionPerformed(evt);

			List deleted = new ArrayList<>();

			if (markDelete) {
				for (int row : getSelectedRows()) {
					BO rowBO = ((BO) ((BOTableModel) KeyPressedTable.this.getModel()).getItem(row));
					deleted.add(rowBO);

					if (doDeleteQuestion) {
						int dialogResult = JOptionPane.showConfirmDialog(KeyPressedTable.this, "Удалить " + rowBO + "?", "Удаление", JOptionPane.YES_NO_OPTION);
						if (dialogResult != JOptionPane.YES_OPTION)
							continue;
					}

					// TODO: Обновлять в кэше. Чтобы не было старого.
					FactoryBO factory = new FactoryBO<>().Create(rowBO.getClass()).DeleteFilterOff();
					BO bo = (BO) factory.GetById(rowBO.id, 0, false);
					if (bo.locked_by == null || bo.locked_by.id == User.zero.id) {
						try {
							// Делаем инверсию
							// TODO: проработать обновление в UI и кэше
							rowBO = (BO) factory.setDeleted(rowBO, !bo.deleted);
						} catch (Exception e) {
							log.ERROR("deleteRowAction", e);
							Console.I().ERROR(getClass(), "deleteRowAction", e.getLocalizedMessage());
						}
					} else {
						// Сообщаем состояние объекта измененного в БД
						Console.I().INFO(getClass(), "deleteRowAction", "Объект заблокирован пользователем: " + rowBO.locked_by);
					}
					// TODO: Search
				}
				// else
				// ((BOTableModel)
				// KeyPressedTable.this.getModel()).removeRow(row);
			} else {
				for (int row : getSelectedRows()) {
					BO rowBO = ((BO) ((BOTableModel) KeyPressedTable.this.getModel()).getItem(row));
					deleted.add(rowBO);
				}
				((BOTableModel) KeyPressedTable.this.getModel()).removeRows(getSelectedRows());
			}

			// Для табчасти - пересчет Итого
			// afterDeleteRowAction =
			// afterDeleteRowAction.stream().distinct().collect(Collectors.toList());
			for (ActionC2 a : afterDeleteRowAction.stream().distinct().collect(Collectors.toList()))
				a.Do(new Object[] { deleted });

			KeyPressedTable.this.getSelectionModel().clearSelection();
			KeyPressedTable.this.revalidate();
			KeyPressedTable.this.repaint();
			UI.Stop(KeyPressedTable.this);
		}
	};

	// TODO: Actions
	public Action lockRowAction = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent evt) {
			if (readonly)
				return;
			UI.Start(KeyPressedTable.this);
			for (int row : getSelectedRows()) {
				BO rowBO = ((BO) ((BOTableModel) KeyPressedTable.this.getModel()).getItem(row));
				FactoryBO factory = new FactoryBO<>().Create(rowBO.getClass());
				// TODO: Обновлять в кэше при получении. Чтобы не было старого.
				BO bo = (BO) factory.GetById(rowBO.id, 0, false);
				if (bo.locked_by == null || bo.locked_by.id == User.zero.id || bo.locked_by.id == User.current.id) {
					try {
						// Проверяем только что полученный
						if (bo.locked_by != null && bo.locked_by.id == User.current.id)
							// снимаем блокировку
							// TODO: check
							rowBO = factory.setLockedBy(rowBO, User.zero);
						else
							// TODO: check
							rowBO = factory.setLockedBy(rowBO, User.current);
					} catch (Exception e) {
						log.ERROR("lockRowAction", e);
					}
				} else {
					// Сообщаем состояние объекта измененного в БД
					Console.I().INFO(getClass(), "lockRowAction", "Объект заблокирован пользователем: " + rowBO.locked_by);
				}
			}

			KeyPressedTable.this.getSelectionModel().clearSelection();
			KeyPressedTable.this.revalidate();
			KeyPressedTable.this.repaint();
			UI.Stop(KeyPressedTable.this);
		}
	};

	public KeyPressedTable() {
		this(null);
	}

	public KeyPressedTable(TableModel model) {
		super(model);

		// TODO: load setting
		SColumnListener columnListener = new SColumnListener(this);
		getColumnModel().addColumnModelListener(columnListener);
		getTableHeader().addMouseListener(columnListener);

		setLockAction();
	}

	public void setColumnSettings() {
		for (int col = 0; col < getColumnModel().getColumnCount(); col++) {
			ColumnSettings sett = ((BOTableModel) getModel()).getVisibleColumn(col);
			if (sett.renderer instanceof CustomCellRenderer)
				((CustomCellRenderer) sett.renderer).setHorizontalAlignment(sett.horizontalAlignment);
			getColumnModel().getColumn(col).setCellRenderer(sett.renderer);
		}

		setColumnCaption();
	}

	public void setColumnCaption() {
		List<ColumnSettings> visibleColumns = ((BOTableModel) getModel()).visibleColumns;
		for (int col = 0; col < getColumnModel().getColumnCount(); col++)
			getColumnModel().getColumn(col).setHeaderValue(visibleColumns.get(col).caption);
	}

	public void EnableMarkDeleteRow() {
		markDelete = true;
		EnableRowDeleting();
	}

	public void EnableRowDeleting() {
		InputMap im = getInputMap(JTable.WHEN_FOCUSED);
		ActionMap am = getActionMap();
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "DeleteRow");
		am.put("DeleteRow", deleteRowAction);
	}

	public void setLockAction() {
		InputMap im = getInputMap(JTable.WHEN_FOCUSED);
		ActionMap am = getActionMap();
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0), "LockRow");
		am.put("LockRow", lockRowAction);
	}

	public void setHeaderHeight(int height) {
		getTableHeader().setPreferredSize(new Dimension(getColumnModel().getTotalColumnWidth(), height));
		MultiLineHeaderRenderer renderer = new MultiLineHeaderRenderer();
		Enumeration e = getColumnModel().getColumns();
		while (e.hasMoreElements()) {
			((TableColumn) e.nextElement()).setHeaderRenderer(renderer);
		}
	}
}