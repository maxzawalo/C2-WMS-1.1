package maxzawalo.c2.full.report.code;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.free.bo.StrictForm;
import maxzawalo.c2.free.bo.document.deliverynote.DeliveryNote;
import maxzawalo.c2.free.bo.store.StoreTP;
import maxzawalo.c2.full.reporter.XlsxReporter;

public class DeliveryNoteReporterTTNAddition extends XlsxReporter {

	public DeliveryNoteReporterTTNAddition() {
		templatePath += "ttn_add.xlsx";
		tplRangeName = "ТТНДлина:ТТНВысота";
		hasTablePart = true;
	}

	@Override
	public <Doc> void PrintDocParams(Doc doc, Cell cell) {
		super.PrintDocParams(doc, cell);

		DeliveryNote note = (DeliveryNote) doc;

		FindCellsContains(cell, "ОтпускРазрешил",
				(note.shipment_permited == null ? "" : note.shipment_permited.toReport()));
		FindCellsContains(cell, "СдалГрузоотправитель",
				(note.shipper_hand_in == null ? "" : note.shipper_hand_in.toReport()));

		StrictForm form = note.getCurrentStrictForm();
		String sf = "";
		if (form != null)
			sf = form.form_batch + " № " + form.form_number;
		FindCellsContains(cell, "_БСО", sf);

		FindCellsContains(cell, "НомерСтраницы", "" + (page + 1));
		FindCellsContains(cell, "ИтогоКоличество", Format.Show(note.CalcCount(fromRow, toRow), 3));
		FindCellsContains(cell, "ИтогоСумма", Format.Show(note.CalcSum(fromRow, toRow)));
		FindCellsContains(cell, "ИтогоНДС", Format.Show(note.CalcSumVat(fromRow, toRow)));
		FindCellsContains(cell, "ИтогоВсего", Format.Show(note.CalcSumTotal(fromRow, toRow)));
	}

	// TODO: super
	@Override
	public void PrintArea(Sheet sheet, CellRangeAddress area, BO bo, int tablePartStartRow) {
		DeliveryNote doc = (DeliveryNote) bo;

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