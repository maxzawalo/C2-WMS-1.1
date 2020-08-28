package maxzawalo.c2.free.ui.pc.document.bank;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

import maxzawalo.c2.base.Actions;
import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.base.ui.pc.controls.KeyPressedTable;
import maxzawalo.c2.base.ui.pc.controls.PopupPanelTable;
import maxzawalo.c2.base.ui.pc.controls.Tab;
import maxzawalo.c2.base.ui.pc.document.DocForm;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.bo.bank.BankDocBO;
import maxzawalo.c2.free.data.factory.document.BankDocFactory;
import maxzawalo.c2.free.ui.pc.control.BankTPTable;
import maxzawalo.c2.free.ui.pc.form.FreeVersionForm;

public class BankDocForm<Doc, Item> extends DocForm<Doc, Item> {
	protected Tab tabPayment;

	public BankDocForm() {
		this(null);
	}

	protected void FreeTimeLimit() {
		// FreeVersionForm.Limit();
	}

	public BankDocForm(JDialog parent) {
		super(parent);
		FreeTimeLimit();
		factory = new BankDocFactory<Doc>();

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
		topPanel.add(contractor);
		contractor.afterBOSelected.add(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SetGridFilterFields();
			}
		});

		tabPayment = new Tab();
		tabPayment.setLayout(null);
		CreateTPGrid(tabPayment, BankDocBO.fields.TablePartPayment);

		tabbedPane.addTab("Расчеты", null, tabPayment, null);
	}

	public BankTPTable CreateTPGrid(Tab tab, String tablePartName) {
		BankTPTable grid = new BankTPTable();
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
		// LotOfProductListFormFree selectForm = CreateSelectForm();
		// selectForm.itemT = ((DocumentBO)
		// elementBO).GetTypeTPByName(tablePartName);
		// selectForm.setDocDate(DocDate.getDate());
		// selectForm.setStore(((StoreDocBO) elementBO).store);
		// selectForm.setDoc((StoreDocBO) elementBO);
		// // if (selectForm.forInvoice)
		// // selectForm.delivery = ((Invoice) elementBO).delivery;
		// selectForm.selectMode = true;
		// selectForm.Search();
		// selectForm.showDialog();
	}

	// protected LotOfProductListFormFree CreateSelectForm() {
	// return new LotOfProductListFormFree(BankDocForm.this, BankDocForm.this);
	// }

	@Override
	protected void ShowTransactions() {
		if (Actions.ShowDocTransactions == null)
			FreeVersionForm.Full();
		else
			Actions.ShowDocTransactions.Do(this, (DocumentBO) this.elementBO);
	}

	@Override
	protected void RefreshTP() {
		for (String name : GetTPNames()) {
			KeyPressedTable grid = grids.get(name);
			if (grid != null)
				grid.Refresh();
		}
	}

	@Override
	public void SetGridFilterFields() {
		super.SetGridFilterFields();
		for (String name : GetTPNames()) {
			PopupPanelTable grid = (PopupPanelTable) grids.get(name);
			if (grid != null) {
				grid.owner = (BO) contractor.getBO();
				// grid.store = (Store) store.getBO();
			}
		}
	}

	@Override
	protected void TPChanged() {
		((DocumentBO) elementBO).EnumTP();
		// TODO: для всех табчастей
		((BankDocBO) elementBO).CalcTotal();
		super.TPChanged();
	}

}
