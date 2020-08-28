package maxzawalo.c2.base.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class WordUtils {

	static Pattern numbersOnly = Pattern.compile("([0-9])");
	
	public static Set<String> WordsFromPhrases(Collection<String> filteredPhrases) {
		return WordsFromPhrases(filteredPhrases, false);
	}

	public static Set<String> WordsFromPhrases(Collection<String> filteredPhrases, boolean withNumbers) {
		String sysChars = ".()+*\\";
		String splitParam = "-| |,|\"|/|;|'|:";
		for (int i = 0; i < sysChars.length(); i++)
			splitParam += "|\\" + sysChars.charAt(i);

		Set<String> words = new HashSet<>();

		for (String ph : filteredPhrases) {
			AddException(words, ph);
			// non-word character. .split("\\W");
			String[] tokens = ph.split(splitParam);
			for (String token : tokens) {
				String word = token.trim();
				// пропускаем короткие
				if (word.length() < 2)
					continue;
				// пропускаем цифры
				if (!withNumbers && numbersOnly.matcher(word).find())
					continue;
				words.add(word);
			}
		}
		return words;
	}

	protected static void AddException(Set<String> words, String phrase) {
		// TODO: исключения типа "к-т" тупо добавить
		String[] exception = { "к-т", "р/к", "р\\к", "р.к.", "а/шина", "а/покр", "р/вал" };
		for (String ex : exception)
			if (phrase.contains(ex))
				words.add(ex);
	}
}