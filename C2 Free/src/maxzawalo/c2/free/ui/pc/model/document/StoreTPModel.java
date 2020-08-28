package maxzawalo.c2.free.ui.pc.model.document;

import javax.swing.JLabel;

import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.base.ui.pc.model.ColumnSettings;
import maxzawalo.c2.base.ui.pc.model.TablePartModel;
import maxzawalo.c2.base.ui.pc.renderer.RedZeroAndMinusCellRenderer;
import maxzawalo.c2.free.bo.Product;
import maxzawalo.c2.free.bo.store.StoreTP;

public class StoreTPModel<Item> extends TablePartModel<Item> {
	@Override
	public boolean isCellEditable(int row, int col) {
		if (IsVisColumn(col, StoreTP.fields.PRODUCT)) {
			EditBoCell(row, col, StoreTP.fields.PRODUCT);
			return false;
		} else if (IsVisColumn(col, StoreTP.fields.PRICE_DISCOUNT_OFF) || IsVisColumn(col, StoreTP.fields.UNITS)) {
			return false;
		}
		return super.isCellEditable(row, col);
	}

	@Override
	protected Class GetVisColumnClass(int col, String colName) {
		switch (colName) {
		case StoreTP.fields.PRODUCT:
			return Product.class;
		}
		return super.GetVisColumnClass(col, colName);
	}

	protected ColumnSettings AddProductColumn() {
		ColumnSettings column = AddVisibleColumns();
		column.name = StoreTP.fields.PRODUCT;
		column.horizontalAlignment = JLabel.LEFT;
		column.to_string_js = "product.name";
		return column;
	}

	protected ColumnSettings AddCountColumn() {
		ColumnSettings column = AddVisibleColumns();
		column.format = "0.000";
		column.name = StoreTP.fields.COUNT;
		column.to_string_js = "number_format";
		return column;
	}

	protected ColumnSettings AddUnitsColumn() {
		ColumnSettings column = AddVisibleColumns();
		column.caption = "Ед.изм.";
		column.name = StoreTP.fields.UNITS;
		column.to_string_js = "product.units.name";
		return column;
	}

	protected ColumnSettings AddPriceColumn() {
		ColumnSettings column = AddVisibleColumns();
		column.name = StoreTP.fields.PRICE;
		column.to_string_js = "number_format";
		return column;
	}

	protected ColumnSettings AddSumColumn() {
		ColumnSettings column = AddVisibleColumns();
		column.name = StoreTP.fields.SUM;
		column.to_string_js = "number_format";
		return column;
	}

	protected ColumnSettings AddPriceDiscountOffColumn() {
		if (!User.current.isSimple()) {
			ColumnSettings column = AddVisibleColumns();
			column.name = StoreTP.fields.PRICE_DISCOUNT_OFF;
			column.to_string_js = "number_format";
			return column;
		}
		return new ColumnSettings();
	}

	protected ColumnSettings AddRateVatColumn() {
		ColumnSettings column = AddVisibleColumns();
		column.name = StoreTP.fields.RATE_VAT;
		column.to_string_js = "number_format";
		return column;
	}

	protected ColumnSettings AddSumVatColumn() {
		ColumnSettings column = AddVisibleColumns();
		column.name = StoreTP.fields.SUM_VAT;
		column.to_string_js = "number_format";
		return column;
	}

	protected ColumnSettings AddDiscountColumn() {
		if (!User.current.isSimple()) {
			ColumnSettings column = AddVisibleColumns();
			column.renderer = new RedZeroAndMinusCellRenderer();
			column.name = StoreTP.fields.DISCOUNT;
			column.to_string_js = "number_format";
			return column;
		}
		return new ColumnSettings();
	}

	protected ColumnSettings AddTotalColumn() {
		ColumnSettings column = AddVisibleColumns();
		column.name = StoreTP.fields.TOTAL;
		column.to_string_js = "number_format";
		return column;
	}
}