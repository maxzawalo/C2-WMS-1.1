package maxzawalo.c2.full.ui.pc.model;

import javax.swing.JLabel;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.ui.pc.model.BOTableModel;
import maxzawalo.c2.base.ui.pc.model.ColumnSettings;
import maxzawalo.c2.base.ui.pc.renderer.DateTimeCellRenderer;
import maxzawalo.c2.base.ui.pc.renderer.DocStateRenderer;
import maxzawalo.c2.full.bo.StoreDaybook;

public class StoreDaybookTableModel extends BOTableModel<StoreDaybook> {

	public StoreDaybookTableModel() {
		ColumnSettings column = AddVisibleColumns();
		column.name = BO.fields.DOC_STATE;
		column.renderer = new DocStateRenderer();
		column.horizontalAlignment = JLabel.LEFT;

		column = AddVisibleColumns();
		column.caption = "Дата/время";
		column.renderer = new DateTimeCellRenderer();
		column.name = "entry_time";

		column = AddVisibleColumns();
		column.caption = "Ф.И.О. Получателя";
		column.name = "who_recieve";
		column.horizontalAlignment = JLabel.LEFT;

		column = AddVisibleColumns();
		column.caption = "Номенклатура";
		column.name = "product";
		column.horizontalAlignment = JLabel.LEFT;

		column = AddVisibleColumns();
		column.caption = "Цена";
		column.name = "price";

		column = AddVisibleColumns();
		// column.renderer = new CountCellRenderer();
		column.caption = "Количество";
		column.name = "count";

		column = AddVisibleColumns();
		column.name = "link_id";

		column = AddVisibleColumns();
		column.name = BO.fields.ID;

		setColumnCaptions();
	}
}