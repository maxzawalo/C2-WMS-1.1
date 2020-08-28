package maxzawalo.c2.free.data.factory.catalogue;

import java.util.ArrayList;
import java.util.List;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.data.ContractNumberGen;
import maxzawalo.c2.base.data.factory.SlaveCatalogueFactory;
import maxzawalo.c2.free.bo.Contract;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.bo.enums.ContractType;

public class ContractFactory extends SlaveCatalogueFactory<Contract, Contractor> {

	public ContractType filteredType;// = new ContractType();

	public ContractFactory() {
		// Добавляем загрузку foreignAutoRefresh = false
		mapper.nonSkipForeign.put("contract_type", ContractType.class);
	}

	@Override
	public List<Contract> GetByOwner(BO owner, boolean loadOwner, String searchData) {
		List<Contract> all = super.GetByOwner(owner, loadOwner, searchData);

		List<Contract> filtered = new ArrayList<>();

		if (filteredType != null) {
			filtered.clear();

			for (Contract c : all)
				if (c.contract_type == null || c.contract_type.id == filteredType.id)
					filtered.add(c);
			all = new ArrayList<>(filtered);
		} else
			filtered = new ArrayList<>(all);

		if (!searchData.trim().equals("")) {

			filtered.clear();

			String[] values = searchData.trim().toLowerCase().split(" ");
			for (Contract c : all) {
				int matchCount = 0;
				for (String value : values)
					if (((Contractor) c.owner) != null && ((Contractor) c.owner).name.toLowerCase().contains(value))
						matchCount++;
				if (matchCount == values.length)
					filtered.add(c);
			}
		} else
			filtered = all;

		return filtered;
	}

	@Override
	protected Contract BeforeSave(Contract bo) throws Exception {
		if (bo.number == null || bo.number.trim().length() == 0)
			bo.number = new ContractNumberGen().GenerateNumber(bo);
		bo.name = "" + bo;
		return bo;
	}
}