package maxzawalo.c2.free.ui.pc.model.catalogue;

import javax.swing.JLabel;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.CatalogueBO;
import maxzawalo.c2.base.ui.pc.model.BOTableModel;
import maxzawalo.c2.base.ui.pc.model.ColumnSettings;
import maxzawalo.c2.free.bo.Units;

public class UnitsTableModel extends BOTableModel<Units> {

	public UnitsTableModel() {
		ColumnSettings column = AddVisibleColumns();
		column.horizontalAlignment = JLabel.CENTER;
		column.name = CatalogueBO.fields.NAME;
		
		column = AddVisibleColumns();		
		column.horizontalAlignment = JLabel.LEFT;
		column.name = CatalogueBO.fields.FULL_NAME;		

		column = AddVisibleColumns();
		column.name = BO.fields.CODE;
		
		column = AddVisibleColumns();
		column.name = BO.fields.ID;

		setColumnCaptions();
	}
}