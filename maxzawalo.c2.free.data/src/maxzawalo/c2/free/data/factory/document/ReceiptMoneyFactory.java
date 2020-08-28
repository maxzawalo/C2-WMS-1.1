package maxzawalo.c2.free.data.factory.document;

import java.util.List;

import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.free.accounting.acc.Acc_51;
import maxzawalo.c2.free.accounting.acc.Acc_62_1_1;
import maxzawalo.c2.free.bo.bank.BankTP;
import maxzawalo.c2.free.bo.document.receiptmoney.ReceiptMoney;
import maxzawalo.c2.free.bo.registry.AccAcc;

public class ReceiptMoneyFactory extends BankDocFactory<ReceiptMoney> {
	@Override
	protected boolean AccTransaction(ReceiptMoney doc) {
		try {
			for (BankTP tp : (List<BankTP>) doc.TablePartPayment) {
				AccAcc acc_51 = new Acc_51();
				acc_51.SubCount1 = doc.bank_account;// BankAccount.class;
				acc_51.SubCount2 = doc.contractor;// Contractor.class;
				acc_51.toDebit(tp.sum);

				AccAcc acc_62_1_1 = new Acc_62_1_1();
				acc_62_1_1.SubCount1 = doc.contractor;// Contractor.class;
				acc_62_1_1.SubCount2 = tp.contract;// Contract.class;
				// //TODO: Расходные накладные - закрываем последовательно - typeSubCount3 =
				// Отгрузка и оплата ТМЦ
				acc_62_1_1.toCredit(tp.sum);

				AccRecord(doc, acc_51, acc_62_1_1);
			}

		} catch (Exception e) {
			log.ERROR("AccTransaction", e);
			Console.I().ERROR(getClass(), "AccTransaction", e.getMessage());
			return false;
		}
		return true;
	}
}