package maxzawalo.c2.free.ui.pc.catalogue;

import maxzawalo.c2.base.bo.CatalogueBO;
import maxzawalo.c2.base.ui.pc.catalogue.CatalogueListForm;
import maxzawalo.c2.free.bo.Product;
import maxzawalo.c2.free.data.factory.catalogue.ProductFactory;
import maxzawalo.c2.free.ui.pc.model.catalogue.ProductTableModel;

public class ProductListForm extends CatalogueListForm<Product, ProductFormFree> {
	public ProductListForm() {
		setBounds(0, 0, 1000, 700);
		factory = new ProductFactory();
		tableModel = new ProductTableModel();
		groupForm = new ProductGroupForm();
	}

	@Override
	protected String GetTreeElName(CatalogueBO cat) {
		double addition = ((Product) cat).addition;
		return cat.name + ((addition == 0) ? "" : " " + addition);
	}
}