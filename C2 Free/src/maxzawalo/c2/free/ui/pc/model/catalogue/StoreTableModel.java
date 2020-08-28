package maxzawalo.c2.free.ui.pc.model.catalogue;

import javax.swing.JLabel;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.CatalogueBO;
import maxzawalo.c2.base.ui.pc.model.BOTableModel;
import maxzawalo.c2.base.ui.pc.model.ColumnSettings;
import maxzawalo.c2.free.bo.Store;

public class StoreTableModel extends BOTableModel<Store> {

	public StoreTableModel() {
		ColumnSettings column = AddVisibleColumns();
		column.horizontalAlignment = JLabel.LEFT;
		column.name = CatalogueBO.fields.NAME;

		column = AddVisibleColumns();
		column.horizontalAlignment = JLabel.LEFT;
		column.name = Store.fields.ADDRESS;

		column = AddVisibleColumns();
		column.name = BO.fields.CODE;

		column = AddVisibleColumns();
		column.name = BO.fields.ID;

		setColumnCaptions();
	}
}