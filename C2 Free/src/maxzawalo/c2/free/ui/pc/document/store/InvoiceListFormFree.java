package maxzawalo.c2.free.ui.pc.document.store;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import maxzawalo.c2.base.ui.pc.document.DocListForm;
import maxzawalo.c2.free.bo.Settings;
import maxzawalo.c2.free.bo.document.invoice.Invoice;
import maxzawalo.c2.free.data.factory.document.InvoiceFactoryFree;
import maxzawalo.c2.free.ui.pc.form.FreeVersionForm;
import maxzawalo.c2.free.ui.pc.model.document.InvoiceTableModel;

public class InvoiceListFormFree extends DocListForm<Invoice, InvoiceFormFree> {
	public InvoiceListFormFree() {
		btnCommit.setBounds(370, 11, 45, 40);

		JButton btnXml = new JButton("Из XML");
		btnXml.setMargin(new Insets(0, 0, 0, 0));
		btnXml.setVisible(Settings.canCreateEDoc());
		btnXml.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				FromXml();
			}
		});
		btnXml.setBounds(907, 17, 67, 29);
		getContentPane().add(btnXml);
		factory = new InvoiceFactoryFree();
		tableModel = new InvoiceTableModel();
	}

	@Override
	protected void BeforeTransaction(Invoice doc) {
		// TODO: StoreDocListForm
		Object[] options = { "Нет", "Да" };
		int n = JOptionPane.showOptionDialog(this, "Обновить партии?", "Проведение", JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		doc.updateLot = (n == 1);
	}

	protected void FromXml() {
		FreeVersionForm.Full();
	}
}