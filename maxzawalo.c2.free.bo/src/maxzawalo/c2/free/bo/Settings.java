package maxzawalo.c2.free.bo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import maxzawalo.c2.base.bo.Coworker;
import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.base.utils.FileUtils;
import maxzawalo.c2.base.utils.Logger;

public class Settings {
	public static boolean isDesignTime = true;

	static Logger log = Logger.getLogger(Settings.class);
	public static Contractor myFirm;
	public static Currency mainCurrency;
	public static Units mainUnits;
	public static Store mainStore;

	public static Store rentStore;
	public static Store rentContractorStore;

	static {
		myFirm = new Contractor();
		// TODO: app.config
		myFirm.unp = "123456789";
		myFirm.full_name = "My Co";
		myFirm.name = "My Co";
		myFirm.legal_address = "My street";
	}

	public static Coworker ChiefAccounting = new Coworker();
	public static Coworker Head = new Coworker();

	// TODO: app.config
	public static double defaultVat = 20;
	public static double defaultDeliveryAddition = 20;

	/**
	 * C2 генерирует ценники
	 */
	public static boolean c2_price_gen = true;

	public static int startComPort = 2;

	public static boolean isServer() {
		try {
			InetAddress IP = InetAddress.getLocalHost();
			return (settings.get("server").equals("localhost") || settings.get("server").equals("127.0.0.1")
					|| settings.get("server").equals(IP.getHostAddress()));
		} catch (UnknownHostException e) {
			log.ERROR("is_show_admin_buttons", e);
		}
		return false;
	}

	public static boolean canCreateEDoc() {
		if (User.current.isAdmin()) {
			return true;
		}
		return false;
	}

	static Map<String, String> settings = new HashMap<String, String>();
	// TODO: app.settings | сохраняемые настройки пользователя
	public static boolean enableSuggestor = false;
	public final static int maxLevel = 5;

	public static void Load() {
		String data = FileUtils.readFileAsString(FileUtils.getAppDir() + "app.settings");

		for (String line : data.split("[\\r\\n]+")) {
			String parts[] = line.split("=");
			settings.put(parts[0], (parts.length > 1 ? parts[1] : ""));
		}
	}

	public static String get(String key) {
		return settings.get(key);
	}

	public static String imagesPath() {
		return get("images_path");
	}
}