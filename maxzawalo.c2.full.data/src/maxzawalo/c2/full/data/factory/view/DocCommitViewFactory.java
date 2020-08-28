package maxzawalo.c2.full.data.factory.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.dao.Dao;

import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.data.DbHelper;
import maxzawalo.c2.base.data.GenericRowMapper;
import maxzawalo.c2.base.data.factory.FactoryBO;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.base.utils.Global;
import maxzawalo.c2.full.bo.view.DocCommitView;

public class DocCommitViewFactory extends FactoryBO<DocCommitView> {

	public static List<DocCommitView> get(Date fromDate, Date toDate, int hourCount) {
		List<DocCommitView> all = new ArrayList<>();
		for (Class cl : Global.dbClasses) {
			try {
				DocumentBO doc = (DocumentBO) cl.newInstance();
				if (doc != null)
					all.addAll(get(doc, fromDate, toDate, hourCount));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}
		return all;
	}

	static List<DocCommitView> get(DocumentBO doc, Date fromDate, Date toDate, int hourCount) {
		String sql = "select";
		sql += " ShowDate(doc.`docdate`) `doc_date`, ";
		sql += " doc.code doc_code ,";
		sql += " GetDocNameByType(" + doc.reg_type + ") `doc_type`, ";
		sql += " round((`changed` - created) /(3600 * 1000),1) diff";
		sql += " FROM " + FactoryBO.getTableName(doc.getClass()) + " doc ";
		sql += " where not deleted and sync_flag = 0 ";
		sql += " and changed >=" + Format.beginOfDay(fromDate).getTime();
		sql += " and changed <=" + Format.endOfDay(toDate).getTime();
		sql += " and `changed` - created > 3600 * 1000 *" + hourCount;
		sql += " order by doc.`docdate`";

		try {
			Dao<DocCommitView, Integer> boDao = DbHelper.geDaos(DocCommitView.class);
			// QueryBuilder<TransactionView, Integer> builder =
			// boDao.queryBuilder();

			return boDao.queryRaw(sql, GenericRowMapper.get(DocCommitView.class)).getResults();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ArrayList<>();
	}
}