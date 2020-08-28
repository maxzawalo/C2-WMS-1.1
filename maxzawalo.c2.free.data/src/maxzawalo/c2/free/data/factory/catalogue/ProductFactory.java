package maxzawalo.c2.free.data.factory.catalogue;

import java.util.ArrayList;
import java.util.List;

import maxzawalo.c2.base.bo.CatalogueBO;
import maxzawalo.c2.base.data.factory.CatalogueFactory;
import maxzawalo.c2.free.bo.Product;

public class ProductFactory extends CatalogueFactory<Product> {
	public ProductFactory Create() {
		return (ProductFactory) super.Create(Product.class);
	}

	@Override
	public List<Product> GetPageByFiltered(long currentPage, long pageSize, CatalogueBO catalogue, CatalogueBO parent,
			String searchData) {
		List<Product> list = super.GetPageByFiltered(currentPage, pageSize, catalogue, parent, searchData);
		// Подгружаем ед.изм. (level 3)
		for (Product p : list) {
			if (p.units != null)
				p.units = new UnitsFactory().GetById(p.units.id);
		}
		return list;
	}

	public List<Product> Select4Site() {
		// try {
		// QueryBuilder<Product, Integer> builder = getQueryBuilder();
		// Where<Product, Integer> where = builder.where();
		// NonDeletedFilter(where);
		// where.and();
		// where.ne(Product.fields.WEB_CAT, "");
		// where.and();
		// where.isNotNull(Product.fields.WEB_CAT);
		// return builder.query();
		// } catch (Exception e) {
		// log.ERROR("SelectByParam", e);
		// }
		List<Product> list = new ArrayList<>();
		for (Product p : GetGroups())
			if (p.web_cat != null && !p.web_cat.isEmpty())
				list.add(p);
		return list;
	}
}