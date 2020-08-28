package maxzawalo.c2.full.ui.pc.model;

import java.awt.Font;

import javax.swing.JLabel;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.base.ui.pc.model.BOTableModel;
import maxzawalo.c2.base.ui.pc.model.ColumnSettings;
import maxzawalo.c2.base.ui.pc.renderer.CheckBoxRenderer;
import maxzawalo.c2.base.ui.pc.renderer.CustomCellRenderer;
import maxzawalo.c2.free.bo.registry.RegistryProduct;
import maxzawalo.c2.free.bo.store.StoreTP;

public class RegistryProductTableModel extends BOTableModel<RegistryProduct> {

	public RegistryProductTableModel() {
		ColumnSettings column = AddVisibleColumns();
		column.name = StoreTP.fields.PRODUCT;
		column.horizontalAlignment = JLabel.LEFT;

		column = AddVisibleColumns();
		column.renderer = new CheckBoxRenderer();
		column.caption = "Резерв";
		column.name = "reserve";

		column = AddVisibleColumns();
		column.caption = "Количество";
		((CustomCellRenderer) column.renderer).fontStyle = Font.BOLD;
		column.name = StoreTP.fields.COUNT;
		column.format = "0.000";

		column = AddVisibleColumns();
		column.name = "units";
		column.horizontalAlignment = JLabel.CENTER;

		if (!User.current.isSimple()) {
			column = AddVisibleColumns();
			column.caption = "Себестоимость";
			column.name = "cost_price";
		}

		column = AddVisibleColumns();
		column.caption = "Цена";
		((CustomCellRenderer) column.renderer).fontStyle = Font.BOLD;
		column.name = StoreTP.fields.PRICE;

		// column = AddVisibleColumns();
		// column.caption = "Цена (расчетная)";
		// column.name = "calc_price";
		//
		// column = AddVisibleColumns();
		// column.caption = "Наценка (справочник)";
		// column.name = "add";

		if (!User.current.isSimple()) {
			column = AddVisibleColumns();
			column.caption = "Наценка (факт)";
			column.name = "doc_add";

			column = AddVisibleColumns();
			column.horizontalAlignment = JLabel.CENTER;
			// column.caption = "Ед. изм.";
			column.name = "price_bo";
		}

		column = AddVisibleColumns();
		column.name = "lotOfProduct";

		column = AddVisibleColumns();
		column.name = BO.fields.ID;

		setColumnCaptions();
	}
}