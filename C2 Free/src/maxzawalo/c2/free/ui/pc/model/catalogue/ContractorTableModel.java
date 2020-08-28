package maxzawalo.c2.free.ui.pc.model.catalogue;

import javax.swing.JLabel;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.CatalogueBO;
import maxzawalo.c2.base.ui.pc.model.BOTableModel;
import maxzawalo.c2.base.ui.pc.model.ColumnSettings;
import maxzawalo.c2.base.ui.pc.renderer.DocStateRenderer;
import maxzawalo.c2.base.ui.pc.renderer.FuzzyCellRenderer;
import maxzawalo.c2.free.bo.Contractor;

public class ContractorTableModel extends BOTableModel<Contractor> {

	public ContractorTableModel() {
		ColumnSettings column = AddVisibleColumns();
		column.name = BO.fields.DOC_STATE;
		column.renderer = new DocStateRenderer();
		
		column = AddVisibleColumns();
		column.horizontalAlignment = JLabel.LEFT;
		column.name = CatalogueBO.fields.NAME;
		column.renderer = new FuzzyCellRenderer();

		column = AddVisibleColumns();
		column.name = Contractor.fields.UNP;

		column = AddVisibleColumns();
		column.name = BO.fields.CODE;

		column = AddVisibleColumns();
		column.name = BO.fields.ID;

		setColumnCaptions();
	}
}