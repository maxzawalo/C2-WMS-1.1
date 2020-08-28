package maxzawalo.c2.free.ui.pc.model.document.bank;

import maxzawalo.c2.base.ui.pc.model.TablePartModel;
import maxzawalo.c2.free.bo.Contract;
import maxzawalo.c2.free.bo.bank.BankTP;
import maxzawalo.c2.free.bo.document.bill.Bill;
import maxzawalo.c2.free.bo.document.receiptmoney.ReceiptMoneyTablePart;

public class BankTPModel<Item> extends TablePartModel<Item> {
	@Override
	public boolean isCellEditable(int row, int col) {
		if (IsVisColumn(col, BankTP.fields.CONTRACT)) {
			EditBoCell(row, col, BankTP.fields.CONTRACT);
			return false;
		} else if (IsVisColumn(col, ReceiptMoneyTablePart.Payment.fields.BILL)) {
			EditBoCell(row, col, ReceiptMoneyTablePart.Payment.fields.BILL);
			return false;
		}

		// else if (IsVisColumn(col, StoreTP.fields.PRICE_DISCOUNT_OFF) ||
		// IsVisColumn(col, StoreTP.fields.UNITS)) {
		// return false;
		// }
		return super.isCellEditable(row, col);
	}

	@Override
	protected Class GetVisColumnClass(int col, String colName) {
//		colName = colName.replace("_id", "");
//		visibleColumns.get(col).name.equals(colName);
		switch (colName) {
		case BankTP.fields.CONTRACT:
			return Contract.class;
		case ReceiptMoneyTablePart.Payment.fields.BILL:
			return Bill.class;
		}
		return super.GetVisColumnClass(col, colName);
	}
}