package maxzawalo.c2.free.ui.pc.model.catalogue;

import javax.swing.JLabel;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.CatalogueBO;
import maxzawalo.c2.base.ui.pc.model.CatalogueTableModel;
import maxzawalo.c2.base.ui.pc.model.ColumnSettings;
import maxzawalo.c2.base.ui.pc.renderer.DocStateRenderer;
import maxzawalo.c2.free.bo.Product;

public class ProductTableModel extends CatalogueTableModel<Product> {

	public ProductTableModel() {
		ColumnSettings column = AddVisibleColumns();
		column.name = BO.fields.DOC_STATE;
		column.renderer = new DocStateRenderer();

		column = AddVisibleColumns();
		column.horizontalAlignment = JLabel.LEFT;
		column.name = CatalogueBO.fields.NAME;

		column = AddVisibleColumns();
		column.horizontalAlignment = JLabel.CENTER;
		column.name = Product.fields.UNITS;
		column.to_string_js = "units.name";

		column = AddVisibleColumns();
		column.name = BO.fields.CODE;

		column = AddVisibleColumns();
		column.name = BO.fields.ID;

		setColumnCaptions();
	}
}