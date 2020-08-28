package maxzawalo.c2.free.ui.pc.document.store;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

import maxzawalo.c2.free.bo.document.deliverynote.DeliveryNote;
import maxzawalo.c2.free.data.factory.document.DeliveryNoteFactory;
import maxzawalo.c2.free.ui.pc.form.FreeVersionForm;
import maxzawalo.c2.free.ui.pc.model.document.DeliveryNoteTableModel;

public class DeliveryNoteListFormFree extends StoreDocListForm<DeliveryNote, DeliveryNoteFormFree> {
	// TODO: super
	public DeliveryNoteListFormFree() {
		factory = new DeliveryNoteFactory();
//		factory.SetConsole(toConsole);
		tableModel = new DeliveryNoteTableModel();

		fromSourcePopup.add(new JMenuItem(new AbstractAction("Приходная") {
			public void actionPerformed(ActionEvent e) {
				CreateInvoiceFromDeliveryNote();
			}
		}));
		fromSourcePopup.addSeparator();
	}

	protected void CreateInvoiceFromDeliveryNote() {
		FreeVersionForm.Full();
	}
}