package maxzawalo.c2.free.bo.comparator;

import java.util.Comparator;

import maxzawalo.c2.free.bo.LotOfProduct;

public class LotOfProductComparator implements Comparator<LotOfProduct> {
	public int compare(LotOfProduct l1, LotOfProduct l2) {
		//TODO: by product.name
		int result = l1.product.id - l2.product.id;
		if (result != 0) {
			return result;
		}

		result = l1.id - l2.id;
		if (result != 0) {
			return result;
		}

		result = boolToInt(l1.reserve) - boolToInt(l2.reserve);
		if (result != 0) {
			return result;
		}

		return result;
	}

	public int boolToInt(boolean b) {
		return b ? 1 : 0;
	}
}