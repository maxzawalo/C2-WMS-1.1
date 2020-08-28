package maxzawalo.c2.free.ui.pc.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.table.TableModel;

import maxzawalo.c2.base.Actions;
import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.ui.pc.controls.PopupPanelTable;
import maxzawalo.c2.base.ui.pc.model.BOTableModel;
import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.free.bo.LotOfProduct;
import maxzawalo.c2.free.bo.Store;
import maxzawalo.c2.free.bo.store.StoreDocBO;
import maxzawalo.c2.free.bo.store.StoreTP;

public class StoreTPTable extends PopupPanelTable {
	List<LotOfProduct> lots = new ArrayList<>();
	public Store store;

	// public Action ChangeLotAction;

	public StoreTPTable() {
		this(null);
	}

	public StoreTPTable(TableModel model) {
		super(model);

	}

	// // TODO: переенести в KeyPressedTable
	// public void ShowMenu(BO selectedBO, int x, int y, int rowIndex, int
	// columnIndex) {
	// if (selectListForm == null)
	// selectListForm = new ProductListForm();
	//
	// selectListForm.rowIndex = rowIndex;
	// selectListForm.columnIndex = columnIndex;
	//
	// startLotPopupY = UIManager.getInt("Table.rowHeight") * rowIndex;
	//
	// this.selectedBO = selectedBO;
	// selectedTP = (TablePartItem) ((BOEditTableModel)
	// getModel()).getList().get(rowIndex);
	//
	// popupPanel.setSize(getColumnModel().getColumn(columnIndex).getWidth() -
	// 5, btnOpenProduct.getHeight());
	//
	// selectLot.setLocation(0, 0);
	// btnOpenProduct.setLocation(popupPanel.getWidth() - btnSize, 0);
	// btnSelectProduct.setLocation(btnOpenProduct.getX() - btnSize - 10, 0);
	// productPopupMenu.show(this, 0, y - 2);
	// }

	@Override
	protected boolean isBigToolTipColumn(int column) {
		// return new ArrayList<String>(new String[]{}).contains(column)
		return (column == ((BOTableModel) getModel()).getColNumByVisibleColumns(StoreTP.fields.PRODUCT) || column == ((BOTableModel) getModel()).getColNumByVisibleColumns(LotOfProduct.fields.GROUP)
				|| column == ((BOTableModel) getModel()).getColNumByVisibleColumns(LotOfProduct.fields.PRICE_BO)
				|| column == ((BOTableModel) getModel()).getColNumByVisibleColumns(LotOfProduct.fields.LOT)
				|| column == ((BOTableModel) getModel()).getColNumByVisibleColumns(StoreDocBO.fields.DOC_CONTRACT));
	}

	protected List<LotOfProduct> GetLots() {
		if (Actions.GetLots4TPAction != null)
			return (List<LotOfProduct>) Actions.GetLots4TPAction.Do(selectedBO, store, docDate);

		return new ArrayList<>();
	}

	@Override
	protected void ChangeElementAction() {
		UI.Start(this);
		if (ChangeElementAction == null)
			// TODO: full
			// FreeVersionForm.Full();
			;
		else
			ChangeElementAction.actionPerformed(null);

		if (selectedTP.bad || User.current.isAdmin()) {
			lots.clear();
			// TODO: full
			for (LotOfProduct lot : GetLots()) {
				// Не показываем с 0 остатком
				if (lot.count == 0)
					continue;
				lots.add(lot);
			}

			lotsPopupMenu.removeAll();
			for (LotOfProduct lot : lots) {
				JMenuItem item = new JMenuItem(new AbstractAction(lot.print()) {
					public void actionPerformed(ActionEvent e) {
						for (int i = 0; i < lotsPopupMenu.getComponentCount(); i++) {
							if (lotsPopupMenu.getComponent(i) == e.getSource()) {
								LotOfProduct lot = lots.get(i);

								// System.out.println(lot.print());
								((StoreTP) selectedTP).price_discount_off = lot.cost_price;
								// price =
								// Format.defaultRound(cost_price *
								// (1 + discount / 100));
								((StoreTP) selectedTP).lotOfProduct = lot;
								((StoreTP) selectedTP).Calc(StoreTP.fields.TOTAL);
								StoreTPTable.this.revalidate();
								StoreTPTable.this.repaint();
								Console.I().INFO(getClass(), "ChangeElementAction", "Партия изменена на " + lot.print());
								break;
							}
						}
					}
				});

				item.setFont(new Font("Terminal", Font.BOLD, 20));
				item.setBackground(Color.WHITE);
				lotsPopupMenu.add(item);
			}

			if (lots.size() == 0) {
				Console.I().WARN(getClass(), "ChangeElementAction", "Нет остатков по этой позиции");
			} else {
				lotsPopupMenu.show(StoreTPTable.this, 0, (int) (startLotPopupY - popupPanel.getPreferredSize().getHeight() - lotsPopupMenu.getPreferredSize().getHeight()));
			}
		} else
			Console.I().WARN(getClass(), "ChangeElementAction", "Смена Партии возможна лишь для 'красных' позиций (0 остаток)");
		UI.Stop(this);
	}
}