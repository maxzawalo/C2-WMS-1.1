package maxzawalo.c2.base.ui.pc.model;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.base.utils.Logger;

public class BOTableModel<TypeBO> extends AbstractTableModel {

	protected Logger log = Logger.getLogger(this.getClass().getSimpleName());

	protected List<TypeBO> list = new ArrayList<>();
	// private List<String> columnNames = new ArrayList<>();
	protected Map<String, String> columnNames = new HashMap<String, String>();
	public List<ColumnSettings> visibleColumns = new ArrayList<ColumnSettings>();

	// @Expose
	Class<TypeBO> typeBO;
	protected JTable table;

	public BOTableModel() {

		Class clazz = this.getClass();
		Type[] gParams = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments();
		if (gParams.length == 0)
			;// TODO:
		else {
			this.typeBO = (Class<TypeBO>) gParams[0];
			Field[] fields = this.typeBO.getFields();

			for (Field f : fields) {
				BoField boFiled = f.getAnnotation(BoField.class);
				String fieldName = f.getName();
				if (boFiled == null)
					columnNames.put(fieldName, fieldName);
				else
					columnNames.put(fieldName, boFiled.caption());
			}
		}

		// this.list = list;
	}

	public void setTable(JTable table) {
		this.table = table;
	}

	public ColumnSettings AddVisibleColumns() {
		ColumnSettings column = new ColumnSettings();
		column.horizontalAlignment = JLabel.RIGHT;
		visibleColumns.add(column);
		return column;
	}

	public ColumnSettings getVisibleColumn(int column) {
		return visibleColumns.get(column);
	}

	public void setColumnCaptions() {
		Field[] fields = this.typeBO.getFields();
		for (ColumnSettings settings : visibleColumns) {
			// Исправляем поля
			settings.name = settings.name.replace("_id", "");
			if (settings.caption.equals("")) {
				for (Field f : fields) {
					if (settings.name.equals(f.getName())) {
						BoField boFiled = f.getAnnotation(BoField.class);
						if (boFiled == null)
							settings.caption = settings.name;
						else
							settings.caption = boFiled.caption();
					}
				}
			}
		}

	}

	public void setList(List<TypeBO> list) {
		this.list = list;
	}

	public List<TypeBO> getList() {
		return this.list;
	}

	public TypeBO getItem(int pos) {
		if (pos < 0 || pos >= list.size())
			return null;
		return list.get(pos);
	}

	// public int getColHorizontalAlignment(int columnIndex) {
	// // if (columnIndex >= visibleColumns.length)
	// // return JLabel.RIGHT;
	// return visibleColumns.get(columnIndex).horizontalAlignment;
	// }

	@Override
	public String getColumnName(int columnIndex) {
		return getVisibleColumnName(columnIndex);
	}

	private String getVisibleColumnName(int columnIndex) {
		String showName = visibleColumns.get(columnIndex).name;
		return columnNames.get(showName);
	}

	public int getColNumByVisibleColumns(String name) {
		name = name.replace("_id", "");
		for (int colNum = 0; colNum < visibleColumns.size(); colNum++)
			if (visibleColumns.get(colNum).name.equals(name))
				return colNum;

		return -1;
	}

	@Override
	public int getRowCount() {
		if (list == null) {
			log.ERROR("getRowCount", "list == null");
			return 0;
		}
		return list.size();
	}

	@Override
	public int getColumnCount() {
		// return columnNames.size();
		return visibleColumns.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// Нету колонки в видимых
		if (columnIndex == -1)
			return null;
		TypeBO bo = list.get(rowIndex);
		try {
			Field f = typeBO.getField(visibleColumns.get(columnIndex).name);
			return FormatCellValue(bo, f, visibleColumns.get(columnIndex).format);
		} catch (NoSuchFieldException e) {
			return ((BO) bo).getCalcField(visibleColumns.get(columnIndex).name);
		} catch (Exception e) {
			log.ERROR("getValueAt", e);
		}

		return null;
	}

	/**
	 * Получение значения поля объекта по реальному имени поля
	 * 
	 * @param rowIndex
	 * @param columnName
	 * @return
	 */
	public Object getValue(int rowIndex, String columnName) {
		TypeBO bo = list.get(rowIndex);
		try {
			Field f = typeBO.getField(columnName);
			return f.get(bo);
		} catch (NoSuchFieldException e) {
			// return ((BO) bo).getCalcField(columnName);
			return null;
		} catch (Exception e) {
			log.ERROR("getValue", e);
		}

		return null;
	}

	protected Object FormatCellValue(TypeBO bo, Field f, String format) throws IllegalAccessException {
		Type type = f.getType();
		if (type == Double.class || double.class == type)
			return Format.formatDouble((double) f.get(bo), format);
		return f.get(bo);
	}

	public void removeRow(int row) {
		list.remove(row);
	}

	public void removeRows(int[] rows) {
		List<TypeBO> deleted = new ArrayList<>();
		for (int row : rows)
			deleted.add(list.get(row));
		for (TypeBO bo : deleted)
			list.remove(bo);
		// list.remove(row);
	}
}