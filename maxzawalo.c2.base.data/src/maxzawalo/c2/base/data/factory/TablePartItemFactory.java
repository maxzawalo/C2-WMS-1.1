package maxzawalo.c2.base.data.factory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.bo.TablePartItem;
import maxzawalo.c2.base.data.DbHelper;
import maxzawalo.c2.base.data.GenericRowMapper;

public class TablePartItemFactory<Item> extends FactoryBO<Item> {

	public TablePartItemFactory() {
		DeleteFilterOn();
	}

	@Override
	public FactoryBO Create(Class typeBO) {
		FactoryBO factory = super.Create(typeBO);
		return factory;
	}

	@Override
	protected Item GenerateCode(Item bo) throws Exception {
		((BO) bo).code = "";
		return bo;
	}

	public List GetByPeriod(Class docType, Date startDate, Date endDate) {
		try {
			Dao<?, Integer> itemDao = DbHelper.geDaos(typeBO);
			QueryBuilder<Item, Integer> itemBuilder = getQueryBuilderT(typeBO);// itemDao.queryBuilder();
			Where<Item, Integer> itemWhere = itemBuilder.where();
			NonDeletedFilter((Where<Item, Integer>) itemWhere);

			QueryBuilder<?, Integer> docBuilder = getQueryBuilderT(docType);
			Where<?, Integer> docWhere = docBuilder.where();
			new DocumentFactory<>().Create(docType).NonDeletedFilter(docWhere);
			docWhere.and();
			docWhere.ge(DocumentBO.fields.DOC_DATE, startDate);
			docWhere.and();
			docWhere.le(DocumentBO.fields.DOC_DATE, endDate);

			itemBuilder.join(TablePartItem.fields.DOC, BO.fields.ID, docBuilder);

			GenericRowMapper mapperTP = GenericRowMapper.get(typeBO);
			mapperTP.level = 0;
			System.err.println(itemBuilder.prepareStatementString());
			List list = itemDao.queryRaw(itemBuilder.prepareStatementString(), mapperTP).getResults();
			return list;
		} catch (Exception e) {
			log.ERROR("GetByPeriod", e);
		}
		return new ArrayList<>();
	}

	public List<TablePartItem> GetByDoc(int doc_id) throws Exception {
		List<TablePartItem> TablePart = new ArrayList<>();
		Dao<Item, Integer> itemDao = DbHelper.geDaos(typeBO);
		// BO bo = ((BO) typeTP.newInstance());
		QueryBuilder<Item, Integer> builder = getQueryBuilderT(typeBO);// itemDao.queryBuilder();
		Where<Item, Integer> where = builder.where();
		NonDeletedFilter(where);
		where.and();
		where.eq("doc_id", doc_id);// TODO: doc filter

		// Для GenericRowMapper GetById

		// (Collection<? extends TablePartItem>)
		GenericRowMapper mapperTP = GenericRowMapper.get(typeBO);
		mapperTP.level = 0;
		// Profiler profiler = new Profiler();
		// profiler.Start(type.getName() + ".GetById_FromDb");
		System.out.println(builder.prepareStatementString());
		for (Object o : itemDao.queryRaw(builder.prepareStatementString(), mapperTP).getResults())
			TablePart.add((TablePartItem) o);
		// profiler.Stop(type.getName() + ".GetById_FromDb");
		// profiler.PrintElapsed(type.getName() + ".GetById_FromDb");
		// TODO: ? в кэш не кладем...пока
		return TablePart;
	}

}