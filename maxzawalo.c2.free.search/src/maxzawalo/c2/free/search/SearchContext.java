package maxzawalo.c2.free.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import maxzawalo.c2.base.bo.CatalogueBO;
import maxzawalo.c2.base.utils.FileUtils;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.base.utils.Profiler;
import maxzawalo.c2.base.utils.WordUtils;
import maxzawalo.c2.free.cache.Cache;

//TODO: отвязать от BO
//TODO: отвязать от Cache

public class SearchContext {

	public static List<String> FromPhrases(Class type, String filterWords) {
		System.out.println("FromPhrases:" + filterWords);
		List<String> filteredPhrases = new ArrayList<>();
		for (String ph : GetPhrases(type)) {
			if (!FullFilter(ph, filterWords))
				continue;
			filteredPhrases.add(ph);
		}

		Set<String> words = WordUtils.WordsFromPhrases(filteredPhrases);

		List<String> exceptEnteredWords = new ArrayList<>();
		// Исключаем уже введенные СЛОВА
		for (String word : words)
			if (!ExceptFilter(word, filterWords))
				exceptEnteredWords.add(word);

		Collections.sort(exceptEnteredWords);
		return exceptEnteredWords;
	}

	static <T> void Create(Class type) {

		Profiler profiler = new Profiler();
		profiler.Start("SearchContext.Create");
		String symb = " (),;=-.\"/+*";
		String data = "";

		GetPhrases(type);

		List<String> sortedWords = FromPhrases(type, "");
		// TODO Частотность - сортировка
		for (String word : sortedWords)
			data += word + "\r\n";

		FileUtils.Text2File(FileUtils.GetSearchContextDir() + type.getName(), data, false);
		profiler.Stop("SearchContext.Create");
		profiler.PrintElapsed("SearchContext.Create");
	}

	public static void Clear(Class type) {
		if (type == null)
			return;
		String key = type.getName() + ".GetPhrases";
		Cache.I().clearCache(key);
	}

	public static List<String> GetPhrases(Class type) {
		try {
			String key = type.getName() + ".GetPhrases";
			List<String> phrases = Cache.I().getList(key);
			if (phrases == null) {
				phrases = new ArrayList<>();
				for (Object cat : Cache.I().getMap(type).values())
					phrases.add(((CatalogueBO) cat).name.toLowerCase());
				Cache.I().putList(key, phrases, 60 * 60);
			}
			return phrases;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	public static boolean FullFilter(String phrase, String filterWords) {
		return FullFilter(phrase, filterWords, true);
	}

	public static boolean FullFilter(String phrase, String filterWords, boolean and) {
		if (!filterWords.equals("")) {
			String[] filter = filterWords.split(" ");
			int containsCount = 0;
			for (String f : filter)
				if (phrase.contains(f)) {
					if (!and)
						return true;
					containsCount++;
				}
			// Содержит все слова из фильтра
			if (containsCount == filter.length)
				return true;
		} else
			return true;
		return false;
	}

	public static boolean ExceptFilter(String phrase, String filterWords) {
		if (!filterWords.equals("")) {
			String[] filter = filterWords.split(" ");
			for (String f : filter)
				if (phrase.contains(f))
					return true;
		} else
			return false;

		return false;
	}

	public static void SaveUserEnter(Class type, CatalogueBO parent, String searchData) {
		if (type == null)
			return;
		String key = type.getName();
		int parent_id = 0;
		String parent_name = "";

		if (parent != null) {
			parent_id = parent.id;
			parent_name = parent.name;
		}
		Date now = new Date();
		String text = Format.Show("HH:mm:ss", now) + "\t" + key + "\t" + parent_id + "\t" + parent_name + "\t"
				+ searchData + "\r\n";
		String fileName = Format.Show("yyyy-MM-dd", now) + ".search_log";
		FileUtils.Text2File(FileUtils.GetSearchLogDir() + fileName, text, true);
	}

	// public static <T> String[] Load(Class<T> type) {
	// String data = FileUtils.readFileAsString(FileUtils.GetSearchContextDir()
	// + type.getName());
	// if (!data.equals(""))
	// return data.split("[\\r\\n]+");
	//
	// return new String[0];
	// }
}