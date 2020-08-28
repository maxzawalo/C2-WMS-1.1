package maxzawalo.c2.full.data.factory.document;

import java.util.List;

import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.free.accounting.acc.Acc_000;
import maxzawalo.c2.free.accounting.acc.Acc_41_1;
import maxzawalo.c2.free.accounting.acc.Acc_90_7;
import maxzawalo.c2.free.bo.registry.AccAcc;
import maxzawalo.c2.free.bo.store.StoreTP;
import maxzawalo.c2.free.data.factory.document.StoreDocFactory;
import maxzawalo.c2.full.bo.document.remaining_stock.RemainingStock;

public class RemainingStockFactory extends StoreDocFactory<RemainingStock> {
	@Override
	protected boolean ProductTransaction(RemainingStock doc) {
		return RegistryProductPlus(doc);
	}

	@Override
	protected boolean AccTransaction(RemainingStock doc) {
		try {
			for (StoreTP tp : (List<StoreTP>) doc.TablePartProduct) {
				AccAcc accDt = new Acc_41_1();
				accDt.SubCount1 = tp.product;
				accDt.SubCount2 = tp.lotOfProduct;
				accDt.SubCount3 = doc.store;
				accDt.toDebit(tp.sum, tp.count);

				AccAcc accKt = new Acc_000();
				if (!doc.is_remaining) {
					accKt = new Acc_90_7();
					// Прочие доходы/расходы
				}
				accKt.toCredit(tp.sum);

				AccRecord(doc, accDt, accKt);
			}
		} catch (Exception e) {
			log.ERROR("AccTransaction", e);
			Console.I().ERROR(getClass(), "AccTransaction", e.getMessage());
			return false;
		}
		return true;
	}
}