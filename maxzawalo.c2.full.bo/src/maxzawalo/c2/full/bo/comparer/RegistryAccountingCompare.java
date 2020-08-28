package maxzawalo.c2.full.bo.comparer;

import maxzawalo.c2.base.interfaces.CompareT;
import maxzawalo.c2.full.bo.registry.RegistryAccounting;

public class RegistryAccountingCompare implements CompareT<RegistryAccounting> {

	@Override
	public boolean Do(RegistryAccounting item1, RegistryAccounting item2) {
		Boolean res = false;
		// (item1.reg_date.getTime() == item2.reg_date.getTime() &&
		// item1.account.equals(item2.account)
		// && item1.sum == item2.sum && item1.contractor.id ==
		// item2.contractor.id);

		// if (res) {
		// System.out.println(item1.account);
		// }

		return res;
	}


	// public static CompareT RegistryAccountingCompare = new
	// CompareT<RegistryAccounting>() {
	// public boolean Do(RegistryAccounting item1, RegistryAccounting item2) {
	// Boolean res = false;
	// // (item1.reg_date.getTime() == item2.reg_date.getTime() &&
	// // item1.account.equals(item2.account)
	// // && item1.sum == item2.sum && item1.contractor.id ==
	// // item2.contractor.id);
	//
	// // if (res) {
	// // System.out.println(item1.account);
	// // }
	//
	// return res;
	// }
	// };
	
}