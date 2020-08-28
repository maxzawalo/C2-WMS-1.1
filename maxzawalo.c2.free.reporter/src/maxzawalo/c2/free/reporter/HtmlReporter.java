package maxzawalo.c2.free.reporter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JLabel;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.os.Run;
import maxzawalo.c2.base.utils.FileUtils;
import maxzawalo.c2.base.utils.Format;

public class HtmlReporter {
	public static <T> String Create(Class<T> viewType, List<T> list, String title) {
		return Create(viewType, list, title, "", "");
	}

	public static <T> String Create(Class<T> viewType, List<T> list, String title, String title2, String title3) {
		List<List<String>> matrix = new ArrayList<>();

		List<Integer> align = new ArrayList<>();
		List<String> header = new ArrayList<>();
		for (Field f : viewType.getFields()) {
			try {
				// если не помечено поле - пропускаем
				BoField ann = f.getAnnotation(BoField.class);
				if (ann == null)
					continue;
				header.add("" + ann.caption());
				align.add(ann.horizontalAlignment());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		matrix.add(header);

		for (T item : list) {
			List<String> row = new ArrayList<>();
			for (Field f : viewType.getFields()) {
				try {
					// если не помечено поле - пропускаем
					BoField ann = f.getAnnotation(BoField.class);
					if (ann == null)
						continue;
					Object value = f.get(item);
					if (value instanceof Date)
						value = Format.Show((Date) value);
					row.add("" + value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			matrix.add(row);
		}
		return Create(matrix, align, viewType.getName(), title, title2, title3);
	}

	public static String Create(List<List<String>> matrix, String reportName, String title, String title2,
			String title3) {
		return Create(matrix, null, reportName, title, title2, title3);
	}

	public static void CreateFile(List<List<String>> matrix, List<Integer> align, String reportName, String title,
			String title2, String title3) {
		String filename = FileUtils.GetReportDir() + reportName + "_" + System.currentTimeMillis() + ".html";
		FileUtils.Text2File(filename, Create(matrix, align, reportName, title, title2, title3), false);
		Run.OpenFile(filename);
	}

	public static String Create(List<List<String>> matrix, List<Integer> align, String reportName, String title,
			String title2, String title3) {

		String data = "<html> <head> <meta http-equiv='content-type' content='text/html; charset=utf-8'>";
		data += "<title>" + title + "</title>";
		data += "</head><body>";
		data += "<h1>" + title + "</h1>";
		data += "<h3>" + title2 + "</h3>";
		data += "<h3>" + title3 + "</h3>";
		data += "<style>" + "table {\r\n" + "border:solid 1px black;\r\n" + "border-collapse: collapse;}\r\n";
		data += "td {\r\n" + "border:solid 1px black;\r\n" + "border-collapse: collapse;}\r\n";
		data += "th {\r\n" + "border:solid 1px black;\r\n" + "border-collapse: collapse; font-weight: bold;}\r\n";
		data += "</style>";
		data += "<table>";
		data += "<tr>";

		for (String cell : matrix.get(0)) {
			data += "<th>";
			try {
				data += cell;
			} catch (Exception e) {
				e.printStackTrace();
			}
			data += "</th>";
		}
		data += "</tr>";

		StringBuilder builder = new StringBuilder();
		builder.append(data);
		for (int i = 1; i < matrix.size(); i++) {
			builder.append("<tr>");
			int col = 0;
			for (String cell : matrix.get(i)) {
				builder.append("<td style='text-align:" + getAlign(align, col) + "'>");
				builder.append(cell);
				builder.append("</td>");
				col++;
			}
			builder.append("</tr>");
		}
		builder.append("</table>");

		builder.append("</body>");
		builder.append("</html>");

		return builder.toString();
	}

	protected static String getAlign(List<Integer> align, int col) {
		if (align == null)
			return "left";

		switch (align.get(col)) {
		case JLabel.RIGHT:
			return "right";
		case JLabel.CENTER:
			return "center";
		default:
			return "left";
		}
	}
}