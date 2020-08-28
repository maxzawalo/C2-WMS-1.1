package maxzawalo.c2.full.bo.daybook;

import java.util.Comparator;

import maxzawalo.c2.full.bo.StoreDaybook;

public class StoreDaybookComparator implements Comparator<StoreDaybook> {
	@Override
	public int compare(StoreDaybook p1, StoreDaybook p2) {
		if (p1.id > p2.id) {
			return 1;
		}
		if (p1.id < p2.id) {
			return -1;
		}
		return 0;
	}
}