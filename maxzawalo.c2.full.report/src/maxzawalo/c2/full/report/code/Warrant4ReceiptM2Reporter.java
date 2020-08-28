package maxzawalo.c2.full.report.code;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.full.bo.document.warrant_4_receipt.Warrant4Receipt;
import maxzawalo.c2.full.reporter.XlsxReporter;

public class Warrant4ReceiptM2Reporter extends XlsxReporter {

	public Warrant4ReceiptM2Reporter() {
		templatePath += "Warrant4ReceiptМ2.xlsx";
		tplRangeName = "Длина:Высота";
		hasTablePart = true;
	}

	@Override
	public <Doc> void PrintDocParams(Doc doc, Cell cell) {
		super.PrintDocParams(doc, cell);

		Warrant4Receipt wr = (Warrant4Receipt) doc;

		FindCellsContains(cell, "ПодотчетноеЛицо", wr.coworker.toReportFullName());
		FindCellsContains(cell, "Контрагент_", "" + wr.contractor.full_name);
		FindCellsContains(cell, "СрокДействия", Format.Show(wr.end_date));

		FindCellsContains(cell, "ПаспортСерия", wr.coworker.passport_batch);
		FindCellsContains(cell, "ПаспортНомер", wr.coworker.passport_number);
		FindCellsContains(cell, "ПаспортКемВыдан", wr.coworker.passport_issued_by);
		FindCellsContains(cell, "ПаспортДатаВыдачи", Format.Show(wr.coworker.passport_issued_date));

		// FindCellsContains(cell, "ОрганизацияУнп", "" + Settings.myFirm.unp);
		// FindCellsContains(cell, "ОрганизацияИмя", "" + Settings.myFirm.name);
		// FindCellsContains(cell, "ОрганизацияАдрес", "" +
		// Settings.myFirm.legal_address);//note.store.address

		// FindCellsContains(cell, "ОснованиеОтпуска", "" + bill.doc_contract);
		//
		// FindCellsContains(cell, "СуммаНДСПропись",
		// MoneyInWords.Full(bill.CalcSumVat()));
		// FindCellsContains(cell, "ВсегоПропись",
		// MoneyInWords.Full(bill.CalcSumTotal()));
		//
		// FindCellsContains(cell, "КонтрагентЗаказчик", "" +
		// bill.contractor.FullName());
		// bill.contractor.setContactInfoFields();
		// FindCellsContains(cell, "КонтрагентПлательщик",
		// "" + bill.contractor.FullName() + ", адрес: " +
		// bill.contractor.legal_address);
		//
		// FindCellsContains(cell, "_Телефон_", "" + bill.contractor.phone);
		// FindCellsContains(cell, "_Факс_", "" + bill.contractor.fax);
		//
		// FindCellsContains(cell, "РасчетныйСчет", "" +
		// bill.contractor.PaymentData());
		//
		// FindCellsContains(cell, "ИтогоСумма", Format.Show(bill.CalcSum()));
		// FindCellsContains(cell, "ИтогоНДС", Format.Show(bill.CalcSumVat()));
		// FindCellsContains(cell, "ИтогоВсего",
		// Format.Show(bill.CalcSumTotal()));
	}

	// TODO: super
		@Override
		public void PrintArea(Sheet sheet, CellRangeAddress area, BO bo, int tablePartStartRow) {
			Warrant4Receipt doc = (Warrant4Receipt) bo;

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

	// @Override
	// protected <Item> void PrintRowExt(Item tp, Cell cell) {
	// super.PrintRowExt(tp, cell);
	// BillTablePart.Product tpe = (BillTablePart.Product) tp;
	//
	// FindCellsContains(cell, "Наименование", tpe.product.full_name);
	// FindCellsContains(cell, "ЕдИзм", tpe.product.units.name);
	// FindCellsContains(cell, "Количество", Format.Show(tpe.count, 3));
	// FindCellsContains(cell, "Цена", Format.Show(tpe.price));
	// FindCellsContains(cell, "Сумма", Format.Show(tpe.sum));
	// FindCellsContains(cell, "СтавкаНДС", Format.Show(tpe.rateVat));
	// FindCellsContains(cell, "СуммаНДС", Format.Show(tpe.sumVat));
	// FindCellsContains(cell, "Всего", Format.Show(tpe.total));
	// }
}