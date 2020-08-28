package maxzawalo.c2.base;

import java.util.Map;

import maxzawalo.c2.base.utils.Logger;
import maxzawalo.c2.base.utils.Profiler;

public class FunctionC2 {
	public static final String ERROR = "func_call_error";
	protected Logger log = Logger.getLogger(this.getClass());
	Profiler profiler = new Profiler();

	public String name = "";
	public String description = "";

	// TODO: типы. (char[N], int[10] - Для UI). Ф-я проверки (mail,int,phone...)
	// regex
	// Для функционального поиска гораздо важнее типы вх и вых
	public String[] in_param_names = {};
	public String[] out_param_names = {};

	protected boolean FuncBody(Map<String, Object> params) throws Exception {
		return false;
	}

	/**
	 * 
	 * @param params
	 *            Тут может быть вектор состояния системы.
	 * @param out_params
	 *            Можно совместить с in_params. !!! out_params не очищать, иначе не
	 *            передадуться параметры в вызывающую ф-ю
	 * @return
	 */
	public boolean call(Map<String, Object> params) {
		assert !name.isEmpty() : "Надо определить имя ф-ии";
		// out_params = new HashMap<>();
		boolean retVal = false;
		// TODO: Проверка входных параметров. Типы данных задают допустимые значения.
		// null? Задавать списком допустимые значения - исп-м для теста.

		// Надо ли профайлить проверку??
		for (String n : in_param_names)
			// TODO: все отсутсвующие
			if (!params.containsKey(n)) {
				params.put(ERROR, "Нету вх. параметра: " + n);
				log.ERROR(name, "Нету вх. параметра: " + n);
				return false;
			}

		profiler.Start(name);
		// TODO: макс время выполнения - снимать задачу с ошибкой - это для системы
		// запуска ф-й
		try {
			retVal = FuncBody(params);
			profiler.Stop(name);
			// Одинаковые название вх. переменных у разных ф-й могут привести к ошибкам.
			// - удалять после вызова, но если данные этой переменной нужны будут ниже по
			// графу - надо будет добавлять заново
			// - делать рекировку в самой ф-ии
			// - делать рекировку в вызывающей ф-ии
			// params.put("var", var1);
			// params.put("var_looong_cache", var1);
			for (String n : in_param_names)
				params.remove(n);
			// TODO: Проверка вЫходных параметров
		} catch (Exception e) {
			params.put(ERROR, e);
			profiler.Stop(name);
			log.ERROR(name, e);
		}

		// TODO: без лога, чтобы не тормозить вызов ф-ии
		profiler.PrintElapsed(name);

		for (String n : out_param_names)
			// TODO: все отсутсвующие
			if (!params.containsKey(n)) {
				params.put(ERROR, "Нету вЫх. параметра: " + n);
				log.ERROR(name, "Нету вЫх. параметра: " + n);
				return false;
			}

		// TODO: profiler всей ф-ии
		return retVal;
	}

	public static boolean Check(Map<String, Object> out_params) {
		return !out_params.containsKey(ERROR);
	}
}
