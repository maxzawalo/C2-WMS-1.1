package maxzawalo.c2.full.bo.document.cashvoucher;

import java.util.List;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.bo.registry.RegType;
import maxzawalo.c2.free.bo.store.StoreDocBO;

@BoField(caption = "Чек")
public class CashVoucher extends StoreDocBO<CashVoucher> {
	public CashVoucher() {
		reg_type = RegType.CashVoucher;
		sum_contains_vat = true;
	}

	@Override
	protected void setTpTypes() {
		itemProductT = CashVoucherTablePart.Product.class;
		itemServiceT = CashVoucherTablePart.Service.class;
		itemEquipmentT = CashVoucherTablePart.Equipment.class;
	}

	@Override
	public boolean CheckDoc() {
		return true;
	}

	@Override
	public List<?> getTablePart4Rep() {
		// TODO Auto-generated method stub
		return TablePartProduct;
	}
}