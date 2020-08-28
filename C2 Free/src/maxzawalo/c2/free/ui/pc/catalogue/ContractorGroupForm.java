package maxzawalo.c2.free.ui.pc.catalogue;

import javax.swing.JFrame;

import maxzawalo.c2.base.ui.pc.catalogue.CatalogueGroupForm;
import maxzawalo.c2.free.bo.Contractor;

public class ContractorGroupForm extends CatalogueGroupForm<Contractor> {
	public ContractorGroupForm() {
		this(null);
	}

	public ContractorGroupForm(JFrame parent) {
		super(parent);
	}	
}