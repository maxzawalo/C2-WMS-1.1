package maxzawalo.c2.free.search;

import java.util.HashMap;
import java.util.Map;

public class WordVector {
	Map<Character, Integer> vector = new HashMap<>();
	String word;
	int hash;

	//TODO: учитывать расстояние от начала (startWith)
	public WordVector(String word) {
		this.word = word.toLowerCase();
		for (Character ch : this.word.toCharArray())
			if (!vector.containsKey(ch))
				vector.put(ch, 1);
			else
				vector.put(ch, vector.get(ch) + 1);
	}

	public static int[] ToIntArray(String word) {
		WordVector w = new WordVector(word);
		int[] v = new int[w.vector.size()];

		int i = 0;
		for (char ch : w.vector.keySet())
			v[i++] = (int) ch;

		return v;
	}

	public static double Distance(WordVector v1, WordVector v2) {
		Map<Character, Integer> result = new HashMap();

		int mulFound = 1;
		int mulNotFoundV1 = 1;

		// Заполняем выходной вектор
		// foreach (char ch in v1.vector.Keys)
		// result[ch] = 0;
		// foreach (char ch in v2.vector.Keys)
		// result[ch] = 0;

		double k = 0;

		// Вхождение искомого v1 в v2
		for (Character ch : v1.vector.keySet())
			if (v2.vector.containsKey(ch)) {
				// result[ch] = v2.vector[ch] * mulFound;
				k += 1;
			}

		return (k / v2.vector.size()) * (k / v1.vector.size());
	}

	public static double Distance(String w1, String w2) {
		int sum = 0;
		// foreach(string word in w1.Split(" ".ToArray(),
		// StringSplitOptions.RemoveEmptyEntries))
		// sum += Compare(new WordVector(word), new WordVector(w2));

		double dist = Distance(new WordVector(w1), new WordVector(w2));
		return dist;
	}

	public static boolean Check(String w1, String w2, double min) {

		double dist = Distance(new WordVector(w1), new WordVector(w2));
		if (dist > min) {
			System.out.println(w1 + "|" + w2 + "|" + dist);
			return true;
		}

		return false;

	}
}