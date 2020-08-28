package maxzawalo.c2.base.ui.pc.model;

import javax.swing.JLabel;
import javax.swing.table.TableCellRenderer;

import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.ui.pc.renderer.CustomCellRenderer;

public class ColumnSettings extends BO<ColumnSettings> {
	@DatabaseField
	public String name = "";

	@DatabaseField
	public String caption = "";

	@DatabaseField
	public int horizontalAlignment = JLabel.RIGHT;

	public TableCellRenderer renderer = new CustomCellRenderer();

	public boolean addCol = false;

	@DatabaseField
	public String format = "0.00";

	@DatabaseField
	public String to_string_js = "";
}