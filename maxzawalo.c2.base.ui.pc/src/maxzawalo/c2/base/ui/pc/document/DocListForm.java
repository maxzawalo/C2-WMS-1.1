package maxzawalo.c2.base.ui.pc.document;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPopupMenu;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.CatalogueBO;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.base.data.factory.DocumentFactory;
import maxzawalo.c2.base.data.factory.FactoryBO;
import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.base.ui.pc.form.BoListForm;
import maxzawalo.c2.base.ui.pc.model.BOTableModel;
import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.base.utils.Global;

public class DocListForm<TypeBO, ItemForm> extends BoListForm<TypeBO, ItemForm> {
	protected JButton btnCommit;
	protected BizControlBase pageSizeCtrl;
	protected JButton btnCreateFromSourceDoc;

	protected JPopupMenu fromSourcePopup = new JPopupMenu();

	public DocListForm() {
		setBounds(0, 0, 1000, 700);

		btnDuplicate.setBounds(66, 11, 98, 40);
		// btnSelect.setLocation(900, 11);
		// btnAdd.setLocation(10, 11);

		JButton button = new JButton("Д/К");
		button.setToolTipText("Проводки документа");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ShowDocTransaction();
			}

		});
		button.setBounds(304, 11, 55, 40);
		getContentPane().add(button);

		btnCommit = new JButton();
		btnCommit.setIcon(UI.getCommitedIcon(20, 22));
		btnCommit.setToolTipText("Провести/Отменить проведение");
		btnCommit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CommitDoc();
			}
		});
		btnCommit.setBounds(370, 11, 45, 40);
		getContentPane().add(btnCommit);

		btnCreateFromSourceDoc = new JButton("Ввод на основании");
		btnCreateFromSourceDoc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				CreateFromSourceDocClick();
			}
		});
		btnCreateFromSourceDoc.setToolTipText("Ввод на основании");
		btnCreateFromSourceDoc.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnCreateFromSourceDoc.setBounds(178, 11, 115, 40);
		getContentPane().add(btnCreateFromSourceDoc);

		pageSizeCtrl = new BizControlBase();
		pageSizeCtrl.fieldType = Integer.class;
		pageSizeCtrl.setCaption("");
		pageSizeCtrl.setToolTipText("Строк на странице");
		pageSizeCtrl.setText(pageSize);
		// pageSizeCtrl.chang
		pageSizeCtrl.setBounds(850, -7, 50, 56);
		pageSizeCtrl.enterPressed.add(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				btnSearchClick();
			}
		});
		getContentPane().add(pageSizeCtrl, BorderLayout.NORTH);

		JButton accCommit = new JButton("Б");
		accCommit.setVisible(User.current.isAdmin());
		accCommit.setToolTipText("Провести по бухгалтерии");
		accCommit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CommitDocAcc();
			}
		});
		accCommit.setBounds(1002, 11, 45, 40);
		getContentPane().add(accCommit);
	}

	protected void CreateFromSourceDocClick() {
		fromSourcePopup.show(btnCreateFromSourceDoc, 0, btnCreateFromSourceDoc.getHeight());
	}

	@Override
	public void Search() {
		pageSize = Integer.parseInt(pageSizeCtrl.getText());
		// super.Search();
		pagesCount = FactoryBO.GetPagesCount(factory.GetCountByWords(searchData), pageSize);

		if (currentPage == Integer.MAX_VALUE)
			currentPage = getButtonsCount() - 1;

		items = factory.GetPageByWords(currentPage, pageSize, searchData);

		tableModel.setList(items);
		setModel();
		SetButtons();

		table.getSelectionModel().clearSelection();
		table.revalidate();
		repaint();
	}

	protected void CommitDoc() {
		UI.Start(this);
		for (int row : table.getSelectedRows()) {
			// TODO: transaction
			DocumentBO rowBO = (DocumentBO) ((BOTableModel) table.getModel()).getItem(row);
			// TODO: Обновлять кэш страниц при изменении.
			// Чтобы не брать из кэша
			DocumentBO doc = (DocumentBO) factory.GetById(rowBO.id, 0, false);
			// TODO: перенести в ядро - транзакцию
			if (doc.locked_by == null || doc.locked_by.id == User.zero.id) {
				if (doc.commited) {
					// TODO: check
					if (((DocumentFactory) factory).RollbackTransaction(rowBO, false))
						Console.I().INFO(getClass(), "CommitDoc", "Проведение отменено: " + rowBO);
				} else {
					BeforeTransaction((TypeBO) rowBO);// (TypeBO) doc);
					// TODO: check
					if (((DocumentFactory) factory).DoTransaction(rowBO, !Global.isAccService, false))
						Console.I().INFO(getClass(), "CommitDoc", rowBO + " " + " проведен.");
				}
			} else {
				// Сообщаем состояние объекта измененного в БД
				Console.I().INFO(getClass(), "CommitDoc", "Документ заблокирован пользователем: " + rowBO.locked_by);
			}
			table.revalidate();
			table.repaint();
		}

		Search();
		table.clearSelection();
		UI.Stop(this);
	}

	protected void CommitDocAcc() {
		// На сервисе такой кнопки не предусмотрено
		if (Global.isAccService)
			return;
		UI.Start(this);
		// Global.AccTransactionOnly = true;
		// Global.ProductTransactionOnly = false;
		try {
			for (int row : table.getSelectedRows()) {
				// TODO: transaction
				DocumentBO rowBO = (DocumentBO) ((BOTableModel) table.getModel()).getItem(row);
				// TODO: Обновлять кэш страниц при изменении.
				// Чтобы не брать из кэша
				DocumentBO doc = (DocumentBO) factory.GetById(rowBO.id, 0, false);
				// TODO: перенести в ядро - транзакцию
				if (doc.locked_by == null || doc.locked_by.id == User.zero.id) {
					// if (doc.commited)
					{
						if (((DocumentFactory) factory).DoTransaction(rowBO, false, true))
							Console.I().INFO(getClass(), "CommitDocAcc", rowBO + " " + " проведен по бухгалтерии.");
					}
				} else {
					// Сообщаем состояние объекта измененного в БД
					Console.I().INFO(getClass(), "CommitDocAcc",
							"Документ заблокирован пользователем: " + rowBO.locked_by);
				}
				table.revalidate();
				table.repaint();
			}
			Search();
			table.clearSelection();
		} catch (Exception e) {
			log.ERROR("CommitDocAcc", e);
		}
		// Global.AccTransactionOnly = false;
		// Global.ProductTransactionOnly = true;
		UI.Stop(this);
	}

	protected void BeforeTransaction(TypeBO doc) {

	}

	@Override
	public void selectItem(Object senderControl, BO bo) {
		String text = SelectItemSetText(bo, searchData);
		searchText.setText(text);
		setSearch(text);
		Search();
		// ((CatalogueListForm) this).ExpandTreeByBo((CatalogueBO) boFromTP);
		// autoSuggestor.TextFieldPressEnter();
		super.selectItem(senderControl, bo);
	}

	protected String SelectItemSetText(BO bo, String text) {
		if (bo instanceof CatalogueBO)
			text = ((CatalogueBO) bo).name;
		return text;
	}

	@Override
	protected void ResizeBtnPanel() {
		int btnPanelWidth = (int) (splitPane.getWidth() * 0.65);
		// if (BoListForm.this instanceof DocListForm)
		btnPanelWidth = buttonsScrollPanel.getParent().getWidth() - 20;

		buttonsScrollPanel.setSize(btnPanelWidth, buttonsScrollPanel.getHeight());
		buttonsScrollPanel.setLocation(buttonsScrollPanel.getParent().getWidth() - buttonsScrollPanel.getWidth() - 10,
				splitPane.getY() + splitPane.getHeight() + 20);

		btnPanel.setSize(buttonsScrollPanel.getWidth(), buttonsScrollPanel.getHeight());
	}

	protected void ShowDocTransaction() {
	}
}