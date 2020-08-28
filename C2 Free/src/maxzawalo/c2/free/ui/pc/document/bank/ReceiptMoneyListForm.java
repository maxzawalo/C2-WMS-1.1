package maxzawalo.c2.free.ui.pc.document.bank;

import maxzawalo.c2.free.bo.document.receiptmoney.ReceiptMoney;
import maxzawalo.c2.free.data.factory.document.ReceiptMoneyFactory;
import maxzawalo.c2.free.ui.pc.model.document.bank.ReceiptMoneyTableModel;

public class ReceiptMoneyListForm extends BankDocListForm<ReceiptMoney, ReceiptMoneyForm> {
	public ReceiptMoneyListForm() {
		btnCommit.setBounds(370, 11, 45, 40);

		factory = new ReceiptMoneyFactory();
		tableModel = new ReceiptMoneyTableModel();
	}
}