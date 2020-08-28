package maxzawalo.c2.free.bo.document.bill;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.bo.registry.RegType;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.free.bo.store.StoreDocBO;

@BoField(caption = "Счет", type1C = "Документы.СчетНаОплатуПокупателю")
public class Bill extends StoreDocBO<Bill> {
	public Bill() {
		reg_type = RegType.Bill;
	}

	@Override
	protected void setTpTypes() {
		itemProductT = BillTablePart.Product.class;
		itemServiceT = BillTablePart.Service.class;
		itemEquipmentT = BillTablePart.Equipment.class;
	}

	@Override
	public String toString() {

		// return "(" + this.id + ") " + Format.Show(this.DocDate) + " "
		// + ((contractor != null) ? contractor.name : "Контрагент") + "|" +
		// Format.Show(total) + "|"
		// + Format.Show(Format.roundDouble(total * 1.2, 1));

		return "Счет " + this.code + " от " + Format.Show(this.DocDate) + "|"
				+ ((contractor != null) ? contractor.name : "Контрагент");
	}
}