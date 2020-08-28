package maxzawalo.c2.free.data.factory.document;

import java.util.List;

import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.free.accounting.acc.Acc_51;
import maxzawalo.c2.free.accounting.acc.Acc_60_1_1;
import maxzawalo.c2.free.bo.bank.BankTP;
import maxzawalo.c2.free.bo.document.writeoffmoney.WriteOffMoney;
import maxzawalo.c2.free.bo.registry.AccAcc;

public class WriteOffMoneyFactory extends BankDocFactory<WriteOffMoney> {

	@Override
	protected boolean AccTransaction(WriteOffMoney doc) {
		try {
			for (BankTP tp : (List<BankTP>) doc.TablePartPayment) {
				// WriteOffMoneyType
				System.out.println(doc.writeoffmoney_type.getEnumById(doc.writeoffmoney_type.id));
				doc.writeoffmoney_type.Check("Прочее списание");

				// TODO: 44.2.1
				AccAcc accDt = new Acc_60_1_1();
				accDt.SubCount1 = doc.contractor;
				accDt.SubCount2 = tp.contract;
				// TODO: Приходные, Списание с р/с. typeSubCount3 = Поступление и оплата ТМЦ
				accDt.toDebit(tp.sum);

				AccAcc acc_51 = new Acc_51();
				acc_51.SubCount1 = doc.bank_account;// BankAccount.class;
				acc_51.SubCount2 = doc.contractor;// Contractor.class;
				acc_51.toCredit(tp.sum);

				AccRecord(doc, accDt, acc_51);
			}

		} catch (Exception e) {
			log.ERROR("AccTransaction", e);
			Console.I().ERROR(getClass(), "AccTransaction", e.getMessage());
			return false;
		}
		return true;
	}
}