package maxzawalo.c2.free.bo.comparator;

import java.util.Comparator;

import maxzawalo.c2.free.bo.registry.RegistryProduct;

public class RegistryProductComparatorManual implements Comparator<RegistryProduct> {

	@Override
	public int compare(RegistryProduct p1, RegistryProduct p2) {
		// Сначала по продукту
		if (p1.product.id > p2.product.id) {
			return 1;
		}
		if (p1.product.id < p2.product.id) {
			return -1;
		}

		// Потом по партии
		if (p1.lotOfProduct.id > p2.lotOfProduct.id) {
			return 1;
		}
		if (p1.lotOfProduct.id < p2.lotOfProduct.id) {
			return -1;
		}
		return 0;
	}

	public boolean equals(Object obj) {
		
		return super.equals(obj);
	}
}