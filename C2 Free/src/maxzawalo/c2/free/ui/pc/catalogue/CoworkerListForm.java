package maxzawalo.c2.free.ui.pc.catalogue;

import maxzawalo.c2.base.bo.Coworker;
import maxzawalo.c2.base.data.factory.CoworkerFactory;
import maxzawalo.c2.base.ui.pc.catalogue.CatalogueListForm;
import maxzawalo.c2.free.ui.pc.model.catalogue.CoworkerTableModel;

public class CoworkerListForm extends CatalogueListForm<Coworker, CoworkerForm> {
	public CoworkerListForm() {
		factory = new CoworkerFactory();
		tableModel = new CoworkerTableModel();
	}
}