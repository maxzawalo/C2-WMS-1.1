package maxzawalo.c2.base.os;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import maxzawalo.c2.base.utils.Logger;

public class CmdUtils {

	static Logger log = Logger.getLogger(CmdUtils.class.getName());

	public static String Exec(String cmd) {
		// TODO: linux
		System.out.println(cmd);
		String retVal = "";
		try {
			final Process process = Runtime.getRuntime().exec("cmd /C " + cmd);
			// new Thread(new Runnable() {
			// public void run() {
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream(), "Cp866"));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream(), "Cp866"));

			String line = null;
			try {
				// Read command standard output
				String s;
				System.out.println("Standard output: ");
				while ((s = stdInput.readLine()) != null) {
					retVal += s;
					System.out.println(s);
				}

				// Read command errors
				System.out.println("Standard error: ");
				while ((s = stdError.readLine()) != null) {
					retVal += s;
					System.out.println(s);
				}
			} catch (IOException e) {
				log.ERROR("Exec", e);
			}
			// }
			// }).start();
			//
			// p.waitFor();
		} catch (Exception e) {
			log.ERROR("Exec", e);
		}

		return retVal;

	}

	private static String printLines(String cmd, InputStream ins) throws Exception {
		String outData = "";
		String line = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(ins));
		while ((line = in.readLine()) != null) {
			System.out.println(cmd + " " + line);
			outData += cmd + " " + line;
		}
		return outData;
	}

	public static String runProcess(String command) {
		String outData = "";
		try {
			System.out.println(command);
			Process pro = Runtime.getRuntime().exec(command);
			outData += printLines(command + " stdout:", pro.getInputStream());
			outData += printLines(command + " stderr:", pro.getErrorStream());
			pro.waitFor();
			System.out.println(command + " exitValue() " + pro.exitValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outData;
	}
}