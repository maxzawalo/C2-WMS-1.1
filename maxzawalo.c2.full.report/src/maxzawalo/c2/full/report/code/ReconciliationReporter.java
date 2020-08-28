package maxzawalo.c2.full.report.code;

import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.base.utils.MoneyInWords;
import maxzawalo.c2.full.bo.registry.RegistryAccounting;
import maxzawalo.c2.full.bo.view.ReconciliationReport;
import maxzawalo.c2.full.reporter.XlsxReporter;

public class ReconciliationReporter extends XlsxReporter {
	public ReconciliationReporter() {
		tplRangeName = "АктДлина:АктВысота";
		templatePath += "ReconciliationReport.xlsx";
		hasTablePart = true;
	}

	@Override
	public <Doc> void PrintDocParams(Doc doc, Cell cell) {
		super.PrintDocParams(doc, cell);
		ReconciliationReport rep = (ReconciliationReport) doc;
		FindCellsContains(cell, "_ДатаС", Format.Show(rep.fromDate));
		FindCellsContains(cell, "_КонтрагентПолноеИмя", rep.contractor.full_name);

		FindCellsContains(cell, "_СальдоНачальное", Format.Show(rep.startSaldo));
		FindCellsContains(cell, "_СальдоКонечное", Format.Show(rep.endSaldo));

		FindCellsContains(cell, "ОборотыДебет", Format.Show(rep.turnoverDebet));
		FindCellsContains(cell, "ОборотыКредит", Format.Show(rep.turnoverKredit));

		FindCellsContains(cell, "_СальдоКонечноеПропись", MoneyInWords.Full(rep.endSaldo));
	}

	// TODO: super
	@Override
	public void PrintArea(Sheet sheet, CellRangeAddress area, BO bo, int tablePartStartRow) {
		ReconciliationReport rep = (ReconciliationReport) bo;

		int tpSize = bo.getTablePart4Rep().size();
		System.out.println("tpSize=" + tpSize);

		for (int row = area.getFirstRow(); row < area.getLastRow(); row++) {
			System.out.println(row + "|" + area.getLastRow());
			for (int col = area.getFirstColumn(); col < area.getLastColumn(); col++) {
				Row r = sheet.getRow(row);
				Cell cell = r.getCell(col);
				PrintDocParams(rep, cell);
			}
		}
		ReplaceCellVariables();
		PrintTablePart(sheet, bo, tablePartStartRow, 0);

		double margin = 0.4;/* inches */
		sheet.setMargin(Sheet.TopMargin, margin);
		sheet.setMargin(Sheet.BottomMargin, margin);
		sheet.setMargin(Sheet.LeftMargin, 0.7);
		sheet.setMargin(Sheet.RightMargin, margin);

		sheet.getPrintSetup().setLandscape(true);
		sheet.getPrintSetup().setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
	}

	@Override
	protected <Item> void PrintRowExt(Item tp, Cell cell) {
		super.PrintRowExt(tp, cell);

		// Оплата Продажа (892 от 26.09.2017)
		// ra.RegMeta = "Продажа";
		// ra.RegMeta = "Оплата";

		RegistryAccounting ra = (RegistryAccounting) tp;
		FindCellsContains(cell, "_ДатаТЧ", Format.Show("dd.MM.yy", ra.reg_date));
		FindCellsContains(cell, "_ИмяДокТЧ", ra.RegMeta);
		FindCellsContains(cell, "_НомерДокТЧ", "");

		if (ra.RegMeta.equals("Продажа")) {
			FindCellsContains(cell, "_Дебет", Format.Show(ra.sum));
			FindCellsContains(cell, "_Кредит", "");
		} else {
			FindCellsContains(cell, "_Кредит", Format.Show(ra.sum));
			FindCellsContains(cell, "_Дебет", "");
		}
		// FindCellsContains(cell, "Цена", Format.Show(tpe.price));
		// FindCellsContains(cell, "Сумма", Format.Show(tpe.total));
	}
}