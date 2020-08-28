package maxzawalo.c2.full.ui.pc.document;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.bo.TablePartItem;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.base.ui.pc.controls.KeyPressedTable;
import maxzawalo.c2.base.ui.pc.form.BoListForm;
import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.free.bo.Settings;
import maxzawalo.c2.free.bo.document.deliverynote.DeliveryNote;
import maxzawalo.c2.free.bo.store.StoreTP;
import maxzawalo.c2.free.data.factory.document.DeliveryNoteFactory;
import maxzawalo.c2.free.ui.pc.catalogue.LotOfProductListFormFree;
import maxzawalo.c2.free.ui.pc.document.store.StoreDocForm;
import maxzawalo.c2.full.bo.document.return_from_customer.ReturnFromCustomer;
import maxzawalo.c2.full.bo.document.return_from_customer.ReturnFromCustomerTablePart;
import maxzawalo.c2.full.data.decoder.BarcodeDecoder;
import maxzawalo.c2.full.data.factory.document.ReturnFromCustomerFactory;
import maxzawalo.c2.full.hardware.terminal.Terminal;
import maxzawalo.c2.full.ui.pc.catalogue.LotOfProductListFormFull;
import maxzawalo.c2.full.ui.pc.form.AnaliticsForm;
import maxzawalo.c2.full.ui.pc.model.document.ReturnFromCustomerTablePartModel;

public class ReturnFromCustomerForm extends StoreDocForm<ReturnFromCustomer, ReturnFromCustomerTablePart.Product> {
	BizControlBase in_form_number;

	@Override
	protected void FreeTimeLimit() {
		// в полной версии отключаем
	}

	public ReturnFromCustomerForm() {
		this(null);
	}

	public ReturnFromCustomerForm(JDialog parent) {
		super(parent);

		setBounds(0, 0, 1000, 700);
		store.setLocation(552, 135);
		factory = new ReturnFromCustomerFactory();

		for (String name : GetTPNames())
			tablePartModels.put(name, new ReturnFromCustomerTablePartModel());

		in_form_number = new BizControlBase();
		in_form_number.setFieldName(ReturnFromCustomer.fields.IN_FORM_NUMBER);
		in_form_number.setCaption("Вх. номер");
		in_form_number.setBounds(739, 12, 128, 56);
		topPanel.add(in_form_number);

		BizControlBase in_form_date = new BizControlBase();
		in_form_date.setFieldName(ReturnFromCustomer.fields.IN_FORM_DATE);
		in_form_date.setCaption("Вх. дата");
		in_form_date.setBounds(630, 12, 116, 56);
		topPanel.add(in_form_date);

		JButton button = new JButton("З");
		button.setToolTipText("Заполнить из расходной");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BoListForm selectListForm = new DeliveryNoteListFormFull();
				// selectListForm.Search();
				selectListForm.selectItem(ReturnFromCustomerForm.this, elementBO);
				// selectListForm.setVisible(true);
			}
		});
		button.setBounds(376, 151, 40, 40);
		topPanel.add(button);

		Terminal.Uninit();
		Terminal.Init(Settings.startComPort);
		Terminal.callbacks.add(this);
	}

	@Override
	public boolean Save() {
		return super.Save();
	}

	@Override
	public boolean Print() {
		if (!super.Print())
			return false;
		return true;
	}

	@Override
	public void onBOSelected(BO selectedBO) {
		DeliveryNoteFactory dnFactory = new DeliveryNoteFactory();
		DeliveryNote dn = dnFactory.GetById(selectedBO.id, 0, false);
		dnFactory.LoadTablePart(dn);
		// TODO: для всех TP
		String tpName = "TablePartProduct";
		{
			KeyPressedTable tableProduct = grids.get(tpName);
			StoreTP.AppendTP(dn, elementBO, new ReturnFromCustomerTablePart.Product(), true);
			for (TablePartItem tp : elementBO.GetTPByName(tpName)) {
				((StoreTP) tp).discount = 0;
				((StoreTP) tp).price = ((StoreTP) tp).price_discount_off;
				((StoreTP) tp).onChanged = onTablePartChanged;
			}

			tableProduct.revalidate();
			tableProduct.repaint();
		}
	}

	@Override
	protected LotOfProductListFormFree CreateSelectForm() {
		return new LotOfProductListFormFull(this, this);
	}

	protected void ProcessScanData(String barcode) {
		String[] sfn = BarcodeDecoder.DecodeStrictNumber(barcode);
		if (sfn.length == 0) {
			Console.I().WARN(getClass(), "ProcessScanData", "Штрихкод не является номером БСО");
			return;
		}
		in_form_number.onBOSelected(sfn[0] + sfn[1]);
	}

	@Override
	protected String GetStrictFormNumber() {
		return in_form_number.getText();
	}

	@Override
	protected void ShowTransactions() {
		AnaliticsForm form = new AnaliticsForm(this);
		form.setVisible(true);
		form.setRegistrator((DocumentBO) this.elementBO);
	}
}