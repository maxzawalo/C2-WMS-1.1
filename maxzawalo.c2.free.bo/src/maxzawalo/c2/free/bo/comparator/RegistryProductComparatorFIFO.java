package maxzawalo.c2.free.bo.comparator;

import java.util.Comparator;

import maxzawalo.c2.free.bo.registry.RegistryProduct;

public class RegistryProductComparatorFIFO implements Comparator<RegistryProduct> {

	@Override
	public int compare(RegistryProduct p1, RegistryProduct p2) {
		// Сначала по продукту
		if (p1.product.id > p2.product.id) {
			return 1;
		}
		if (p1.product.id < p2.product.id) {
			return -1;
		}

		if (p1.lotOfProduct.doc == null || p1.lotOfProduct.doc.DocDate == null || p2.lotOfProduct.doc == null || p2.lotOfProduct.doc.DocDate == null) {
			System.out.print("");
		}

		// Потом по дате партии
		if (p1.lotOfProduct.doc.DocDate.getTime() > p2.lotOfProduct.doc.DocDate.getTime()) {
			return 1;
		}
		if (p1.lotOfProduct.doc.DocDate.getTime() < p2.lotOfProduct.doc.DocDate.getTime()) {
			return -1;
		}

		// Потом по партии(id)
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