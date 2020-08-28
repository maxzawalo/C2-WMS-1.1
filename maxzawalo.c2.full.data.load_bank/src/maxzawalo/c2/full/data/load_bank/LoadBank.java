package maxzawalo.c2.full.data.load_bank;

import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.base.utils.Logger;

public class LoadBank {
	static Logger log = Logger.getLogger(LoadBank.class);

	public static void Load(BankLoader loader, String fileName) {
		Console.I().INFO(LoadBank.class, "Load", "==== Загрузка выписки " + loader.getName() + " ====");

		try {
			loader.Load(fileName);
		} catch (Exception e) {
			log.ERROR("LoadBank", e);
			Console.I().ERROR(LoadBank.class, "Load", "Ошибка загрузки. См. лог.");
		}

		// System.out.println(account);
		Console.I().INFO(LoadBank.class, "Load", "Добавлено " + loader.newCount);
		// log.CONSOLE("Всего " + allCount);

		Console.I().INFO(LoadBank.class, "Load", "==== Загрузка завершена ====");
	}
}