package maxzawalo.c2.full.data.factory.document;

import java.util.List;

import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.free.accounting.acc.Acc_18_3;
import maxzawalo.c2.free.accounting.acc.Acc_41_1;
import maxzawalo.c2.free.accounting.acc.Acc_60_1_1;
import maxzawalo.c2.free.bo.registry.AccAcc;
import maxzawalo.c2.free.bo.store.StoreTP;
import maxzawalo.c2.free.data.factory.document.StoreDocFactory;
import maxzawalo.c2.full.bo.document.return_of_goods.ReturnOfGoods;

public class ReturnOfGoodsFactory extends StoreDocFactory<ReturnOfGoods> {
	@Override
	protected boolean ProductTransaction(ReturnOfGoods doc) {
		return RegistryProductMinus(doc);
	}

	@Override
	protected boolean AccTransaction(ReturnOfGoods doc) {
		try {
			for (StoreTP tp : (List<StoreTP>) doc.TablePartProduct) {
				// Сумма
				AccAcc acc_60_1_1 = new Acc_60_1_1();
				acc_60_1_1.SubCount1 = doc.contractor;
				acc_60_1_1.SubCount2 = doc.doc_contract;
				// typeSubCount3 = Поступление и оплата ТМЦ
				acc_60_1_1.toDebit(tp.sum);
				AccAcc acc_41_1 = new Acc_41_1();
				acc_41_1.SubCount1 = tp.product;
				acc_41_1.SubCount2 = tp.lotOfProduct;
				acc_41_1.SubCount3 = doc.store;
				acc_41_1.toCredit(tp.sum, tp.count);
				AccRecord(doc, acc_60_1_1, acc_41_1);

				// НДС
				acc_60_1_1 = new Acc_60_1_1();
				acc_60_1_1.SubCount1 = doc.contractor;
				acc_60_1_1.SubCount2 = doc.doc_contract;
				// typeSubCount3 = Поступление и оплата ТМЦ
				acc_60_1_1.toDebit(tp.sumVat);
				AccAcc acc_18_3 = new Acc_18_3();
				acc_18_3.SubCount1 = doc.contractor;
				// typeSubCount2 = Поступление и оплата ТМЦ
				acc_18_3.SubCount3 = tp.product;
				acc_18_3.toCredit(tp.sumVat);
				AccRecord(doc, acc_60_1_1, acc_18_3);
			}
		} catch (Exception e) {
			log.ERROR("AccTransaction", e);
			Console.I().ERROR(getClass(), "AccTransaction", e.getMessage());
			return false;
		}
		return true;
	}
}