package maxzawalo.c2.base.data.factory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.DatabaseTable;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.CatalogueBO;
import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.base.data.CodeGenBO;
import maxzawalo.c2.base.data.DbHelper;
import maxzawalo.c2.base.data.GenericRowMapper;
import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.base.utils.Global;
import maxzawalo.c2.base.utils.Logger;
import maxzawalo.c2.base.utils.Profiler;
import maxzawalo.c2.base.utils.WordUtils;
import maxzawalo.c2.free.cache.Cache;

public class FactoryBO<TypeBO> {

	protected Logger log = Logger.getLogger(this.getClass());

	protected Class typeBO;

	public Class getTypeBO() {
		return typeBO;
	}

	// protected BO currentObj;

	public boolean enableDeletedFilter = true;
	protected boolean enableSyncFilter = true;

	public FactoryBO Create(Class typeBO) {
		this.typeBO = typeBO;
		mapper = new GenericRowMapper(typeBO);
		return this;
	}

	public FactoryBO() {
		Class clazz = this.getClass();
		// Рекировочка
		if (clazz.getSuperclass().getName().contains("Free"))
			clazz = clazz.getSuperclass();
		Object sc = clazz.getGenericSuperclass();
		try {

			if (sc != null && sc instanceof ParameterizedType) {
				Type[] gParams = ((ParameterizedType) sc).getActualTypeArguments();
				if (gParams.length != 0)// для dao - иначе не создает BO
				{
					this.typeBO = (Class<TypeBO>) gParams[0];
					mapper = new GenericRowMapper(typeBO);
				}
			}
		} catch (ClassCastException e) {
			// log.ERROR("BO", e);
		}

		// TODO: BO sync_flag
		if (Global.sync_flag != 0)
			ForSync();
	}

	private void ForSync() {
		this.enableDeletedFilter = false;
		this.enableSyncFilter = false;
		// return (TypeBO) this;
	}

	public FactoryBO<TypeBO> DeleteFilterOff() {
		this.enableDeletedFilter = false;
		// return (TypeBO) this;
		return this;
	}

	public FactoryBO<TypeBO> DeleteFilterOn() {
		this.enableDeletedFilter = true;
		return this;
	}

	protected TypeBO GenerateCode(TypeBO bo) throws Exception {
		// TODO:link
		((BO) bo).code = CodeGenBO.GenerateCode(bo);
		return bo;
	}

	TypeBO Insert(TypeBO bo, Dao<TypeBO, Integer> boDao) throws Exception {
		if (((BO) bo).code.length() == 0)
			bo = GenerateCode(bo);
		boDao.create(bo);
		return bo;
	}

	protected TypeBO BeforeSave(TypeBO bo) throws Exception {
		return bo;
	}

	public TypeBO Save(TypeBO bo) throws Exception {
		return Save(bo, false);
	}

	public TypeBO Save(TypeBO bo, boolean bulk) throws Exception {
		TypeBO bo_ = BeforeSave(bo);
		Dao<TypeBO, Integer> boDao = DbHelper.geDaos(typeBO);
		for (int i = 0; true; i++) {
			try {
				// Dao<TypeBO, Integer> boDao = DbHelper.geDaos(typeBO);
				// CommunicationsException: Communications link failure
				if (((BO) bo_).id == 0) {
					// TODO: вынести из базового класса
					if (bulk || Global.sync_flag != 0 || Global.groupTransaction) {
						// TODO: блокируем АРМы - только чтение (кроме сервера)
						// TODO: bo = Insert((TypeBO) bo);
						Insert((TypeBO) bo_, boDao);
						// Cache.I().putInList(typeBO, this);
					} else {
						// TODO: Cache.I().putInList(typeBO, this); ??
						TransactionManager.callInTransaction(DbHelper.getConnectionSource(), new Callable<Void>() {
							@Override
							public Void call() throws Exception {
								// TODO: bo = Insert((TypeBO) bo);
								Insert(bo_, boDao);
								return null;
							}
						});
					}
				} else {
					UpdateBuilder<TypeBO, Integer> builder = getUpdateBuilder(boDao);
					// set the criteria like you would a QueryBuilder
					builder.where().eq(BO.fields.ID, ((BO) bo_).id);
					for (Field field : bo_.getClass().getFields()) {
						String fieldName = field.getName();
						if (fieldName.equals(BO.fields.ID) || fieldName.equals(BO.fields.CREATED))
							continue;

						if (field.getAnnotation(DatabaseField.class) == null)
							continue;

						Object value = field.get(bo_);

						if (fieldName.equals(BO.fields.CODE)) {
							// Пропускаем классы, которые в 1С не имеют кодов
							// (таб
							// части)
							// TODO: выносим из базового класса
							if (((BO) bo_).HasNoCode())
								continue;

							if (User.current.isAdmin()) {
								if (CheckCodeExists(((BO) bo_), "" + value)) {
									// log.CONSOLE("Код " + value + "
									// существует");
									throw new Exception("Код " + value + " существует");
									// return bo;
								}
							} else
								continue;
						}

						// log.DEBUG("BO.Save|value=" + value);
						fieldName = DbHelper.GetFieldDbName(field);
						if (field.getAnnotation(DatabaseField.class).foreign()) {
							builder.updateColumnValue(fieldName, value);
							// if (value != null)
							// builder.updateColumnValue(fieldName + "_id",
							// ((BO) value).id);
							// else
							// builder.updateColumnValue(fieldName + "_id",
							// null);
						} else
							builder.updateColumnValue(fieldName, new SelectArg(value));
					}

					// Ставим для синхронизации с 1С
					((BO) bo_).changed = new Date();
					builder.updateColumnValue(BO.fields.CHANGED, ((BO) bo_).changed);
					builder.updateColumnValue(BO.fields.CHANGED_BY, User.current);
					builder.update();

					((BO) bo_).changed_by = User.current;
				}

				// TODO: если сбой будет ли в памяти и в БД разные данные?
				Cache.I().putInMap(typeBO, ((BO) bo_));
				return bo_;
			} catch (Exception commExp) {
				if (IsNetworkError(commExp) && i < 60) {
					Thread.sleep(1000);// TODO: random
					continue;
					// java.sql.SQLException: invalid database address:
					// jdbc:mysql://127.0.0.1/warehouse?a
					// CommunicationsException: Communications link failure
					// com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException:
					// Could not create connection to database server. Attempted
					// reconnect 3 times. Giving up.
				} else
					throw commExp;
			}
		}

		// TODO: null?
		// return bo;
	}

	protected boolean IsNetworkError(Exception commExp) {
		return commExp.getClass().getSimpleName().contains("CommunicationsException")
				|| commExp.getClass().getSimpleName().contains("MySQLNonTransientConnectionException");
	}

	public Where<TypeBO, Integer> NonDeletedFilter(Where<TypeBO, Integer> where) throws SQLException {
		SynchronizationFilter(where);
		if (enableDeletedFilter) {
			where.and();
			where.eq(BO.fields.DELETED, false);
		}
		// else
		// where.ne(BO.fields.ID, -1);// TODO: убрать - сделать стек фильтров
		return where;
	}

	protected List<TypeBO> NonDeletedFilter(List<TypeBO> all) throws Exception {
		List<TypeBO> filtered = SynchronizationFilter(all);
		if (enableDeletedFilter)
			filtered.removeIf(p -> ((BO) p).deleted == true);
		return filtered;
	}

	protected Where<TypeBO, Integer> SynchronizationFilter(Where<TypeBO, Integer> where) throws SQLException {
		// Это выглядит (`sync_flag` = 555555 OR `sync_flag` = 0 ) так что норм
		// TODO: check bo sync_flag
		where.eq(BO.fields.SYNC_FLAG, Global.sync_flag);
		// При синхронизации берем с текущим флагом и те что прошли
		// Плохие синхронизации отсекаем - смотрим только через SQL
		if (!enableSyncFilter) {
			where.or();
			where.eq(BO.fields.SYNC_FLAG, 0);
		}
		return RoleFilter(where);
	}

	protected List<TypeBO> SynchronizationFilter(List<TypeBO> all) throws Exception {
		List<TypeBO> filtered = new ArrayList<>();
		for (BO item : (List<BO>) all)
			if (item.sync_flag == Global.sync_flag || (!enableSyncFilter && item.sync_flag == 0))
				filtered.add((TypeBO) item);

		return RoleFilter(filtered);
	}

	// TODO: возможно фильтровать уже на АРМе (List<TypeBO>
	// RoleFilter(List<TypeBO> all) ) или такой ф-й на сервере приложений
	protected Where<TypeBO, Integer> RoleFilter(Where<TypeBO, Integer> where) throws SQLException {
		// where.and();
		// where.eq(BO.fields.DELETED, false);
		return where;
	}

	protected List<TypeBO> RoleFilter(List<TypeBO> all) throws Exception {
		List<TypeBO> filtered = new ArrayList<>();
		// for (BO item : (List<BO>) all)
		// if (item.sync_flag == Global.sync_flag || (!enableSyncFilter &&
		// item.sync_flag == 0))
		// filtered.add((TypeBO) item);
		return all;
	}

	public TypeBO GetById(int id) {
		return GetById(id, 0, true);
	}

	public TypeBO GetByIdFromDb(int id) {
		return GetById(id, 0, false);
	}

	// TODO:???
	protected GenericRowMapper mapper;

	public TypeBO GetById(int id, int level, boolean fromCache) {
		try {
			// log.DEBUG("CreateBO", "BO.GetById " + typeBO.getName() + " " +
			// id);
			if (fromCache) {
				TypeBO obj = (TypeBO) Cache.I().getById(typeBO, id, level);
				if (obj != null)
					return obj;
			}

			Dao<TypeBO, Integer> boDao = DbHelper.geDaos(typeBO);
			QueryBuilder<TypeBO, Integer> builder = boDao.queryBuilder();
			Where<TypeBO, Integer> where = builder.where();
			NonDeletedFilter(where);
			where.and();
			where.eq(BO.fields.ID, id);
			// System.out.println(builder.prepareStatementString());
			// return builder.queryForFirst();

			mapper.level = level;
			// System.out.println("BO.GetById " + typeBO.getName() + " " +
			// level);
			// Для GenericRowMapper GetById
			mapper.setEntityClass(typeBO);
			// Profiler profiler = new Profiler();
			// profiler.Start(typeBO.getName() + ".GetById_FromDb");
			List<TypeBO> list = boDao.queryRaw(builder.prepareStatementString(), mapper).getResults();
			// profiler.Stop(typeBO.getName() + ".GetById_FromDb");
			// profiler.PrintElapsed(typeBO.getName() + ".GetById_FromDb");

			TypeBO obj = null;
			if (list.size() != 0)
				obj = list.get(0);
			// TODO: проверяем changed. Возможно перед получением из БД.
			Cache.I().putInMap(typeBO, (BO) obj);

			return obj;

		} catch (SQLException e) {
			log.ERROR("GetById", e);
		}
		return null;
	}

	protected QueryBuilder<TypeBO, Integer> getQueryBuilder() {
		return getQueryBuilderT(typeBO);
	}

	protected <T> QueryBuilder<T, Integer> getQueryBuilderT(Class<T> type) {
		// System.out.println("getQueryBuilderT " + type == null ? null :
		// type.getName());
		// TODO: getQueryBuilderT by transaction dao
		Dao<T, Integer> boDao = DbHelper.geDaos(type);
		return boDao.queryBuilder();
	}

	protected <T> DeleteBuilder<T, Integer> getDeleteBuilder() {
		Dao<T, Integer> boDao = DbHelper.geDaos(typeBO);
		return boDao.deleteBuilder();
	}

	protected <T> DeleteBuilder<T, Integer> getDeleteBuilderT(Class<T> type) {
		Dao<T, Integer> boDao = DbHelper.geDaos(type);
		return boDao.deleteBuilder();
	}

	public UpdateBuilder<TypeBO, Integer> getUpdateBuilder() {
		return getUpdateBuilder(DbHelper.geDaos(typeBO));
	}

	public UpdateBuilder<TypeBO, Integer> getUpdateBuilder(Dao<TypeBO, Integer> boDao) {
		return boDao.updateBuilder();
	}

	public static <TypeBO> Where<TypeBO, Integer> CreateMultipleLike(String fieldName, String value,
			Where<TypeBO, Integer> where) throws SQLException {

		String[] values = value.split(" ");
		// Where<TypeBO, Integer> where = builder.where();

		// selectArg = new SelectArg();
		// selectArg.setValue("%" + values[0] + "%");
		// where.like(CatalogueBO.fields.NAME, selectArg);

		for (int pos = 0; pos < values.length; pos++) {
			SelectArg selectArg = new SelectArg();
			selectArg.setValue("%" + values[pos] + "%");
			if (pos > 0)
				where.and();
			where.like(fieldName, selectArg);
		}

		return where;
	}

	public List<TypeBO> GetAll(String param, Object value) {
		try {
			QueryBuilder<TypeBO, Integer> builder = getQueryBuilder();
			Where<TypeBO, Integer> where = builder.where();
			NonDeletedFilter(where);
			where.and();
			where.eq(param, value);
			return builder.query();
		} catch (Exception e) {
			log.ERROR("GetAll", e);
		}
		return new ArrayList<>();
	}

	public List<TypeBO> GetAllContains(String param, Object value) {
		try {
			QueryBuilder<TypeBO, Integer> builder = getQueryBuilder();
			Where<TypeBO, Integer> where = builder.where();
			SynchronizationFilter(where);
			where.and();
			SelectArg selectArg = new SelectArg();
			where.like(param, selectArg);
			selectArg.setValue("%" + value + "%");
			return builder.query(); // returns list of ten items
		} catch (Exception e) {
			log.ERROR("GetAllContains", e);
		}
		return null;
	}

	public int ClearSyncFlag(long flag) {
		try {
			UpdateBuilder<TypeBO, Integer> builder = getUpdateBuilder();
			Where<TypeBO, Integer> where = builder.where();
			where.eq(BO.fields.SYNC_FLAG, flag);
			builder.updateColumnValue(BO.fields.SYNC_FLAG, 0);
			// TODO: builder.updateColumnValue(BO.fields.CHANGED, changed);

			// TODO: Cache.I().putInList(typeBO, this); ???
			return builder.update();
		} catch (Exception e) {
			log.ERROR("ClearSyncFlag", e);
		}
		return -1;
	}

	public TypeBO GetByUUID(String uuid) {
		return GetByUUID(UUID.fromString(uuid));
	}

	public TypeBO GetByUUID(UUID uuid) {
		try {
			// TODO: BO sync_flag
			if (Global.sync_flag != 0) {

				TypeBO bo = (TypeBO) Cache.I().getByUUID(typeBO, uuid);
				// TODO: при синхронизации делаем HeatingUp
				// if (bo != null)
				return bo;
			}

			Dao<TypeBO, Integer> boDao = DbHelper.geDaos(typeBO);
			QueryBuilder<TypeBO, Integer> builder = boDao.queryBuilder();
			Where<TypeBO, Integer> where = builder.where();
			NonDeletedFilter(where);
			where.and();
			where.eq(BO.fields.UUID, uuid);
			// System.out.println(builder.prepareStatementString());
			// return builder.queryForFirst();

			// Для GenericRowMapper GetById
			List<TypeBO> list = boDao.queryRaw(builder.prepareStatementString(), GenericRowMapper.get(typeBO))
					.getResults();
			if (list.size() == 0)
				return null;
			else
				return list.get(0);

		} catch (Exception e) {
			log.ERROR("GetByUUID", e);
			log.ERROR("GetByUUID", typeBO.getName());
		}
		return null;
	}

	public boolean CheckCodeExists(BO bo, String code) {
		try {
			Dao<TypeBO, Integer> boDao = DbHelper.geDaos(typeBO);
			QueryBuilder<TypeBO, Integer> builder = boDao.queryBuilder();
			Where<TypeBO, Integer> where = builder.where();
			NonDeletedFilter(where);
			where.and();
			where.ne(BO.fields.ID, bo.id);
			where.and();
			where.eq(BO.fields.CODE, code);

			builder.setCountOf(true);
			if (builder.countOf() == 0)
				return false;

		} catch (Exception e) {
			log.ERROR("CheckCodeExists", e);
		}

		return true;
	}

	public TypeBO GetByIdNewCore(int id) {
		try {
			// TODO: list, cache
			Dao<TypeBO, Integer> boDao = DbHelper.geDaos(typeBO);
			QueryBuilder<TypeBO, Integer> builder = boDao.queryBuilder();
			Where<TypeBO, Integer> where = builder.where();
			NonDeletedFilter(where);
			where.and();
			where.eq(BO.fields.ID, id);
			// System.out.println(builder.prepareStatementString());
			// return builder.queryForFirst();

			// Для GenericRowMapper GetById
			List<TypeBO> list = boDao.queryRaw(builder.prepareStatementString(), GenericRowMapper.get(typeBO, true))
					.getResults();
			if (list.size() == 0)
				return null;
			else
				return list.get(0);

		} catch (SQLException e) {
			log.ERROR("GetByIdNew", e);
		}

		return null;
	}

	public TypeBO GetByParam(String param, Object value) {
		try {
			QueryBuilder<TypeBO, Integer> builder = getQueryBuilder();
			Where<TypeBO, Integer> where = builder.where();
			NonDeletedFilter(where);
			where.and();
			where.eq(param, value);
			return builder.queryForFirst();
		} catch (Exception e) {
			log.ERROR("GetByParam", e);
		}
		return null;
	}

	public List<TypeBO> SelectByParam(String param, Object value) {
		try {
			QueryBuilder<TypeBO, Integer> builder = getQueryBuilder();
			Where<TypeBO, Integer> where = builder.where();
			NonDeletedFilter(where);
			where.and();
			where.eq(param, value);
			return builder.query();
		} catch (Exception e) {
			log.ERROR("SelectByParam", e);
		}
		return null;
	}

	public TypeBO GetByCode(String code) {
		return GetByParam(BO.fields.CODE, code);
	}

	public List<TypeBO> GetAll() {
		try {
			QueryBuilder<TypeBO, Integer> builder = getQueryBuilder();
			Where<TypeBO, Integer> where = builder.where();
			NonDeletedFilter(where);
			return builder.query();
		} catch (Exception e) {
			// TODO: Table ... doesn't exist
			log.ERROR("GetAll", e);
		}

		return null;
	}

	public List<TypeBO> GetRange(int fromId, int toId) {
		try {
			QueryBuilder<TypeBO, Integer> builder = getQueryBuilder();
			Where<TypeBO, Integer> where = builder.where();
			NonDeletedFilter(where);
			where.and();
			where.ge(BO.fields.ID, fromId);
			where.and();
			where.le(BO.fields.ID, toId);
			return builder.query();
		} catch (Exception e) {
			// TODO: Table ... doesn't exist
			log.ERROR("GetRange", e);
		}

		return null;
	}

	public List<TypeBO> GetAll4Cache() {
		try {
			Date lastUpdate = Cache.I().lastUpdate.get(typeBO);// getClass()
			if (lastUpdate == null)
				lastUpdate = new Date(0);
			Dao<TypeBO, Integer> boDao = DbHelper.geDaos(typeBO);
			QueryBuilder<TypeBO, Integer> builder = getQueryBuilder();
			Where<TypeBO, Integer> where = builder.where();
			// NonDeletedFilter(where);
			SynchronizationFilter(where);
			where.and();
			where.ge(BO.fields.CHANGED, lastUpdate);

			// return builder.query();
			mapper.level = 1;
			// Для GenericRowMapper GetById
			mapper.setEntityClass(typeBO);
			List<TypeBO> list = boDao.queryRaw(builder.prepareStatementString(), mapper).getResults();
			Cache.I().lastUpdate.put(getClass(), new Date());

			if (typeBO.newInstance() instanceof CatalogueBO)
				for (TypeBO item : list) {
					Set<String> words = WordUtils.WordsFromPhrases(
							Arrays.asList(new String[] { ((CatalogueBO) item).full_name.trim().toLowerCase() }), true);
					((BO) item).calcFields.put(BO.fields.CALC_FULL_NAME_WORDS, words);
				}
			return list;
		} catch (Exception e) {
			// TODO: Table ... doesn't exist
			log.ERROR("GetAll4Cache", e);
		}
		return null;
	}

	public List<TypeBO> GetPage(long currentPage, long pageSize) {
		try {
			QueryBuilder<TypeBO, Integer> builder = getQueryBuilder();
			Where<TypeBO, Integer> where = builder.where();
			NonDeletedFilter(where);
			builder.offset(currentPage * pageSize).limit(pageSize);
			return builder.query();
		} catch (Exception e) {
			log.ERROR("GetPage", e);
		}

		return null;
	}

	public List<TypeBO> GetPageByWords(long currentPage, long pageSize, String value) {
		try {
			QueryBuilder<TypeBO, Integer> builder = getQueryBuilder();
			Where<TypeBO, Integer> where = builder.where();
			// NonDeletedFilter(where);
			SynchronizationFilter(where);
			where.and();
			CreateMultipleLike(CatalogueBO.fields.NAME, value, where);
			builder.offset(currentPage * pageSize).limit(pageSize);
			return builder.query(); // returns list of ten items
		} catch (Exception e) {
			log.ERROR("GetPageByWords", e);
		}

		return new ArrayList<>();
	}

	public static long GetPagesCount(long count, long pageSize) {
		return count / pageSize + ((count % pageSize != 0) ? 1 : 0);
	}

	public long GetCount() {
		try {
			QueryBuilder<TypeBO, Integer> builder = getQueryBuilder();
			Where<TypeBO, Integer> where = builder.where();
			NonDeletedFilter(where);
			builder.setCountOf(true);
			return builder.countOf();
		} catch (Exception e) {
			log.ERROR("GetCount", e);
		}
		return 0;
	}

	public long GetCountByWords(String value) {
		try {
			QueryBuilder<TypeBO, Integer> builder = getQueryBuilder();
			Where<TypeBO, Integer> where = builder.where();
			// NonDeletedFilter(where);
			SynchronizationFilter(where);
			where.and();
			CreateMultipleLike(CatalogueBO.fields.NAME, value, where);
			builder.setCountOf(true);
			return builder.countOf();
		} catch (Exception e) {
			log.ERROR("GetCountByWords", e);
		}

		return 0;
	}

	public BO UpdateUUID(BO bo, UUID uuid) throws Exception {
		UpdateBuilder<TypeBO, Integer> builder = getUpdateBuilder();
		Where<TypeBO, Integer> where = builder.where();
		where.eq(BO.fields.ID, bo.id);
		builder.updateColumnValue(BO.fields.UUID, uuid);
		// Это поле не надо, чтобы 1с заново не принимало
		// builder.updateColumnValue(BO.fields.CHANGED, new Date());
		builder.updateColumnValue(BO.fields.CHANGED_BY, User.current);
		builder.update();

		bo.uuid = uuid;
		bo.changed_by = User.current;
		// TODO: если сбой будет ли в памяти и в БД разные данные?
		Cache.I().putInMap(typeBO, bo);
		// TODO: link
		return bo;
	}

	public TypeBO setDeleted(TypeBO bo, boolean deleted) throws Exception {
		return setDeletedByParameter(bo, BO.fields.ID, ((BO) bo).id, deleted);
	}

	public TypeBO setDeletedByParameter(TypeBO bo, String param, Object value, boolean deleted) throws Exception {

		UpdateBuilder<TypeBO, Integer> builder = getUpdateBuilder();
		Where<TypeBO, Integer> where = builder.where();
		where.eq(param, value);
		// параметр ф-ии, а не поле объекта
		builder.updateColumnValue(BO.fields.DELETED, deleted);

		// Ставим для синхронизации с 1С
		((BO) bo).changed = new Date();
		builder.updateColumnValue(BO.fields.CHANGED, ((BO) bo).changed);
		builder.updateColumnValue(BO.fields.CHANGED_BY, User.current);
		builder.update();

		// Устанавливаем в объекте. Если Exception, то локальный так же не
		// изменится
		((BO) bo).deleted = deleted;
		((BO) bo).changed_by = User.current;

		// TODO: если сбой будет ли в памяти и в БД разные данные?
		Cache.I().putInMap(typeBO, ((BO) bo));

		// TODO: link
		return bo;
	}

	public BO setLockedBy(BO bo, User locked_by) throws Exception {
		UpdateBuilder<TypeBO, Integer> builder = getUpdateBuilder();
		Where<TypeBO, Integer> where = builder.where();
		where.eq(BO.fields.ID, bo.id);

		// TODO: Ставим для синхронизации с 1С ???
		// builder.updateColumnValue(BO.fields.CHANGED, new Date());
		builder.updateColumnValue(BO.fields.LOCKED_BY, locked_by.id);
		bo.changed = new Date();
		builder.updateColumnValue(BO.fields.CHANGED, bo.changed);
		builder.updateColumnValue(BO.fields.CHANGED_BY, User.current);
		builder.update();

		// Устанавливаем в объекте. Если Exception, то локальный так же не
		// изменится
		bo.locked_by = locked_by;
		bo.changed_by = User.current;

		// TODO: если сбой будет ли в памяти и в БД разные данные?
		Cache.I().putInMap(typeBO, bo);

		// TODO: link
		return bo;
	}

	public int DeleteByParameter(String param, Object value) {
		Dao<TypeBO, Integer> boDao = DbHelper.geDaos(typeBO);
		try {
			DeleteBuilder<TypeBO, Integer> deleteBuilder = boDao.deleteBuilder();
			deleteBuilder.where().eq(param, value);
			return deleteBuilder.delete();
		} catch (Exception e) {
			log.ERROR("DeleteByParameter", e);
		}
		return 0;
	}

	public static int maxPerTransation = 1000;

	public <T> void BulkSave(final List<T> all) throws Exception {
		BulkSave(all, maxPerTransation);
	}

	// public <T> void BulkSave(final List<T> all, int maxPerTransation) throws
	// Exception {
	// Profiler profiler = new Profiler();
	// profiler.Start("BulkSave");
	// if (all.size() == 0) {
	// profiler.Stop("BulkSave");
	// profiler.PrintElapsed("BulkSave");
	// return;
	// }
	// int pagesCount = (int) GetPagesCount(all.size(), maxPerTransation);
	// for (int p = 0; p < pagesCount; p++) {
	// System.out.println(p * maxPerTransation + " из " + all.size());
	// if (Global.InMemoryGroupTransaction)
	// log.CONSOLE(p * maxPerTransation + " из " + all.size());
	// final int page = p;
	// TransactionManager.callInTransaction(DbHelper.getConnectionSource(), new
	// Callable<Void>() {
	// @SuppressWarnings("unchecked")
	// @Override
	// public Void call() throws Exception {
	// int end = Math.min(all.size(), (page + 1) * maxPerTransation);
	// for (int i = page * maxPerTransation; i < end; i++)
	// Save((TypeBO) all.get(i));
	// return null;
	// }
	// });
	// }
	// profiler.Stop("BulkSave");
	// profiler.PrintElapsed("BulkSave");
	// }

	public <T> boolean BulkSave(List<T> all, int maxPerTransation) throws Exception {
		boolean retVal = false;
		Profiler profiler = new Profiler();
		profiler.Start("BulkSave");

		Dao<T, Integer> dao = (Dao<T, Integer>) DbHelper.geDaos(typeBO);
		DatabaseConnection conn = dao.startThreadConnection();
		Savepoint savepoint = null;

		int pagesCount = (int) GetPagesCount(all.size(), maxPerTransation);

		conn.setAutoCommit(false);
		for (int p = 0; p < pagesCount; p++) {

			Global.CheckAndPause();

			System.out.println(p * maxPerTransation + " из " + all.size());
			if (Global.InMemoryGroupTransaction)
				Console.I().INFO(getClass(), "BulkSave", (p * maxPerTransation + " из " + all.size()));
			try {

				// TODO: + User + ip
				savepoint = conn.setSavePoint(typeBO.getSimpleName() + "_BulkSave_" + System.currentTimeMillis());
				int end = Math.min(all.size(), (p + 1) * maxPerTransation);
				for (int i = p * maxPerTransation; i < end; i++)
					Save((TypeBO) all.get(i), true);
			} catch (Exception e) {
				log.ERROR("saveBulkData", e);
			} finally {
				try {
					conn.commit(savepoint);
					dao.endThreadConnection(conn);
					retVal = true;
				} catch (Exception e) {
					log.ERROR("saveBulkData", e);
				}
			}
		}
		// TODO: conn.rollback(savepoint);
		conn.setAutoCommit(true);

		profiler.Stop("BulkSave");
		profiler.PrintElapsed("BulkSave");

		return retVal;
	}

	public void ClearTable() {
		// OrmLiteSqliteOpenHelper myDbHelper = DbHelper.getHelper();
		try {
			Dao<TypeBO, Integer> dao = DbHelper.geDaos(typeBO);
			DatabaseTable dt = (DatabaseTable) typeBO.getAnnotation(DatabaseTable.class);
			String tableName = typeBO.getSimpleName();
			if (dt != null)
				tableName = dt.tableName();
			dao.executeRawNoArgs("TRUNCATE " + tableName);
			// SQLiteDatabase db = myDbHelper.getWritableDatabase();
			// String TABLE_NAME = bo.getClass().getSimpleName().toLowerCase();
			// db.delete(TABLE_NAME, null, null);
			// db.delete("SQLITE_SEQUENCE", "NAME = ?", new String[] {
			// TABLE_NAME
			// });
		} catch (Exception exp) {
			log.ERROR("ClearTable", exp);
		}
	}

	public List<TypeBO> getActualTable() {
		try {
			// Выбираем добавленные, а не из текущей синхронизации
			// Выбираем и удаленные,т.к. принимаем и их из 1с
			ForSync();
			// (Collection<? extends DocumentBO>)
			return GetAll();
		} catch (Exception e) {
			log.ERROR("getActualTable", e);
		}
		return new ArrayList<>();
	}

	public boolean CheckDeleted(BO bo) throws Exception {
		QueryBuilder<TypeBO, Integer> builder = getQueryBuilder();
		Where<TypeBO, Integer> where = builder.where();
		SynchronizationFilter(where);
		where.and();
		where.eq(BO.fields.ID, bo.id);
		builder = builder.selectColumns(BO.fields.DELETED);
		return ((BO) builder.queryForFirst()).deleted;
	}

	public static String getTableName(Class<?> type) {
		// if (typeBO == null)
		// return typeBO.getSimpleName().toLowerCase()
		DatabaseTable table = type.getAnnotation(DatabaseTable.class);
		String tableName = (table == null) ? type.getSimpleName().toLowerCase() : table.tableName();
		return tableName;
	}

	public String getTableName() {
		return getTableName(typeBO);
	}

	public List GetByPeriod(Date startDate, Date endDate) {
		try {
			QueryBuilder<TypeBO, Integer> builder = getQueryBuilder();
			Where<TypeBO, Integer> where = builder.where();
			NonDeletedFilter(where);
			where.and();
			where.ge(BO.fields.CREATED, startDate);
			where.and();
			where.le(BO.fields.CREATED, endDate);
			return builder.query();
		} catch (Exception e) {
			log.ERROR("GetByPeriod", e);
		}
		return new ArrayList<>();
	}
}