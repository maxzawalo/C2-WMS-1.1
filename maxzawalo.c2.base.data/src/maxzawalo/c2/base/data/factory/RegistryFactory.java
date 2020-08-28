package maxzawalo.c2.base.data.factory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

import maxzawalo.c2.base.FunctionC2;
import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.base.bo.registry.Registry;
import maxzawalo.c2.base.bo.registry.Registry.fields;
import maxzawalo.c2.base.data.DbHelper;
import maxzawalo.c2.base.interfaces.FilterT;
import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.base.utils.Global;
import maxzawalo.c2.base.utils.ListUtils;
import maxzawalo.c2.free.bo.registry.AccAcc;
import maxzawalo.c2.free.bo.registry.RegistryProduct;
import maxzawalo.c2.free.cache.Cache;

public class RegistryFactory<T> extends FactoryBO<T> {
	public static final int IN_MEMORY_CACHE_TIME_SEC = 24 * 60 * 60;

	public static String listGroupTransactionKey(Class type) {
		return listGroupTransactionKey(type, "");
	}

	public static String listGroupTransactionKey(Class type, String postfix) {
		return "GroupTransaction_" + type.getName() + postfix;
	}

	public static String grouptransactionRegGactory(Class type) {
		return "GroupTransactionRegGactory_" + type.getName();
	}

	public RegistryFactory() {
		DeleteFilterOn();
	}

	public RegistryFactory Create(Class typeBO) {
		RegistryFactory factory = (RegistryFactory) super.Create(typeBO);
		// listGroupTransactionKey = "GroupTransaction_" + typeBO;
		return factory;
	}

	// TODO: test code filter
	protected void RegistratorFilter(T r, Where<T, Integer> where) throws SQLException {
		NonDeletedFilter(where);
		where.and();
		where.eq(Registry.fields.REG_TYPE, ((Registry) r).reg_type);
		where.and();
		where.eq(Registry.fields.REG_ID, ((Registry) r).reg_id);
	}

	/**
	 * Администраторский
	 * 
	 * @param where
	 * @throws SQLException
	 */
	protected void RegistratorFilterAll(Registry r, Where<T, Integer> where) throws SQLException {
		where.eq(Registry.fields.REG_TYPE, r.reg_type);
		where.and();
		where.eq(Registry.fields.REG_ID, r.reg_id);
	}

	protected void ItemFilter(T r, Where<T, Integer> where) throws SQLException {
		RegistratorFilter(r, where);
	}

	/**
	 * В этот фильтр надо пихать все Измерения
	 * 
	 * @param where
	 * @param filterItem
	 * @throws SQLException
	 */
	protected void BalanceFilter(Where<T, Integer> where, T filterItem) throws SQLException {
		// Баланс не зависист от Регистратора. Он общий для всех.
		NonDeletedFilter(where);
	}

	protected List<T> BalanceFilter(List<T> all, T filterItem) throws Exception {
		// Баланс не зависист от Регистратора. Он общий для всех.
		return NonDeletedFilter(all);
	}

	public int RemoveEntries(final T r, boolean AccTransactionOnly) throws SQLException {
		// TODO: MinusEntries - удаляет только отрицательные. PlusEntries -
		// положительные.
		// TODO: Так как эти функции выполняются в разных док-х и
		// функциях проведения (теперь это не так)

		if (AccTransactionOnly) {
			// TODO: Не должно быть тут класса наследника
			if (BO.instanceOf(typeBO, RegistryProduct.class))
				return 0;
		}

		if (Global.InMemoryGroupTransaction) {
			List<T> all = Cache.I().getList(listGroupTransactionKey(typeBO));
			if (all == null) {
				System.out.println("all == null: " + typeBO);
				// ProductTransactionOnly
				if (r instanceof AccAcc)
					return 0;
			}

			for (T item : all)
				if (((Registry) item).reg_type == ((Registry) r).reg_type
						&& ((Registry) item).reg_id == ((Registry) r).reg_id) {
					// Помечаем на удаление вхождения документа в регистр -
					// потом эти проводки будут сохраняться
					((Registry) item).deleted = true;
				}
			Cache.I().putList(listGroupTransactionKey(typeBO), all, IN_MEMORY_CACHE_TIME_SEC);
		} else {
			Dao<T, Integer> boDao = DbHelper.geDaos(typeBO);
			UpdateBuilder<T, Integer> builder = boDao.updateBuilder();
			// параметр ф-ии, а не поле объекта
			builder.updateColumnValue(BO.fields.DELETED, true);
			RegistratorFilter(r, builder.where());
			((Registry) r).changed = new Date();
			builder.updateColumnValue(BO.fields.CHANGED, ((Registry) r).changed);
			builder.updateColumnValue(BO.fields.CHANGED_BY, User.current);
			int res = builder.update();
			((Registry) r).changed_by = User.current;

			FilterT RegistryCacheFilter = new FilterT<Registry>() {
				public boolean Check(Registry item) {
					return (item.reg_type == ((Registry) r).reg_type && item.reg_id == ((Registry) r).reg_id);// &&
					// item.deleted);
				}
			};

			return res;
		}
		// или все deleted
		// Cache.I().removeFromList(this.getClass(), RegistryCacheFilter);
		return 0;
	}

	public double SelectBalanceWithNewEntry(T newEntry) throws Exception {
		// TODO: cache
		List<T> filtered = new ArrayList<>();
		if (Global.InMemoryGroupTransaction) {
			// В этот фильтр надо пихать все Измерения
			List<T> all = Cache.I().getList(listGroupTransactionKey(typeBO));
			filtered = BalanceFilter(all, newEntry);
		} else {
			Dao<T, Integer> boDao = DbHelper.geDaos(typeBO);
			QueryBuilder<T, Integer> builder = boDao.queryBuilder();
			// b.selectRaw("SUM(" + "count" + ")");
			// b.groupBy(UsageStats.COLUMN_TYPE);
			// // b.where().eq(UsageStats.COLUMN_TYPE, type.toString());
			// boDao.queryRawValue(b.prepareStatementString());

			BalanceFilter(builder.where(), newEntry);
			filtered = boDao.query(builder.prepare());
		}

		return CalcBalanceWithNewEntry(filtered, newEntry);
	}

	protected double CalcBalanceWithNewEntry(List<T> items, T newEntry) {
		return 0;
	}

	public double SelectBalanceByEntry(T filterItem) throws Exception {
		List<T> items = new ArrayList<>();
		Dao<T, Integer> boDao = DbHelper.geDaos(typeBO);
		QueryBuilder<T, Integer> builder = boDao.queryBuilder();
		BalanceFilter(builder.where(), filterItem);
		items = boDao.query(builder.prepare());

		return CalcBalanceByEntry(items, filterItem);
	}

	protected double CalcBalanceByEntry(List<T> items, T filterItem) {
		return 0;
	}

	public boolean PlusEntries(final Registry r, final List<T> entries) {
		try {
			List<T> all = Cache.I().getList(listGroupTransactionKey(r.getClass()));
			for (T item : entries) {
				((Registry) r).SetRegIdAndType(item);
				((BO) item).id = 0;
			}
			if (Global.InMemoryGroupTransaction) {
				for (T item : entries) {
					all.add(item);
					AddEntry2Balance(r, item, false);
				}
			} else
				BulkSave(entries);

			if (Global.InMemoryGroupTransaction)
				Cache.I().putList(listGroupTransactionKey(r.getClass()), all, IN_MEMORY_CACHE_TIME_SEC);
			return true;
		} catch (Exception e) {
			log.ERROR("PlusEntries", e);
			return false;
		}
	}

	protected void AddEntry2Balance(Registry r, T item, boolean isMinus) {
		List<T> balance = Cache.I().getList(listGroupTransactionKey(r.getClass(), "balance"));
		if (balance == null)
			balance = new ArrayList<>();
		// TODO: memory !!!
		// Клонируем чтобы проводки не занулять
		balance.add((T) ((BO) item).cloneObject());
		Cache.I().putList(listGroupTransactionKey(r.getClass(), "balance"), balance, IN_MEMORY_CACHE_TIME_SEC);
	}

	// Все проводки в базе. Даже удаленные.
	public List<T> getAllRegEntries(Registry r) throws SQLException {
		Dao<T, Integer> boDao = DbHelper.geDaos(typeBO);
		QueryBuilder<T, Integer> builder = boDao.queryBuilder();
		RegistratorFilterAll(r, builder.where());
		return boDao.query(builder.prepare());
	}

	protected void AbsResource(T item) {
	}

	protected void ReverseResource(T item) {
	}

	public boolean BeforeTransaction(Registry r, boolean AccTransactionOnly) {
		typeBO = (Class<T>) r.getClass();
		try {
			RemoveEntries((T) r, AccTransactionOnly);
		} catch (Exception e) {
			log.ERROR("BeforeTransaction", e);
			return false;
		}
		return true;
	}

	public boolean MinusEntries(final Registry r, final List<T> entries) {
		try {
			List<T> all = Cache.I().getList(listGroupTransactionKey(typeBO));
			for (T item : entries) {
				if (!Global.canBalanceBeMinus && SelectBalanceWithNewEntry(item) < 0) {
					// TODO: конкретно в какой записи
					Registry rp = (Registry) item;
					String message = NotEnoughItemMessage(rp);
					log.ERROR("MinusEntries", message);
					Console.I().ERROR(getClass(), "MinusEntries", message);
					throw new SQLException("Отрицательный баланс");
				}
			}
			for (T item : entries) {
				ReverseResource(item);
				r.SetRegIdAndType(item);
				((BO) item).id = 0;
			}
			if (Global.InMemoryGroupTransaction) {
				for (T item : entries) {
					all.add(item);
					AddEntry2Balance(r, item, true);
				}
			} else
				BulkSave(entries);

			if (Global.InMemoryGroupTransaction)
				Cache.I().putList(listGroupTransactionKey(typeBO), all, IN_MEMORY_CACHE_TIME_SEC);
			return true;
		} catch (Exception e) {
			log.ERROR("MinusEntries", e);
			return false;
		}
	}

	public String NotEnoughItemMessage(Registry r) {
		return "";
	}

	// public <T> boolean GroupTransactionT(List<T> entries, Date startDate, Date
	// endDate) {
	// List<DocumentBO> loc_entries = new ArrayList<>();
	// loc_entries.addAll((Collection<? extends DocumentBO>) entries);
	// return GroupTransaction(loc_entries, startDate, endDate);
	// }

	// public void setRPFactory(DocumentFactory factory) {
	// }

	public void ReplaceDocUsedReg(DocumentBO doc) {
	}

	/**
	 * Для теста
	 *
	 * @return
	 * @throws Exception
	 */
	public long getDocEntriesCount(T r) throws Exception {
		Dao<T, Integer> boDao = DbHelper.geDaos(typeBO);
		QueryBuilder<T, Integer> builder = boDao.queryBuilder();
		RegistratorFilter(r, builder.where());

		builder.setCountOf(true);
		return builder.countOf();
	}

	@Override
	protected T GenerateCode(T bo) throws Exception {
		((BO) bo).code = "";
		return bo;
	}

	@Override
	public boolean CheckCodeExists(BO bo, String code) {
		return false;
	}

	// TODO: fromDate не нужен - иначе не попадут предыдущие транзакции
	// public List<T> SelectEntriesByPeriod(Registry r, Date fromDate, Date
	// toDate) throws Exception {
	// Dao<T, Integer> boDao = DbHelper.geDaos(typeBO);
	// QueryBuilder<T, Integer> builder = boDao.queryBuilder();
	// Where<T, Integer> where = builder.where();
	// NonDeletedFilter(where);
	// where.and();
	// where.eq("reg_type", r.reg_type);
	// where.and();
	// where.ge("reg_date", fromDate);
	// where.and();
	// where.le("reg_date", toDate);
	// // builder.orderBy("reg_date", true);
	//
	// List<T> all = boDao.query(builder.prepare());
	//
	// return all;
	// }

	public List<T> SelectDocEntries(Registry r) throws Exception {
		// TODO: from cache Global.InMemoryGroupTransaction?
		// Dao<T, Integer> boDao = DbHelper.geDaos(typeBO);

		QueryBuilder<T, Integer> builder = getQueryBuilder();
		RegistratorFilter((T) r, builder.where());

		// log.DEBUG("SelectDocEntries", builder.prepareStatementString());
		List<T> all = builder.query();
		// if (reverse) {
		// for (T item : all)
		// ReverseResource(item);
		// }
		return all;
	}

	public int DeleteByPeriod(Date startDate, Date endDate) {
		try {
			UpdateBuilder<T, Integer> builder = getUpdateBuilder();
			Where<T, Integer> where = builder.where();
			where.ge(Registry.fields.REG_DATE, startDate);
			where.and();
			where.le(Registry.fields.REG_DATE, endDate);
			builder.updateColumnValue(BO.fields.DELETED, true);
			return builder.update();
		} catch (Exception e) {
			log.ERROR("DeleteByPeriod", e);
		}
		return 0;
	}

	public void Load2Cache(Date fromDate, Date toDate, Registry r) {
		try {
			QueryBuilder<T, Integer> builder = getQueryBuilder();
			Where<T, Integer> where = builder.where();
			NonDeletedFilter(where);

			FromDateFilter(fromDate, where);

			where.and();
			where.le(Registry.fields.REG_DATE, toDate);
			System.out.println(builder.prepareStatementString());
			List<T> all = builder.query();
			AfterLoad(all);
			Cache.I().putList(listGroupTransactionKey(typeBO), all, IN_MEMORY_CACHE_TIME_SEC);
			Add2BalanceCache(fromDate, all);
		} catch (Exception e) {
			log.ERROR("Load2Cache", e);
		}
	}

	protected void Add2BalanceCache(Date fromDate, List<T> all) {
		// Добавляем проводки в кэш баланс, т.к. в процессе берутся проводки
		// после начала периода
		// TODO: надо ли в Бух Регистрах
		List<T> balance = new ArrayList<>();
		for (T item : all)
			if (((Registry) item).reg_date.getTime() < fromDate.getTime())
				balance.add((T) ((BO) item).cloneObject());
		Cache.I().putList(listGroupTransactionKey(typeBO, "balance"), balance, IN_MEMORY_CACHE_TIME_SEC);
	}

	protected void FromDateFilter(Date fromDate, Where<T, Integer> where) throws SQLException {

	}

	protected void AfterLoad(List<T> all) {

	}

	@Override
	public List GetByPeriod(Date startDate, Date endDate) {
		try {
			QueryBuilder<T, Integer> builder = getQueryBuilder();
			Where<T, Integer> where = builder.where();
			NonDeletedFilter(where);
			where.and();
			where.ge(fields.REG_DATE, Format.beginOfDay(startDate));
			where.and();
			where.le(fields.REG_DATE, Format.endOfDay(endDate));
			System.out.println(builder.prepareStatementString());
			return builder.query();
		} catch (Exception e) {
			log.ERROR("GetByPeriod", e);
		}
		return new ArrayList<>();
	}

	public static FunctionC2 SortRegistriesByDate = new FunctionC2() {
		{
			name = "SortRegistriesByDate";
			in_param_names = new String[] { "registries" };
			out_param_names = new String[] { "registries_sorted_by_date" };
		}

		@Override
		protected boolean FuncBody(Map<String, Object> params) throws Exception {
			List<Registry> trs = (List<Registry>) params.get("registries");
			trs = trs.stream()//
					.sorted((e1, e2) -> e1.reg_date.compareTo(e2.reg_date))//
					.collect(Collectors.toList());

			params.put("registries_sorted_by_date", trs);
			return true;
		}
	};

	public static FunctionC2 DistinctByRegistrator = new FunctionC2() {
		{
			name = "DistinctByRegistrator";
			in_param_names = new String[] { "registries" };
			out_param_names = new String[] { "registries_distinct_by_registrator" };
		}

		@Override
		protected boolean FuncBody(Map<String, Object> params) throws Exception {
			List<Registry> trs = (List<Registry>) params.get("registries");

			trs = trs.stream()//
					.filter(ListUtils.distinctByKey(t -> t.getRegistratorKey()))//
					.collect(Collectors.toList());

			params.put("registries_distinct_by_registrator", trs);
			return true;
		}
	};
}