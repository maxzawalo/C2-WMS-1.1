package maxzawalo.c2.base.ui.pc.model;

import java.lang.reflect.Field;

import maxzawalo.c2.base.bo.TablePartItem;
import maxzawalo.c2.base.utils.Format;

public class BOEditTableModel<TypeBO> extends BOTableModel<TypeBO> {

	// public BOEditTableModel() {
	// // super(list);
	// }

	public boolean isCellEditable(int row, int col) {
		return true;
	}

	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		TypeBO bo = list.get(rowIndex);

		try {
			String fName = visibleColumns.get(columnIndex).name;
			Field f = typeBO.getField(fName);
			f.set(bo, getValueFromCell(value, f.getType()));
			((TablePartItem) bo).Calc(fName);

			if (((TablePartItem) bo).onChanged != null)
				((TablePartItem) bo).onChanged.Do(null);

		} catch (Exception e) {
			log.ERROR("setValueAt", e);
		}
		// Обновляем всю строку
		fireTableRowsUpdated(rowIndex, rowIndex);
		// fireTableCellUpdated(rowIndex, columnIndex);
	}

	public Object getValueFromCell(Object value, Class type) {
		if (type == Double.class || double.class == type) {
			value = Format.extractDouble(value + "");
		}
		return value;
	}

}