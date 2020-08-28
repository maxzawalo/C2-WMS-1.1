package maxzawalo.c2.free.ui.pc.document.bank;

import maxzawalo.c2.free.bo.document.writeoffmoney.WriteOffMoney;
import maxzawalo.c2.free.data.factory.document.WriteOffMoneyFactory;
import maxzawalo.c2.free.ui.pc.model.document.bank.WriteOffMoneyTableModel;

public class WriteOffMoneyListForm extends BankDocListForm<WriteOffMoney, WriteOffMoneyForm> {
	public WriteOffMoneyListForm() {
		btnCommit.setBounds(370, 11, 45, 40);

		factory = new WriteOffMoneyFactory();
		tableModel = new WriteOffMoneyTableModel();
	}
}