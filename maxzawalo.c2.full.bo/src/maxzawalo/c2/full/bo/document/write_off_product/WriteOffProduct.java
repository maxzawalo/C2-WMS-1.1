package maxzawalo.c2.full.bo.document.write_off_product;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.bo.registry.RegType;
import maxzawalo.c2.free.bo.store.StoreDocBO;

@BoField(caption = "Списание товаров")
public class WriteOffProduct extends StoreDocBO<WriteOffProduct> {

	public WriteOffProduct() {
		reg_type = RegType.WriteOffProduct;
	}

	@Override
	protected void setTpTypes() {
		itemProductT = WriteOffProductTablePart.Product.class;
		itemServiceT = WriteOffProductTablePart.Service.class;
		itemEquipmentT = WriteOffProductTablePart.Equipment.class;
	}

	@Override
	public boolean CheckDoc() {
		return true;
	}
}