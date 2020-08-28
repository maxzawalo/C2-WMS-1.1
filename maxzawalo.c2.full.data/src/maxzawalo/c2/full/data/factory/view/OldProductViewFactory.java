package maxzawalo.c2.full.data.factory.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.dao.Dao;

import maxzawalo.c2.base.data.DbHelper;
import maxzawalo.c2.base.data.GenericRowMapper;
import maxzawalo.c2.base.data.factory.FactoryBO;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.base.utils.Profiler;
import maxzawalo.c2.free.bo.Product;
import maxzawalo.c2.free.bo.Store;
import maxzawalo.c2.free.bo.registry.RegistryProduct;
import maxzawalo.c2.free.data.factory.catalogue.UnitsFactory;
import maxzawalo.c2.full.bo.view.OldProductView;
import maxzawalo.c2.full.data.factory.ImageFactory;

public class OldProductViewFactory extends FactoryBO<OldProductView> {

	public static List<OldProductView> get(Store store, int days) {
		List<OldProductView> list = new ArrayList<>();
		String sql = "";
		sql += " select";
		sql += " rp.reg_date doc_date,";
		sql += " rp.product_id product_id,";
		sql += " ROUND(sum(rp.count),3) count,";
		sql += " ROUND((ROUND(UNIX_TIMESTAMP(CURTIME(4))) - rp.reg_date/1000)/86400) days";
		sql += " from registry_product rp";
		sql += " where not rp.deleted and rp.sync_flag=0";
		if (store != null)
			sql += " and rp." + RegistryProduct.fields.STORE + " = " + store.id;
		sql += " group by rp.product_id";
		sql += " having count > 0 ";
		sql += " and days > " + days;
		// -- and sum(rp.count) < 0.00001
		sql += " order by doc_date, count";

		try {
			Dao<OldProductView, Integer> boDao = DbHelper.geDaos(OldProductView.class);
			System.out.println(sql);
			Profiler p = new Profiler();
			p.Start("OldProductViewGet");
			list = boDao.queryRaw(sql, GenericRowMapper.get(OldProductView.class)).getResults();
			System.out.println(list.size());
			ImageFactory imageFactory = new ImageFactory();
			for (OldProductView tw : list) {
				// tw.doc_type = RegType.ToText(Integer.parseInt(tw.doc_type));
				tw.date = Format.Show(new Date(tw.doc_date));
				Product product = tw.product;
				if (product != null) {
					tw.product_id = "" + tw.product.id;
					tw.product_code = tw.product.code;
					tw.product_name = tw.product.name;
					tw.product_units = (tw.product.units == null ? ""
							: "" + new UnitsFactory().GetById(tw.product.units.id));

					tw.has_image = (imageFactory.HasImage(product.id) ? "+" : "");
				}
			}
			p.Stop("OldProductViewGet");
			p.PrintElapsed("OldProductViewGet");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}
}