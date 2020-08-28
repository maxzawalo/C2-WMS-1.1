package maxzawalo.c2.full.ui.pc.daybook;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.ui.pc.form.BoListForm;
import maxzawalo.c2.full.bo.StoreDaybook;
import maxzawalo.c2.full.data.factory.StoreDaybookFactory;
import maxzawalo.c2.full.ui.pc.model.StoreDaybookTableModel;

public class StoreDaybookHistoryListForm extends BoListForm<StoreDaybook, StoreDaybookForm> {
	public StoreDaybookHistoryListForm() {
		setBounds(0, 0, 1000, 700);
		tableModel = new StoreDaybookTableModel();

		btnDuplicate.setLocation(976, 11);
		btnDuplicate.setVisible(false);

		searchPanel.setLocation(979, 0);
		searchPanel.setVisible(false);
		btnAdd.setVisible(false);

		JButton btnRefreshLoc = new JButton();
		btnRefreshLoc.setIcon(UI.getRefreshIcon(20, 20));
		btnRefreshLoc.setToolTipText("Обновить");
		btnRefreshLoc.setBounds(141, 11, 40, 40);
		getContentPane().add(btnRefreshLoc);
		btnRefreshLoc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnRefreshClick();
			}
		});
		// Отключаем удаление
		table.readonly = true;
	}

	@Override
	protected void OpenSelected() {
		// Отключаем изменение
	}

	@Override
	public void Search() {
		pagesCount = 0;
		// pagesCount = BO.GetPagesCount(((BO) elementBO).GetCount(), pageSize);

		if (currentPage == Integer.MAX_VALUE)
			currentPage = getButtonsCount() - 1;

		SetButtons();

		items = new StoreDaybookFactory().GetHistory(elementBO);

		tableModel.setList(items);
		setModel();
		table.revalidate();
		table.repaint();
		// setGroupTree();
	}

	public void setElementBO(StoreDaybook elementBO) {
		this.elementBO = elementBO;
		setTitle("История изменений: " + elementBO);
	}
}