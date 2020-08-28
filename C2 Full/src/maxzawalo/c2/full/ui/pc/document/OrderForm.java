package maxzawalo.c2.full.ui.pc.document;

import javax.swing.JDialog;

import maxzawalo.c2.free.ui.pc.catalogue.LotOfProductListFormFree;
import maxzawalo.c2.free.ui.pc.document.store.StoreDocForm;
import maxzawalo.c2.full.bo.document.order.Order;
import maxzawalo.c2.full.bo.document.order.OrderTablePart;
import maxzawalo.c2.full.data.factory.document.OrderFactory;
import maxzawalo.c2.full.ui.pc.catalogue.LotOfProductListFormFull;
import maxzawalo.c2.full.ui.pc.model.document.OrderTablePartModel;

public class OrderForm extends StoreDocForm<Order, OrderTablePart.Product> {
	@Override
	protected void FreeTimeLimit() {
		// в полной версии отключаем
	}

	public OrderForm() {
		this(null);
	}

	public OrderForm(JDialog parent) {
		super(parent);
		setBounds(0, 0, 1000, 700);
		factory = new OrderFactory();
		for (String name : GetTPNames())
			tablePartModels.put(name, new OrderTablePartModel());
	}

	@Override
	public boolean Print() {
		if (!super.Print())
			return false;
		// Xlsx.PrintDoc(elementBO, elementBO.TablePartProduct, new
		// BillReporter());

		return true;
	}

	@Override
	protected LotOfProductListFormFree CreateSelectForm() {
		return new LotOfProductListFormFull(this, this);
	}
}