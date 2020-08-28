package maxzawalo.c2.free.ui.pc.model.document.bank;

import javax.swing.JLabel;

import maxzawalo.c2.base.ui.pc.model.ColumnSettings;
import maxzawalo.c2.free.bo.bank.BankTP;
import maxzawalo.c2.free.bo.document.writeoffmoney.WriteOffMoneyTablePart;

public class WriteOffMoneyTablePartModel extends BankTPModel<WriteOffMoneyTablePart.Payment> {
	public WriteOffMoneyTablePartModel() {
		// visibleColumns.clear();

		ColumnSettings column = AddVisibleColumns();
		column.caption = "Договор";
		column.name = BankTP.fields.CONTRACT;
		column.horizontalAlignment = JLabel.LEFT;

		column = AddVisibleColumns();
		column.name = BankTP.fields.SUM;
		column.horizontalAlignment = JLabel.RIGHT;

		column = AddVisibleColumns();
		column.name = BankTP.fields.RATE_VAT;
		column.horizontalAlignment = JLabel.RIGHT;

		column = AddVisibleColumns();
		column.name = BankTP.fields.SUM_VAT;
		column.horizontalAlignment = JLabel.RIGHT;

		// column = AddVisibleColumns();
		// column.name = TablePart.Payment.fields.SERVICE_SUM;
		// column.horizontalAlignment = JLabel.RIGHT;
		//
		// column = AddVisibleColumns();
		// column.name = Payment.fields.BILL;
		// column.horizontalAlignment = JLabel.RIGHT;

		setColumnCaptions();
	}
}