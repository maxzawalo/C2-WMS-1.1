package maxzawalo.c2.full.bo.daybook;

import java.util.Comparator;

import maxzawalo.c2.full.bo.StoreDaybook;

public class StoreDaybookUIComparator implements Comparator<StoreDaybook> {
	@Override
	public int compare(StoreDaybook obj1, StoreDaybook obj2) {
		if (obj1.product.name == null) {
			return -1;
		}
		if (obj2.product.name == null) {
			return 1;
		}
		if (obj1.product.name.equals(obj2.product.name)) {
			return 0;
		}
		return obj1.product.name.compareTo(obj2.product.name);
	}
}