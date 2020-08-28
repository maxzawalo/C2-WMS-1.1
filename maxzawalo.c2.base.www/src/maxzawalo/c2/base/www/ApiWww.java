package maxzawalo.c2.base.www;

import java.util.Map;

public class ApiWww {
	public static String Get(ClientSession clientSession, String api, Map<String, String> q) throws Exception {
		String cacheKey = "";// TODO: sid
		String json = "";

		if (api.equals("bar")) {
			String code = GetDefParam(q, "code", "");
			if (code.startsWith("login_")) {
			} else if (code.length() == 8) {
				// EAN8
				// price etc
				// TODO: check CRC
				code = code.substring(0, code.length() - 1);
				code = "00-" + code;
				clientSession.Redirect("../" + ClientSession.SCAN_RESULT_PAGE + "?price_code=" + code);
			} else if (code.length() == 10) {
				// price QR

				// if (code.contains("00-"))
				// code = code.replace("00-", "") + "0";
				clientSession.Redirect("../" + ClientSession.SCAN_RESULT_PAGE + "?price_code=" + code);
			}
			return code;
		}

		return json;
	}

	protected static String GetDefParam(Map<String, String> q, String name, String defValue) {
		if (q.get(name) == null || q.get(name).equals("null"))
			return defValue;

		System.out.println("GetDefParam: " + q.get(name));
		return q.get(name);
	}
}