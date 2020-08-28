package maxzawalo.c2.full.report.code;

import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.full.bo.StoreDaybook;
import maxzawalo.c2.full.data.factory.StoreDaybookFactory;
import maxzawalo.c2.full.reporter.XlsxReporter;

public class StoreDaybookReporter extends XlsxReporter {

	Date printTime = new Date();

	public StoreDaybookReporter() {
		templatePath += "StoreDaybook.xlsx";
		tplRangeName = "ЦенникДлина:ЦенникВысота";
		hasTablePart = true;
	}

	@Override
	public void PrintArea(Sheet sheet, CellRangeAddress area, BO bo, int tablePartStartRow) {
		StoreDaybook book = (StoreDaybook) bo;
		List tablePart = bo.getTablePart4Rep();

		for (int row = area.getFirstRow(); row < area.getLastRow(); row++) {
			for (int col = area.getFirstColumn(); col < area.getLastColumn(); col++) {
				Cell cell = sheet.getRow(row).getCell(col);
				// if (cell == null)
				// continue;

				FindCellsContains(cell, "_Контрагент", book.contractor.name);

				FindCellsContains(cell, "_Подпись", "Время печати: " + Format.Show("dd.MM.yy HH:mm:ss", printTime)
						+ ".   Подпись: " + new StoreDaybookFactory().GetLastSign(book));
			}
		}
		ReplaceCellVariables();

		// System.out.println("====" + book.contractor.name);
		// System.out.println("tablePartStartRow=" + tablePartStartRow);
		for (int row = tablePartStartRow; row < tablePartStartRow + tablePart.size(); row++) {
			for (int col = area.getFirstColumn(); col < area.getLastColumn(); col++) {
				Cell cell = sheet.getRow(row).getCell(col);
				// if (cell == null)
				// continue;

				int pos = row - tablePartStartRow;
				StoreDaybook b = (StoreDaybook) tablePart.get(pos);

				// System.out.println(row + " " + pos);
				// System.out.println(b);

				// FindCellsContains(cell, "_Контрагент", book.contractor.name);
				FindCellsContains(cell, "_Статус", (b.deleted ? "x" : ""));
				FindCellsContains(cell, "_Дата", Format.Show("dd.MM.yy", b.entry_time));
				FindCellsContains(cell, "_КтоВзял", b.who_recieve);
				FindCellsContains(cell, "_Номенклатура", b.product.name);
				FindCellsContains(cell, "_ЕдИзм", b.product.units);
				FindCellsContains(cell, "_Количество", Format.Show(b.count, 3));
				FindCellsContains(cell, "_Цена", Format.Show(b.price));

				FindCellsContains(cell, "_ПодписьСтрока", b.sign);
				// link_id вверху, т.к. проще видеть всю историю
				FindCellsContains(cell, "_link_id_id", b.link_id + "\n" + b.id);
				FindCellsContains(cell, "_Изменено", Format.Show("dd.MM.yy HH:mm:ss", b.changed));
				FindCellsContains(cell, "_Комментарий", b.comment);

				ReplaceCellVariables();
			}
		}

		sheet.getPrintSetup().setLandscape(true);
		sheet.getPrintSetup().setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
	}
}