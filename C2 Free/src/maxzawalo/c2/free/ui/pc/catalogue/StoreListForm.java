package maxzawalo.c2.free.ui.pc.catalogue;

import maxzawalo.c2.base.ui.pc.catalogue.CatalogueListForm;
import maxzawalo.c2.free.bo.Store;
import maxzawalo.c2.free.ui.pc.model.catalogue.StoreTableModel;

public class StoreListForm extends CatalogueListForm<Store, StoreForm> {
	public StoreListForm() {
		tableModel = new StoreTableModel();
	}
}