package maxzawalo.c2.full.ui.pc.document;

import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.free.bo.Settings;
import maxzawalo.c2.free.ui.pc.document.store.StoreDocListForm;
import maxzawalo.c2.full.bo.document.remaining_stock.RemainingStock;
import maxzawalo.c2.full.data.factory.document.RemainingStockFactory;
import maxzawalo.c2.full.ui.pc.form.AnaliticsForm;
import maxzawalo.c2.full.ui.pc.model.document.TakeIntoInventoryTableModel;

public class TakeIntoInventoryListForm extends StoreDocListForm<RemainingStock, TakeIntoInventoryForm> {
	public TakeIntoInventoryListForm() {
		// Можем провести только на сервере
		btnCommit.setVisible(Settings.isServer());
		factory = new RemainingStockFactory();
		tableModel = new TakeIntoInventoryTableModel();
	}
	
	@Override
	protected void ShowDocTransaction() {
		AnaliticsForm form = new AnaliticsForm(this);
		form.setVisible(true);
		form.setRegistrator((DocumentBO) GetSelectedItem());
	}
}