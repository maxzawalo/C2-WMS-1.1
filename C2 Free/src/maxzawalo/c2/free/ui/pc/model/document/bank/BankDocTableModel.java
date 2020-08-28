package maxzawalo.c2.free.ui.pc.model.document.bank;

import javax.swing.JLabel;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.ui.pc.model.ColumnSettings;
import maxzawalo.c2.base.ui.pc.model.DocTableModel;
import maxzawalo.c2.free.bo.bank.BankDocBO;

public class BankDocTableModel<T> extends DocTableModel<T> {

	public BankDocTableModel() {
		ColumnSettings column = AddVisibleColumns();
		column.name = DocumentBO.fields.CONTRACTOR;
		column.caption = "Контрагент";
		column.horizontalAlignment = JLabel.LEFT;

		column = AddVisibleColumns();
		column.caption = "Сумма";
		column.name = BankDocBO.fields.ShowTotalSum;

		column = AddVisibleColumns();
		column.caption = "НДС";
		column.name = BankDocBO.fields.ShowTotalVat;

		column = AddVisibleColumns();
		column.name = BO.fields.CODE;

		column = AddVisibleColumns();
		column.name = BO.fields.ID;

		setColumnCaptions();
	}
}