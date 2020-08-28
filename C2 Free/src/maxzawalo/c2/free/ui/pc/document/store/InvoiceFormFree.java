package maxzawalo.c2.free.ui.pc.document.store;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.base.ui.pc.controls.CheckBoxBizControl;
import maxzawalo.c2.base.ui.pc.controls.DateBizControl;
import maxzawalo.c2.free.bo.Settings;
import maxzawalo.c2.free.bo.document.invoice.Invoice;
import maxzawalo.c2.free.bo.document.invoice.InvoiceTablePart;
import maxzawalo.c2.free.data.factory.document.InvoiceFactoryFree;
import maxzawalo.c2.free.ui.pc.form.FreeVersionForm;
import maxzawalo.c2.free.ui.pc.model.document.InvoiceTablePartModel;

public class InvoiceFormFree extends StoreDocForm<Invoice, InvoiceTablePart.Product> {
	protected BizControlBase in_form_number;
	protected JButton btnAddFromSourceDoc;
	protected JButton btnFromClipboard; 

	public InvoiceFormFree() {
		this(null);
	}

	public InvoiceFormFree(JDialog parent) {
		super(parent);
		FreeTimeLimit();
		factory = new InvoiceFactoryFree();
		setBounds(0, 0, 1000, 700);
		store.setLocation(552, 135);
		for (String name : GetTPNames())
			tablePartModels.put(name, new InvoiceTablePartModel());

		in_form_number = new  BizControlBase();
		in_form_number.setFieldName(Invoice.fields.IN_FORM_NUMBER);
		in_form_number.setCaption("Вх. номер");
		in_form_number.setBounds(739, 12, 128, 56);
		topPanel.add(in_form_number);

		BizControlBase in_form_date = new DateBizControl();
		in_form_date.setFieldName(Invoice.fields.IN_FORM_DATE);
		in_form_date.setCaption("Вх. дата");
		in_form_date.setBounds(630, 12, 116, 56);
		topPanel.add(in_form_date);

		BizControlBase delivery = new CheckBoxBizControl();
		delivery.setFieldName(Invoice.fields.DELIVERY);
		delivery.setCaption("Доставка");
		delivery.setBounds(453, 145, 140, 46);
		topPanel.add(delivery);

		btnFromClipboard = new JButton("Б");
		btnFromClipboard.setVisible(Settings.canCreateEDoc());
		btnFromClipboard.setToolTipText("Заполнить из буфера");
		btnFromClipboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				FromClipboard("TablePartProduct");
			}
		});
		btnFromClipboard.setBounds(357, 154, 40, 35);
		topPanel.add(btnFromClipboard);

		btnAddFromSourceDoc = new JButton("Д");
		btnAddFromSourceDoc.setToolTipText("Дополнить");
		btnAddFromSourceDoc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AddFromSourceDoc();
			}
		});
		btnAddFromSourceDoc.setBounds(930, 12, 40, 30);
		topPanel.add(btnAddFromSourceDoc);
	}

	@Override
	public boolean Save() {
		JOptionPane.showMessageDialog(this, "Доставка установлена?", "ВНИМАНИЕ", JOptionPane.WARNING_MESSAGE);
		return super.Save();
	}

	@Override
	public boolean Print() {
		if (!super.Print())
			return false;
		FreeVersionForm.Soon();
		// Xlsx.PrintDoc(elementBO, elementBO.TablePart, new
		// DeliveryNoteReporterTN2());
		return true;
	}

	@Override
	public void Load(int id) {
		super.Load(id);
		System.out.println(elementBO.CalcSumTotal());
	}

	protected void FromClipboard(String tpName) {
		FreeVersionForm.Full();
	}

	public void AddFromSourceDoc() {
		FreeVersionForm.Full();
	}

	@Override
	protected void onFormResized() {
		super.onFormResized();
		btnAddFromSourceDoc.setLocation(getWidth() - btnAddFromSourceDoc.getWidth() - 30, btnAddFromSourceDoc.getY());
	}

	@Override
	protected String GetStrictFormNumber() {
		return in_form_number.getText();
	}
}