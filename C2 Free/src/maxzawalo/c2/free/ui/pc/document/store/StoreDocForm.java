package maxzawalo.c2.free.ui.pc.document.store;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

import maxzawalo.c2.base.Actions;
import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.bo.TablePartItem;
import maxzawalo.c2.base.bo.registry.RegType;
import maxzawalo.c2.base.interfaces.ActionC2;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.base.ui.pc.controls.ComboBoxBizControl;
import maxzawalo.c2.base.ui.pc.controls.KeyPressedTable;
import maxzawalo.c2.base.ui.pc.controls.Tab;
import maxzawalo.c2.base.ui.pc.document.DocForm;
import maxzawalo.c2.base.ui.pc.renderer.DocStateRenderer;
import maxzawalo.c2.free.bo.Contract;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.bo.Currency;
import maxzawalo.c2.free.bo.Store;
import maxzawalo.c2.free.bo.StrictForm;
import maxzawalo.c2.free.bo.StrictFormType;
import maxzawalo.c2.free.bo.enums.ContractType;
import maxzawalo.c2.free.bo.store.StoreDocBO;
import maxzawalo.c2.free.bo.store.StoreTP;
import maxzawalo.c2.free.data.factory.catalogue.StrictFormFactory;
import maxzawalo.c2.free.data.factory.document.StoreDocFactory;
import maxzawalo.c2.free.ui.pc.catalogue.LotOfProductListFormFree;
import maxzawalo.c2.free.ui.pc.control.StoreTPTable;
import maxzawalo.c2.free.ui.pc.form.FreeVersionForm;
import maxzawalo.c2.free.ui.pc.model.catalogue.StrictFormModel;

public class StoreDocForm<Doc, Item> extends DocForm<Doc, Item> {
	protected Tab tabProduct;
	// protected StoreTPTable tableProduct;
	// protected JScrollPane tableProductScrollPane;

	protected Tab tabService;
	// protected StoreTPTable tableService;
	// protected JScrollPane tableServiceScrollPane;

	// protected BizControlBase contractor;
	protected BizControlBase doc_contract;
	// TODO: если не основная валюта - показывать курс
	protected BizControlBase doc_currency;
	protected BizControlBase store;

	// ===== StrictForm =====
	protected BizControlBase out_form_batch;
	protected BizControlBase out_form_number;
	protected JComboBox<StrictFormType> out_form_type;
	protected KeyPressedTable strictFormTable;
	// ===== StrictForm =====

	public StoreDocForm() {
		this(null);
	}

	protected void FreeTimeLimit() {
		FreeVersionForm.Limit();
	}

	public StoreDocForm(JDialog parent) {
		super(parent);
		FreeTimeLimit();
		factory = new StoreDocFactory<Doc>();

		setBounds(0, 0, 1000, 700);
		topPanel.setBounds(0, 0, 945, 496);
		bottomPanel.setBounds(0, 640, 1008, 90);
		btnSave.setLocation(876, 30);
		code.setBounds(12, 12, 135, 56);
		sum_contains_vat.setLocation(804, 151);

		contractor = new BizControlBase();
		contractor.fieldType = Contractor.class;
		contractor.setFieldName(DocumentBO.fields.CONTRACTOR);
		contractor.setBounds(12, 67, 428, 56);
		contractor.afterBOSelected.add(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ContractorSelected();
			}
		});
		topPanel.add(contractor);

		doc_contract = new BizControlBase();
		doc_contract.fieldType = Contract.class;
		doc_contract.setEnabled(false);
		doc_contract.setFieldName(StoreDocBO.fields.DOC_CONTRACT);
		doc_contract.setBounds(457, 67, 336, 56);
		doc_contract.afterBOSelected.add(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (((StoreDocBO) elementBO).doc_contract != null) {
					((StoreDocBO) elementBO).doc_currency = ((StoreDocBO) elementBO).doc_contract.doc_currency;
					doc_currency.onBOSelected(((StoreDocBO) elementBO).doc_currency);
					doc_currency.revalidate();
				}
			}
		});
		topPanel.add(doc_contract);

		doc_currency = new ComboBoxBizControl();
		doc_currency.fieldType = Currency.class;
		doc_currency.setFieldName(StoreDocBO.fields.DOC_CURRENCY);
		doc_currency.LoadList();
		doc_currency.setBounds(457, 12, 103, 56);
		topPanel.add(doc_currency);

		store = new ComboBoxBizControl();
		store.fieldType = Store.class;
		store.LoadList();
		store.setFieldName(StoreDocBO.fields.STORE);
		// store.setCaption("Склад");
		store.setBounds(457, 135, 236, 56);
		topPanel.add(store);
		store.afterBOSelected.add(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SetGridFilterFields();
			}
		});

		tabProduct = new Tab();
		tabProduct.setLayout(null);
		CreateTPGrid(tabProduct, "TablePartProduct");

		// tabProduct.add(tableProductScrollPane);

		tabService = new Tab();
		tabService.setLayout(null);
		CreateTPGrid(tabService, "TablePartService");

		tabbedPane.addTab("Товары", null, tabProduct, null);
		tabbedPane.addTab("Услуги", null, tabService, null);
	}

	public StoreTPTable CreateTPGrid(Tab tab, String tablePartName) {
		StoreTPTable grid = new StoreTPTable();
		grid.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		grid.settingsKey = getClass().getSimpleName() + "." + tablePartName;
		grid.setFillsViewportHeight(true);
		grid.setRowHeight(UIManager.getInt("Table.rowHeight"));
		grid.EnableRowDeleting();
		grids.put(tablePartName, grid);

		JScrollPane tableScrollPane = new JScrollPane();
		tableScrollPane.setBounds(0, 0, 100, 100);
		tableScrollPane.setViewportView(grid);
		tableScrollPanels.put(tablePartName, tableScrollPane);

		tab.tablePartName = tablePartName;
		tab.add(tableScrollPane);

		return grid;
	}

	@Override
	protected void ItemTPSelection(String tablePartName) {
		LotOfProductListFormFree selectForm = CreateSelectForm();
		selectForm.itemT = ((DocumentBO) elementBO).GetTypeTPByName(tablePartName);
		selectForm.setDocDate(DocDate.getDate());
		selectForm.setStore(((StoreDocBO) elementBO).store);
		selectForm.setDoc((StoreDocBO) elementBO);
		// if (selectForm.forInvoice)
		// selectForm.delivery = ((Invoice) elementBO).delivery;
		selectForm.selectMode = true;
		selectForm.Search();
		selectForm.showDialog();
	}

	protected LotOfProductListFormFree CreateSelectForm() {
		return new LotOfProductListFormFree(StoreDocForm.this, StoreDocForm.this);
	}

	@Override
	protected void ShowTransactions() {
		FreeVersionForm.Full();
	}

	@Override
	protected void MarkBadRows(DocumentBO doc) {
		// Для раскраски плохих строк
		if (doc instanceof StoreDocBO)
			for (int i = 0; i < ((StoreDocBO) doc).TablePartProduct.size(); i++) {
				((TablePartItem) ((StoreDocBO) elementBO).TablePartProduct
						.get(i)).bad = ((TablePartItem) ((StoreDocBO) doc).TablePartProduct.get(i)).bad;
			}
	}

	@Override
	protected void AfterSetData() {
		doc_contract.setEnabled(doc_contract.owner != null || contractor.getBO() != null);
		doc_contract.owner = ((StoreDocBO) elementBO).contractor;
	}

	// public String[] GetTPNames() {
	// return ((StoreDocBO) elementBO).GetTPNames();
	// }

	@Override
	protected void ScrolToNewRow(String tablePartName) {
		int row = tablePartModels.get(tablePartName).getRowCount();
		KeyPressedTable grid = grids.get(tablePartName);
		if (grid != null)
			grid.scrollRectToVisible(new Rectangle(0, row * grid.getRowHeight(), grid.getWidth(), grid.getRowHeight()));
	}

	@Override
	protected void setSumContainsVat2TP() {
		for (String name : GetTPNames()) {
			StoreDocBO doc = ((StoreDocBO) elementBO);
			for (Object tp : doc.GetTPByName(name))
				((StoreTP) tp).sum_contains_vat = doc.sum_contains_vat;
		}
	}

	public void CopyFromSelectForm(List<StoreTP> allSelected) {
		Tab tab = (Tab) tabbedPane.getSelectedComponent();
		if (tab != null && !tab.tablePartName.equals("")) {
			String tablePartName = tab.tablePartName;
			((StoreDocBO) elementBO).GetTPByName(tablePartName).addAll(allSelected);
			setSumContainsVat2TP();
			setTableModel();
			setEvents();
			onTablePartChanged.Do(null);
		}
	}

	@Override
	public void AddFilter() {
		// Устанавливаем фильтр для фильтрации по виду договора
		ContractType contractType = (RegType.isInDoc(((DocumentBO) elementBO).reg_type)
				|| ((DocumentBO) elementBO).reg_type == RegType.ReturnOfGoods
						? new ContractType().getEnumByName("С поставщиком")
						: new ContractType().getEnumByName("С покупателем"));
		if (((DocumentBO) elementBO).reg_type == RegType.ReturnFromCustomer)
			contractType = new ContractType().getEnumByName("С покупателем");

		doc_contract.filter.put(Contract.fields.CONTRACT_TYPE, contractType);
	}

	@Override
	protected void TPChanged() {
		((DocumentBO) elementBO).EnumTP();
		// TODO: для всех табчастей
		((StoreDocBO) elementBO).CalcTotal();
		super.TPChanged();
	}

	// protected void EnumTP() {
	// for (String name : GetTPNames()) {
	// List tp = ((StoreDocBO) elementBO).GetTPByName(name);
	// for (int i = 0; i < tp.size(); i++) {
	// ((StoreTP) tp.get(i)).calcFields.put(TablePartItem.fields.POS, i + 1);
	// }
	// }
	// }

	// TODO: adapter?
	@Override
	protected void CreateGson() {
		
//		if(gson == null)
//			new jsonc
		
//		GsonBuilder gsonBuilder = new GsonBuilder();
//		Map<Class, BoAdapter> adapters = new HashMap<>();
//
//		AddAdapters(adapters);
//
//		for (Class cl : adapters.keySet())
//			gsonBuilder.registerTypeAdapter(cl, adapters.get(cl));
//
//		gsonBuilder.excludeFieldsWithoutExposeAnnotation();
//		gsonBuilder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
//		gsonBuilder.serializeNulls();
//		gsonBuilder.setPrettyPrinting();
//		gson = gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
//
//		for (Class cl : adapters.keySet()) {
//			adapters.get(cl).SetSkipFields();
//			adapters.get(cl).gson = gson;
//		}
	}

//	protected void AddAdapters(Map<Class, BoAdapter> adapters) {
//		adapters.put(User.class, new UserAdapter());
//		adapters.put(Contractor.class, new ContractorAdapter());
//		adapters.put(Coworker.class, new CoworkerAdapter());
//		adapters.put(Units.class, new UnitsAdapter());
//		adapters.put(Product.class, new ProductAdapter());
//
//		adapters.put(ContractType.class, new ContractTypeAdapter());
//		adapters.put(Contract.class, new ContractAdapter());
//		adapters.put(Currency.class, new CurrencyAdapter());
//		adapters.put(Store.class, new StoreAdapter());
//		adapters.put(ContactInfo.class, new ContactInfoAdapter());
//
//		adapters.put(StrictForm.class, new StrictFormAdapter());
//
//		adapters.put(LotOfProduct.class, new LotOfProductAdapter());
//		adapters.put(DeliveryNote.class, new DeliveryNoteAdapter());
//		adapters.put(DeliveryNoteTablePart.Product.class, new DeliveryNoteTablePartAdapter.Product());
//		adapters.put(DeliveryNoteTablePart.Service.class, new DeliveryNoteTablePartAdapter.Service());
//		adapters.put(DeliveryNoteTablePart.Equipment.class, new DeliveryNoteTablePartAdapter.Equipment());
//
//		adapters.put(Bill.class, new BillAdapter());
//		adapters.put(BillTablePart.Product.class, new BillTablePartAdapter.Product());
//		adapters.put(BillTablePart.Service.class, new BillTablePartAdapter.Service());
//		adapters.put(BillTablePart.Equipment.class, new BillTablePartAdapter.Equipment());
//	}

	@Override
	public void setData() {
		super.setData();
		SetLockBtnIcon();
	}

	@Override
	public List FilterTPAction(String tpName) {
		// тут фильтруем таб часть
		List filtered = new ArrayList<>();
		if (!filterTP.isEmpty()) {
			// TODO: сделать общий класс для фильтра (см. Договоры).
			// Совместить с нечетким.
			filtered.clear();
			String[] values = filterTP.split(" ");
			for (StoreTP tp : (List<StoreTP>) ((StoreDocBO) elementBO).GetTPByName(tpName)) {
				int matchCount = 0;
				for (String value : values)
					if (tp.product != null && tp.product.name.toLowerCase().contains(value))
						matchCount++;
				if (matchCount == values.length)
					filtered.add(tp);
			}
		} else
			filtered = super.FilterTPAction(tpName);

		((DocumentBO) elementBO).EnumTP();

		return filtered;
	}

	protected void setStrictFormTable() {
		// List<StrictForm> strictForms = (List<StrictForm>)
		// elementBO.calcFields.get("strictForms");

		StrictFormModel strictFormModel = new StrictFormModel();
		strictFormModel.setList(((StoreDocBO) elementBO).strictForms);

		strictFormTable.setModel(strictFormModel);
		strictFormTable.setColumnSettings();
		strictFormTable.setRowHeight(UIManager.getInt("Table.rowHeight"));
		strictFormTable.LoadTableSettings();
		strictFormTable.getColumnModel().getColumn(strictFormModel.getColNumByVisibleColumns(BO.fields.DOC_STATE))
				.setCellRenderer(new DocStateRenderer());
		strictFormTable.afterDeleteRowAction.add(new ActionC2() {

			@Override
			public Object Do(Object[] params) {
				ChangeStrictForm();
				return null;
			}
		});

		strictFormTable.revalidate();
		strictFormTable.repaint();
	}

	protected void setStrictForm() {
		StrictForm form = ((StoreDocBO) elementBO).getCurrentStrictForm();
		if (form == null)
			return;

		out_form_batch.setText(form.form_batch);
		out_form_number.setText(form.form_number);

		for (int i = 0; i < out_form_type.getItemCount(); i++)
			if (out_form_type.getItemAt(i).name.equals(form.form_type_name)) {
				out_form_type.setSelectedIndex(i);
				break;
			}
	}

	protected void LoadStrictFormTypes() {
		out_form_type.removeAllItems();
		for (StrictFormType formType : new StrictFormFactory().getTypes()) {
			// TODO: Пропускаем -т.к. новые коды у БСО
			if (!formType.name.equals("ТТН-1"))
				out_form_type.addItem(formType);
		}
	}

	protected void SaveStrictForm(String write_off_type) {
		StrictFormFactory sfFactory = new StrictFormFactory();
		StrictForm form = getCurrentStrictFormFromUI(write_off_type);
		if (form == null)
			return;
		if (!sfFactory.Exists(form)) {
			try {
				sfFactory.Save(form);
			} catch (Exception e) {
				log.ERROR("SaveStrictForm", e);
			}
		} else
			System.out.println("StrictForm Exists");
	}

	protected StrictForm getCurrentStrictFormFromUI(String write_off_type) {
		StrictForm form = new StrictForm();
		form.reg_type = ((StoreDocBO) elementBO).reg_type;
		form.reg_id = ((StoreDocBO) elementBO).id;

		if (out_form_batch == null) {
			// Приходные документы
			String in_form_number = GetStrictFormNumber();

			if ((boolean) Actions.CheckSFAction.Do(in_form_number)) {
				// TODO: Обработка в процессор ML
				String form_batch = in_form_number.substring(0, 2);
				String form_number = in_form_number.substring(2, in_form_number.length());
				form.form_batch = form_batch;
				form.form_number = form_number;
			} else
				return null;

			form.form_type_code = "2";
			form.form_type_name = "ТТН-1";
		} else {
			form.form_batch = out_form_batch.getText();
			form.form_number = out_form_number.getText();
			form.form_type_code = ((StrictFormType) out_form_type.getSelectedItem()).code;
			form.form_type_name = ((StrictFormType) out_form_type.getSelectedItem()).name;
		}
		form.write_off_type = write_off_type;

		if (form.form_batch.equals("") || form.form_number.equals(""))
			return null;

		return form;
	}

	protected String GetStrictFormNumber() {
		return "";
	}

	protected void RuinStrictForm() {
		StrictForm form = getCurrentStrictFormFromUI("Испорчены");
		StrictFormFactory strictFormFactory = new StrictFormFactory();
		strictFormFactory.UpdateWriteOff(form);
		out_form_batch.setText("");
		out_form_number.setText("");
		((StoreDocFactory) factory).LoadStrictForm((StoreDocBO) elementBO);
		setStrictForm();
		setStrictFormTable();
	}

	protected void ChangeStrictForm() {
		SaveStrictForm("Списание");
		if (RegType.isStrictFromDoc(((DocumentBO) elementBO).reg_type)) {
			((StoreDocFactory) factory).LoadStrictForm((StoreDocBO) elementBO);
			setStrictForm();
			setStrictFormTable();
		}
	}

	@Override
	public boolean Save() {
		if (!super.Save())
			return false;
		ChangeStrictForm();
		return true;
	}

	@Override
	public void SetGridFilterFields() {
		super.SetGridFilterFields();
		for (String name : GetTPNames()) {
			StoreTPTable grid = (StoreTPTable) grids.get(name);
			if (grid != null) {
				grid.owner = (BO) contractor.getBO();
				grid.store = (Store) store.getBO();
			}
		}
	}

	@Override
	protected void BeforeTransaction(Doc doc) {
		if (RegType.isInDoc(((StoreDocBO) doc).reg_type)) {
			// if (doc instanceof Invoice) {
			Object[] options = { "Нет", "Да" };
			int n = JOptionPane.showOptionDialog(this, "Обновить партии?", "Проведение",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
			((StoreDocBO) doc).updateLot = (n == 1);
		}
	}

	protected void ContractorSelected() {
		((StoreDocBO) elementBO).doc_contract = ((StoreDocBO) elementBO).contractor.main_contract;
		doc_contract.owner = ((StoreDocBO) elementBO).contractor;
		doc_contract.setEnabled(doc_contract.owner != null);
		doc_contract.onBOSelected(((StoreDocBO) elementBO).doc_contract);
		doc_contract.revalidate();
	}
}