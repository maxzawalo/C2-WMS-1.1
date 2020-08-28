package maxzawalo.c2.full.data.factory.document;

import maxzawalo.c2.free.data.factory.document.StoreDocFactory;
import maxzawalo.c2.full.bo.document.order.Order;

public class OrderFactory extends StoreDocFactory<Order> {
	@Override
	protected boolean ProductTransaction(Order doc) {
		return RegistryProductMinus(doc);
	}
}