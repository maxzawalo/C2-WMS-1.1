package maxzawalo.c2.full.bo.document.return_of_goods;

import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.bo.Coworker;
import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.base.bo.registry.RegType;
import maxzawalo.c2.free.bo.store.StoreDocBO;

@BoField(caption = "Возврат поставщику")
public class ReturnOfGoods extends StoreDocBO<ReturnOfGoods> {
	public static class fields {
		public static final String SHIPMENT_MOTIVE = "shipment_motive";
		public static final String SHIPMENT_PERMITED = "shipment_permited_id";
		public static final String SHIPMENT_PRODUCED = "shipment_produced_id";
	}

	@BoField(caption = "Основание отпуска")
	@DatabaseField(index = true, width = 100, columnName = ReturnOfGoods.fields.SHIPMENT_MOTIVE)
	public String shipment_motive = "";

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, columnName = ReturnOfGoods.fields.SHIPMENT_PERMITED)
	public Coworker shipment_permited = (Coworker) User.current.coworker;

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, columnName = ReturnOfGoods.fields.SHIPMENT_PRODUCED)
	public Coworker shipment_produced = (Coworker) User.current.coworker;

	public ReturnOfGoods() {
		reg_type = RegType.ReturnOfGoods;
	}

	@Override
	protected void setTpTypes() {
		itemProductT = ReturnOfGoodsTablePart.Product.class;
		itemServiceT = ReturnOfGoodsTablePart.Service.class;
		itemEquipmentT = ReturnOfGoodsTablePart.Equipment.class;
	}
}