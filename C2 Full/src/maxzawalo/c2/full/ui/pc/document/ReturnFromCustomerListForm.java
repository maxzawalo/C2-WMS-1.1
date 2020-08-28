package maxzawalo.c2.full.ui.pc.document;

import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.free.ui.pc.document.store.StoreDocListForm;
import maxzawalo.c2.full.bo.document.return_from_customer.ReturnFromCustomer;
import maxzawalo.c2.full.data.factory.document.ReturnFromCustomerFactory;
import maxzawalo.c2.full.ui.pc.form.AnaliticsForm;
import maxzawalo.c2.full.ui.pc.model.document.ReturnFromCustomerTableModel;

public class ReturnFromCustomerListForm extends StoreDocListForm<ReturnFromCustomer, ReturnFromCustomerForm> {
	public ReturnFromCustomerListForm() {
		btnCommit.setBounds(370, 11, 45, 40);
		factory = new ReturnFromCustomerFactory();
		tableModel = new ReturnFromCustomerTableModel();
	}

	@Override
	protected void ShowDocTransaction() {
		AnaliticsForm form = new AnaliticsForm(this);
		form.setVisible(true);
		form.setRegistrator((DocumentBO) GetSelectedItem());
	}
}