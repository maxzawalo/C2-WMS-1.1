package maxzawalo.c2.full.report.code;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.base.utils.MoneyInWords;
import maxzawalo.c2.free.bo.document.bill.Bill;
import maxzawalo.c2.free.bo.store.StoreTP;
import maxzawalo.c2.free.data.factory.catalogue.ContractorFactory;
import maxzawalo.c2.full.reporter.XlsxReporter;

public class BillReporter extends XlsxReporter {

	public BillReporter() {
		templatePath += "bill.xlsx";
		tplRangeName = "СчетДлина:СчетВысота";
		hasTablePart = true;
	}

	@Override
	public <Doc> void PrintDocParams(Doc doc, Cell cell) {
		super.PrintDocParams(doc, cell);

		Bill bill = (Bill) doc;

		FindCellsContains(cell, "ОснованиеОтпуска", bill.doc_contract);

		FindCellsContains(cell, "СуммаНДСПропись", MoneyInWords.Full(bill.CalcSumVat()));
		FindCellsContains(cell, "ВсегоПропись", MoneyInWords.Full(bill.CalcSumTotal()));

		FindCellsContains(cell, "КонтрагентЗаказчик", (bill.contractor == null ? "" : bill.contractor.full_name));
		FindCellsContains(cell, "КонтрагентПлательщик", (bill.contractor == null ? ""
				: bill.contractor.full_name + ", адрес: " + bill.contractor.legal_address));

		FindCellsContains(cell, "_Телефон_", (bill.contractor == null ? "" : bill.contractor.phone));
		FindCellsContains(cell, "_Факс_", (bill.contractor == null ? "" : bill.contractor.fax));

		FindCellsContains(cell, "РасчетныйСчет",
				(bill.contractor == null ? "" : ContractorFactory.ReportPaymentData(bill.contractor)));

		FindCellsContains(cell, "ИтогоСумма", Format.Show(bill.CalcSum()));
		FindCellsContains(cell, "ИтогоНДС", Format.Show(bill.CalcSumVat()));
		FindCellsContains(cell, "ИтогоВсего", Format.Show(bill.CalcSumTotal()));
	}

	// TODO: super
	@Override
	public void PrintArea(Sheet sheet, CellRangeAddress area, BO bo, int tablePartStartRow) {
		Bill doc = (Bill) bo;

		// int tpSize = doc.getTablePart4Rep().size();
		// System.out.println("tpSize=" + tpSize);

		for (int row = area.getFirstRow(); row < area.getLastRow(); row++) {
			System.out.println(row + "|" + area.getLastRow());
			for (int col = area.getFirstColumn(); col < area.getLastColumn(); col++) {
				Row r = sheet.getRow(row);
				Cell cell = r.getCell(col);
				PrintDocParams(doc, cell);
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
		FindCellsContains(cell, "Сумма", Format.Show(tpe.sum));
		FindCellsContains(cell, "СтавкаНДС", Format.Show(tpe.rateVat));
		FindCellsContains(cell, "СуммаНДС", Format.Show(tpe.sumVat));
		FindCellsContains(cell, "Всего", Format.Show(tpe.total));
	}
}