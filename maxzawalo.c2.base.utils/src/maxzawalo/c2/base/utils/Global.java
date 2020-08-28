package maxzawalo.c2.base.utils;

import java.util.ArrayList;
import java.util.List;

public class Global {
	// Настройки релиза
	public static final boolean isAccService = false;

	public static boolean groupTransaction = false;
	public static long sync_flag = 0;
	// TODO:
	public static final int max_cache_level = 1;
	public static boolean canBalanceBeMinus = false;

	// TODO: копировать в GenericRowMapper и др классах локально
	public static Class[] dbClasses = {};

	public static Class[] enums = {};

	// Price.class Invoice.class для Подбора(партия)
	// TODO: граф - для последовательности
	// TODO: LotOfProduct.class
	public static Class[] heatingUpClasses = {};

	// TODO: WriteOffProduct - RemainingStock
	public static Class[] transactionChains = {};

	public static String VERSION = "?";
	public final static String changesHistory = "https://vk.com/page-150126300_52220734";
	public final static String site = "https://vk.com/c2_wms";
	public final static String downloadPage = "https://vk.com/c2_wms";
	public final static String downloadFullPage = "https://vk.com/c2_wms";

	public static boolean RunInTest = false;
	// public static Gson gson;
	static Object badTransactionDoc;
	public static boolean InMemoryGroupTransaction = false;
	public static String groupTransactionKey;
	public static List<String> GroupTransactionErrorMessages = new ArrayList<>();
	
	
	public static synchronized Object GetBadTransactionDoc() {
		return badTransactionDoc;
	}

	public static synchronized void SetBadTransactionDoc(Object doc) {
		badTransactionDoc = doc;
	}

	static boolean paused = false;

	public static synchronized void StartPause() {
		paused = !paused;
	}

	public static synchronized boolean getPaused() {
		return paused;
	}

	static int sleepPriority = 0;

	public static boolean AccTransactionOnlyTest = false;

	public static boolean InMemoryDb = false;

	public static int defaultRoundZeros = 2;

	public static boolean SkipTransactionErrors = false;

	// public static boolean AccTransactionOnly = false;
	// // TODO: регулирование при сборке релиза согласно лицензии
	// public static boolean ProductTransactionOnly = true;

	public static synchronized int getPriority() {
		return sleepPriority;
	}

	public static synchronized void setPriority(int value) {
		sleepPriority = value;
	}

	public static void CheckAndPause() throws Exception {
		if (getPaused()) {
			while (getPaused()) {
				try {
					if (Thread.currentThread().isInterrupted())
						throw new Exception("Останов потока");
					// TODO: регулируется. особенно на сервисе. чтобы не
					// перегрузили
					// сервера.
					Thread.sleep(200);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
			}
		} else {
			try {
				if (Thread.currentThread().isInterrupted())
					throw new Exception("Останов потока");
				Thread.sleep(getPriority());
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		}
	}
}