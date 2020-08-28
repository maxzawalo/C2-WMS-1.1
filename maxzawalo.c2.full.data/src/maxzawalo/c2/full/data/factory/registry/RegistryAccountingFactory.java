package maxzawalo.c2.full.data.factory.registry;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import maxzawalo.c2.base.data.DbHelper;
import maxzawalo.c2.base.data.factory.RegistryFactory;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.full.bo.registry.RegistryAccounting;

public class RegistryAccountingFactory extends RegistryFactory<RegistryAccounting> {

	public int ClearLastMonth() {
		Dao<RegistryAccounting, Integer> boDao = DbHelper.geDaos(typeBO);
		try {
			DeleteBuilder<RegistryAccounting, Integer> deleteBuilder = boDao.deleteBuilder();
			deleteBuilder.where().ge("reg_date", Format.AddMonth(new Date(), -1));
			return deleteBuilder.delete();
		} catch (Exception e) {
			log.ERROR("ClearLastMonth", e);
		}
		return 0;
	}

	public List<RegistryAccounting> GetByAccount(Date fromDate, Date toDate, String acc, String filter) {
		try {
			QueryBuilder<RegistryAccounting, Integer> builder = getQueryBuilder();
			Where<RegistryAccounting, Integer> where = builder.where();
			NonDeletedFilter(where);
			where.and();
			where.ge("reg_date", Format.beginOfDay(fromDate));
			where.and();
			where.le("reg_date", Format.endOfDay(toDate));
			String sql = where.getStatement();
			sql += " and (DtAccount like '#acc%' or KtAccount like '#acc%') ";
			sql += filter;
			where = builder.where();
			where.raw(sql.replaceAll("#acc", acc));
			return builder.query();
		} catch (SQLException e) {
			log.ERROR("GetBy", e);
		}
		return new ArrayList<>();
	}

	public static String CustomerDebtFilter() {
		String filter = " and not (RegMeta = 'Корректировка долга.Зачет авансов') ";
		filter += " and not (DtAccount like '62.1%' and KtAccount like '62.5%') ";
		filter += " and not (DtAccount like '62.5%' and KtAccount like '62.1%') ";
		return filter;
	}

	public static String ContributorFilter() {
		String filter = " and DtAccount like '#acc%' ";
		return filter;
	}
}