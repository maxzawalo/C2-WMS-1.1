package maxzawalo.c2.full.data.factory.document;

import java.util.List;

import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.free.accounting.acc.Acc_41_1;
import maxzawalo.c2.free.accounting.acc.Acc_90_1_1;
import maxzawalo.c2.free.bo.registry.AccAcc;
import maxzawalo.c2.free.bo.store.StoreTP;
import maxzawalo.c2.free.data.factory.document.StoreDocFactory;
import maxzawalo.c2.full.bo.document.write_off_product.WriteOffProduct;

public class WriteOffProductFactory extends StoreDocFactory<WriteOffProduct> {
	@Override
	protected boolean ProductTransaction(WriteOffProduct doc) {
		return RegistryProductMinus(doc);
	}

	@Override
	protected boolean AccTransaction(WriteOffProduct doc) {
		try {
			for (StoreTP tp : (List<StoreTP>) doc.TablePartProduct) {
				AccAcc acc_90_1_1 = new Acc_90_1_1();
				acc_90_1_1.toDebit(tp.sum);
				// TODO: 10.6 для материалов
				AccAcc acc_41_1 = new Acc_41_1();
				acc_41_1.SubCount1 = tp.product;
				acc_41_1.SubCount2 = tp.lotOfProduct;
				acc_41_1.SubCount3 = doc.store;
				acc_41_1.toCredit(tp.sum, tp.count);
				AccRecord(doc, acc_90_1_1, acc_41_1);
			}
		} catch (Exception e) {
			log.ERROR("AccTransaction", e);
			Console.I().ERROR(getClass(), "AccTransaction", e.getMessage());
			return false;
		}
		return true;
	}
}