package maxzawalo.c2.base.ui.pc.document;

import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.bo.TablePartItem;
import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.base.data.factory.DocumentFactory;
import maxzawalo.c2.base.interfaces.ActionC2;
import maxzawalo.c2.base.os.Run;
import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.base.ui.pc.controls.CheckBoxBizControl;
import maxzawalo.c2.base.ui.pc.controls.DateBizControl;
import maxzawalo.c2.base.ui.pc.controls.KeyPressedTable;
import maxzawalo.c2.base.ui.pc.controls.MyCellEditor;
import maxzawalo.c2.base.ui.pc.controls.PopupPanelTable;
import maxzawalo.c2.base.ui.pc.controls.SwingLink;
import maxzawalo.c2.base.ui.pc.controls.Tab;
import maxzawalo.c2.base.ui.pc.form.BoForm;
import maxzawalo.c2.base.ui.pc.model.BOEditTableModel;
import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.base.utils.Global;

public class DocForm<Doc, Item> extends BoForm<Doc> {

	private static final String ФИЛЬТРОВАТЬ_ТАБ_ЧАСТЬ = "Без фильтра";
	protected Map<String, JScrollPane> tableScrollPanels = new HashMap<>();
	protected Map<String, BOEditTableModel> tablePartModels = new HashMap<>();
	protected Map<String, PopupPanelTable> grids = new HashMap<>();

	protected BizControlBase contractor;

	protected JTabbedPane tabbedPane;

	protected JButton btnAddToTablePart;
	protected JButton btnPrint;
	protected JButton btnCommit;

	protected JButton btnLock;

	protected BizControlBase DocDate;

	// protected Class<Item> itemType;
	// protected BOEditTableModel tablePartModel;

	protected JButton btnShowTransactions;

	protected BizControlBase total;
	protected BizControlBase totalVat;

	protected int tabbedPaneYPos = 200;

	protected BizControlBase sum_contains_vat;
	protected BizControlBase filterTPControl;
	protected SwingLink showTpFilter;

	protected JButton btnSelectLot;

	JDialog parent;

	public DocForm() {
		this(null);
	}

	public DocForm(JDialog parent) {
		this.parent = parent;
		try {
			Class clazz = this.getClass();
			// Рекировочка
			if (clazz.getSuperclass().getName().contains("Free"))
				clazz = clazz.getSuperclass();

			java.lang.reflect.Type[] gParams = ((ParameterizedType) clazz.getGenericSuperclass())
					.getActualTypeArguments();
			if (gParams.length == 2)// для dao - иначе не создает BO
			{
				typeBO = (Class<Doc>) gParams[0];
				elementBO = typeBO.newInstance();
				// itemType = (Class<Item>) gParams[1];

			}
		} catch (Exception e) {
			log.ERROR("DocForm", e);
		}

		setBounds(0, 0, 1000, 700);
		topPanel.setBounds(0, 0, 945, 496);
		bottomPanel.setBounds(0, 640, 1008, 90);
		btnSave.setLocation(876, 30);
		code.setBounds(12, 12, 135, 56);

		btnAddToTablePart = new JButton("+");
		btnAddToTablePart.setLocation(12, 151);
		btnAddToTablePart.setSize(40, 40);
		btnAddToTablePart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AddToTablePart();
			}
		});
		topPanel.add(btnAddToTablePart);

		btnPrint = new JButton("Печать");
		btnPrint.setMargin(new Insets(0, 0, 0, 0));
		btnPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!Print()) {
					JOptionPane.showMessageDialog(DocForm.this, "Печать невозможна. Сохраните и проведите документ.",
							"Печать", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		btnPrint.setBounds(78, 30, 67, 40);
		// getContentPane().add(btnPrint);
		bottomPanel.add(btnPrint);

		DocDate = new DateBizControl();
		DocDate.setSize(109, 56);
		DocDate.setLocation(148, 12);
		DocDate.setCaption("Дата");
		DocDate.setFieldName("DocDate");
		topPanel.add(DocDate);
		DocDate.afterBOSelected.add(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SetGridFilterFields();
			}
		});

		btnShowTransactions = new JButton("Д/К");
		btnShowTransactions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ShowTransactions();
			}
		});
		btnShowTransactions.setBounds(244, 151, 55, 40);
		topPanel.add(btnShowTransactions);

		total = new BizControlBase();
		total.setReadOnly();
		total.setFieldName(DocumentBO.fields.TOTAL);
		total.setCaption("Всего");
		total.setBounds(682, 22, 100, 56);
		bottomPanel.add(total);

		totalVat = new BizControlBase();
		totalVat.setReadOnly();
		totalVat.setFieldName(DocumentBO.fields.TOTAL_VAT);
		totalVat.setCaption("НДС");
		totalVat.setBounds(597, 22, 87, 56);
		bottomPanel.add(totalVat);

		BizControlBase comment = new BizControlBase();
		comment.fieldType = String.class;
		comment.setFieldName(DocumentBO.fields.COMMENT);
		comment.setCaption("Комментарий");
		comment.setBounds(157, 22, 428, 56);
		bottomPanel.add(comment);

		btnCommit = new JButton();
		btnCommit.setIcon(UI.getCommitedIcon(20, 22));
		btnCommit.setToolTipText("Провести/Отменить проведение");
		btnCommit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CommitDoc();
			}
		});
		btnCommit.setBounds(812, 32, 40, 40);
		bottomPanel.add(btnCommit);

		btnSelectLot = new JButton("Подбор");
		btnSelectLot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Tab tab = (Tab) tabbedPane.getSelectedComponent();
				if (tab != null && !tab.tablePartName.equals(""))
					ItemTPSelection(tab.tablePartName);
			}
		});
		btnSelectLot.setBounds(81, 151, 115, 40);
		topPanel.add(btnSelectLot);

		sum_contains_vat = new CheckBoxBizControl();
		sum_contains_vat.setFieldName(DocumentBO.fields.SUM_CONTAINS_VAT);
		sum_contains_vat.setCaption("Сумма вкл. НДС");
		sum_contains_vat.setBounds(804, 140, 168, 56);
		topPanel.add(sum_contains_vat);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		tabbedPane.setLocation(10, tabbedPaneYPos);
		tabbedPane.setSize(984, 404);
		// tabbedPane.addTab("Товары", new JPanel());

		topPanel.add(tabbedPane);

		btnLock = new JButton();
		btnLock.setIcon(UI.getLockIcon(18, 18));
		btnLock.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BO bo = (BO) factory.GetById(((BO) elementBO).id, 0, false);
				if (bo.locked_by == null || bo.locked_by.id == User.zero.id || bo.locked_by.id == User.current.id) {
					try {
						// Проверяем только что полученный
						if (bo.locked_by != null && bo.locked_by.id == User.current.id) {
							// снимаем блокировку
							// TODO: check
							elementBO = (Doc) factory.setLockedBy((BO) elementBO, User.zero);
							SetLockBtnIcon();
							Console.I().INFO(getClass(), "btnLock", "Блокировка снята");
						} else {
							// TODO: check
							elementBO = (Doc) factory.setLockedBy((BO) elementBO, User.current);
							SetLockBtnIcon();
							Console.I().INFO(getClass(), "btnLock", "Блокировка установлена");
						}
					} catch (Exception ex) {
						log.ERROR("btnLockClick", ex);
					}
				} else {
					// Сообщаем состояние объекта измененного в БД
					Console.I().INFO(getClass(), "btnLock",
							"Объект заблокирован пользователем: " + ((BO) elementBO).locked_by);
				}
			}
		});
		btnLock.setToolTipText("Заблокировать/Отменить");
		btnLock.setBounds(932, 63, 40, 40);
		topPanel.add(btnLock);

		filterTPControl = new BizControlBase();
		filterTPControl.setFieldType(String.class);
		filterTPControl.setFieldName("");
		filterTPControl.setCaption("");
		filterTPControl.setBounds(0, -25, 245, 56);

		filterTPControl.enterPressed.add(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				filterTP = filterTPControl.getText();
				filterTP = filterTP.toLowerCase();
				filterTP = filterTP.replaceAll("  ", " ");
				System.out.println("filterTP: " + filterTP);
				if (filterTP.isEmpty())
					showTpFilter.setup(ФИЛЬТРОВАТЬ_ТАБ_ЧАСТЬ);
				else
					showTpFilter.setup(filterTP);
				filterTPControl.setVisible(false);
				setTableModel();
			}
		});
		filterTPControl.setVisible(false);
		bottomPanel.add(filterTPControl);

		showTpFilter = new SwingLink();
		showTpFilter.setup(ФИЛЬТРОВАТЬ_ТАБ_ЧАСТЬ);
		showTpFilter.setBounds(10, 0, 204, 20);
		showTpFilter.setFont(showTpFilter.getFont().deriveFont(18.0f));
		showTpFilter.onClick = new AbstractAction() {
			public void actionPerformed(final ActionEvent evt) {
				filterTPControl.setVisible(true);
				filterTPControl.SetFocus();
			}
		};
		bottomPanel.add(showTpFilter);

		btnInit = new JButton("");
		btnInit.setIcon(UI.getBarcodeScannerIcon(30, 30));
		btnInit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InitTerminal();
			}
		});
		btnInit.setBounds(10, 30, 40, 40);
		bottomPanel.add(btnInit);

		topPanel.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				tabbedPane.setSize(topPanel.getWidth() - 20, topPanel.getHeight() - tabbedPaneYPos);
				ResizeTP();
				sum_contains_vat.setLocation(sum_contains_vat.getParent().getWidth() - sum_contains_vat.getWidth() - 10,
						sum_contains_vat.getY());
				// bottomPanel.setSize(DocForm.this.getWidth(),
				// topPanel.getHeight());
			}
		});
	}

	protected JComponent makeTextPanel(String text) {
		JPanel panel = new JPanel(false);
		JLabel filler = new JLabel(text);
		filler.setHorizontalAlignment(JLabel.CENTER);
		panel.setLayout(new GridLayout(1, 1));
		panel.add(filler);
		return panel;
	}

	protected String reportPath = "";

	public boolean Print() {
		return Print("");
	}

	public boolean Print(String reportName) {
		// Не печатаем с пустым кодом
		if (!Check4Print(reportName))
			return false;
		UI.Start(this);
		FreeSoon();
		JustFull();
		CreateGson();
		GenerateReportData(reportName);
		SendReport2Service(reportName);
		LoadReportFromService(reportName);
		ShowReport(reportName);
		UI.Stop(this);
		return true;
	}

	protected void CreateGson() {
		

	}

	protected void ShowReport(String reportName) {
		String pathname = reportPath.replace("c2_report", "xlsx");
		if (!Global.RunInTest)
			Run.OpenFile(pathname);
		else if (!new File(pathname).exists())
			throw new UnsupportedOperationException("File not exists" + pathname);
	}

	protected void SendReport2Service(String name) {
	}

	protected void LoadReportFromService(String name) {
	}

	protected void JustFull() {
	}

	protected void FreeSoon() {
	}

	protected boolean Check4Print(String reportName) {
		return !((BO) elementBO).code.equals("") && ((DocumentBO) elementBO).commited;
	}

	protected void AddToTablePart() {
		Tab tab = (Tab) tabbedPane.getSelectedComponent();
		if (tab != null && !tab.tablePartName.equals(""))
			try {
				DocumentBO doc = ((DocumentBO) elementBO);
				TablePartItem tp = (TablePartItem) doc.GetTypeTPByName(tab.tablePartName).newInstance();
				doc.GetTPByName(tab.tablePartName).add(tp);
				setSumContainsVat2TP();
				setTableModel();
				ScrolToNewRow(tab.tablePartName);
				setEvents();
				onTablePartChanged.Do(null);
			} catch (Exception e) {
				log.ERROR("AddToTablePart", e);
			}
	}

	protected void setSumContainsVat2TP() {
		// // TODO: для всех табчастей StoreDocBO
		// StoreDocBO doc = ((StoreDocBO) elementBO);
		// for (Object tp : doc.TablePartProduct)
		// ((StoreTP) tp).sum_contains_vat = doc.sum_contains_vat;
	}

	@Override
	public void NewBO() {
		
		super.NewBO();
		AddFilter();
		setTableModel();
	}

	public String[] GetTPNames() {
		return ((DocumentBO) elementBO).GetTPNames();
	}

	protected String filterTP = "";

	// // @Override
	// public void setTableModel() {
	// for (String name : GetTPNames()) {
	// // TODO: if visible
	// BOEditTableModel tablePartModel = tablePartModels.get(name);
	// tablePartModel.setList(FilterTPAction(name));
	//
	// KeyPressedTable grid = grids.get(name);
	// if (grid == null)
	// continue;
	// grid.setModel(tablePartModel);
	// grid.setDefaultEditor(new Object().getClass(), new MyCellEditor());
	//
	// grid.LoadTableSettings();
	// grid.setColumnSettings();
	// tablePartModel.setTable(grid);
	// grid.afterDeleteRowAction.add(new ActionC2() {
	// @Override
	// public Object Do(Object[] params) {
	// Tab tab = (Tab) tabbedPane.getSelectedComponent();
	// if (tab != null && !tab.tablePartName.equals("")) {
	// String tablePartName = tab.tablePartName;
	// List<TablePartItem> deleted = (List<TablePartItem>) params[0];
	// ((DocumentBO) elementBO).GetTPByName(tablePartName).removeAll(deleted);
	// // for (TablePartItem item : deleted)
	// // ;
	// }
	//
	// return true;
	// }
	// });
	//
	// grid.revalidate();
	// grid.repaint();
	// }
	// }

	public void setTableModel() {
		for (String name : GetTPNames()) {
			// TODO: if visible
			BOEditTableModel tablePartModel = tablePartModels.get(name);
			// тут фильтруем таб часть
			tablePartModel.setList(FilterTPAction(name));
			// tablePartModel.setList((List) ((DocumentBO)
			// elementBO).GetTPByName(name));
			KeyPressedTable grid = grids.get(name);
			if (grid == null)
				continue;
			grid.setModel(tablePartModel);
			grid.setDefaultEditor(new Object().getClass(), new MyCellEditor());

			grid.LoadTableSettings();
			grid.setColumnSettings();
			tablePartModel.setTable(grid);

			grid.Refresh();
		}
	}

	/**
	 * Фильтруем таб часть
	 * 
	 * @param tpName
	 * @return
	 */
	public List FilterTPAction(String tpName) {
		return ((DocumentBO) elementBO).GetTPByName(tpName);
	}

	// protected void RemoveTPRow() {
	// // TODO: для всех табчастей
	// // TODO: IndexOutOfBoundsException
	// int dialogResult = JOptionPane.showConfirmDialog(DocForm.this, "Удалить
	// выбранную строку?", "Удаление",
	// JOptionPane.YES_NO_OPTION);
	// if (dialogResult == JOptionPane.YES_OPTION) {
	//
	// ((DocumentBO)
	// elementBO).TablePartProduct.remove(tableProduct.getSelectedRow());
	// setTableModel();
	// }
	// }

	@Override
	public void Load(int id) {
		try {
			elementBO = factory.GetById(id, 0, false);
			AddFilter();
			((DocumentFactory) factory).LoadTablePart(((DocumentBO) elementBO));
			setTableModel();
			setData();
		} catch (Exception e) {
			log.ERROR("Load", e);
		}
	}

	public void AddFilter() {
	}

	@Override
	public void setData() {
		super.setData();
		CommitedDocBtnState();
		SetLockBtnIcon();
		// TODO: проверить ставится ли при вводе на основании
		SetGridFilterFields();
		log.DEBUG("setData", "DocDate=" + Format.Show("yyyy-MM-dd HH:mm:ss.SSS", ((DocumentBO) elementBO).DocDate));
	}

	protected void CommitedDocBtnState() {
		// TODO: полностью блокировать форму(Readonly)
		btnSave.setEnabled(!((DocumentBO) elementBO).commited);
		btnSelectLot.setEnabled(!((DocumentBO) elementBO).commited);
		btnAddToTablePart.setEnabled(!((DocumentBO) elementBO).commited);
	}

	public ActionC2 onTablePartChanged = new ActionC2() {
		@Override
		public Object Do(Object[] params) {
			TPChanged();
			return false;
		}
	};
	protected JButton btnInit;

	public String getRusTitle() {
		return super.getRusTitle() + " " + ((BO) elementBO).code + " " + Format.Show(((DocumentBO) elementBO).DocDate)
				+ ((((DocumentBO) elementBO).commited) ? " Проведен" : "");
	}

	// Выбран элемент из списка BO
	public void onBOSelected(BO selectedBO) {
		System.out.println("Doc.onBOSelected " + selectedBO);
	}

	protected void CommitDoc() {
		UI.Start(this);
		DocumentBO doc = (DocumentBO) elementBO;
		doc = (DocumentBO) factory.GetById(doc.id, 0, false);
		// TODO: перенести в ядро - транзакцию
		// Проверяем только что полученный
		if (doc.locked_by == null || doc.locked_by.id == User.zero.id) {
			if (doc.commited) {
				// TODO:check
				if (((DocumentFactory) factory).RollbackTransaction(doc, false)) {
					// Если здесь сделать так elementBO = (Doc) doc, то
					// не сохранятся изменения сделанные после
					// предыдущего сохранения.
					// Например удалится таб часть - не есть гуд.
					Console.I().INFO(getClass(), "CommitDoc", "Проведение отменено");
				}
			} else {
				BeforeTransaction((Doc) doc);
				// TODO:check
				if (((DocumentFactory) factory).DoTransaction(doc, !Global.isAccService, false)) {
					Console.I().INFO(getClass(), "CommitDoc", "Документ проведен");
				}
			}
			// Для регулирования доступности Сохранить и Печать
			((DocumentBO) elementBO).commited = doc.commited;

		} else {
			// Сообщаем состояние объекта измененного в БД
			Console.I().INFO(getClass(), "CommitDoc", "Документ заблокирован пользователем: " + doc.locked_by);
		}
		CommitedDocBtnState();

		MarkBadRows(doc);

		RefreshTP();
		UI.Stop(this);
	}

	protected void RefreshTP() {
		for (String name : GetTPNames()) {
			KeyPressedTable grid = grids.get(name);
			if (grid != null)
				grid.Refresh();
		}
	}

	/**
	 * Для раскраски плохих строк
	 * 
	 * @param doc
	 */
	protected void MarkBadRows(DocumentBO doc) {
	}

	protected void BeforeTransaction(Doc doc) {
	}

	protected void ItemTPSelection(String tablePartName) {

	}

	protected void ShowTransactions() {
	}

	protected void TPChanged() {
		// Обновляем визуально total при сохранении
		total.setBo((BO) elementBO);
		totalVat.setBo((BO) elementBO);
		total.revalidate();
		totalVat.revalidate();
	}

	protected void GenerateReportData(String reportName) {

	}

	@Override
	protected void onFormResized() {
		super.onFormResized();
		btnCommit.setLocation(btnSave.getX() - btnCommit.getWidth() - 10, btnCommit.getY());
		btnLock.setLocation(getWidth() - btnLock.getWidth() - 30, btnLock.getY());
		btnInit.setLocation(btnInit.getX(), bottomPanel.getHeight() - btnInit.getHeight() - 20);
	}

	protected void SetLockBtnIcon() {
		btnLock.setIcon(
				((BO) elementBO).locked_by.id == User.zero.id ? UI.getLockOffIcon(18, 18) : UI.getLockIcon(18, 18));
	}

	protected void ScrolToNewRow(String tablePartName) {
		int row = tablePartModels.get(tablePartName).getRowCount();
		KeyPressedTable grid = grids.get(tablePartName);
		if (grid != null)
			grid.scrollRectToVisible(new Rectangle(0, row * grid.getRowHeight(), grid.getWidth(), grid.getRowHeight()));
	}

	@Override
	protected void setEvents() {
		for (String name : GetTPNames()) {
			for (TablePartItem tp : (List<TablePartItem>) ((DocumentBO) elementBO).GetTPByName(name))
				tp.onChanged = onTablePartChanged;
			KeyPressedTable grid = grids.get(name);
			if (grid != null)
				// TODO: distinct
				grid.afterDeleteRowAction.add(onTablePartChanged);
		}
	}

	// @Override
	protected void ResizeTP() {
		for (String name : GetTPNames()) {
			JScrollPane sp = tableScrollPanels.get(name);
			if (sp != null)
				sp.setSize(tabbedPane.getWidth() - 5, tabbedPane.getHeight() - 40);
		}
	}

	public void SetGridFilterFields() {
		for (String name : GetTPNames()) {
			PopupPanelTable grid = (PopupPanelTable) grids.get(name);
			if (grid != null) {
				grid.docDate = DocDate.getDate();
			}
		}
	}
}