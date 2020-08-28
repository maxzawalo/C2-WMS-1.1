package maxzawalo.c2.base.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Profiler {
	protected Logger log = Logger.getLogger(this.getClass().getSimpleName());

	class ProfilerItem {
		public long start = 0;
		public long stop = 0;
	}

	static Map<String, ProfilerItem> items = new HashMap<>();

	public void Start(String name) {
		ProfilerItem pi = new ProfilerItem();
		pi.start = System.currentTimeMillis();
		items.put(name, pi);
		log.DEBUG("Start", name + " start");
	}

	public void Stop(String name) {
		ProfilerItem pi = items.get(name);
		pi.stop = System.currentTimeMillis();
		// Logger.DEBUG(name + " stop");
	}

	public long Elapsed(String name) {
		ProfilerItem pi = items.get(name);
		if (pi != null)
			return pi.stop - pi.start;

		return -1;
	}

	public String ElapsedStr(String name) {
		return name + " " + milliToString(Elapsed(name));
	}

	public void PrintElapsed(String name) {
		// log.INFO("PrintElapsed", name + " " + milliToString(Elapsed(name)));
		log.PROFILER(name, Elapsed(name));
	}

	public String PrintCurrentElapsed(String name) {
		ProfilerItem pi = items.get(name);
		// log.INFO("PrintCurrentElapsed", name + " " +
		// milliToString(System.currentTimeMillis() - pi.start));
		String el = milliToString(System.currentTimeMillis() - pi.start);
		System.out.println(name + " " + el);
		return el;
	}

	public static String milliToString(long millis) {
		if (millis < 1000)
			return millis + " ms";

		long hrs = TimeUnit.MILLISECONDS.toHours(millis) % 24;
		long min = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
		long sec = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
		// millis = millis - (hrs * 60 * 60 * 1000); //alternative way
		// millis = millis - (min * 60 * 1000);
		// millis = millis - (sec * 1000);
		// long mls = millis ;
		long mls = millis % 1000;
		String toRet = String.format("%02d:%02d:%02d.%03d", hrs, min, sec, mls);
		// System.out.println(toRet);
		return toRet;
	}
}