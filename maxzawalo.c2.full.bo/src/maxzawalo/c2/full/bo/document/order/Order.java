package maxzawalo.c2.full.bo.document.order;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.bo.registry.RegType;
import maxzawalo.c2.free.bo.store.StoreDocBO;

@BoField(caption = "Заказ")
public class Order extends StoreDocBO<Order> {
	public Order() {
		reg_type = RegType.Order;
	}

	@Override
	protected void setTpTypes() {
		itemProductT = OrderTablePart.Product.class;
		itemServiceT = OrderTablePart.Service.class;
		itemEquipmentT = OrderTablePart.Equipment.class;
	}

//	@Override
//	public String toString() {
//
//		return "(" + this.id + ") " + Format.Show(this.DocDate) + " "
//				+ ((contractor != null) ? contractor.name : "Контрагент") + "|" + Format.Show(total) + "|"
//				+ Format.Show(Format.roundDouble(total * 1.2, 1));
//	}
}