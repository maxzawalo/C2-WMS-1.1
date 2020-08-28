package maxzawalo.c2.free.data.json.adapter;

import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.free.bo.Contract;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.data.json.SlaveCatalogueAdapter;

public class ContractAdapter extends SlaveCatalogueAdapter<Contract, Contractor> {
	public ContractAdapter() {
		replaces.add(new ReplacedField("Дата", DocumentBO.fields.DOC_DATE));
		replaces.add(new ReplacedField("Номер", "number"));
		replaces.add(new ReplacedField("ВалютаУИ", "doc_currency"));
		replaces.add(new ReplacedField("ВидДоговора", "contract_type"));

		//TODO: 
		skipFields.add("bill");
	}
}