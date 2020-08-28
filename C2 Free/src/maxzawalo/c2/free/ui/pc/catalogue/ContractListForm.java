package maxzawalo.c2.free.ui.pc.catalogue;

import javax.swing.JFrame;

import maxzawalo.c2.base.ui.pc.catalogue.SlaveCatalogueListForm;
import maxzawalo.c2.free.bo.Contract;
import maxzawalo.c2.free.bo.enums.ContractType;
import maxzawalo.c2.free.data.factory.catalogue.ContractFactory;
import maxzawalo.c2.free.ui.pc.model.catalogue.ContractTableModel;

public class ContractListForm extends SlaveCatalogueListForm<Contract, ContractForm> {
	public ContractListForm() {
		this(null);
	}

	public ContractListForm(JFrame parent) {
		super(parent);
		factory = new ContractFactory();
		tableModel = new ContractTableModel();
	}

	@Override
	public void Search() {
		// Чтобы не фильтровалось
		searchData = "";
		((ContractFactory) factory).filteredType = (ContractType) filter.get(Contract.fields.CONTRACT_TYPE);
		super.Search();
	}
}