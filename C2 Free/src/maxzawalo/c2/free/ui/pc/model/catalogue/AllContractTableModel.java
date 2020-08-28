package maxzawalo.c2.free.ui.pc.model.catalogue;

import javax.swing.JLabel;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.bo.SlaveCatalogueBO;
import maxzawalo.c2.base.ui.pc.model.BOTableModel;
import maxzawalo.c2.base.ui.pc.model.ColumnSettings;
import maxzawalo.c2.base.ui.pc.renderer.CheckBoxRenderer;
import maxzawalo.c2.base.ui.pc.renderer.DateCellRenderer;
import maxzawalo.c2.base.ui.pc.renderer.DocStateRenderer;
import maxzawalo.c2.base.ui.pc.renderer.LockStateRenderer;
import maxzawalo.c2.free.bo.Contract;

public class AllContractTableModel extends BOTableModel<Contract> {

	public AllContractTableModel() {
		ColumnSettings column = AddVisibleColumns();
		column.name = BO.fields.LOCKED_BY;
		column.caption = " ";
		column.renderer = new LockStateRenderer();
		column.horizontalAlignment = JLabel.CENTER;

		column = AddVisibleColumns();
		column.name = BO.fields.DOC_STATE;
		column.renderer = new DocStateRenderer();

		column = AddVisibleColumns();
		column.caption = "Вернулся";
		column.name = Contract.fields.RETURN_WITH_SIGN;
		column.renderer = new CheckBoxRenderer();

		column = AddVisibleColumns();
		column.caption = "Контрагент";
		column.name = SlaveCatalogueBO.fields.OWNER;
		column.horizontalAlignment = JLabel.LEFT;

		column = AddVisibleColumns();
		column.caption = "Дата";
		column.renderer = new DateCellRenderer();
		column.name = DocumentBO.fields.DOC_DATE;

		column = AddVisibleColumns();
		column.caption = "Номер";
		column.name = Contract.fields.NUMBER;

		column = AddVisibleColumns();
		column.caption = "Вид договора";
		column.name = Contract.fields.CONTRACT_TYPE;
		column.horizontalAlignment = JLabel.LEFT;

		column = AddVisibleColumns();
		column.caption = "Валюта";
		column.name = Contract.fields.DOC_CURRENCY;

		column = AddVisibleColumns();
		column.name = BO.fields.CODE;

		column = AddVisibleColumns();
		column.name = BO.fields.ID;

		setColumnCaptions();
	}
}