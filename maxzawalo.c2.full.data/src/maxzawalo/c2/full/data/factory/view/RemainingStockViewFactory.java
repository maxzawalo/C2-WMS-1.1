package maxzawalo.c2.full.data.factory.view;

import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.CatalogueBO;
import maxzawalo.c2.base.data.DbHelper;
import maxzawalo.c2.base.data.GenericRowMapper;
import maxzawalo.c2.base.data.factory.CatalogueFactory;
import maxzawalo.c2.base.data.factory.FactoryBO;
import maxzawalo.c2.free.bo.Store;
import maxzawalo.c2.free.bo.registry.RegistryProduct;
import maxzawalo.c2.full.bo.view.RemainingStockView;

public class RemainingStockViewFactory extends FactoryBO<RemainingStockView> {

	public static List<RemainingStockView> get() {
		return get(null, null);
	}

	public static List<RemainingStockView> get(Store store, BO parent) {
		String sql = "select p.code, p." + CatalogueBO.fields.NAME + ", sum(rp.count) count ";
		sql += " from registry_product rp ";
		sql += " join product p on rp.product_id = p.id ";
		sql += " where not rp.deleted and rp.sync_flag=0 ";
		if (store != null)
			sql += " and rp." + RegistryProduct.fields.STORE + " = " + store.id;
		if (parent != null)
			sql += CatalogueFactory.ParentFilter((CatalogueBO) parent, true, "p.");
		// sql += " and p." + CatalogueBO.fields.PARENT + " = " + parent.id;
		sql += " group by rp.product_id ";
		sql += " having sum(rp.count) > 0 ";
		sql += " order by p." + CatalogueBO.fields.NAME + ", p.code ";
		// sql += " limit 0,100";
		try {
			Dao<RemainingStockView, Integer> boDao = DbHelper.geDaos(RemainingStockView.class);
			QueryBuilder<RemainingStockView, Integer> builder = boDao.queryBuilder();
			return boDao.queryRaw(sql, GenericRowMapper.get(RemainingStockView.class)).getResults();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ArrayList<>();
	}
}