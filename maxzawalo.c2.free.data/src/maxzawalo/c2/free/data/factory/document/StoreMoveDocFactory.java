package maxzawalo.c2.free.data.factory.document;

import maxzawalo.c2.free.bo.store.StoreDocBO;

public class StoreMoveDocFactory<Doc> extends StoreDocFactory<Doc> {
	@Override
	protected boolean ProductTransaction(Doc doc) {
		try {
			// TODO: full transaction
			if (RegistryProductMinus((StoreDocBO) doc))
				return RegistryProductPlus((StoreDocBO) doc);
		} catch (Exception e) {
			log.ERROR("TransactionBody", e);
		}
		return false;
	}
}