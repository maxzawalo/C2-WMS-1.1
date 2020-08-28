package maxzawalo.c2.full.ai.ml;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import maxzawalo.c2.base.utils.FileUtils;
import maxzawalo.c2.base.utils.ListUtils;

public class Rule {
	String delimiter = ";";
	Set<String> checkRules = new HashSet<>();

	public void AddRule(String rule) {
		checkRules.add(rule);
	}

	public boolean Check(String str) {
		String rule = RuleFromString(str);
		return checkRules.contains(rule);
	}

	public void Create(Set<String> allStr) {
		// ==== Правила ====
		// 2 вида обработки: Проверка, Дальнешая обработка(убрать
		// пробелы,обрезать, подстрока)
		// --------------------------------
		// Это число
		// Это буква
		// Это А,Б,В,0,1, пробел|
		// паттерны - что то после чегото(число после буквы) - как имперически?
		// >,<, !=,Count(leter,digit)
		Map<String, Integer> rules = new HashMap<>();
		Map<String, String> samples = new HashMap<>();

		for (String str : allStr) {
			String rule = RuleFromString(str);
			samples.put(rule, str);
			// r += (n.length() > 0 && Character.isLetter(n.charAt(0)) ?
			// "[0]=isletter" : "") + delimiter;
			// r += (n.length() > 1 && Character.isLetter(n.charAt(1)) ?
			// "[1]=isletter" : "") + delimiter;
			// // r += (n.length() > 2 && n.charAt(2) == ' ' ? "[2]=space" :
			// "")+
			// // delimiter;
			// r += (n.contains(" ") ? "contains space" : "") + delimiter;

			if (!rules.containsKey(rule))
				rules.put(rule, 1);
			else
				rules.put(rule, rules.get(rule) + 1);
		}
		rules = ListUtils.sortMapByValue(rules);
		String data = "";
		for (String r : rules.keySet())
			data += rules.get(r) + delimiter + samples.get(r) + delimiter + r + "\r\n";
		System.out.println(data);
		FileUtils.Text2File(FileUtils.GetDataDir() + "rules.csv", data, false);
		System.out.println("rules count=" + rules.size());
	}

	protected String RuleFromString(String str) {
		String r = "";
		r += "length=" + str.length() + delimiter;
		for (int i = 0; i < str.length(); i++)
			if (str.charAt(i) == ' ')
				r += "[" + i + "]=space" + delimiter;
			else if (Character.isLetter(str.charAt(i)))
				r += "[" + i + "]=isletter" + delimiter;
			else if (Character.isDigit(str.charAt(i)))
				r += "[" + i + "]=isDigit" + delimiter;
			else
				r += "[" + i + "]=" + delimiter;
		return r;
	}
}