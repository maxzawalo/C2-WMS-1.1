package maxzawalo.c2.free.data.factory.document;

import java.util.List;

import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.free.bo.document.deliverynote.DeliveryNote;
import maxzawalo.c2.free.bo.store.StoreTP;

public class DeliveryNoteFactory extends StoreDocFactory<DeliveryNote> {
	// TODO: StoreDocFactoryWithStrict
	@Override
	protected boolean ProductTransaction(DeliveryNote doc) {
		return RegistryProductMinus(doc);
	}

	@Override
	protected boolean AccTransaction(DeliveryNote doc) {
		try {
			for (StoreTP tp : (List<StoreTP>) doc.TablePartProduct)
				AccProductExpenseTransaction(doc, tp, false, false);

			for (StoreTP tp : (List<StoreTP>) doc.TablePartService)
				AccProductExpenseTransaction(doc, tp, false, true);
		} catch (Exception e) {
			log.ERROR("AccTransaction", e);
			Console.I().ERROR(getClass(), "AccTransaction", e.getMessage());
			return false;
		}
		return true;
	}
}