package maxzawalo.c2.full.data.decoder;

import java.util.HashMap;
import java.util.Map;

public class BarcodeDecoder {

	public static String[] DecodeStrictNumber(String barcode) {
		String[] items = new String[2];

		// TODO: что выдают штрихкоды меньше чем 7 символов в номере?
		if (barcode.length() != 14)
			return new String[] {};

		Map<Integer, String> letters = new HashMap<>();
		letters.put(1, "А");
		letters.put(2, "Б");
		letters.put(3, "В");
		letters.put(4, "Г");
		letters.put(5, "Д");
		letters.put(6, "Е");
		letters.put(7, "Ж");
		letters.put(8, "И");
		letters.put(9, "К");
		letters.put(10, "Л");
		letters.put(11, "М");
		letters.put(12, "Н");
		letters.put(13, "О");
		letters.put(14, "П");
		letters.put(15, "Р");
		letters.put(16, "С");
		letters.put(17, "Т");
		letters.put(18, "У");
		letters.put(19, "Ф");
		letters.put(20, "Х");
		letters.put(21, "Ч");
		letters.put(22, "Ш");
		letters.put(23, "Э");
		letters.put(24, "Ю");
		letters.put(25, "Я");

		String type = barcode.substring(0, 3);
		String lett1 = barcode.substring(3, 5);
		String lett2 = barcode.substring(5, 7);

		String number = barcode.substring(7, barcode.length());

		items[0] = letters.get(Integer.parseInt(lett1)) + letters.get(Integer.parseInt(lett2));

		items[1] = number;

		return items;
	}
}