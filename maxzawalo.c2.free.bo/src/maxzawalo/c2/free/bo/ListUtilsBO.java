package maxzawalo.c2.free.bo;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.CatalogueBO;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.bo.SlaveCatalogueBO;
import maxzawalo.c2.base.interfaces.CompareT;
import maxzawalo.c2.free.bo.store.StoreTP;

public class ListUtilsBO {
	public static CompareT DocumentBOCompare = new CompareT<DocumentBO>() {
		public boolean Do(DocumentBO item1, DocumentBO item2) {
			// TODO: item2.code.equals("ostatki") убрать
			return (item1.code.equals(item2.code) || item2.code.equals("ostatki"))
					&& (item1.DocDate.getTime() == item2.DocDate.getTime());
		}
	};

	/**
	 * Инкрементальное сравнение при синхронизации. Проверка на существование
	 */
	public static CompareT CatalogueBOCompare = new CompareT<CatalogueBO>() {
		public boolean Do(CatalogueBO item1, CatalogueBO item2) {
			// TODO: Проработать

			if (item1 instanceof StrictForm)
				return (((StrictForm) item1).form_batch.equals(((StrictForm) item2).form_batch)
						&& ((StrictForm) item1).form_number.equals(((StrictForm) item2).form_number));

			if (!item1.uuid.equals(BO.zero_uuid))
				return item1.uuid.equals(item2.uuid);

			if (item1 instanceof SlaveCatalogueBO)
				return ((SlaveCatalogueBO) item1).owner.uuid.equals(((SlaveCatalogueBO) item2).owner.uuid)
						&& item1.name.equals(item2.name);

			return item1.code.equals(item2.code);
		}
	};

	public static CompareT CommonTablePartCompare = new CompareT<StoreTP>() {
		public boolean Do(StoreTP item1, StoreTP item2) {
			// TODO: Lot
			// TODO: && (item1.product.id == item2.product.id);
			// TODO: Учитываем кол-во строк и позицию
			return (item1.doc == item2.doc && item1.count == item2.count && item1.sum == item2.sum);
		}
	};
}