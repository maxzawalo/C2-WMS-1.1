package maxzawalo.c2.full.bo.document.remaining_stock;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.bo.registry.RegType;
import maxzawalo.c2.free.bo.store.StoreDocBO;

@BoField(caption = "Оприходование товара")
@DatabaseTable(tableName = "remaining_stock")
public class RemainingStock extends StoreDocBO<RemainingStock> {
	public static class fields {
		public static final String IS_REMAINING = "is_remaining";
	}

	@BoField(caption = "Остатки")
	@DatabaseField(index = true, columnName = fields.IS_REMAINING, defaultValue = "0")
	public boolean is_remaining = false;

	public RemainingStock() {
		reg_type = RegType.RemainingStock;
	}

	@Override
	protected void setTpTypes() {
		itemProductT = RemainingStockTablePart.Product.class;
		// itemServiceT = RemainingStockTablePart.Service.class;
		// itemEquipmentT = RemainingStockTablePart.Equipment.class;
	}

	@Override
	public boolean CheckDoc() {
		return true;
	}
}