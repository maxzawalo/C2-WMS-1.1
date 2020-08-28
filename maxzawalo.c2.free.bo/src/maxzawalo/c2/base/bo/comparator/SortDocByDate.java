package maxzawalo.c2.base.bo.comparator;

import java.util.Comparator;

import maxzawalo.c2.base.bo.DocumentBO;

public class SortDocByDate implements Comparator<DocumentBO> {
	public int compare(DocumentBO doc1, DocumentBO doc2) {

		if (doc1.DocDate.getTime() > doc2.DocDate.getTime()) {
			return 1;
		}
		if (doc1.DocDate.getTime() < doc2.DocDate.getTime()) {
			return -1;
		}
		return 0;
	}
}