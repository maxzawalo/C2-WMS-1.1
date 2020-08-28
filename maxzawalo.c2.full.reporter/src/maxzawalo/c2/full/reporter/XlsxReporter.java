package maxzawalo.c2.full.reporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.base.utils.Logger;
import maxzawalo.c2.free.bo.Settings;

public class XlsxReporter {

	Logger log = Logger.getLogger(getClass());

	// protected Core core;

	public String templatePath = "/maxzawalo/c2/full/report/tpl/";
	public String tplRangeName = "";

	public <Item> void PrintTableRow(List<Item> tablePart, int pos, Row row) {
		Item tp = tablePart.get(pos - 1);
		for (Cell cell : row) {
			FindCellsContains(cell, "Номер", "" + pos);
			PrintRowExt(tp, cell);
		}
	}

	protected <Item> void PrintRowExt(Item tp, Cell cell) {

	}

	// public int tablePartSize = 0;
	public int page = 0;
	public int fromRow = 0;
	public int toRow = 0;

	public <Doc> void PrintDocParams(Doc doc, Cell cell) {
		FindCellsContains(cell, "НомерДок", ((BO) doc).code);
		// TODO: если коды имеют буквы
		String code = ((BO) doc).code;
		if (code.contains("-"))
			code = code.split("-")[1];
		FindCellsContains(cell, "НомерДокЧисло", "" + Integer.parseInt(code));
		FindCellsContains(cell, "ДатаДок", Format.Show(((DocumentBO) doc).DocDate));

		FindCellsContains(cell, "_Директор_", Settings.Head.toReportShort());
		FindCellsContains(cell, "_ГлавБух_", Settings.ChiefAccounting.toReportShort());
		
		FindCellsContains(cell, "МойУнп", Settings.myFirm.unp);
		FindCellsContains(cell, "МоеИмя", Settings.myFirm.name);
		FindCellsContains(cell, "МойАдрес", Settings.myFirm.legal_address);
	}

	Map<String, List<Cell>> cells = new HashMap<>();
	Map<String, String> vars = new HashMap<>();

	public boolean hasTablePart = false;

	public void FindCellsContains(Cell cell, String var, Object value) {
		if (value == null || value == "null") {
			log.WARN("FindCellsContains", "Переменная [" + var + "] = null");
			value = "";
		}

		String cellValue = "" + getCellValue(cell);
		// System.out.println(cellValue);
		if (cellValue.equals(""))
			return;
		// TODO: exept exists var
		if (cellValue.contains(var)) {
			List<Cell> list = cells.get(var);
			if (list == null)
				list = new ArrayList<>();
			list.add(cell);
			cells.put(var, list);
			vars.put(var, "" + value);
		}
	}

	public void ReplaceCellVariables() {
		for (String var : cells.keySet()) {
			for (Cell cell : cells.get(var)) {
				String cellValue = "" + getCellValue(cell);
				String value = vars.get(var);
				cellValue = cellValue.replaceAll("\\" + var + "\\b", (value == null ? "" : value));
				cell.setCellValue(cellValue);
			}
		}
		cells.clear();
		vars.clear();
	}

	// public void ReplaceContains(Cell cell, String var, String value) {
	// String cellValue = "" + getCellValue(cell);
	// if (cellValue.contains(var)) {
	//
	// cellValue = cellValue.replaceAll("\\" + var + "\\b", value);
	// cell.setCellValue(cellValue);
	// }
	// }

	public void PrintArea(Sheet sheet, CellRangeAddress area, BO bo, int tablePartStartRow) {

	}

	public List<String> CreateAreaImages(BO bo) {
		return new ArrayList<>();
	}

	public static boolean FindTablePart(Cell cell, String tpName) {
		String cellValue = "" + getCellValue(cell);
		return cellValue.equals(tpName);

	}

	@SuppressWarnings("deprecation")
	public static Object getCellValue(Cell cell) {
		// Alternatively, get the value and format it yourself
		switch (cell.getCellTypeEnum()) {
		case STRING:
			return cell.getRichStringCellValue().getString();
		// break;
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue();
			} else {
				return cell.getNumericCellValue();
			}
			// break;
		case BOOLEAN:
			return cell.getBooleanCellValue();
		// break;
		case FORMULA:
			return cell.getCellFormula();
		// break;
		case BLANK:
			return "";
		// System.out.println();
		// break;
		default:
			return "";
		// System.out.println();
		}
	}

	protected void PrintTablePart(Sheet sheet, BO bo, int tablePartStartRow, int tablePartSize) {
		Workbook book = sheet.getWorkbook();
		List tablePart = bo.getTablePart4Rep();
		if (tablePartSize == 0)
			tablePartSize = tablePart.size();

		// TODO MergedRegions
		for (int tpRow = tablePartStartRow; tpRow < tablePartStartRow + tablePartSize; tpRow++) {
			int pos = tpRow - tablePartStartRow + 1;
			Row row = sheet.getRow(tpRow);
			// if (row == null)
			// row = sheet.createRow(tpRow);
			PrintTableRow(tablePart, pos, row);
			ReplaceCellVariables();
			Xlsx.CalcRowHeight(book, sheet, row, null);
		}
	}
}