package maxzawalo.c2.free.ui.pc.catalogue;

import maxzawalo.c2.base.ui.pc.catalogue.CatalogueListForm;
import maxzawalo.c2.free.bo.Units;
import maxzawalo.c2.free.data.factory.catalogue.UnitsFactory;
import maxzawalo.c2.free.ui.pc.model.catalogue.UnitsTableModel;

public class UnitsListForm extends CatalogueListForm<Units, UnitsForm> {

	public UnitsListForm() {
		factory = new UnitsFactory();
		tableModel = new UnitsTableModel();
	}
}