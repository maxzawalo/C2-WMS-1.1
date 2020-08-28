package maxzawalo.c2.base.ui.pc.controls;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.table.TableModel;

import maxzawalo.c2.base.Actions;
import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.bo.TablePartItem;
import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.ui.pc.form.BoListForm;
import maxzawalo.c2.base.ui.pc.model.BOEditTableModel;
import maxzawalo.c2.free.bo.Contractor;

public class PopupPanelTable extends KeyPressedTable {
	protected JPanel popupPanel;
	JPopupMenu elementPopupMenu;
	protected JPopupMenu lotsPopupMenu;
	JButton btnChangeElement;
	BoListForm selectListForm;

	JButton btnSelectElement;
	JButton btnOpenElement;

	public Action ChangeElementAction;

	public Date docDate;
	public BO owner;

	public PopupPanelTable() {
		this(null);
	}

	public PopupPanelTable(TableModel model) {
		super(model);

		lotsPopupMenu = new JPopupMenu();
		lotsPopupMenu.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		elementPopupMenu = new JPopupMenu();

		popupPanel = new JPanel();
		popupPanel.setLayout(null);
		elementPopupMenu.add(popupPanel);
		elementPopupMenu.setOpaque(false);

		btnSelectElement = new JButton("v");
		btnSelectElement.setToolTipText("Выбрать из списка");
		btnSelectElement.setMargin(new Insets(0, 0, 0, 0));
		btnSelectElement.setSize(btnSize, btnSize);
		btnSelectElement.setBackground(Color.WHITE);
		btnSelectElement.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SelectElementAction();
			}
		});
		popupPanel.add(btnSelectElement);
		popupPanel.setOpaque(false);

		btnOpenElement = new JButton("[ ]");
		btnOpenElement.setToolTipText("Открыть");
		btnOpenElement.setMargin(new Insets(0, 0, 0, 0));
		btnOpenElement.setSize(btnSize, btnSize);
		btnOpenElement.setBackground(Color.WHITE);
		btnOpenElement.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnOpenElement.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				OpenElementAction();
			}
		});
		popupPanel.add(btnOpenElement);

		btnChangeElement = new JButton("--");
		btnChangeElement.setToolTipText("Изменить");
		btnChangeElement.setMargin(new Insets(0, 0, 0, 0));
		btnChangeElement.setSize(btnSize, btnSize);
		btnChangeElement.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnChangeElement.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ChangeElementAction();
			}
		});
		popupPanel.add(btnChangeElement);

		popupPanel.setBackground(Color.LIGHT_GRAY);
		elementPopupMenu.setBorder(null);
		elementPopupMenu.setBackground(Color.WHITE);

		elementPopupMenu.revalidate();
	}

	protected void OpenElementAction() {
		UI.Start(this);
		Actions.OpenBoFormByInstanceAction.Do(selectedBO);
		// BoForm selectForm = new ProductFormFree();
		// selectForm.Load(selectedBO.id);
		// selectForm.setVisible(true);
		UI.Stop(this);
	}

	protected void ChangeElementAction() {
	}

	protected void SelectElementAction() {
		UI.Start(PopupPanelTable.this);
		if (selectedBO != null && selectedBO instanceof DocumentBO)
			((DocumentBO) selectedBO).contractor = (Contractor) owner;
		selectListForm.selectItem(PopupPanelTable.this.getModel(), selectedBO);
		UI.Stop(PopupPanelTable.this);
	}

	public void ShowMenu(Class typeBO, BO selectedBO, int x, int y, int rowIndex, int columnIndex) {
		// если оставить if (selectListForm == null) owner не будет
		// устанавливаться при изменении
		selectListForm = (BoListForm) Actions.ListFormByClassAction.Do(typeBO, selectedBO, false, owner);

		selectListForm.rowIndex = rowIndex;
		selectListForm.columnIndex = columnIndex;

		startLotPopupY = UIManager.getInt("Table.rowHeight") * rowIndex;

		if (selectedBO == null)
			// Чтобы в SelectElementAction отработал поиск
			try {
				selectedBO = (BO) typeBO.newInstance();
			} catch (Exception e) {
				log.ERROR("ShowMenu", e);
			}

		this.selectedBO = selectedBO;
		selectedTP = (TablePartItem) ((BOEditTableModel) getModel()).getList().get(rowIndex);

		int popupPanelX = 0;
		for (int i = 0; i < columnIndex; i++)
			popupPanelX += getColumnModel().getColumn(i).getWidth();

		// popupPanel.setLocation(popupPanelX, popupPanel.getY());
		popupPanel.setSize(getColumnModel().getColumn(columnIndex).getWidth() - 5, btnOpenElement.getHeight());

		btnChangeElement.setLocation(0, 0);
		btnOpenElement.setLocation(popupPanel.getWidth() - btnSize, 0);
		btnSelectElement.setLocation(btnOpenElement.getX() - btnSize - 10, 0);
		elementPopupMenu.show(this, popupPanelX, y - 2);
	}
}