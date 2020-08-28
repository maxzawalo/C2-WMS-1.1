//TODO: отвязаться от data
package maxzawalo.c2.free.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import maxzawalo.c2.base.Actions;
import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.interfaces.FilterT;
import maxzawalo.c2.base.utils.Global;
import maxzawalo.c2.base.utils.Logger;
import maxzawalo.c2.base.utils.Profiler;
import maxzawalo.c2.free.bo.enums.EnumC2;

public class Cache {

	static Logger log = Logger.getLogger(Cache.class);
	Profiler profiler = new Profiler();

	Map<String, CacheObj> data = new HashMap<>();

	class CacheObj {
		public Object obj;
		public long time = 0;
		public long expiryTime = 0;
	}

	private static volatile Cache instance;

	public static Cache I() {
		Cache localInstance = instance;
		if (localInstance == null) {
			synchronized (Cache.class) {
				localInstance = instance;
				if (localInstance == null) {
					instance = localInstance = new Cache();
				}
			}
		}
		return localInstance;
	}

	private Cache() {
		updater.start();
	}

	Set<Class> updateTasks = new HashSet<>();

	void AddUpdateTask(final Class type) {
		// System.out.println("==AddUpdateTask " + type);
		updateTasks.add(type);
	}

	// TODO: full
	Thread updater = new Thread(new Runnable() {
		public synchronized void run() {
			while (true) {
				try {
					for (Class type : updateTasks) {
						// java.util.ConcurrentModificationException
						// at java.util.HashMap$HashIterator.nextNode(HashMap.java:1437)
						// at java.util.HashMap$KeyIterator.next(HashMap.java:1461)
						// at maxzawalo.c2.free.cache.Cache$1.run(Cache.java:67)
						// at java.lang.Thread.run(Thread.java:748)

						// если getMap(type); - зацикливание
						Map<Integer, ? extends BO> map = (Map<Integer, ? extends BO>) data.get(getDbCacheKey(type)).obj;
						for (Object newObj : GetFromDb(type)) {
							BO oldObj = map.get(((BO) newObj).id);
							if (oldObj == null || ((BO) newObj).changed.getTime() > ((BO) oldObj).changed.getTime()) {
								System.out.println("Обновляем " + newObj);
								// новый объект или обновили
								putInMap(type, (BO) newObj, heatingCacheTimeSec);
							}
						}
						// TODO: SET lastUpdate
					}
					updateTasks.clear();

					// System.out.println("cache updated");
					Thread.sleep(heatingCacheTimeSec * 1000 / 4);
				} catch (Exception e) {
					log.ERROR("UpdateTask", e);
				}
			}
		}
	});

	public Object get(String key) {
		// TODO: замеряем частоту/период обращений по каждому ключу
		if (!data.containsKey(key))
			return null;
		CacheObj obj = data.get(key);
		if (System.currentTimeMillis() - obj.time < obj.expiryTime * 1000) {
			// System.out.println("found key " + key);
			return obj.obj;
		} else if (obj.obj instanceof Map) {
			Map<Integer, ?> map = ((Map<Integer, ?>) obj.obj);
			Class type = null;
			if (map.size() != 0) {
				type = map.values().toArray()[0].getClass();
				if (isHeatType(type)) {
					// TODO: по сигналу от других АРМов
					AddUpdateTask(type);
					return obj.obj;
				}
			}
			// Если список пустой - return null даже для HeatType

		} else {
			data.remove(key);
			// TODO: GC
		}

		return null;
	}

	// public <T> void putInList(String key, T value, long cacheTimeSec) {
	//
	// // Поиск по id. Если 0 - надо добавить параметр(ы) - кластерный индекс.
	// }

	/**
	 * Фильтруем и удаляем из кэша (в частности ТабЧасть, т.к. удаленные нам не
	 * нужны). Работает с Id.
	 */
	public <T> void removeFromList(Class<T> type, FilterT filter) {
		String key = getDbCacheKey(type);
		Map<Integer, T> map = (Map<Integer, T>) get(key);
		if (map == null) {
			map = new HashMap<Integer, T>();
			putMap(type, map, getCacheTime(type));
		}
		System.out.println("removeFromList start " + key + " " + map.size());

		List<Integer> deletedIds = new ArrayList<>();

		for (Integer id : map.keySet())
			if (filter.Check(map.get(id)))
				deletedIds.add(id);

		for (Integer id : deletedIds)
			map.remove(id);

		map = (Map<Integer, T>) get(key);
		if (map == null) {
			map = new HashMap<Integer, T>();
			putMap(type, map, getCacheTime(type));
		}
		System.out.println("removeFromList end " + key + " " + map.size());
	}

	<T> String getDbCacheKey(Class<T> type) {
		return type.getName() + ".DbTable";
	}

	public <T> void putInMap(Class<T> type, BO obj) {
		// Пропускаем объекты не разогреваемые..пока
		if (!isHeatType(type))
			return;
		putInMap(type, obj, getCacheTime(type));
	}

	protected <T> long getCacheTime(Class<T> type) {
		return isHeatType(type) ? heatingCacheTimeSec : commonCacheTimeSec;
	}

	public <T> void putInMap(Class<T> type, BO obj, long cacheTimeSec) {
		System.out.println("putInMap " + getDbCacheKey(type));
		Map<Integer, T> map = (Map<Integer, T>) get(getDbCacheKey(type));
		if (map == null) {
			map = new HashMap<Integer, T>();
			// putMap(type, map, cacheTimeSec);
		}
		// if (list == null)
		// list = new ArrayList<>();
		// T deleted = null;
		// Проверяем наличие по id
		if (obj != null)
			map.put(obj.id, (T) obj);

		// put(getDbCacheKey(type), map, cacheTimeSec);
		// Обновляем cacheTime
		putMap(type, map, cacheTimeSec);
	}

	public <T> void putList(String key, List<T> list, long cacheTimeSec) {
		put(key, list, cacheTimeSec);
	}

	public <T> void putList(Class<T> type, List<T> list, long cacheTimeSec) {
		log.DEBUG("putList", "putList " + type.getName() + ".All" + " " + list.size());
		putList(type.getName() + ".All", list, cacheTimeSec);
	}

	// <T> void putMap(Class<T> type, List<T> list, long cacheTimeSec) {
	// log.DEBUG("putMap", "putMap " + getDbCacheKey(type) + " " + list.size());
	// put(getDbCacheKey(type), list, cacheTimeSec);
	// }

	<T> void putMap(Class<T> type, List<T> list, long cacheTimeSec) {
		log.DEBUG("putMap", "putMap " + getDbCacheKey(type));
		Map<Integer, T> map = new HashMap<>();
		map = List2Map(map, list);
		putMap(type, map, cacheTimeSec);
	}

	<T> void putMap(Class<T> type, Map<Integer, T> list, long cacheTimeSec) {
		log.DEBUG("putMap", "putMap " + getDbCacheKey(type) + " " + list.size());
		put(getDbCacheKey(type), list, cacheTimeSec);
	}

	// TODO: by BO
	public Map<Class, Date> lastUpdate = new HashMap<>();

	public <T> T getById(Class<T> type, int id, int level) {
		// System.out.println("Cache.getById " + type.getName() + " Id=" + id);
		Map<Integer, T> map = (Map<Integer, T>) get(getDbCacheKey(type));

		T bo = null;
		try {
			bo = type.newInstance();
			((BO) bo).id = id;
			if (Arrays.asList(Global.enums).contains(type))
				// TODO: isnull
				return (T) ((EnumC2) bo).getEnumById(id);
		} catch (Exception e) {
			log.ERROR("getById", e);
		}

		// Если список пустой - list == null даже для HeatType
		// TODO: Возможно зацикливание
		if (map == null) {
			map = new HashMap<Integer, T>();
			if (isHeatType(type)) {
				if (level <= Global.max_cache_level) {
					System.out.println("Cache.GetById GetFromDb()");
					// List<T> table = ((BO) bo).GetAll4Cache();
					List<T> table = GetFromDb(type);
					map = List2Map(map, table);
				} else {
					// System.out.println("Cache.GetById max_level");
				}
				putMap(type, map, heatingCacheTimeSec);
			} else {
				// System.out.println("Cache.GetById DB");
				// bo = (T) ((BO) bo).GetById(id, level + 1, false);
				bo = GetByIdFromDb(type, id, level);
				// Сразу возвращаем - чтобы не искать в кэше
				return null;
				// TODO: task for thread load
				// putList(type, list, commonCacheTimeSec);
			}
		}

		// ZERO
		if (id == 0)
			return (T) bo;

		// System.out.println("Cache.GetById find in list");
		bo = map.get(id);
		if (bo != null) {
			// System.out.println("Cache.GetById found in list");
			return bo;
		}
		// for (T item : list)
		// if (((BO) item).id == id) {
		// System.out.println("Cache.GetById found in list");
		// return item;
		// }
		// Если не находим объект - запрос в БД. Если есть новые - добавляем
		System.out.println("Cache.GetById return null");
		return null;
	}

	public static <T> Map<Integer, T> List2Map(Map<Integer, T> map, List<T> list) {
		for (T row : list)
			map.put(((BO) row).id, row);
		return map;
	}

	public static <T> List<T> Map2List(Map<Integer, T> map) {
		List<T> list = new ArrayList<>();
		for (T value : map.values())
			list.add(value);
		return list;
	}

	protected <T> boolean isHeatType(Class<T> type) {
		return heatingUpClasses != null && Arrays.asList(heatingUpClasses).contains(type);
	}

	public <T> T getByUUID(Class<T> type, UUID uuid) {
		List<T> list = (List<T>) get(type.getName() + ".All");
		if (list != null)
			for (T item : list)
				if (((BO) item).uuid.equals(uuid))
					return item;
		return null;
	}

	public <T> int getListSize(Class<T> type) {
		List<T> list = (List<T>) get(type.getName() + ".All");
		if (list != null)
			return list.size();
		return 0;
	}

	public <T> List<T> getList(String key) {
		List<T> list = (List<T>) get(key);// , Class<T> type

		// TODO: if (list == null) list = new ArrayList<>();

		return list;
	}

	public void put(String key, Object value, long cacheTimeSec) {
		CacheObj obj = new CacheObj();
		obj.obj = value;
		obj.time = System.currentTimeMillis();
		obj.expiryTime = cacheTimeSec;
		data.put(key, obj);
	}

	public boolean containsKey(String key) {
		return data.containsKey(key);
	}

	public void clearCache(String key) {
		if (data.containsKey(key))
			data.remove(key);
	}

	public void clearBySubstr(String str) {
		List<String> k = new ArrayList<>();
		// Иначе в цикле меняется Map и падает
		k.addAll(data.keySet());
		for (String key : k)
			if (key.contains(str)) {
				System.out.println("clearBySubstr " + key);
				data.remove(key);
			}
	}

	public void ShowAllKeys() {
		for (String key : data.keySet())
			System.out.println("ShowAllKeys: " + key);

	}

	public void clearAllCache() {
		data.clear();
	}

	Class[] heatingUpClasses;

	public void SetHeatingUpClasses(Class[] classes) {
		this.heatingUpClasses = classes;
	}

	// TODO: потом увеличить - когда АРМы сигналить будут
	public static long heatingCacheTimeSec = 60;
	public static long commonCacheTimeSec = 20;

	public void setCacheTime(long heatingCacheTimeSec, long commonCacheTimeSec) {
		this.heatingCacheTimeSec = heatingCacheTimeSec;
		this.commonCacheTimeSec = commonCacheTimeSec;
	}

	// public void HeatingUp() {
	// HeatingUp(heatingUpClasses);
	// }

	public void HeatingUp(Class[] classes) {
		heatingUpClasses = classes;
		profiler.Start("HeatingUp");
		System.out.println("mem: " + Runtime.getRuntime().totalMemory() / 10E5);
		for (Class cl : classes) {
			profiler.Start("HeatingUp_" + cl.getName());
			try {
				putMap(cl, GetFromDb(cl), heatingCacheTimeSec);
			} catch (Exception e) {
				log.ERROR("HeatingUp", e);
			}
			System.out.println("mem: " + Runtime.getRuntime().totalMemory() / 10E5);
			profiler.Stop("HeatingUp_" + cl.getName());
			profiler.PrintElapsed("HeatingUp_" + cl.getName());
		}

		profiler.Stop("HeatingUp");
		profiler.PrintElapsed("HeatingUp");
	}

	public <T> Map<Integer, T> getMap(Class<T> type) {
		Map<Integer, T> map = (Map<Integer, T>) get(getDbCacheKey(type));
		if (map == null)
			map = new HashMap<>();
		return map;
	}

	public <T> List<T> getMapList(Class<T> type) {
		return Map2List(getMap(type));
	}

	List GetFromDb(Class type) {
		if (Actions.GetFromDbAction == null)
			throw new UnsupportedOperationException("GetFromDb не определен.");
		else
			return (List) Actions.GetFromDbAction.Do(type);
		// return new FactoryBO().Create(type).GetAll4Cache();
	}

	<T> T GetByIdFromDb(Class<T> type, int id, int level) {
		if (Actions.GetByIdFromDbAction == null)
			throw new UnsupportedOperationException("GetByIdFromDb не определен.");
		else
			return (T) Actions.GetByIdFromDbAction.Do(type, id, level);
		// new FactoryBO().Create(type).GetById(id, level + 1, false);
	}
}