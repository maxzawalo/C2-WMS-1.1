package maxzawalo.c2.base.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import maxzawalo.c2.base.interfaces.CompareT;
import maxzawalo.c2.base.interfaces.MapSortT;

public class ListUtils {
	public static <T> List<T> Except(List<T> list, List<T> exceptList, List<T> intersection, CompareT comparer) {
		List<T> diffList = new ArrayList<>();
		// List<T> intersection = new ArrayList<>();
		if (list.size() == 0 || exceptList.size() == 0)
			return list;
		else {
			// TODO: 1 цикл по меньшему списку
			for (T item1 : list) {
				boolean found = false;
				for (T item2 : exceptList)
					if (comparer.Do(item1, item2)) {
						// В объединение попадают новые объекты
						intersection.add(item1);
						found = true;
						break;
					}
				if (!found)
					diffList.add(item1);
			}
		}
		return diffList;
	}

	public static <T> List<T> reverse(List<T> src) {
		List<T> results = new ArrayList<T>();
		for (int i = src.size() - 1; i >= 0; i--) {
			results.add(src.get(i));
		}
		// Collections.reverse(results);
		return results;
	}

	public static <K, V> Map<K, V> sortMapByValue(Map<K, V> map) {
		return sortMapByValue(map, MapSortByValue, false);
	}

	public static <K, V> Map<K, V> sortMapByValue(Map<K, V> map, boolean desc) {
		return sortMapByValue(map, MapSortByValue, desc);
	}

	public static <K, V> Map<K, V> sortMapByValue(Map<K, V> map, MapSortT mapComparer) {
		return sortMapByValue(map, mapComparer, false);
	}

	public static <K, V> int MapByValueComparer(Object o1, Object o2, boolean desc) {
		if (desc)
			return ((Comparable<V>) ((Map.Entry<K, V>) (o2)).getValue()).compareTo(((Map.Entry<K, V>) (o1)).getValue());
		else
			return ((Comparable<V>) ((Map.Entry<K, V>) (o1)).getValue()).compareTo(((Map.Entry<K, V>) (o2)).getValue());
	}

	public static MapSortT MapSortByValue = new MapSortT<Integer, Double>() {
		@Override
		public int Do(Object o1, Object o2, boolean desc) {
			return MapByValueComparer(o1, o2, desc);
		}
	};

	public static <K, V> Map<K, V> sortMapByValue(Map<K, V> map, final MapSortT mapComparer, final boolean desc) {
		List<Entry<K, V>> list = new LinkedList<>(map.entrySet());

		Collections.sort(list, new Comparator<Object>() {
			@SuppressWarnings("unchecked")
			public int compare(Object o1, Object o2) {
				return mapComparer.Do(o1, o2, desc);
				// return MapComparer(o1, o2, desc);
			}
		});

		Map<K, V> result = new LinkedHashMap<>();
		for (Iterator<Entry<K, V>> it = list.iterator(); it.hasNext();) {
			Map.Entry<K, V> entry = (Map.Entry<K, V>) it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public static <T> T[] joinArrayGeneric(T[]... arrays) {
		int length = 0;
		for (T[] array : arrays) {
			length += array.length;
		}

		// T[] result = new T[length];
		final T[] result = (T[]) Array.newInstance(arrays[0].getClass().getComponentType(), length);

		int offset = 0;
		for (T[] array : arrays) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}

		return result;
	}

	public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> map = new ConcurrentHashMap<>();
		return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
}