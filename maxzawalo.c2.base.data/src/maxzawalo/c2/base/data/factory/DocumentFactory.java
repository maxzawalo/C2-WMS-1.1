package maxzawalo.c2.base.data.factory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

import maxzawalo.c2.base.Actions;
import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.bo.TablePartItem;
import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.base.bo.registry.Registry;
import maxzawalo.c2.base.data.DbHelper;
import maxzawalo.c2.base.interfaces.FilterT;
import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.base.utils.Global;
import maxzawalo.c2.free.bo.registry.AccAcc;
import maxzawalo.c2.free.cache.Cache;
//import maxzawalo.c2.free.data.AccFactory;

public class DocumentFactory<Doc> extends FactoryBO<Doc> {

	public static final String DocTransactionsCacheKey = "DocTransactionsCacheKey";

	public RegistryFactory accountingFactory;

	protected int transaction_num = 1;

	public DocumentFactory() {
		// TODO: только в формах списка + галочка
		enableDeletedFilter = false;
	}

	public DocumentFactory Create(Class typeBO) {
		return (DocumentFactory) Actions.FactoryByTypeAction.Do(typeBO);
	}

	@Override
	public List<Doc> GetAll() {
		return GetAll(false);
	}

	public List<Doc> GetAll(boolean loadTablePart) {
		// TODO: LoadTablePart()
		List<Doc> list = super.GetAll();
		if (loadTablePart)
			for (Doc doc : list)
				LoadTablePart(((DocumentBO) doc));
		return list;
	}

	public List<Doc> GetAll(String param, Object value, boolean loadTablePart) {
		List<Doc> list = super.GetAll(param, value);
		if (loadTablePart)
			for (Doc doc : list)
				LoadTablePart(((DocumentBO) doc));
		return list;
	}

	public List<Doc> GetAllContains(String param, Object value, boolean loadTablePart) {
		List<Doc> list = super.GetAllContains(param, value);
		if (loadTablePart)
			for (Doc doc : list)
				LoadTablePart(((DocumentBO) doc));
		return list;
	}

	@Override
	public Doc Save(Doc bo) throws Exception {
		// TODO: transaction
		bo = super.Save(bo);
		SaveTablePart((DocumentBO) bo);
		return bo;
	}

	protected void SaveTablePart(DocumentBO doc) throws Exception {
		for (String tpName : doc.GetTPNames()) {
			DeleteTPItems((Doc) doc, doc.GetTypeTPByName(tpName));
			BulkSaveTPItem((Doc) doc, doc.GetTPByName(tpName));
		}
	}

	protected void BulkSaveTPItem(Doc doc, List tp) throws Exception {
		Class tpType = null;
		for (TablePartItem item : (List<TablePartItem>) tp) {
			item.doc = ((BO) doc).id;
			// Обнуляем - иначе не сохраняются - deleteBuilder надо фильтровать
			((BO) item).id = 0;
			tpType = item.getClass();
		}
		if (tpType != null)
			new TablePartItemFactory<>().Create(tpType).BulkSave(tp);
	}

	protected void DeleteTPItems(final Doc doc, Class itemT) throws Exception {
		// Не все типы ТЧ есть у док-в
		if (itemT == null)
			return;
		// Dao<Item, Integer> itemDao = DbHelper.geDaos(itemT);
		// TODO: mark deleted
		// TODO: удалять только удаленные из таблич. части
		// DeleteBuilder<Item, Integer> deleteBuilder = itemDao.deleteBuilder();
		// deleteBuilder.where().eq("doc_id", this.id);
		// deleteBuilder.delete();

		UpdateBuilder<?, Integer> builder = new FactoryBO().Create(itemT).getUpdateBuilder();
		Where<?, Integer> where = builder.where();
		where.eq("doc_id", ((BO) doc).id);
		where.and();
		where.eq(BO.fields.DELETED, false);
		builder.updateColumnValue(BO.fields.DELETED, true);
		// Ставим для синхронизации с 1С
		((BO) doc).changed = new Date();
		builder.updateColumnValue(BO.fields.CHANGED, ((BO) doc).changed);
		builder.updateColumnValue(BO.fields.CHANGED_BY, User.current);
		builder.update();

		((BO) doc).changed_by = User.current;

		FilterT TPCacheFilter = new FilterT<TablePartItem>() {
			public boolean Check(TablePartItem item) {
				return (item.doc == ((BO) doc).id);// && item.deleted);
			}
		};
		// или все deleted
		Cache.I().removeFromList(itemT, TPCacheFilter);
	}

	protected QueryBuilder<Doc, Integer> getBuilderWithFilter(String value) throws SQLException {
		return null;
	}

	@Override
	public List<Doc> GetPageByWords(long currentPage, long pageSize, String value) {
		try {
			QueryBuilder<Doc, Integer> builder = getBuilderWithFilter(value);
			builder.offset(currentPage * pageSize).limit(pageSize);
			builder.orderBy(DocumentBO.fields.DOC_DATE, true);
			return builder.query();
		} catch (Exception e) {
			log.ERROR("GetPageByWords", e);
		}

		return new ArrayList<>();
	}

	@Override
	public long GetCountByWords(String value) {
		try {
			QueryBuilder<Doc, Integer> builder = getBuilderWithFilter(value);
			builder.setCountOf(true);
			return builder.countOf();
		} catch (Exception e) {
			log.ERROR("GetCountByWords", e);
		}

		return 0;
	}

	public List<Doc> GetByPeriod(Date fromDate, Date toDate) {
		return GetByPeriod(fromDate, toDate, null);
	}

	public List<Doc> GetByPeriod(Date fromDate, Date toDate, Boolean commited) {
		try {
			fromDate = Format.beginOfDay(fromDate);
			toDate = Format.endOfDay(toDate);
			QueryBuilder<Doc, Integer> builder = getQueryBuilder();
			Where<Doc, Integer> where = builder.where();
			NonDeletedFilter(where);
			if (commited != null) {
				where.and();
				where.eq(DocumentBO.fields.COMMITED, commited);
			}
			where.and();
			where.ge(DocumentBO.fields.DOC_DATE, fromDate);
			where.and();
			where.le(DocumentBO.fields.DOC_DATE, toDate);

			builder.orderBy(DocumentBO.fields.DOC_DATE, true);
			return builder.query();
		} catch (Exception e) {
			log.ERROR("GetByPeriod", e);
		}

		return new ArrayList<>();
	}

	@Override
	public Doc setDeleted(Doc bo, boolean deleted) throws Exception {
		// Если документ удаляем, то изо всех регистров
		RollbackTransaction((DocumentBO) bo, false);
		// TODO: DeleteTPItems
		return super.setDeleted(bo, deleted);
	}

	protected boolean setCommited(DocumentBO doc, boolean value) {
		if (Global.InMemoryGroupTransaction) {
			// Ставим для синхронизации с 1С
			doc.changed = new Date();
			doc.commited = value;
			// TODO: сохранение чтобы появился флаг
			Cache.I().putInMap(doc.getClass(), doc);
			return true;

		} else {
			try {
				UpdateBuilder<Doc, Integer> builder = getUpdateBuilder();
				Where<Doc, Integer> where = builder.where();
				where.eq(BO.fields.ID, doc.id);
				builder.updateColumnValue(DocumentBO.fields.COMMITED, value);
				// Ставим для синхронизации с 1С
				doc.changed = new Date();
				builder.updateColumnValue(BO.fields.CHANGED, doc.changed);
				builder.updateColumnValue(BO.fields.CHANGED_BY, doc.changed_by);
				builder.update();
				doc.commited = value;
				Cache.I().putInMap(doc.getClass(), doc);
				return true;
			} catch (Exception e) {
				log.ERROR("setCommited", e);
				return false;
			}
		}
	}

	public void LoadTablePart(DocumentBO doc) {
		for (String tpName : doc.GetTPNames())
			doc.SetTPByName(tpName, LoadTP(doc, doc.GetTypeTPByName(tpName)));
	}

	/**
	 * Разогрев кеша табличной части док-та
	 * 
	 * @param cacheTimeSec
	 */
	public void LoadAllTablePart2Cache(DocumentBO doc, Date startDate, Date endDate, long cacheTimeSec) {
		try {
			// TODO: sync filter off
			Console.I().INFO(getClass(), "LoadAllTablePart2Cache", "Загрузка ТЧ в кэш - " + doc.getRusName());
			int count = 0;
			for (String tpName : doc.GetTPNames()) {
				List list = ((TablePartItemFactory) new TablePartItemFactory().Create(doc.GetTypeTPByName(tpName)))
						.GetByPeriod(doc.getClass(), startDate, endDate);
				count += list.size();
				Cache.I().putList(doc.GetTypeTPByName(tpName).getName(), list, cacheTimeSec);
			}
			Console.I().INFO(getClass(), "LoadAllTablePart2Cache", "Загружено " + count + "строк");
		} catch (Exception e) {
			log.ERROR("LoadAllTablePart2Cache", e);
		}
	}

	// public <Item> List<Item> LoadTP(Class<Item> type) {
	public List<TablePartItem> LoadTP(DocumentBO doc, Class typeTP) {
		// TODO: boolean fromCache
		try {
			// TODO: Map by Id
			List<TablePartItem> TablePart = Cache.I().getList(typeTP.getName());
			if (TablePart != null) {
				List<TablePartItem> filtered = new ArrayList<>();
				// фильтруем по doc_id
				for (TablePartItem item : TablePart)
					if (!((TablePartItem) item).deleted && ((TablePartItem) item).doc == doc.id)
						filtered.add(item);
				TablePart = filtered;
			} else {
				TablePart = ((TablePartItemFactory) new TablePartItemFactory<>().Create(typeTP)).GetByDoc(doc.id);
			}
			// TODO: перенести в Save()
			for (TablePartItem i : TablePart) {
				// Устанавливаем документ - для Save() - для только что
				// добавленнных в таб.часть
				((TablePartItem) i).doc = doc.id;
			}

			return TablePart;
		} catch (Exception e) {
			log.ERROR("LoadTP", e);
		}

		return null;
	}

	public boolean BeforeTransaction(DocumentBO doc, boolean AccTransactionOnly) {
		// Сбрасываем номер ранзакции - нумеруем в TransactionBody
		transaction_num = 1;
		if (doc.deleted) {
			String message = "Документ удален. Проведение невозможно.";
			Console.I().WARN(getClass(), "BeforeTransaction", message);
			log.WARN("BeforeTransaction", message);
		} else if (doc.locked_by != null && doc.locked_by.id != 0 && !doc.commited && doc.comment != null
				&& !doc.comment.isEmpty()) {
			String message = "Документ не проведен, блокирован и имеет коментарий - пропускаем.";
			Console.I().WARN(getClass(), "BeforeTransaction", message);
			log.WARN("BeforeTransaction", message);
		} else if (doc.comment != null && doc.comment.contains("[пропуск]")) {
			String message = "Документ содержит [пропуск]. Пропускаем.";
			Console.I().WARN(getClass(), "BeforeTransaction", message);
			log.WARN("BeforeTransaction", message);
		} else
			LoadTablePart(doc);

		return RollbackTransaction(doc, AccTransactionOnly);
	}

	public boolean AfterTransaction(DocumentBO doc, boolean ProductTransactionOnly, boolean AccTransactionOnly) {
		if (!doc.deleted)
			return ((doc.HasProductRegistry() && AccTransactionOnly) || setCommited(doc, true));
		else
			return true;
	}

	/**
	 * Проведение до-та
	 * 
	 * @param ProductTransactionOnly
	 * @param AccTransactionOnly
	 * 
	 * @return
	 */
	public boolean DoTransaction(DocumentBO doc, boolean ProductTransactionOnly, boolean AccTransactionOnly) {
		if (BeforeTransaction(doc, AccTransactionOnly))
			if (!doc.deleted) {
				if (doc.HasProductRegistry() && AccTransactionOnly)
					if (!doc.commited) {
						String message = "В режиме \"Бухгалтерские проводки\" берем только проведенные документы";
						log.WARN("DoTransaction", message);
						Console.I().WARN(getClass(), "DoTransaction", message);
						// При групповом проведении надо true иначе остановится процесс
						return Global.InMemoryGroupTransaction || ProductTransactionOnly;
					}
				if (doc.CheckDoc())
					if (TransactionBody((Doc) doc, ProductTransactionOnly, AccTransactionOnly))
						return AfterTransaction(doc, ProductTransactionOnly, AccTransactionOnly);
					else {
						// TODO: if ?
						RollbackTransaction(doc, AccTransactionOnly);
						return false;
					}
			} else {
				return AfterTransaction(doc, ProductTransactionOnly, AccTransactionOnly);
			}
		return false;
	}

	public void setRegistersDocLinks(DocumentBO doc) {
		// Сюда передаем весь список используемых регистров
		if (doc.id == 0)
			log.ERROR("setRegistersDocLinks", doc.getClass().getSimpleName() + ".setUsedRegistries id = 0");
		if (doc.reg_type == 0)
			log.ERROR("setRegistersDocLinks", doc.getClass().getSimpleName() + ".setUsedRegistries reg_type = 0");

		for (Object r : doc.usedRegistries.values())
			((Registry) r).setRegistrator(doc);
	}

	/**
	 * Отменить проведение до-та
	 * 
	 * @return
	 */
	public boolean RollbackTransaction(DocumentBO doc, boolean AccTransactionOnly) {
		setRegistersDocLinks(doc);
		// Удаляем из всех регистров вхождения док-та
		for (Object r : doc.usedRegistries.values())
			if (!new RegistryFactory<>().Create(r.getClass()).BeforeTransaction((Registry) r, AccTransactionOnly))
				return false;
		// Не снимаем флаг при чисто бух. проведении(исп-м commited для проверки)
		return ((doc.HasProductRegistry() && AccTransactionOnly) || setCommited(doc, false));
	}

	boolean TransactionBody(Doc doc, boolean ProductTransactionOnly, boolean AccTransactionOnly) {
		if (AccTransactionOnly || ProductTransaction(doc)) {
			return (ProductTransactionOnly || (AccTransaction(doc) && SaveDocAccCache((DocumentBO) doc)));
		}
		return false;
	}

	protected boolean ProductTransaction(Doc doc) {
		log.ERROR("ProductTransaction", "Ф-я не определена");
		return false;
	}

	protected boolean AccTransaction(Doc doc) {
		log.ERROR("AccTransaction", "Ф-я не определена");
		return false;
	}

	// Для теста
	public int DeleteFullYear(Date docDate) {
		Dao<Doc, Integer> boDao = DbHelper.geDaos(typeBO);
		try {
			DeleteBuilder<Doc, Integer> deleteBuilder = boDao.deleteBuilder();
			Where<Doc, Integer> where = deleteBuilder.where();
			where.ge(DocumentBO.fields.DOC_DATE, Format.beginOfDay(Format.FirstDayOfYear(docDate)));
			where.and();
			where.le(DocumentBO.fields.DOC_DATE, Format.endOfDay(Format.LastDayOfYear(docDate)));
			System.out.println(deleteBuilder.prepareStatementString());
			return deleteBuilder.delete();
		} catch (Exception e) {
			log.ERROR("DeleteByParameter", e);
		}
		return 0;
	}

	@Override
	public boolean CheckCodeExists(BO bo, String code) {
		try {
			Dao<Doc, Integer> boDao = DbHelper.geDaos(typeBO);
			QueryBuilder<Doc, Integer> builder = boDao.queryBuilder();
			Where<Doc, Integer> where = builder.where();
			NonDeletedFilter(where);
			where.and();
			where.ne(BO.fields.ID, bo.id);
			where.and();
			where.eq(BO.fields.CODE, code);
			where.and();
			where.ge(DocumentBO.fields.DOC_DATE, Format.beginOfDay(Format.FirstDayOfYear(((DocumentBO) bo).DocDate)));
			where.and();
			where.le(DocumentBO.fields.DOC_DATE, Format.endOfDay(Format.LastDayOfYear(((DocumentBO) bo).DocDate)));

			builder.setCountOf(true);
			if (builder.countOf() == 0)
				return false;

		} catch (Exception e) {
			log.ERROR("CheckCodeExists", e);
		}

		return true;
	}

	public void ClearAllTPFromCache() {
		DocumentBO doc = null;
		try {
			doc = (DocumentBO) typeBO.newInstance();
		} catch (Exception e) {
			log.ERROR("ClearAllTPFromCache", e);
		}
		Console.I().INFO(getClass(), "ClearAllTPFromCache", "Удаление из кэша ТЧ " + doc.getRusName());
		try {
			for (String tpName : doc.GetTPNames())
				Cache.I().clearCache(doc.GetTypeTPByName(tpName).getName());
		} catch (Exception e) {
			log.ERROR("ClearAllTPFromCache", e);
		}
	}

	/**
	 * Нет разницы где Дт и Кт
	 * 
	 * @param doc
	 * @param Dt
	 * @param Kt
	 * @throws Exception
	 */
	protected void AccRecord(DocumentBO doc, AccAcc Dt, AccAcc Kt) throws Exception {
		// Провереряем типы Субконто и их заполнение(null?), разные счета в Дт и
		// Кт,чтобы не было по 2 Дт или Кт, pos != 0
		if (Dt.sum == 0 && (!(Dt.isQuantitative() ^ Dt.count == 0)))
			throw new Exception("Dt.sum == 0 && Dt.count == 0");

		if (Kt.sum == 0 && (!(Kt.isQuantitative() ^ Kt.count == 0)))
			throw new Exception("Kt.sum == 0 && Kt.count == 0");

		if (!(Dt.is_debit ^ Kt.is_debit))
			throw new Exception("или Дт или Кт");

		Dt.transaction_num = transaction_num;
		Kt.transaction_num = transaction_num;

		Dt.reg_date = doc.DocDate;
		Dt.reg_type = doc.reg_type;
		Dt.reg_id = doc.id;

		Kt.reg_date = doc.DocDate;
		Kt.reg_type = doc.reg_type;
		Kt.reg_id = doc.id;

		if (Global.InMemoryGroupTransaction) {
			PutAccInCache(Dt);
			PutAccInCache(Kt);
		} else {
			PutAccInDocCache(doc, Dt);
			PutAccInDocCache(doc, Kt);
		}

		transaction_num++;
	}

	protected void PutAccInCache(AccAcc acc) {
		List all = Cache.I().getList(RegistryFactory.listGroupTransactionKey(acc.getClass()));
		// if (all == null || acc == null)
		all.add(acc);
		Cache.I().putList(RegistryFactory.listGroupTransactionKey(acc.getClass()), all,
				RegistryFactory.IN_MEMORY_CACHE_TIME_SEC);
	}

	protected void PutAccInDocCache(DocumentBO doc, AccAcc acc) {
		List all = (List) doc.calcFields.get(DocTransactionsCacheKey);
		if (all == null)
			all = new ArrayList<>();

		all.add(acc);
		doc.calcFields.put(DocTransactionsCacheKey, all);
	}

	protected boolean SaveDocAccCache(DocumentBO doc) {
		// Если групповое проведение, то сохраняются все проводки в конце
		if (Global.InMemoryGroupTransaction)
			return true;

		try {
			List<AccAcc> all = (List<AccAcc>) doc.calcFields.get(DocTransactionsCacheKey);
			// тут разбиваем на счета и сохраняем в разных потоках
			List<String> accCodes = all.stream().map(a -> a.code).distinct().collect(Collectors.toList());
			for (String code : accCodes) {
				System.out.println(code);
				List<AccAcc> filtered = all.stream().filter(a -> a.code.equals(code)).collect(Collectors.toList());
				//!!! вернуть
//				if (filtered.size() != 0)
//					new AccFactory().Create(filtered.get(0).getClass()).BulkSave(filtered);
			}
			doc.calcFields.remove(DocTransactionsCacheKey);
			return true;
		} catch (Exception e) {
			log.ERROR("SaveDocAccCache", e);
		}
		return false;
	}
}