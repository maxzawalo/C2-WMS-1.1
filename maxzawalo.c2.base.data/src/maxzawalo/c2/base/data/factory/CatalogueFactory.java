package maxzawalo.c2.base.data.factory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.CatalogueBO;
import maxzawalo.c2.base.data.DbHelper;
import maxzawalo.c2.base.data.GenericRowMapper;
import maxzawalo.c2.base.utils.ListUtils;
import maxzawalo.c2.base.utils.Profiler;
import maxzawalo.c2.base.utils.WordUtils;
import maxzawalo.c2.free.bo.Settings;
import maxzawalo.c2.free.cache.Cache;
import maxzawalo.c2.free.search.WordVector;

public class CatalogueFactory<TypeBO> extends FactoryBO<TypeBO> {

	public CatalogueFactory() {
		DeleteFilterOff();
	}

	public CatalogueFactory Create(Class typeBO) {
		this.typeBO = typeBO;
		mapper = new GenericRowMapper(typeBO);
		return this;
	}

	@Override
	public TypeBO GetById(int id, int level, boolean fromCache) {
		TypeBO obj = super.GetById(id, level, fromCache);
		if (obj != null) {
			((CatalogueBO) obj).parent = (BO) getParent(obj);
			if (((CatalogueBO) obj).root != null)
				((CatalogueBO) obj).root = (BO) GetById(((CatalogueBO) obj).root.id);
		}
		return obj;
	}

	@Override
	public TypeBO GetById(int id) {
		TypeBO obj = super.GetById(id);
		if (obj != null) {
			((CatalogueBO) obj).parent = (BO) getParent(obj);
			if (((CatalogueBO) obj).root != null)
				((CatalogueBO) obj).root = (BO) GetById(((CatalogueBO) obj).root.id);
		}
		return obj;
	}

	public TypeBO getParent(TypeBO obj) {
		if (((CatalogueBO) obj).parent != null)
			for (TypeBO parent : GetGroups())
				if (((BO) parent).id == ((CatalogueBO) obj).parent.id)
					return parent;

		return null;
	}

	public TypeBO getParent() {
		return getParent((TypeBO) this);
	}

	public List<TypeBO> GetGroups() {
		String key = typeBO.getName() + ".GetGroups";
		List<TypeBO> all = (List<TypeBO>) Cache.I().get(key);
		if (all == null) {
			System.out.println("TODO: GetGroups() from cache");
			System.out.println("TODO: GetGroups() childs разогрев + при обновлении кэша - ускорение");
			try {
				QueryBuilder<TypeBO, Integer> builder = (QueryBuilder<TypeBO, Integer>) getQueryBuilder();
				Where<TypeBO, Integer> where = builder.where();
				NonDeletedFilter(where);
				where.and();
				where.eq(CatalogueBO.fields.IS_GROUP, true);
				builder.orderBy(CatalogueBO.fields.NAME, true);
				all = builder.query();
				Map<Integer, CatalogueBO> map = new HashMap<>();
				for (TypeBO item : all)
					map.put(((BO) item).id, (CatalogueBO) item);

				for (CatalogueBO cat : map.values())
					if (cat.parent != null) {
						CatalogueBO parent = map.get(cat.parent.id);
						// Устанваливаем ссылку, а не ID
						cat.parent = parent;
						parent.childs.add(cat);
					}

				Cache.I().put(key, all, 60);
			} catch (Exception e) {
				log.ERROR("GetGroups", e);
			}
		}

		return all;
	}

	public void UpdateParent(CatalogueBO cat) throws Exception {
		if (cat.parent == null)
			return;
		Dao<TypeBO, Integer> boDao = DbHelper.geDaos(typeBO);
		UpdateBuilder<TypeBO, Integer> builder = boDao.updateBuilder();
		builder.where().eq(BO.fields.ID, cat.id);
		// TODO:check
		builder.updateColumnValue(CatalogueBO.fields.PARENT, cat.parent);
		builder.update();
	}

	public long GetCount(CatalogueBO catalogue, CatalogueBO parent, String searchData) {
		try {
			QueryBuilder<TypeBO, Integer> builder = getQueryBuilder();
			Where<TypeBO, Integer> where = builder.where();
			NonDeletedFilter(where);
			// SynchronizationFilter(where);
			where.and();
			where.eq(CatalogueBO.fields.IS_GROUP, false);
			String sql = where.getStatement();
			sql += ItemFilter(catalogue, parent, searchData);
			where = builder.where();
			where.raw(sql);
			builder.setCountOf(true);
			System.out.println("CatalogueBO.GetCount |" + builder.prepareStatementString());
			return builder.countOf();
		} catch (Exception e) {
			log.ERROR("GetCountByWords", e);
		}

		return 0;
	}

	public List<TypeBO> GetPageByFiltered(long currentPage, long pageSize, CatalogueBO catalogue, CatalogueBO parent,
			String searchData) {
		try {
			QueryBuilder<TypeBO, Integer> builder = getQueryBuilder();
			Where<TypeBO, Integer> where = builder.where();
			SynchronizationFilter(where);
			where.and();
			ElementFilter(catalogue, parent, searchData, where);
			builder.offset(currentPage * pageSize).limit(pageSize);
			return builder.query();
		} catch (Exception e) {
			log.ERROR("GetPageFiltered", e);
		}

		return new ArrayList<>();
	}

	public static String Set2String(Set<Integer> set) {
		return set.toString().replaceAll("\\[|\\]", "").replace(" ", "");
	}

	public Set<Integer> String2Set(String str) {
		Set<Integer> set = new HashSet<>();
		if (!str.equals("")) {
			for (String i : str.split(","))
				set.add(Integer.parseInt(i));
		}
		return set;
	}

	public String CreateMultipleLike(String fieldName, String value) {
		String[] values = value.split(" ");

		String where = "";
		for (int pos = 0; pos < values.length; pos++) {
			if (pos > 0)
				where += "AND ";
			where += fieldName + " LIKE '%" + values[pos] + "%' ";
		}
		return where;
	}

	public static List<CatalogueBO> GetChildGroups(CatalogueBO top) {
		List<CatalogueBO> parents = new ArrayList<>();
		parents.add(top);
		GetChilds(parents, Settings.maxLevel, 1);
		return parents;
	}

	public static List<CatalogueBO> GetChilds(List<CatalogueBO> parents, int maxLevel, int level) {
		List<CatalogueBO> childs = new ArrayList<>();
		if (level >= maxLevel)
			return childs;
		for (CatalogueBO parent : parents)
			childs.addAll(((CatalogueBO) parent).childs);

		parents.addAll(GetChilds(childs, maxLevel, level + 1));
		parents.addAll(childs);

		return childs;
	}

	public String ItemFilter(CatalogueBO catalogue, CatalogueBO parent, boolean selectChildGroups, String pref,
			String searchData) {
		Profiler profiler = new Profiler();
		profiler.Start("ItemFilter");

		searchData = searchData.replace("  ", " ").trim().toLowerCase();
		Class<?> type = catalogue.ReplaceType();

		String itemFilterCacheKey = type.getName() + "_" + (parent == null ? "" : parent.id) + "_" + selectChildGroups
				+ "_" + searchData;
		itemFilterCacheKey = itemFilterCacheKey.replace(" ", "_");
		String idsCacheKey = itemFilterCacheKey + "_ids";

		String sql = (String) Cache.I().get(itemFilterCacheKey);
		if (sql == null) {
			catalogue.fuzzy_ids.clear();
			sql = "";
			Set<Integer> parent_ids = new HashSet<>();
			sql = ParentFilter(parent, selectChildGroups, pref, searchData, parent_ids);
			if (searchData.trim().equals("")) {
				// TODO: для ИИ поиска при parent_ids.size() != 0 берем элементы
				// в
				// этих группах
			} else {
				String[] searchValues = searchData.split(" ");

				Map<Integer, String> names = new HashMap<>();

				profiler.Start("CreateNames");
				for (Entry<Integer, ?> entry : Cache.I().getMap(type).entrySet()) {
					CatalogueBO cat = (CatalogueBO) entry.getValue();
					if (!cat.is_group
							&& (parent_ids.size() == 0 || (cat.parent != null && parent_ids.contains(cat.parent.id))))
						names.put(entry.getKey(), cat.name.toLowerCase());
				}
				profiler.PrintCurrentElapsed("CreateNames");

				Map<Integer, Double> rating = new HashMap<>();
				Set<Integer> ids = new HashSet<>();
				for (Entry<Integer, String> name : names.entrySet()) {
					int count = 0;

					for (String value : searchValues) {
						if (name.getValue().contains(value)) {
							count++;
						}
					}
					if (searchValues.length == count)
						ids.add(name.getKey());

					if (catalogue.fuzzy) {
						// Четкого совпадения подстроки нет - ищем нечеткое

						// TODO: ((BO)
						// item).calcFields.put(BO.fields.CALC_FULL_NAME_WORDS,
						// words);
						Set<String> words = WordUtils.WordsFromPhrases(Arrays.asList(new String[] { name.getValue() }),
								true);
						count = 0;
						// for (String value : searchValues) {
						// for (String word : words)
						// if (WordVector.Check(value, word, 0.5)) {
						// count++;
						// break;
						// }
						// }
						// if (searchValues.length == count) {
						// fuzzy_ids.add(name.getKey());
						// }

						double Distance = 0;
						for (String value : searchValues) {
							double d = 0;
							for (String word : words) {
								if (word.equals(value)) {
									d = 2;
									break;
								}
								if (word.contains(value)) {
									d = 1;
									break;
								}
								// Ищем слово с макс откликом
								// d = Math.max(d, WordVector.Distance(value,
								// word));
								d = WordVector.Distance(value, word);
								if (d > 0.7)
									break;

							}
							Distance += d;
						}

						// афра блок 210 - для теста
						// Формируем FUZZY AND
						Distance /= searchValues.length;
						// Distance /= (searchValues.length + words.size()) / 2;
//						System.out.println("Distance=" + Distance);
						// if (Distance >= 0.85)
						rating.put(name.getKey(), Distance);

					}
				}

				rating = ListUtils.sortMapByValue(rating, true);
				int top = 0;
				for (Entry<Integer, Double> entry : rating.entrySet()) {
					catalogue.fuzzy_ids.add(entry.getKey());
					top++;
					if (top >= 10 && entry.getValue() < 0.85)
						break;
				}

				// Исключаем из fuzzy то, что нашли по подстроке
				Set<Integer> except = new HashSet<>();
				// TODO:???
				for (Object id : catalogue.fuzzy_ids)
					if (ids.contains(id))
						except.add((int) id);
				for (int id : except)
					catalogue.fuzzy_ids.remove(id);
				// если ids пустой - выводить fuzzy_ids, иначе - не
				// выводить?
				if (ids.size() == 0)
					ids.addAll(catalogue.fuzzy_ids);
				else
					catalogue.fuzzy_ids.clear();

				// if (ids.size() != 0)
				{
					sql += " AND ";
					sql += pref + BO.fields.ID + " IN (";
					sql += Set2String(ids);
					sql += ")";
				}
			}

			Cache.I().put(itemFilterCacheKey, sql, 20);
			Cache.I().put(idsCacheKey, Set2String(catalogue.fuzzy_ids), 60);
		} else {
			catalogue.fuzzy_ids = String2Set((String) Cache.I().get(idsCacheKey));
			System.err.println("fuzzy_ids " + idsCacheKey);
		}
		profiler.PrintCurrentElapsed("ItemFilter");
		return sql;
	}

	public static String ParentFilter(CatalogueBO parent, boolean selectChildGroups, String pref) {
		return ParentFilter(parent, selectChildGroups, pref, "", new HashSet());
	}

	public static String ParentFilter(CatalogueBO parent, boolean selectChildGroups, String pref, String searchData,
			Set<Integer> parent_ids) {
		String sql = "";
		if (parent != null) {
			if (selectChildGroups) {
				for (Object cat : GetChildGroups(parent))
					parent_ids.add(((BO) cat).id);
			} else {
				parent_ids.add(parent.id);
			}

			if (searchData.trim().equals("")) {
				sql += " AND ";
				sql += pref + CatalogueBO.fields.PARENT + " IN (";
				sql += Set2String(parent_ids);
				sql += ")";
			}
		}
		return sql;
	}

	protected void ElementFilter(CatalogueBO catalogue, CatalogueBO parent, String searchData,
			Where<TypeBO, Integer> where) throws SQLException {
		// Фильтруем только элементы.Группы выдаются в дерево сразу все.
		where.eq(CatalogueBO.fields.IS_GROUP, false);

		if (parent != null) {
			where.and();
			List<Where<TypeBO, Integer>> listWhere = new ArrayList<>();
			List<Integer> parents = new ArrayList<>();
			for (Object cat : GetChildGroups(parent))
				parents.add(((BO) cat).id);
			int i = 0;
			for (; i < parents.size() - 1; i++) {
				where.eq(CatalogueBO.fields.PARENT, parents.get(i));
				where.or();
			}
			where.eq(CatalogueBO.fields.PARENT, parents.get(i));
			// where.(listWhere.toArray());
		}
		if (!searchData.trim().equals("")) {
			where.and();
			CreateMultipleLike(CatalogueBO.fields.NAME, searchData, where);
		}
	}

	public String ItemFilter(CatalogueBO catalogue, CatalogueBO parent, String searchData) {
		return ItemFilter(catalogue, parent, true, searchData);
	}

	public String ItemFilter(CatalogueBO catalogue, CatalogueBO parent, boolean selectChildGroups, String searchData) {
		return ItemFilter(catalogue, parent, selectChildGroups, "", searchData);
	}

	public void ClearGroups() {
		String key = typeBO.getName() + ".GetGroups";
		Cache.I().clearCache(key);
	}

	public List<TypeBO> GetElements() {
		try {
			QueryBuilder<TypeBO, Integer> builder = getQueryBuilder();
			Where<TypeBO, Integer> where = builder.where();
			NonDeletedFilter(where);
			where.and();
			where.eq(CatalogueBO.fields.IS_GROUP, false);
			return builder.query();
		} catch (Exception e) {
			log.ERROR("GetElements", e);
		}

		return null;
	}

	public List<List<TypeBO>> TraceGroupTree(TypeBO root) {
		List<List<TypeBO>> chains = new ArrayList<>();
		for (Object first : ((CatalogueBO) root).childs) {
			List<TypeBO> chain = new ArrayList<>();
			chain.add((TypeBO) first);
			chains.add(chain);
		}

		List<List<TypeBO>> newChains = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			// Расщепление цепочки
			for (List<TypeBO> chain : chains) {
				TypeBO last = chain.get(chain.size() - 1);
				for (Object child : ((CatalogueBO) last).childs) {
					List<TypeBO> newChain = new ArrayList<>();
					newChain.addAll(chain);
					newChain.add((TypeBO) child);
					newChains.add(newChain);
				}
			}
			if (newChains.size() == 0)
				break;

			chains.clear();
			chains.addAll(newChains);
			newChains.clear();
		}
		return chains;
	}
}