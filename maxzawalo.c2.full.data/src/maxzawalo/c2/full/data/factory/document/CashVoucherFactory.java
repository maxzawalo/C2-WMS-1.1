package maxzawalo.c2.full.data.factory.document;

import java.util.List;

import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.free.bo.store.StoreTP;
import maxzawalo.c2.free.data.factory.document.StoreDocFactory;
import maxzawalo.c2.full.bo.document.cashvoucher.CashVoucher;

public class CashVoucherFactory extends StoreDocFactory<CashVoucher> {
	@Override
	protected boolean ProductTransaction(CashVoucher doc) {
		return RegistryProductMinus(doc);
	}

	@Override
	protected boolean AccTransaction(CashVoucher doc) {
		try {
			for (StoreTP tp : (List<StoreTP>) doc.TablePartProduct)
				AccProductExpenseTransaction(doc, tp, true, false);
			// for (StoreTP tp : (List<StoreTP>) doc.TablePartService)
			// AccProductExpenseTransaction(doc, tp, false, true);
		} catch (Exception e) {
			log.ERROR("AccTransaction", e);
			Console.I().ERROR(getClass(), "AccTransaction", e.getMessage());
			return false;
		}
		return true;
	}
}