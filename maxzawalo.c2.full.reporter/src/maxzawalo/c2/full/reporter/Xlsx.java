package maxzawalo.c2.full.reporter;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.data.factory.FactoryBO;
import maxzawalo.c2.base.utils.Logger;
import maxzawalo.c2.base.utils.StringUtils;
import maxzawalo.c2.free.bo.store.StoreDocBO;

public class Xlsx {

	static Logger log = Logger.getLogger(Xlsx.class);
	static int maxCol = 100;

	private static void copyRow(Workbook workbook, Sheet worksheet, int sourceRowNum, int destinationRowNum) {
		copyRow(workbook, worksheet, worksheet, sourceRowNum, destinationRowNum);
	}

	private static void copyRow(Workbook workbook, Sheet fromSheet, Sheet toSheet, int sourceRowNum,
			int destinationRowNum) {
		copyRow(workbook, fromSheet, toSheet, sourceRowNum, destinationRowNum, 0, 0, 0);
	}

	@SuppressWarnings("deprecation")
	private static void copyRow(Workbook workbook, Sheet fromSheet, Sheet toSheet, int sourceRowNum,
			int destinationRowNum, int fromSourceColNum, int toSourceColNum, int destinationColNum) {
		// Get the source / new row
		Row sourceRow = fromSheet.getRow(sourceRowNum);
		Row newRow = toSheet.getRow(destinationRowNum);

		// If the row exist in destination, push down all rows by 1 else create
		// a new row
		if (newRow != null) {
			// System.out.println("");
			// toSheet.shiftRows(destinationRowNum, toSheet.getLastRowNum(), 1);
		} else {
			newRow = toSheet.createRow(destinationRowNum);
		}

		newRow.setHeight(sourceRow.getHeight());

		// Loop through source columns to add to new row
		for (int sourceCol = fromSourceColNum; sourceCol < ((toSourceColNum == 0) ? maxCol
				: toSourceColNum); sourceCol++) {
			// Grab a copy of the old/new cell
			Cell oldCell = sourceRow.getCell(sourceCol);
			int idx = sourceCol + destinationColNum - fromSourceColNum;
			// System.out.println(idx);
			Cell newCell = newRow.createCell(idx);

			// If the old cell is null jump to next cell
			if (oldCell == null) {
				newCell = null;
				continue;
			}

			// Copy style from old cell and apply to new cell
			CellStyle newCellStyle = workbook.createCellStyle();
			newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
			newCell.setCellStyle(newCellStyle);

			// If there is a cell comment, copy
			if (newCell.getCellComment() != null) {
				newCell.setCellComment(oldCell.getCellComment());
			}

			// If there is a cell hyperlink, copy
			if (oldCell.getHyperlink() != null) {
				newCell.setHyperlink(oldCell.getHyperlink());
			}

			// Set the cell data type
			newCell.setCellType(oldCell.getCellType());

			// Set the cell data value
			switch (oldCell.getCellType()) {
			case Cell.CELL_TYPE_BLANK:
				newCell.setCellValue(oldCell.getStringCellValue());
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				newCell.setCellValue(oldCell.getBooleanCellValue());
				break;
			case Cell.CELL_TYPE_ERROR:
				newCell.setCellErrorValue(oldCell.getErrorCellValue());
				break;
			case Cell.CELL_TYPE_FORMULA:
				newCell.setCellFormula(oldCell.getCellFormula());
				break;
			case Cell.CELL_TYPE_NUMERIC:
				newCell.setCellValue(oldCell.getNumericCellValue());
				break;
			case Cell.CELL_TYPE_STRING:
				newCell.setCellValue(oldCell.getRichStringCellValue());
				break;
			}
		}

		// If there are are any merged regions in the source row, copy to new
		// row
		for (int i = 0; i < fromSheet.getNumMergedRegions(); i++) {
			CellRangeAddress cellRangeAddress = fromSheet.getMergedRegion(i);
			// TODO: except system merged
			if (cellRangeAddress.getFirstRow() == sourceRow.getRowNum()) {
				CellRangeAddress newCellRangeAddress = null;
				if (fromSourceColNum == 0 && toSourceColNum == 0) {
					newCellRangeAddress = new CellRangeAddress(newRow.getRowNum(),
							(newRow.getRowNum() + (cellRangeAddress.getLastRow() - cellRangeAddress.getFirstRow())),
							cellRangeAddress.getFirstColumn(), cellRangeAddress.getLastColumn());

				} else {
					if (cellRangeAddress.getFirstColumn() >= fromSourceColNum
							&& cellRangeAddress.getFirstColumn() <= toSourceColNum) {
						newCellRangeAddress = new CellRangeAddress(newRow.getRowNum(),
								(newRow.getRowNum() + (cellRangeAddress.getLastRow() - cellRangeAddress.getFirstRow())),
								cellRangeAddress.getFirstColumn() + destinationColNum - fromSourceColNum,
								cellRangeAddress.getLastColumn() + destinationColNum - fromSourceColNum);
					}
				}

				try {
					toSheet.addMergedRegion(newCellRangeAddress);
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * 
	 * @param sheet
	 * @param cell
	 * @return Если не MergedRegions - возвращает 0
	 */
	public static int getCellInMergedRegionWidth(Sheet sheet, Cell cell) {

		for (int i = 0; i < sheet.getNumMergedRegions(); ++i) {
			CellRangeAddress range = sheet.getMergedRegion(i);

			if (range.getFirstRow() <= cell.getRowIndex() && range.getLastRow() >= cell.getRowIndex()
					&& range.getFirstColumn() <= cell.getColumnIndex()
					&& range.getLastColumn() >= cell.getColumnIndex()) {
				int width = 0;
				// System.out.println("-Region");
				for (int col = range.getFirstColumn(); col <= range.getLastColumn(); col++) {
					// System.out.println("sheet.getColumnWidth(col)=" +
					// sheet.getColumnWidth(col));
					width += sheet.getColumnWidth(col);
				}

				return width;
			}

		}
		return sheet.getColumnWidth(cell.getColumnIndex());
	}

	static BufferedImage bImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
	static int xlsxMult = 20;

	static void CalcRowHeight(Workbook workbook, Sheet sheet, Row row) {
		CalcRowHeight(workbook, sheet, row, null);
	}

	public static void CalcRowHeight(Workbook workbook, Sheet sheet, Row row, String[] contains) {
		// System.out.println("---CalcRowHeight");
		boolean foundContains = (contains == null);

		int maxHeight = row.getHeight();
		int lineCnt = 0;
		for (Cell cell : row) {
			int mergedCellWidth = (int) (getCellInMergedRegionWidth(sheet, cell) / xlsxMult / 3.1);
			// System.out.println("mergedCellWidth=" + mergedCellWidth);
			if (mergedCellWidth == 0)
				continue;

			// mergedCellWidth = 100;

			CellStyle style = cell.getCellStyle();
			short fontIdx = style.getFontIndex();
			Font font = workbook.getFontAt(fontIdx);

			String cellValue = "" + XlsxReporter.getCellValue(cell);
			if (cellValue.equals(""))
				continue;

			if (!foundContains && contains != null)
				for (String s : contains)
					foundContains |= cellValue.contains(s);

			java.awt.Font currFont = new java.awt.Font(font.getFontName(), 0, font.getFontHeight() / xlsxMult);
			AttributedString attrStr = new AttributedString(cellValue);
			attrStr.addAttribute(TextAttribute.FONT, currFont);

			// Use LineBreakMeasurer to count number of lines needed for the
			// text
			FontRenderContext frc = new FontRenderContext(null, true, true);
			LineBreakMeasurer measurer = new LineBreakMeasurer(attrStr.getIterator(), frc);
			int nextPos = 0;
			lineCnt = 0;
			// while (measurer.getPosition() < cellValue.length()) {
			// // mergedCellWidth is the max width of each line
			// nextPos = measurer.nextOffset(mergedCellWidth);
			// lineCnt++;
			// measurer.setPosition(nextPos);
			// }

			Graphics2D graphics = bImage.createGraphics();
			FontMetrics metrics = graphics.getFontMetrics(currFont);
			lineCnt = StringUtils.wrap(cellValue, metrics, mergedCellWidth).size();

			// Row currRow = currSht.getRow(rowNum);
			// currRow.setHeight((short) (currRow.getHeight() * lineCnt));
			// System.out.println("lineCnt=" + lineCnt);
			maxHeight = (int) Math.max(maxHeight, (1.4 * currFont.getSize() * xlsxMult * lineCnt));
			// maxHeight = (int) Math.max(maxHeight, row.getHeight() * lineCnt);

		}

		// System.out.println("maxHeight=" + maxHeight);
		if (foundContains)
			row.setHeight((short) maxHeight);
	}

	public static <Doc, Item> void PrintDoc(String filename, Doc doc, List<Item> tablePart, XlsxReporter reporter) {
		PrintDoc(filename, doc, tablePart, reporter, true);
	}

	public static XSSFClientAnchor a(Sheet sheet, int imgIndex) {
		XSSFDrawing dp = ((XSSFSheet) sheet).createDrawingPatriarch();
		List<XSSFShape> pics = dp.getShapes();
		if (pics.size() == 0)
			return null;

		XSSFPicture inpPic = (XSSFPicture) pics.get(imgIndex);

		XSSFClientAnchor clientAnchor = inpPic.getPreferredSize();// inpPic.getClientAnchor();

		System.out.println("col1: " + clientAnchor.getCol1() + ", col2: " + clientAnchor.getCol2() + ", row1: "
				+ clientAnchor.getRow1() + ", row2: " + clientAnchor.getRow2());
		System.out.println("x1: " + clientAnchor.getDx1() + ", x2: " + clientAnchor.getDx2() + ", y1: "
				+ clientAnchor.getDy1() + ", y2: " + clientAnchor.getDy2());

		return clientAnchor;
	}

	public static <Doc, Item> void PrintDocAddition(String reportPath, Doc doc, XlsxReporter reporter,
			int rowsPerPage) {
		try {
			List tablePart = ((BO) doc).getTablePart4Rep();

			// List<Item> tablePart = ((DocumentBO) doc).GetReportTP();
			InputStream in = Xlsx.class.getClass().getResourceAsStream(reporter.templatePath);
			Workbook book = new XSSFWorkbook(in);
			// Sheet sheet = book.getSheetAt(0);

			String tplName = "шаблон";
			Sheet fromSheet = book.getSheet(tplName);

			int pageCount = (int) FactoryBO.GetPagesCount(tablePart.size(), rowsPerPage);
			for (int page = 0; page < pageCount; page++) {
				Sheet sheet = CreateSheetFromTpl(fromSheet, "Страница " + (page + 1));
				PrintSheet(sheet, doc, tablePart, reporter, true, page, rowsPerPage);
			}

			// Удаляем шаблон
			book.removeSheetAt(book.getSheetIndex(tplName));

			// String filename = FileUtils.GetReportDir() + ((BO)
			// doc).getClass().getSimpleName() + "_"
			// + ((DocumentBO) doc).code + "_Addition" +
			// System.currentTimeMillis() + ".xlsx";
			// Записываем всё в файл
			book.write(new FileOutputStream(reportPath));
			book.close();
			// Run.OpenFile(filename);

		} catch (Exception e) {
			log.ERROR("PrintDocAddition", e);
		}
	}

	public static <Doc, Item> void PrintMultipleDocs(String reportPath, List<Doc> docs, XlsxReporter reporter) {
		try {
			// List<Item> tablePart = ((DocumentBO) doc).GetReportTP();
			InputStream in = Xlsx.class.getClass().getResourceAsStream(reporter.templatePath);
			Workbook book = new XSSFWorkbook(in);
			// Sheet sheet = book.getSheetAt(0);

			String tplName = "шаблон";
			Sheet fromSheet = book.getSheet(tplName);

			for (int page = 0; page < docs.size(); page++) {
				Sheet sheet = CreateSheetFromTpl(fromSheet, "Страница " + (page + 1));
				Doc d = docs.get(page);
				// TODO:TablePart? rowsPerPage?
				PrintSheet(sheet, d, ((StoreDocBO) d).TablePartProduct, reporter, true, 0, 1000);

				// шаблон!$A$1:$K$16
				String printArea = book.getPrintArea(0);
				printArea = printArea.split("!")[1];

				int ind = printArea.lastIndexOf('$');
				String first = printArea.substring(0, ind);
				String second = printArea.substring(ind + 1, printArea.length());
				printArea = first + "$" + (Integer.parseInt(second) + ((StoreDocBO) d).TablePartProduct.size());
				// set print area with indexes
				book.setPrintArea(page + 1, printArea
				// 0, // start column
				// 20, // end column
				// 0, // start row
				// 20 // end row
				);

			}

			// Удаляем шаблон
			book.removeSheetAt(book.getSheetIndex(tplName));

			// String filename = FileUtils.GetReportDir() + ((BO)
			// doc).getClass().getSimpleName() + "_"
			// + ((DocumentBO) doc).code + "_Addition" +
			// System.currentTimeMillis() + ".xlsx";
			// Записываем всё в файл
			book.write(new FileOutputStream(reportPath));
			book.close();
			// Run.OpenFile(filename);

		} catch (Exception e) {
			log.ERROR("PrintMultipleDocs", e);
		}
	}

	public static <Item> void PrintMatrix(String filename, List<Item> list, XlsxReporter reporter, int maxX, int maxY) {
		PrintMatrix(filename, list, reporter, maxX, maxY, false);
	}

	public static <Item> void PrintMatrix(String filename, List<Item> list, XlsxReporter reporter, int maxX, int maxY,
			boolean hasTablePart) {
		try {
			// BO doc = (BO) type.newInstance();
			// System.out.println("list.size()=" + list.size());

			// String templatePath = ;
			InputStream in = Xlsx.class.getClass().getResourceAsStream(reporter.templatePath);
			// InputStream in =
			// Xlsx.class.getClass().getResourceAsStream(reporter.templatePath);
			Workbook book = new XSSFWorkbook(in);
			// Sheet sheet = book.getSheetAt(0);

			int startRow = 0;
			int startCol = 0;

			String tplName = "шаблон";
			Sheet fromSheet = book.getSheet(tplName);
			int page = 1;
			int pos = 0;
			boolean endList = false;
			int Y = 0;
			int X = 0;
			int tpSizeSum = 0;
			while (!endList) {
				String sheetName = "Страница " + page;
				Sheet toSheet = book.createSheet(sheetName);

				CellRangeAddress area = new CellRangeAddress(0, 0, 0, 0);
				for (int y = 0; y < maxY && !endList; y++) {
					startCol = 0;
					for (int x = 0; x < maxX && !endList; x++) {
						Object[] retParams = new Object[10];
						int tpSize = ((BO) list.get(pos)).getTablePart4Rep().size();
						tpSizeSum += tpSize;
						area = CreateAreaFromTpl(fromSheet, toSheet, reporter.tplRangeName, startRow, startCol,
								reporter.CreateAreaImages((BO) list.get(pos)), reporter.hasTablePart, tpSize,
								retParams);
						// System.out.println("pos=" + pos);
						int tablePartStartRow = (int) retParams[0];
						reporter.PrintArea(toSheet, area, (BO) list.get(pos), tablePartStartRow);
						pos++;
						endList = (pos >= list.size());

						for (int row = tablePartStartRow; row < tablePartStartRow + tpSize; row++)
							CalcRowHeight(book, toSheet, toSheet.getRow(row), new String[] { "ТабЧасть", "Подпись:" });

						// Прячем системную колонку ТабЧасть
						toSheet.setColumnHidden(startCol, true);

						// Через Math.max - может разная длина таб части
						startCol = Math.max(startCol, area.getLastColumn());
						X = Math.max(X, x);
					}
					// TODO: проработать
					startRow = Math.max(startRow, area.getLastRow() + 1);
					Y = Math.max(Y, y);
				}

				String printArea = book.getPrintArea(0);// TODO: by name
				if (printArea != null) {
					printArea = printArea.split("!")[1];
					int ind = printArea.lastIndexOf(':');
					String bottomRight = printArea.substring(ind + 2, printArea.length());
					printArea = printArea.substring(0, ind + 2);

					String letter = bottomRight.split("\\$")[0];
					String number = bottomRight.split("\\$")[1];
					// Учитываем 2 системные колонки слева
					int colIdx = CellReference.convertColStringToIndex(letter);
					printArea = "$A$1:$" + CellReference.convertNumToColString((X + 1) * colIdx) + "$"
							+ ((Y + 1) * (Integer.parseInt(number) - 1) + tpSizeSum);
					System.out.println("===" + printArea);// U54
					// TODO: это устанавливает область печати
					// book.setPrintArea(page, printArea);
				}

				page++;

				startRow = 0;
				tpSizeSum = 0;
				Y = 0;
				X = 0;
			}

			// Удаляем шаблон
			book.removeSheetAt(book.getSheetIndex(tplName));

			// Записываем всё в файл
			book.write(new FileOutputStream(filename));
			book.close();
		} catch (Exception e) {
			log.ERROR("PrintMatrix", e);
		}
	}

	protected static Sheet CreateSheetFromTpl(Sheet fromSheet, String sheetName) {
		Workbook book = fromSheet.getWorkbook();
		// for (int page = 1; page <= pageCount; page++) {
		Sheet newSheet = book.createSheet(sheetName);
		for (Row row : fromSheet)
			copyRow(book, fromSheet, newSheet, row.getRowNum(), row.getRowNum());

		for (int i = 0; i < maxCol; i++)
			newSheet.setColumnWidth(i, fromSheet.getColumnWidth(i));

		for (Row row : newSheet)
			CalcRowHeight(book, newSheet, row, new String[] { "доверенности", "Погрузка", "Разгрузка" });

		// CopyImages(book, fromSheet, toSheet);
		return newSheet;
	}

	protected static CellRangeAddress CreateAreaFromTpl(Sheet fromSheet, Sheet toSheet, String rangeName, int startRow,
			int startCol, List<String> imagePath, boolean hasTablePart, int tablePartSize, Object[] retParams) {
		Workbook book = fromSheet.getWorkbook();
		// for (int page = 1; page <= pageCount; page++) {
		int picCount = 1;// TODO:
		if (imagePath.equals(""))
			picCount = 0;

		String[] r = rangeName.split(":");
		// int systemColCount = 2;

		CellRangeAddress horizRegion = getHorizRegion(fromSheet, r[0]);
		int fromSourceColNum = horizRegion.getFirstColumn();
		int toSourceColNum = horizRegion.getLastColumn() + 1;
		int areaWidth = toSourceColNum - fromSourceColNum;

		CellRangeAddress vertRegion = getVertRegion(fromSheet, r[1]);
		int fromSourceRow = vertRegion.getFirstRow();
		int toSourceRow = vertRegion.getLastRow() + 1;
		int areaHeight = toSourceRow - fromSourceRow;

		CellRangeAddress area = new CellRangeAddress(startRow, startRow + areaHeight, startCol, startCol + areaWidth);
		int tablePartStartRow = 0;

		// List<CellRangeAddress> afterTPMergedRegions = new ArrayList<>();
		if (hasTablePart) {
			// Ищем таб часть
			tablePartStartRow = FindTablePart(fromSheet, area, "ТабЧасть") - fromSourceRow;

			// Копируем область до ТабЧасти включительно
			for (int row = area.getFirstRow(); row <= tablePartStartRow; row++) {
				copyRow(book, fromSheet, toSheet, row + fromSourceRow - area.getFirstRow(), row, fromSourceColNum,
						toSourceColNum, area.getFirstColumn());
			}

			// Копируем область после ТабЧасти с учетом сдвига = tablePartSize
			for (int row = tablePartStartRow + 1; row < area.getLastRow(); row++) {
				copyRow(book, fromSheet, toSheet, row + fromSourceRow - area.getFirstRow(), row + tablePartSize - 1,
						fromSourceColNum, toSourceColNum, area.getFirstColumn());
			}

			// Копируем строку с переменными табличной части
			for (int i = 1; i < tablePartSize; i++) {
				copyRow(book, toSheet, tablePartStartRow, tablePartStartRow + i);
			}
		} else {
			tablePartSize = 0;
			for (int row = area.getFirstRow(); row < area.getLastRow(); row++) {
				copyRow(book, fromSheet, toSheet, row + fromSourceRow - area.getFirstRow(), row, fromSourceColNum,
						toSourceColNum, area.getFirstColumn());
			}
		}

		for (int col = area.getFirstColumn(); col < area.getLastColumn(); col++)
			toSheet.setColumnWidth(col, fromSheet.getColumnWidth(col + fromSourceColNum - area.getFirstColumn()));

		// // TODO: Определить в какое место табчасти попадает картинка
		// // if(tablePartStartRow + tablePartSize > fromSourceRow)
		try {
			CopyImage(fromSheet, toSheet, fromSourceRow, toSourceRow, fromSourceColNum, toSourceColNum,
					area.getFirstColumn(), area.getFirstRow() + Math.max(tablePartSize - 1, 0), imagePath);
		} catch (IndexOutOfBoundsException e) {
			// Сгенерировани картинок больше, чем есть в шаблоне
			log.ERROR("CreateAreaFromTpl", "Сгенерировани картинок больше, чем есть в шаблоне");
		}

		area.setLastRow(area.getLastRow() + tablePartSize - 1);
		// retParams = new Object[1];
		retParams[0] = tablePartStartRow;

		return area;
	}

	protected static int FindTablePart(Sheet sheet, CellRangeAddress area, String tpName) {
		int tablePartStartRow = -1;
		for (Row row : sheet) {
			if (row.getRowNum() >= area.getFirstRow() && row.getRowNum() <= area.getLastRow()) {
				for (Cell cell : row) {
					if (XlsxReporter.FindTablePart(cell, tpName))
						tablePartStartRow = row.getRowNum();
				}
			}
		}
		return tablePartStartRow;
	}

	protected static CellRangeAddress getHorizRegion(Sheet fromSheet, String rangeName) {
		for (CellRangeAddress merged : fromSheet.getMergedRegions()) {
			// String value = ""
			// +
			// Reporter.getCellValue(fromSheet.getRow(merged.getFirstRow()).getCell(merged.getFirstColumn()));
			// System.out.println(value);
			// if (merged.getFirstRow() == 0 && merged.getLastRow() == 0)
			{
				String value = "" + XlsxReporter
						.getCellValue(fromSheet.getRow(merged.getFirstRow()).getCell(merged.getFirstColumn()));
				if (value.equals(rangeName)) {
					return merged;
				}
			}
		}
		return null;
	}

	protected static CellRangeAddress getVertRegion(Sheet fromSheet, String rangeName) {
		for (CellRangeAddress merged : fromSheet.getMergedRegions()) {
			// if (merged.getFirstColumn() == 0 && merged.getLastColumn() == 0)
			{
				String value = "" + XlsxReporter
						.getCellValue(fromSheet.getRow(merged.getFirstRow()).getCell(merged.getFirstColumn()));
				if (value.equals(rangeName)) {
					return merged;
				}
			}
		}
		return null;
	}

	protected static void CopyImage(Sheet fromSheet, Sheet toSheet, int fromSourceRow, int toSourceRow,
			int fromSourceColNum, int toSourceColNum, int destinationColNum, int destinationRow,
			List<String> imagePath) {

		Workbook book = fromSheet.getWorkbook();
		int picCount = imagePath.size();
		// book.getAllPictures().size();
		for (int picId = 0; picId < picCount; picId++) {
			XSSFClientAnchor fromAnchor = a(fromSheet, picId);
			if (fromAnchor == null)
				continue;
			if (fromAnchor.getCol1() >= fromSourceColNum && fromAnchor.getCol1() <= toSourceColNum
					&& fromAnchor.getRow1() >= fromSourceRow && fromAnchor.getRow2() <= toSourceRow) {
				try {
					XSSFClientAnchor newAnchor = new XSSFClientAnchor();

					newAnchor.setCol1(fromAnchor.getCol1() + destinationColNum - 1);
					newAnchor.setCol2(fromAnchor.getCol2() + destinationColNum - 1);
					newAnchor.setRow1(fromAnchor.getRow1() + destinationRow - 1);
					newAnchor.setRow2(fromAnchor.getRow2() + destinationRow - 1);

					newAnchor.setDx1(fromAnchor.getDx1());
					newAnchor.setDx2(fromAnchor.getDx2());
					newAnchor.setDy1(fromAnchor.getDy1());
					newAnchor.setDy2(fromAnchor.getDy2());

					System.out.println("col1: " + newAnchor.getCol1() + ", col2: " + newAnchor.getCol2() + ", row1: "
							+ newAnchor.getRow1() + ", row2: " + newAnchor.getRow2());

					CreateImage(book, newAnchor, toSheet, imagePath.get(picId));
				} catch (Exception e) {
					
					e.printStackTrace();
				}
			}
		}
	}

	protected static void CreateImage(Workbook book, XSSFClientAnchor fromAnchor, Sheet toSheet, String path)
			throws FileNotFoundException, IOException {
		// Returns an object that handles instantiating concrete classes
		CreationHelper helper = book.getCreationHelper();
		// Creates the top-level drawing patriarch.
		Drawing drawing = toSheet.createDrawingPatriarch();
		// Create an anchor that is attached to the worksheet
		ClientAnchor anchor = helper.createClientAnchor();
		// set top-left corner for the image
		anchor.setCol1(fromAnchor.getCol1());
		anchor.setCol2(fromAnchor.getCol2());
		anchor.setRow1(fromAnchor.getRow1());
		anchor.setRow2(fromAnchor.getRow2());

		anchor.setDx1(fromAnchor.getDx1());
		anchor.setDx2(fromAnchor.getDx2());
		anchor.setDy1(fromAnchor.getDy1());
		anchor.setDy2(fromAnchor.getDy2());

		// FileInputStream obtains input bytes from the image file
		InputStream inputStream = new FileInputStream(path);
		// Get the contents of an InputStream as a byte[].
		byte[] bytes = IOUtils.toByteArray(inputStream);
		// Adds a picture to the workbook
		int pictureIdx = book.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
		// close the input stream
		inputStream.close();

		// Creates a picture
		Picture pict = drawing.createPicture(anchor, pictureIdx);
		// Reset the image to the original size
		// pict.resize();
	}

	public static <Doc, Item> void PrintDoc(String filename, Doc doc, List<Item> tablePart, XlsxReporter reporter,
			boolean hasTablePart) {
		try {
			// TODO: для всех табчастей
			// if (hasTablePart)
			// ((DocumentBO) doc).LoadTablePart();
			// TODO:
			// List<Item> tablePart = null;// = ((StoreDocBO)
			// doc).TablePartProduct;

			InputStream in = reporter.getClass().getResourceAsStream(reporter.templatePath);
			Workbook book = new XSSFWorkbook(in);
			Sheet sheet = book.getSheetAt(0);

			// reporter.tablePartSize = tablePart.size();// (int)
			// BO.GetPagesCount(tablePart.size(),
			// rowsPerPage);
			PrintSheet(sheet, doc, tablePart, reporter, hasTablePart, 0, 0);

			// filename = FileUtils.GetReportDir() + ((BO)
			// doc).getClass().getSimpleName() + "_"
			// + ((DocumentBO) doc).id + "_" + ((DocumentBO) doc).code + "_" +
			// System.currentTimeMillis()
			// + ".xlsx";
			// Записываем всё в файл
			book.write(new FileOutputStream(filename));
			book.close();
			// Run.OpenFile(filename);

		} catch (Exception e) {
			log.ERROR("PrintDoc", e);
		}
	}

	public static <Doc, Item> void PrintSheet(Sheet sheet, Doc doc, List<Item> tablePart, XlsxReporter reporter,
			boolean hasTablePart, int page, int rowsPerPage) {
		Workbook book = sheet.getWorkbook();
		int rowsCount = sheet.getLastRowNum();
		int tablePartStartRow = 0;

		int tablePartSize = Math.min(tablePart.size() - page * rowsPerPage, rowsPerPage);
		if (page == 0 && rowsPerPage == 0)
			tablePartSize = tablePart.size();
		int shiftRows = page * rowsPerPage;

		reporter.page = page;
		reporter.fromRow = shiftRows;
		reporter.toRow = shiftRows + tablePartSize;

		for (Row row : sheet) {
			for (Cell cell : row) {
				CellReference cellRef = new CellReference(row.getRowNum(), cell.getColumnIndex());
				// System.out.println(cellRef.formatAsString());
				// System.out.print(" - ");
				// String cellValue = "" + getCellValue(cell);
				// System.out.println(cellValue);
				if (XlsxReporter.FindTablePart(cell, "ТабЧасть"))
					tablePartStartRow = row.getRowNum();
				reporter.PrintDocParams(doc, cell);
			}
		}

		reporter.ReplaceCellVariables();

		List<CellRangeAddress> afterTPMergedRegions = new ArrayList<>();
		for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
			// System.out.println(sheet.getMergedRegion(i));
			// TODO: учесть многострочные
			if (sheet.getMergedRegion(i).getFirstRow() > tablePartStartRow) {
				// System.out.println("-after");
				afterTPMergedRegions.add(sheet.getMergedRegion(i));
			}
		}

		int startCol = 1;

		if (hasTablePart) {

			for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
				// System.out.println(sheet.getMergedRegion(i));
				// TODO: учесть многострочные
				if (sheet.getMergedRegion(i).getFirstRow() > tablePartStartRow) {
					sheet.removeMergedRegion(i);
				}
			}
			// int pageCount = (int) BO.GetPagesCount(tablePart.size(),
			// rowsPerPage);
			if (tablePartSize > 1)
				sheet.shiftRows(tablePartStartRow + 1, sheet.getLastRowNum(), tablePartSize - 1);

			// Копируем строку с переменными табличной части
			for (int i = 1; i < tablePartSize; i++) {
				copyRow(book, sheet, tablePartStartRow, tablePartStartRow + i);
			}
			// if (tablePartSize > 1)
			// copyRow(book, sheet, tablePartStart + 1, tablePartStart);

			for (int tpRow = tablePartStartRow; tpRow < tablePartStartRow + tablePartSize; tpRow++) {
				int pos = tpRow - tablePartStartRow + 1 + shiftRows;
				Row row = sheet.getRow(tpRow);
				if (row == null)
					row = sheet.createRow(tpRow);
				reporter.PrintTableRow(tablePart, pos, row);
				reporter.ReplaceCellVariables();
				CalcRowHeight(book, sheet, row, null);
			}

			// Учитывая сдвиг таб части, меняем адреса merged cell снизу
			for (CellRangeAddress mergedCell : afterTPMergedRegions) {
				int row = mergedCell.getFirstRow() + tablePartSize - 1;
				int firstCol = mergedCell.getFirstColumn();
				int lastCol = mergedCell.getLastColumn();
				CellRangeAddress address = new CellRangeAddress(row, row, firstCol, lastCol);
				try {
					sheet.addMergedRegion(address);
					CalcRowHeight(book, sheet, sheet.getRow(address.getFirstRow()),
							new String[] { "доверенности", "Погрузка", "Разгрузка" });
				} catch (Exception e) {
					log.ERROR("PrintDoc", e);
				}
			}
			sheet.validateMergedRegions();
		}
		// // Очищаем первую колонку - от посторонних глаз
		// for (Row r : sheet) {
		// r.removeCell(r.getCell(startCol - 1));
		// }

		sheet.setColumnHidden(startCol - 1, true);
	}
}