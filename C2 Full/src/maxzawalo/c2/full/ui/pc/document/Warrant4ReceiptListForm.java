package maxzawalo.c2.full.ui.pc.document;

import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.free.ui.pc.document.store.StoreDocListForm;
import maxzawalo.c2.full.bo.document.warrant_4_receipt.Warrant4Receipt;
import maxzawalo.c2.full.data.factory.document.Warrant4ReceiptFactory;
import maxzawalo.c2.full.ui.pc.form.AnaliticsForm;
import maxzawalo.c2.full.ui.pc.model.document.Warrant4ReceiptTableModel;

public class Warrant4ReceiptListForm extends StoreDocListForm<Warrant4Receipt, Warrant4ReceiptForm> {
	public Warrant4ReceiptListForm() {
		btnCommit.setBounds(370, 11, 45, 40);
		factory = new Warrant4ReceiptFactory();
		tableModel = new Warrant4ReceiptTableModel();
	}
	
	@Override
	protected void ShowDocTransaction() {
		AnaliticsForm form = new AnaliticsForm(this);
		form.setVisible(true);
		form.setRegistrator((DocumentBO) GetSelectedItem());
	}
}