package maxzawalo.c2.full.ui.pc.document;

import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.ui.pc.document.DocListForm;
import maxzawalo.c2.full.bo.document.order.Order;
import maxzawalo.c2.full.data.factory.document.OrderFactory;
import maxzawalo.c2.full.ui.pc.form.AnaliticsForm;
import maxzawalo.c2.full.ui.pc.model.document.OrderTableModel;

public class OrderListForm extends DocListForm<Order, OrderForm> {

	public OrderListForm() {
		factory = new OrderFactory();
		tableModel = new OrderTableModel();
	}

	@Override
	protected void ShowDocTransaction() {
		AnaliticsForm form = new AnaliticsForm(this);
		form.setVisible(true);
		form.setRegistrator((DocumentBO) GetSelectedItem());
	}
}