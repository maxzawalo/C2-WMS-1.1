package maxzawalo.c2.base.utils;

import static maxzawalo.c2.base.utils.FileUtils.GetLogDir;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sun.org.apache.bcel.internal.generic.FMUL;

import maxzawalo.c2.base.utils.Logger.LogLevel;

public class Console {

	static Logger log = Logger.getLogger(Console.class);

	private static volatile Console instance;

	public static Console I() {
		Console localInstance = instance;
		if (localInstance == null) {
			synchronized (Console.class) {
				localInstance = instance;
				if (localInstance == null) {
					instance = localInstance = new Console();
				}
			}
		}
		return localInstance;
	}

	private Console() {
		saver.start();
	}

	public class LogItem {
		public Date time = new Date();
		public int level;
		public String message = "";
		public String sender = "";
	}

	List<LogItem> items = new ArrayList<>();

	void Print(Class fromClass, String fromFunc, String message, int level) {
		LogItem item = new LogItem();
		item.sender = fromClass.getName() + "." + fromFunc;
		item.level = level;
		item.message = message;

		items.add(item);
	}

	public void INFO(Class fromClass, String fromFunc, String message) {
		Print(fromClass, fromFunc, message, LogLevel.INFO);
	}

	public void WARN(Class fromClass, String fromFunc, String message) {
		Print(fromClass, fromFunc, message, LogLevel.WARN);
	}

	public void ERROR(Class fromClass, String fromFunc, String message) {
		Print(fromClass, fromFunc, message, LogLevel.ERROR);
	}

	void Dump(List<LogItem> items) {
		String fileName = "";
		String data = "";
		for (LogItem i : items) {
			String fn = Format.Show("yyyy-MM-dd", i.time) + ".log_console";
			if (fileName.isEmpty())
				fileName = fn;
			else if (!fileName.equals(fn)) {
				// Сменилась дата
				fileName = fn;
				FileUtils.Text2File(GetLogDir() + fileName, data, true);
				data = "";
			}
			data += Format.Show("HH:mm:ss", i.time) + "\t" + i.time.getTime() + "\t" + i.level + "\t" + i.message + "\t"
			    + i.sender + "\r\n";
		}
		// Не все сохранили
		if (!data.isEmpty())
			FileUtils.Text2File(GetLogDir() + fileName, data, true);
	}

	Thread saver = new Thread(new Runnable() {
		public synchronized void run() {
			while (true) {
				try {
					Thread.sleep(8 * 1000);

					Calendar calendar = Calendar.getInstance();
					calendar.add(Calendar.SECOND, -10);

					// Сортируем - вдруг потоки напихали не по времени
					List<LogItem> old = items.stream()//
					    .filter(i -> i.time.getTime() < calendar.getTimeInMillis())//
					    .sorted((e1, e2) -> e1.time.compareTo(e2.time))//
					    .collect(Collectors.toList());
					
					if (old.size() == 0)
						continue;
					System.out.println("Dump " + old.size());
					Dump(old);

					items.removeIf(i -> i.time.getTime() < calendar.getTimeInMillis());
				} catch (Exception e) {
					log.ERROR("saver", e);
				}
			}
		}
	});

	public List<LogItem> getMessages(Date lastRead) {
		// Сортируем - вдруг потоки напихали не по времени
		return items.stream()//
		    .filter(i -> i.time.getTime() > lastRead.getTime())//
		    .sorted((e1, e2) -> e1.time.compareTo(e2.time))//
		    .collect(Collectors.toList());
	}
}