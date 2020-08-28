package maxzawalo.c2.base.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Max on 26.03.2017.
 */

public class Format {

	static Logger log = Logger.getLogger(Format.class);

	public static String Show(double value) {
		return Show(value, 2);
	}

	public static String Show(double value, int places) {
		if (places == 0)
			return formatDouble(value, "0");
		String after = "";
		for (int i = 0; i < places; i++)
			after += "0";

		if (value == 0)
			return "0." + after;
		return formatDouble(value, "0." + after);
	}

	public static String formatDouble(double value, String format) {
		return new DecimalFormat(format).format(value).replaceAll(",", ".");
	}

	public static String get(double value) {
		DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
		DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
		symbols.setGroupingSeparator(' ');
		formatter.setDecimalFormatSymbols(symbols);
		return formatter.format(value);
	}

	static String defDateFormat = "dd.MM.yyyy";

	public static String Show(String format, Date date) {
		SimpleDateFormat dt = new SimpleDateFormat(format);
		return dt.format(date);
	}

	public static String Show(Date date) {
		return Show(defDateFormat, date);
	}

	public static Date GetDate(String dateValue, String format) {
		SimpleDateFormat dt = new SimpleDateFormat(format);
		try {
			return dt.parse(dateValue);
		} catch (ParseException e) {
			log.ERROR("GetDate", e);
		}
		return null;
	}

	public static Date FirstDayOfYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DAY_OF_MONTH, 1);

		return cal.getTime();
	}

	public static Date LastDayOfYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MONTH, 11);
		cal.set(Calendar.DAY_OF_MONTH, 31);

		return cal.getTime();
	}

	public static Date AddMonth(Date date, int monthCount) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, monthCount);
		return cal.getTime();
	}

	public static Date AddDay(Date date, int dayCount) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, dayCount);
		return cal.getTime();
	}

	public static Date beginOfDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();
	}

	public static Date endOfDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);

		return cal.getTime();
	}

	public static Date GetDate(String dateValue) {
		return GetDate(dateValue, defDateFormat);
	}

	// public static Double roundDouble(Double valueToFormat, int places) {
	// long mul = 1;
	// for (int i = 0; i < places; i++)
	// mul *= 10;
	// long rounded = Math.round(valueToFormat * mul);
	// return rounded / (double) mul;
	// }

	public static Double roundDouble(Double valueToFormat, int places) {
		BigDecimal bd = new BigDecimal(Double.toString(valueToFormat));
		bd = bd.setScale(places, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}

	public static Double truncDouble(Double valueToFormat, int places) {
		BigDecimal bd = new BigDecimal(Double.toString(valueToFormat));
		bd = bd.setScale(places, BigDecimal.ROUND_DOWN);
		return bd.doubleValue();
	}

	public static Double defaultRound(Double valueToFormat) {
		return roundDouble(valueToFormat, 2);
	}

	public static final int countRoundPlaces = 3;

	public static Double countRound(Double valueToFormat) {
		return roundDouble(valueToFormat, countRoundPlaces);
	}

	public static Double countTrunc(Double valueToFormat) {
		return truncDouble(valueToFormat, countRoundPlaces);
	}

	public static double extractDouble(String value) {
		value = value.replace("%", "");
		// Ни в коем случае не убирать МИНСУС!!!
		// value = value.replace("-", "");
		value = value.replace(" ", "");
		// 1.063,43
		if (value.contains(".") && value.contains(","))
			value = value.replace(".", "");
		value = value.replace(",", ".");
		if (value.equals("") || value.equals("-"))
			return 0;

		return Double.parseDouble(value);
	}

	public static Date extractJsonDate(String value) {
		return GetDate(value, "yyyy-MM-dd'T'HH:mm:ss");
	}

	public static Double roundTo10Kop(Double valueToFormat) {
		int places = 1;
		// Чтобы не учитывались тысячные и меньше
		valueToFormat = defaultRound(valueToFormat);
		Double value = roundDouble(valueToFormat, places);
		value = (value < valueToFormat) ? roundDouble(value + 0.1, places) : value;
		return value;
	}

	public static String intToString(int num, int digits) {
		String output = Integer.toString(num);
		while (output.length() < digits)
			output = "0" + output;
		return output;
	}

	public static Date extractDate(String data) {
		return extractDate(new Date(), data);
	}

	public static Date extractDate(Date current, String data) {
		Date date = null;
		data = data.trim();
		data = data.replace("  ", " ");
		String parts[] = data.split("[\\., ]+");
		if (parts.length > 0) {
			Calendar currentСal = Calendar.getInstance();
			currentСal.setTime(current);

			int day = Integer.parseInt(parts[0]);
			// Вдруг день ввели год
			if (day > 31)
				day = 31;

			int month = currentСal.get(Calendar.MONTH) + 1;
			int year = currentСal.get(Calendar.YEAR);

			// 2 числа
			if (parts.length > 1) {
				month = Integer.parseInt(parts[1]);
				// Вдруг день ввели день или год
				if (month > 12)
					month = currentСal.get(Calendar.MONTH) + 1;
				// 3 числа
				if (parts.length > 2) {
					year = Integer.parseInt(parts[2]);
					if (year < 100)
						year += 100 * Format.truncDouble((double) currentСal.get(Calendar.YEAR) / 100, 0);

					date = Format.GetDate(intToString(day, 2) + "." + intToString(month, 2) + "." + year);
					// Если есть год, то добавляем его в текущий, чтобы можно
					// было корректно вводить прошлые и будущие года
					if (year != currentСal.get(Calendar.YEAR)) {
						// TODO: Если месяц ввели некоректно - увы
						// MONTH zero based
						currentСal.set(Calendar.MONTH, month - 1);
						// Пока учитываем только месяц - на будущее все
						// добавляем
						currentСal.set(Calendar.YEAR, year);
						// День лучше не ставить - если неправильный - сменит
						// месяц
						// currentСal.set(Calendar.DAY_OF_MONTH, day);
					}
					date = CheckDayRace(date, day, month, currentСal);
					return date;
				}
				date = Format.GetDate(intToString(day, 2) + "." + intToString(month, 2) + "." + year);
				date = CheckDayRace(date, day, month, currentСal);
				return date;
			}

			date = Format.GetDate(intToString(day, 2) + "." + intToString(month, 2) + "." + year);
			date = CheckDayRace(date, day, month, currentСal);
		}
		return date;
	}

	protected static Date CheckDayRace(Date date, int parsedDay, int parsedMonth, Calendar currentСal) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		// Только следующий месяц и год (12 -> 1)
		// MONTH zero based
		if ((cal.get(Calendar.DAY_OF_MONTH) != parsedDay && cal.get(Calendar.MONTH) + 1 - parsedMonth == 1)
				|| (cal.get(Calendar.YEAR) - currentСal.get(Calendar.YEAR) == 1 && cal.get(Calendar.MONTH) == 0)) {
			// смотрим не перескочили ли с датой
			while (cal.get(Calendar.MONTH) != currentСal.get(Calendar.MONTH)) {
				// Откатываем по дню назад до последего дня в месяце
				date = Format.AddDay(date, -1);
				cal.setTime(date);
			}
		}
		return date;
	}
}