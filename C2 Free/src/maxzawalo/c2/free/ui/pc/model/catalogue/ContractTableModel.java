package maxzawalo.c2.free.ui.pc.model.catalogue;

import javax.swing.JLabel;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.ui.pc.model.BOTableModel;
import maxzawalo.c2.base.ui.pc.model.ColumnSettings;
import maxzawalo.c2.base.ui.pc.renderer.DateCellRenderer;
import maxzawalo.c2.base.ui.pc.renderer.DocStateRenderer;
import maxzawalo.c2.base.ui.pc.renderer.LockStateRenderer;
import maxzawalo.c2.free.bo.Contract;

public class ContractTableModel extends BOTableModel<Contract> {

	public ContractTableModel() {
		ColumnSettings column = AddVisibleColumns();
		column.name = BO.fields.LOCKED_BY.replace("_id", "");
		column.caption = " ";
		column.renderer = new LockStateRenderer();
		column.horizontalAlignment = JLabel.CENTER;

		column = AddVisibleColumns();
		column.name = BO.fields.DOC_STATE;
		column.renderer = new DocStateRenderer();

		column = AddVisibleColumns();
		column.caption = "Дата";
		column.renderer = new DateCellRenderer();
		column.name = DocumentBO.fields.DOC_DATE;

		column = AddVisibleColumns();
		column.caption = "Номер";
		column.name = Contract.fields.NUMBER;

		column = AddVisibleColumns();
		column.caption = "Вид договора";
		column.name = Contract.fields.CONTRACT_TYPE.replace("_id", "");
		column.horizontalAlignment = JLabel.LEFT;

		column = AddVisibleColumns();
		column.caption = "Валюта";
		column.name = Contract.fields.DOC_CURRENCY.replace("_id", "");

		column = AddVisibleColumns();
		column.name = BO.fields.CODE;

		column = AddVisibleColumns();
		column.name = BO.fields.ID;
		setColumnCaptions();
	}
}