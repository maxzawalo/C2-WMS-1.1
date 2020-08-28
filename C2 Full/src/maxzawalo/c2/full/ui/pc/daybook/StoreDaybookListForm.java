package maxzawalo.c2.full.ui.pc.daybook;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JMenuItem;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.interfaces.ActionC2;
import maxzawalo.c2.base.os.Run;
import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.base.ui.pc.document.DocForm;
import maxzawalo.c2.base.ui.pc.form.BoListForm;
import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.base.utils.FileUtils;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.bo.Settings;
import maxzawalo.c2.free.bo.document.deliverynote.DeliveryNote;
import maxzawalo.c2.free.bo.document.deliverynote.DeliveryNoteTablePart;
import maxzawalo.c2.free.bo.store.StoreDocBO;
import maxzawalo.c2.free.data.factory.catalogue.ContractorFactory;
import maxzawalo.c2.free.data.factory.document.StoreDocFactory;
import maxzawalo.c2.free.ui.pc.document.store.DeliveryNoteFormFree;
import maxzawalo.c2.full.bo.StoreDaybook;
import maxzawalo.c2.full.bo.daybook.StoreDaybookUIComparator;
import maxzawalo.c2.full.data.factory.StoreDaybookFactory;
import maxzawalo.c2.full.report.code.StoreDaybookReporter;
import maxzawalo.c2.full.reporter.Xlsx;
import maxzawalo.c2.full.ui.pc.model.StoreDaybookTableModel;

public class StoreDaybookListForm extends BoListForm<StoreDaybook, StoreDaybookForm> {
	BizControlBase contractor;
	BizControlBase date;
	JCheckBox enableDateFilter;

	public StoreDaybookListForm() {
		setBounds(0, 0, 1000, 700);
		tableModel = new StoreDaybookTableModel();

		btnDuplicate.setLocation(976, 11);
		btnDuplicate.setVisible(false);

		searchPanel.setLocation(979, 0);
		searchPanel.setVisible(false);

		btnAdd.setEnabled(false);

		contractor = new BizControlBase();
		contractor.fieldType = Contractor.class;
		// contractor.setFieldName("contractor");
		contractor.setCaption("Контрагент");
		contractor.setBounds(228, 0, 389, 56);
		contractor.setBo(new Contractor());
		contractor.afterBOSelected.add(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Contractor c = (Contractor) contractor.getBO();
				if (!new ContractorFactory().CheckUnp(c)) {
					Console.I().WARN(getClass(), "contractor.afterBOSelected", "У контрагента нет УНП. Отбор невозможен.");
					return;
				}
				// Изначально кнопка Добавить не активна. Только после выбора
				// контрагента
				if (c.id != 0)
					btnAdd.setEnabled(true);
				Search();
			}
		});
		getContentPane().add(contractor);

		enableDateFilter = new JCheckBox("");
		enableDateFilter.setToolTipText("Включить фильтрацию по дате");
		enableDateFilter.setBounds(715, 3, 17, 17);
		enableDateFilter.setRolloverEnabled(false);
		enableDateFilter.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				Search();
			}
		});

		getContentPane().add(enableDateFilter);

		date = new BizControlBase();
		date.fieldType = Date.class;
		date.setCaption("Дата");
		date.setBounds(633, 0, 109, 56);
		date.onBOSelected(new Date());
		getContentPane().add(date);
		date.enterPressed.add(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Search();
			}
		});

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

		table.afterDeleteRowAction.add(new ActionC2() {

			@Override
			public Object Do(Object[] params) {
				Search();
				return false;
			}
		});

		JButton button = new JButton("И");
		button.setToolTipText("История изменений");
		button.setBounds(753, 11, 40, 40);
		getContentPane().add(button);

		JButton button_1 = new JButton("Печать");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// TODO: печать по контрагенту за период (предыдущий месяц)
				// TODO: учесть количество строк и поделить на страницы - в
				// репортере параметр

				String filename = FileUtils.GetReportDir() + "StoreDaybook_" + System.currentTimeMillis() + ".xlsx";
				StoreDaybook book = new StoreDaybook();

				List<StoreDaybook> rep = new StoreDaybookFactory().Get4Report(date.getDate(), false);
				Xlsx.PrintMatrix(filename, rep, new StoreDaybookReporter(), 1, Integer.MAX_VALUE, true);
				Run.OpenFile(filename);
				// for (StoreDaybook book : rep)
				// for (Object b : book.getTablePart4Rep())
				// System.out.println(b);
			}
		});
		button_1.setToolTipText("История изменений");
		button_1.setBounds(884, 11, 90, 40);
		getContentPane().add(button_1);

		JButton btnCreateSign = new JButton();
		btnCreateSign.setBorder(BorderFactory.createEmptyBorder());
		btnCreateSign.setIcon(UI.getSignatureIcon());
		btnCreateSign.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Подписываем по каждому контрагенту
				new StoreDaybookFactory().CreateSign();
				Console.I().INFO(getClass(), "btnSign", "Журнал подписан.");
			}
		});
		btnCreateSign.setToolTipText("История изменений");
		btnCreateSign.setBounds(822, 0, 51, 51);
		getContentPane().add(btnCreateSign);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (selectedBO != null) {
					StoreDaybookHistoryListForm history = new StoreDaybookHistoryListForm();
					history.setElementBO((StoreDaybook) selectedBO);
					history.Search();
					history.setVisible(true);

					// List<StoreDaybook> list = ((StoreDaybook)
					// selectedBO).GetHistory();
					// for (StoreDaybook b : list)
					// System.out.println(b);
				}
			}
		});

		table.rowPopup.removeAll();
		JMenuItem item = new JMenuItem("Расходная");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				try {
					// Подписываем сначала потому что могут быть пустые -
					// пришедшие с мобильного
					// Будет глюк при сохранении
					new StoreDaybookFactory().CreateSign();

					DocForm toForm = new DeliveryNoteFormFree();
					StoreDocBO toDoc = new DeliveryNote();
					toDoc.contractor = (Contractor) contractor.getBO();
					toDoc.doc_contract = toDoc.contractor.main_contract;
					toDoc.comment = "Сформирован по складскому журналу.";

					List<StoreDaybook> selected = getSelectedBooks();

					if (selected.size() == 0) {
						Console.I().INFO(getClass(), "JMenuItem(\"Расходная\")", "Выбраны только вычеркнутые или не выбрано ничего.");
					} else {
						for (StoreDaybook book : selected) {
							DeliveryNoteTablePart.Product tp = new DeliveryNoteTablePart.Product();
							tp.product = book.product;
							tp.count = book.count;
							tp.price_discount_off = book.price * 100 / (Settings.defaultVat + 100);
							tp.price = tp.price_discount_off;
							tp.Calc("");
							toDoc.TablePartProduct.add(tp);
						}
						new StoreDocFactory<>().Create(toDoc.getClass()).Save(toDoc);

						// Сохранили без ошибок - меняем Журнал
						for (StoreDaybook book : selected)
							new StoreDaybookFactory().toDoc(book, "Счет|" + toDoc.code + "|" + toDoc.id);

						toForm.Load(toDoc.id);
						toForm.setVisible(true);

						Search();
					}
				} catch (Exception e) {
					log.ERROR("", e);
				}
			}
		});
		table.rowPopup.add(item);
		table.rowPopup.addSeparator();

		JMenuItem returned = new JMenuItem("Контрагент вернул");
		// Обновляем Ценник
		// Подтверждение

		table.rowPopup.add(returned);

	}

	@Override
	protected void Add() {
		super.Add();
		((StoreDaybookForm) itemForm).SetContractor((Contractor) contractor.getBO());
		itemForm.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				Search();
			}
		});
	}

	@Override
	protected void OpenSelected() {
		super.OpenSelected();
		itemForm.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				Search();
			}
		});
	}

	@Override
	public void Search() {
		pagesCount = 0;
		// pagesCount = BO.GetPagesCount(((BO) elementBO).GetCount(), pageSize);

		if (currentPage == Integer.MAX_VALUE)
			currentPage = getButtonsCount() - 1;

		SetButtons();

		Date filterDate = null;
		if (enableDateFilter.isSelected())
			filterDate = date.getDate();

		items = new StoreDaybookFactory().GetPageByFiltered((Contractor) contractor.selectedBO, filterDate);

		Collections.sort(items, new StoreDaybookUIComparator());

		tableModel.setList(items);
		setModel();
		table.revalidate();
		table.repaint();
	}

	protected List<StoreDaybook> getSelectedBooks() {
		List<StoreDaybook> selected = new ArrayList<>();

		for (int row : table.getSelectedRows()) {
			int id = ((BO) tableModel.getList().get(row)).id;
			// Берем напрямую из БД
			StoreDaybook book = new StoreDaybookFactory().GetById(id, 0, false);
			// Берем неудаленные
			if (!book.deleted)
				selected.add(book);
		}
		return selected;
	}
}