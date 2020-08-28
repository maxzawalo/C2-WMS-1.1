package maxzawalo.c2.free.ui.pc.document.store;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

import maxzawalo.c2.free.bo.document.bill.Bill;
import maxzawalo.c2.free.data.factory.document.BillFactory;
import maxzawalo.c2.free.ui.pc.form.FreeVersionForm;
import maxzawalo.c2.free.ui.pc.model.document.BillTableModel;

public class BillListFormFree extends StoreDocListForm<Bill, BillFormFree> {

	public BillListFormFree() {
		factory = new BillFactory();
		tableModel = new BillTableModel();

		fromSourcePopup.add(new JMenuItem(new AbstractAction("Расходная") {
			public void actionPerformed(ActionEvent e) {
				CreateDeliveryNoteFromBills();
			}
		}));
		fromSourcePopup.addSeparator();
		fromSourcePopup.add(new JMenuItem(new AbstractAction("Счет") {
			public void actionPerformed(ActionEvent e) {
				CreateBillFromBills();
			}
		}));
	}

	protected void CreateDeliveryNoteFromBills() {
		FreeVersionForm.Full();
	}

	protected void CreateBillFromBills() {
		FreeVersionForm.Full();
	}
}