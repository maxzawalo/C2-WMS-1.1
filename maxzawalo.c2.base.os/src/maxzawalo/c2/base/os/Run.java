package maxzawalo.c2.base.os;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import maxzawalo.c2.base.utils.Logger;

public class Run {
	static Logger log = Logger.getLogger(Run.class);

	public static String OpenFile(String filename) {
		// TODO: linux
		String result = "";
		Process p;
		try {
			p = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + filename);
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = input.readLine()) != null) {
				result += line;
			}
			input.close();
			System.out.println(result);

		} catch (IOException e) {
			log.ERROR("OpenFile", e);
		}
		log.DEBUG("OpenFile", result);
		return result;
	}

}
