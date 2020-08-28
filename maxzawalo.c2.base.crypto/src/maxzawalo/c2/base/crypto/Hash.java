package maxzawalo.c2.base.crypto;

import java.security.MessageDigest;

public class Hash {

	public static String sha256(String base) {
		try {
			System.out.print("sha256 [" + base + "] ");
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(base.getBytes("UTF-8"));
			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			System.out.println("[" + hexString.toString() + "]");
			return hexString.toString();
		} catch (Exception ex) {
			// throw new RuntimeException(ex);
		}
		return "";
	}

	public static String md5(String base) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] hash = digest.digest(base.getBytes("UTF-8"));
			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}

			return hexString.toString();
		} catch (Exception ex) {
			// throw new RuntimeException(ex);
		}
		return "";
	}
}