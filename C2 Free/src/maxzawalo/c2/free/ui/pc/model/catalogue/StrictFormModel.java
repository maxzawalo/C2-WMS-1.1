package maxzawalo.c2.free.ui.pc.model.catalogue;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.ui.pc.model.BOTableModel;
import maxzawalo.c2.base.ui.pc.model.ColumnSettings;
import maxzawalo.c2.free.bo.StrictForm;

public class StrictFormModel extends BOTableModel<StrictForm> {

	public StrictFormModel() {
		ColumnSettings column = AddVisibleColumns();
		column.name = BO.fields.DOC_STATE;
		
		column = AddVisibleColumns();
		column.name = StrictForm.fields.FORM_TYPE_NAME;

		column = AddVisibleColumns();
		column.name = StrictForm.fields.FORM_BATCH;

		column = AddVisibleColumns();
		column.name = StrictForm.fields.FORM_NUMBER;
				
		column = AddVisibleColumns();
		column.name = StrictForm.fields.WRITE_OFF_TYPE;		

		setColumnCaptions();
	}
}