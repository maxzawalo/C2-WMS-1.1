package maxzawalo.c2.base.utils;

import static maxzawalo.c2.base.utils.FileUtils.GetLogDir;

import java.awt.event.ActionEvent;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Action;

import maxzawalo.c2.base.Actions;
import maxzawalo.c2.base.interfaces.ActionC2;

public class Logger {

	public class LogLevel {
		public static final int TEST = 0;
		public static final int DEBUG = 1;
		public static final int INFO = 2;
		public static final int WARN = 3;
		public static final int ERROR = 4;
		public static final int FATAL = 5;
		public static final int OFF = 6;
	}

	int level = globalLevel;
	static int globalLevel = LogLevel.DEBUG;
	static Map<String, Logger> all = new HashMap<>();

	String logClass;

	protected Logger(String logClass) {
		this.logClass = logClass;
	}

	public static Logger getLogger(Class logClass) {
		return getLogger(logClass.getName());
	}

	public static Logger getLogger(String logClass) {
		Logger logger = all.get(logClass);
		if (logger == null) {
			logger = new Logger(logClass);
			all.put(logClass, logger);
		}
		return logger;
	}

	public static void setLevel(int levelValue) {
		globalLevel = levelValue;
	}

	public void DEBUG(String funcName, String message) {
		if (level > LogLevel.DEBUG)
			return;
		Print(message, "DEBUG");
		Log(LogLevel.DEBUG, funcName, message);
	}

	void Print(String message, String lvl) {
		// TODO: UI console?
		if (level == LogLevel.TEST)
			System.out.println(Format.Show("HH:mm:ss", new Date()) + " " + lvl + " " + message);
	}

	public void INFO(String funcName, String message) {
		if (level > LogLevel.INFO)
			return;
		Print(message, "INFO");
		Log(LogLevel.INFO, funcName, message);
	}

	public void WARN(String funcName, String message) {
		if (level > LogLevel.WARN)
			return;
		Print(message, "WARN");
		Log(LogLevel.WARN, funcName, message);
	}

	public void WARN(String funcName, Exception e) {
		if (level > LogLevel.WARN)
			return;

		PrintStack(e);

		if (level <= LogLevel.DEBUG) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log(LogLevel.WARN, funcName, sw.toString());
		} else {
			Log(LogLevel.WARN, funcName, e.getLocalizedMessage());
		}
	}

	void PrintStack(Exception e) {
		if (level == LogLevel.TEST)
			e.printStackTrace();
	}

	public void ERROR(String funcName, String message) {
		if (level > LogLevel.ERROR)
			return;
		Print(message, "ERROR");
		Log(LogLevel.ERROR, funcName, message);
	}

	public void ERROR(String funcName, Exception e) {
		if (level > LogLevel.ERROR)
			return;
		PrintStack(e);
		if (level <= LogLevel.DEBUG) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log(LogLevel.ERROR, funcName, sw.toString());
		} else {
			Log(LogLevel.ERROR, funcName, e.getLocalizedMessage());
		}
	}

	public void FATAL(String funcName, String message) {
		if (level > LogLevel.FATAL)
			return;
		Print(message, "FATAL");
		Log(LogLevel.FATAL, funcName, message);
	}

	public void FATAL(String funcName, Exception e) {
		if (level > LogLevel.FATAL)
			return;

		PrintStack(e);

		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		Log(LogLevel.FATAL, funcName, sw.toString());
	}

	public int getLineNumber() {
		return ___8drrd3148796d_Xaf();
	}

	/**
	 * This methods name is ridiculous on purpose to prevent any other method names
	 * in the stack trace from potentially matching this one.
	 * 
	 * @return The line number of the code that called the method that called this
	 *         method(Should only be called by getLineNumber()).
	 * @author Brian_Entei
	 */
	private int ___8drrd3148796d_Xaf() {
		boolean thisOne = false;
		int thisOneCountDown = 1;
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		for (StackTraceElement element : elements) {
			String methodName = element.getMethodName();
			int lineNum = element.getLineNumber();
			if (thisOne && (thisOneCountDown == 0)) {
				return lineNum;
			} else if (thisOne) {
				thisOneCountDown--;
			}
			if (methodName.equals("___8drrd3148796d_Xaf")) {
				thisOne = true;
			}
		}
		return -1;
	}

	public void BP(String process, String message) {
		if (level == LogLevel.OFF)
			return;
		// TODO: Print(message, lvl);
		LogBP(process, message);
	}

	void LogBP(String process, String message) {
		Date now = new Date();
		String fileName = Format.Show("yyyy-MM-dd", now) + "_bp.log";
		String time = "" + Format.Show("HH:mm:ss", now);

		FileUtils.Text2File(GetLogDir() + fileName, time + "\t" + process + "\t" + message + "\r\n", true);
	}

	public void Log(int logLevel, String funcName, String message) {
		Date now = new Date();
		String pref = "";
		String postf = (Global.sync_flag == 0) ? "" : "_sync_" + Global.sync_flag;
		String fileName = Format.Show("yyyy-MM-dd", now) + postf + ".log";
		String time = "" + Format.Show("HH:mm:ss", now);
		
		FileUtils.Text2File(GetLogDir() + fileName,
				time + "\t" + logLevel + "\t" + logClass + "." + funcName + "\t" + message + "\r\n", true);
		if (logLevel >= LogLevel.ERROR)
			FileUtils.Text2File(GetLogDir() + fileName + "_error",
					time + "\t" + logLevel + "\t" + logClass + "." + funcName + "\t" + message + "\r\n", true);
	}

	public void PROFILER(String name, long elapsed) {
		System.out.println(name + " " + Profiler.milliToString(elapsed) + "(" + elapsed + ")");
		Date now = new Date();
		String fileName = Format.Show("yyyy-MM-dd", now) + ".log_profiler";
		String time = "" + Format.Show("HH:mm:ss", now);
		String data = time + "\t" + "totalMemory" + "\t" + Runtime.getRuntime().totalMemory() / 10E5 + "\r\n";
		data += time + "\t" + logClass + "." + name + "\t" + elapsed + "\r\n";
		FileUtils.Text2File(GetLogDir() + fileName, data, true);
	}

}