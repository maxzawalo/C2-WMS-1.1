package maxzawalo.c2.free.ui.pc.catalogue;

import javax.swing.JFrame;

import maxzawalo.c2.base.ui.pc.catalogue.SlaveCatalogueListForm;
import maxzawalo.c2.free.bo.Contract;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.data.factory.catalogue.ContractFactory;
import maxzawalo.c2.free.ui.pc.model.catalogue.AllContractTableModel;

public class AllContractListForm extends SlaveCatalogueListForm<Contract, ContractForm> {

	public AllContractListForm() {
		this(null);
	}

	public AllContractListForm(JFrame parent) {
		super(parent);
		btnAdd.setVisible(false);
		factory = new ContractFactory();
		tableModel = new AllContractTableModel();
		loadOwner = true;
	}
	
	@Override
	protected void setSearchContext() {
		searchContext = Contractor.class;
	}
}