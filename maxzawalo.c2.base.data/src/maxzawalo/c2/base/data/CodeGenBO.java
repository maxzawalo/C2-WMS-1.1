package maxzawalo.c2.base.data;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.utils.Format;

public class CodeGenBO {

	public static <TypeBO> String GenerateCode(TypeBO bo) throws Exception {

		Dao<TypeBO, Integer> boDao = (Dao<TypeBO, Integer>) DbHelper.geDaos(bo.getClass());
		QueryBuilder<TypeBO, Integer> builder = boDao.queryBuilder();
		builder.selectColumns(BO.fields.CODE);

		// BO bo = (BO) type.newInstance();
		// boolean isCatalogue = bo instanceof CatalogueBO;

		// if (type.newInstance() instanceof CatalogueBO)
		builder.orderBy(BO.fields.CODE, false);
		// else
		// builder.orderBy(BO.fields.ID, false);
		Where<TypeBO, Integer> where = builder.where();
		// TODO: from BO
		where.eq(BO.fields.SYNC_FLAG, 0);
		where.and();

		if (bo instanceof DocumentBO) {

			// where.ne(BO.fields.CODE, "ostatki");
			// where.and();
			// where.ge(DocumentBO.fields.DOC_DATE,
			// Format.FirstDayOfYear(((DocumentBO) bo).DocDate));
			where.ge(DocumentBO.fields.DOC_DATE, Format.beginOfDay(Format.FirstDayOfYear(((DocumentBO) bo).DocDate)));
			where.and();
			where.le(DocumentBO.fields.DOC_DATE, Format.endOfDay(Format.LastDayOfYear(((DocumentBO) bo).DocDate)));

			// 00БС-000001
			// СТБУ-000024
			// СТБД-000003
			where.and();

		}
		String[] exc = new String[] { "БС", "БУ", "БД" };

		for (int i = 0; i < exc.length; i++) {
			if (i > 0)
				where.and();
			where.not().like(BO.fields.CODE, "%" + exc[i] + "%");
		}
		// TODO: а если нет "-"
		where.and();
		where.like(BO.fields.CODE, "%-%");

		String code = "";
		String[] res = builder.queryRawFirst();
		if (res != null)
			code = res[0];
		// TODO: а если нет "-"
		if (code.equals("") || !code.contains("-"))
			code = ((BO) bo).zeroCode;

		String[] part = code.split("-");
		if (part.length > 1) {
			int len = part[1].length();
			code = part[0] + "-" + String.format("%0" + len + "d", Integer.parseInt(part[1]) + 1);
		}

		return code;
	}
}