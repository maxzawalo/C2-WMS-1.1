package maxzawalo.c2.free.ui.pc.model.document;

import javax.swing.JLabel;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.ui.pc.model.ColumnSettings;
import maxzawalo.c2.base.ui.pc.model.DocTableModel;
import maxzawalo.c2.free.bo.store.StoreDocBO;

public class StoreDocTableModel<T> extends DocTableModel<T> {

	public StoreDocTableModel() {
		ColumnSettings column = AddVisibleColumns();
		column.name = DocumentBO.fields.CONTRACTOR;
		column.caption = "Контрагент";
		column.horizontalAlignment = JLabel.LEFT;
		column.to_string_js = "contractor.name";

		column = AddVisibleColumns();
		column.name = StoreDocBO.fields.STORE;
		column.caption = "Склад";
		column.horizontalAlignment = JLabel.LEFT;
		column.to_string_js = "store.name";

		column = AddVisibleColumns();
		column.caption = "Сумма";
		column.name = StoreDocBO.fields.ShowTotalSum;

		column = AddVisibleColumns();
		column.caption = "НДС";
		column.name = StoreDocBO.fields.ShowTotalVat;

		column = AddVisibleColumns();
		column.caption = "Договор";
		column.name = "doc_contract";
		column.to_string_js = "doc_contract.name";

		column = AddVisibleColumns();
		column.name = "doc_currency";
		column.to_string_js = "doc_currency.name";

		column = AddVisibleColumns();
		column.name = BO.fields.CODE;

		column = AddVisibleColumns();
		column.name = BO.fields.ID;

		setColumnCaptions();
	}
}