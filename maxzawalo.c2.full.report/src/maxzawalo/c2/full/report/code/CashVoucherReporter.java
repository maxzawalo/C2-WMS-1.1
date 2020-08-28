package maxzawalo.c2.full.report.code;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.utils.FileUtils;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.base.utils.MoneyInWords;
import maxzawalo.c2.free.bo.store.StoreTP;
import maxzawalo.c2.full.bo.document.cashvoucher.CashVoucher;
import maxzawalo.c2.full.reporter.XlsxReporter;

public class CashVoucherReporter extends XlsxReporter {

	public CashVoucherReporter() {
		this(false);
	}

	public CashVoucherReporter(boolean withSign) {
		tplRangeName = "ЧекДлина:ЧекВысота";
		if (withSign)
			templatePath += "CashVoucher_Sign.xlsx";
		else
			templatePath += "CashVoucher.xlsx";
		hasTablePart = true;
	}

	@Override
	public <Doc> void PrintDocParams(Doc doc, Cell cell) {
		super.PrintDocParams(doc, cell);
		CashVoucher cv = (CashVoucher) doc;
		FindCellsContains(cell, "ИтогоКоличество", Format.Show(cv.CalcCount(), 3));
		FindCellsContains(cell, "ИтогоСумма", Format.Show(cv.total));
		FindCellsContains(cell, "СуммаПропись", MoneyInWords.Full(cv.CalcSum()));
		FindCellsContains(cell, "СуммаПрописьНДС", MoneyInWords.Full(cv.CalcSumVat()));
	}

	// TODO: super
	@Override
	public void PrintArea(Sheet sheet, CellRangeAddress area, BO bo, int tablePartStartRow) {
		CashVoucher cv = (CashVoucher) bo;

		for (int row = area.getFirstRow(); row < area.getLastRow(); row++) {
			for (int col = area.getFirstColumn(); col < area.getLastColumn(); col++) {
				Cell cell = sheet.getRow(row).getCell(col);
				PrintDocParams(cv, cell);
			}
		}
		ReplaceCellVariables();

		PrintTablePart(sheet, bo, tablePartStartRow, 0);

		double margin = 0.4;/* inches */
		sheet.setMargin(Sheet.TopMargin, margin);
		sheet.setMargin(Sheet.BottomMargin, margin);
		sheet.setMargin(Sheet.LeftMargin, 0.7);
		sheet.setMargin(Sheet.RightMargin, margin);
	}

	@Override
	protected <Item> void PrintRowExt(Item tp, Cell cell) {
		super.PrintRowExt(tp, cell);
		StoreTP tpe = (StoreTP) tp;
		FindCellsContains(cell, "Наименование", tpe.product.full_name);
		FindCellsContains(cell, "ЕдИзм", (tpe.product.units == null ? "" : tpe.product.units.name));
		FindCellsContains(cell, "Количество", Format.Show(tpe.count, 3));
		FindCellsContains(cell, "Цена", Format.Show(tpe.price));
		FindCellsContains(cell, "Сумма", Format.Show(tpe.total));
	}

	@Override
	public List<String> CreateAreaImages(BO bo) {
		List<String> path = new ArrayList<>();
		// path.add("D:\\AMpJ3IvDvJQ.jpg");
		path.add(FileUtils.GetImgDir() + "Печать.png");
		path.add(FileUtils.GetImgDir() + "Подпись.png");
		return path;
	}
}