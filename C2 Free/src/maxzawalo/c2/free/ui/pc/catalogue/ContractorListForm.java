package maxzawalo.c2.free.ui.pc.catalogue;

import javax.swing.JFrame;

import maxzawalo.c2.base.ui.pc.catalogue.CatalogueListForm;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.ui.pc.model.catalogue.ContractorTableModel;

public class ContractorListForm extends CatalogueListForm<Contractor, ContractorForm> {
	public ContractorListForm() {
		this(null);
	}

	public ContractorListForm(JFrame parent) {
		super(parent);
		groupForm = new ContractorGroupForm();
		tableModel = new ContractorTableModel();
		elementBO.fuzzy = true;
	}
}