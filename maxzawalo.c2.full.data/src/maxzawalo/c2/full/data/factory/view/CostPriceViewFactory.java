package maxzawalo.c2.full.data.factory.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.dao.Dao;

import maxzawalo.c2.base.data.DbHelper;
import maxzawalo.c2.base.data.GenericRowMapper;
import maxzawalo.c2.base.data.factory.FactoryBO;
import maxzawalo.c2.free.bo.document.deliverynote.DeliveryNoteTablePart;
import maxzawalo.c2.free.bo.store.StoreTP;
import maxzawalo.c2.full.bo.view.CostPriceView;

public class CostPriceViewFactory extends FactoryBO<CostPriceView> {

	public static List<CostPriceView> get(Date fromDate, Date toDate) {
		List<CostPriceView> all = new ArrayList<>();
		for (Class cl : new Class[] { DeliveryNoteTablePart.Product.class }) {
			try {
				StoreTP tp = (StoreTP) cl.newInstance();
				all.addAll(get(tp, fromDate, toDate));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}
		return all;
	}

	static List<CostPriceView> get(StoreTP tp, Date fromDate, Date toDate) {
		String sql = "call CostPriceView();";

		try {
			Dao<CostPriceView, Integer> boDao = DbHelper.geDaos(CostPriceView.class);
			// QueryBuilder<TransactionView, Integer> builder =
			// boDao.queryBuilder();

			return boDao.queryRaw(sql, GenericRowMapper.get(CostPriceView.class)).getResults();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
}