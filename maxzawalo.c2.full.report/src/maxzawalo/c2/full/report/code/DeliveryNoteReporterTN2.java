package maxzawalo.c2.full.report.code;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.base.utils.MoneyInWords;
import maxzawalo.c2.free.bo.Settings;
import maxzawalo.c2.free.bo.StrictForm;
import maxzawalo.c2.free.bo.document.deliverynote.DeliveryNote;
import maxzawalo.c2.free.bo.store.StoreTP;
import maxzawalo.c2.full.reporter.XlsxReporter;

public class DeliveryNoteReporterTN2 extends XlsxReporter {

	int pageBreak = 0;

	public DeliveryNoteReporterTN2(int pageBreak) {
		this.pageBreak = pageBreak;
		tplRangeName = "ТН2Длина:ТН2Высота";
		templatePath += "tn2.xlsx";
		hasTablePart = true;
	}

	@Override
	public <Doc> void PrintDocParams(Doc doc, Cell cell) {
		super.PrintDocParams(doc, cell);

		DeliveryNote note = (DeliveryNote) doc;

		FindCellsContains(cell, "ГрузоотправительУнп", Settings.myFirm.unp);
		FindCellsContains(cell, "ГрузоотправительИмя", Settings.myFirm.name);
		FindCellsContains(cell, "ГрузоотправительАдрес", note.store.address);// Settings.myFirm.legal_address

		FindCellsContains(cell, "ГрузополучательУнп", (note.contractor == null ? "" : note.contractor.unp));
		// ((BO)
		// note.get(StoreDocBO.fields.CONTRACTOR)).get(Contractor.fields.UNP));
		FindCellsContains(cell, "ГрузополучательИмя", (note.contractor == null ? "" : note.contractor.full_name));
		FindCellsContains(cell, "ГрузополучательАдрес", (note.contractor == null ? "" : note.contractor.legal_address));
		FindCellsContains(cell, "ОснованиеОтпуска", note.shipment_motive);

		FindCellsContains(cell, "ОтпускРазрешил",
				(note.shipment_permited == null ? "" : note.shipment_permited.toReport()));
		FindCellsContains(cell, "ОтпускПроизвел",
				(note.shipment_produced == null ? "" : note.shipment_produced.toReport()));

		String procuration = Format.Show("dd.MM.yy", note.procuration_date);
		// По печати
		if (note.procuration_number != null && !note.procuration_number.trim().equals(""))
			procuration = "№ " + note.procuration_number + " от " + procuration;
		FindCellsContains(cell, "ДоверенностьНомерДата", procuration);

		FindCellsContains(cell, "ДоверенностьФИО", note.procuration_name);
		FindCellsContains(cell, "ДоверенностьОрганизация", (note.contractor == null ? "" : note.contractor.full_name));

		StrictForm form = note.getCurrentStrictForm();
		String sf = "";
		if (form != null)
			sf = form.form_type_name + " " + form.form_batch + " № " + form.form_number;
		FindCellsContains(cell, "_БСО", sf);

		// ReplaceContains(cell, "КонтрагентЗаказчик", "" +
		// invoice.contractor.FullName());
		// ReplaceContains(cell, "КонтрагентПлательщик", "" +
		// invoice.contractor.FullName());
		// ReplaceContains(cell, "РасчетныйСчет", "" +
		// invoice.contractor.PaymentData());

		FindCellsContains(cell, "ИтогоКоличество", Format.Show(note.CalcCount(), 3));
		FindCellsContains(cell, "ИтогоСумма", Format.Show(note.CalcSum()));
		FindCellsContains(cell, "ИтогоНДС", Format.Show(note.CalcSumVat()));
		FindCellsContains(cell, "ИтогоВсего", Format.Show(note.CalcSumTotal()));

		FindCellsContains(cell, "СуммаНДСПропись", MoneyInWords.Full(note.CalcSumVat()));
		FindCellsContains(cell, "ВсегоПропись", MoneyInWords.Full(note.CalcSumTotal()));
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
		sheet.setMargin(Sheet.TopMargin, 0.8);
		sheet.setMargin(Sheet.BottomMargin, margin);
		sheet.setMargin(Sheet.LeftMargin, 0.5);
		sheet.setMargin(Sheet.RightMargin, margin);

		if (pageBreak != 0)
			sheet.setRowBreak(tablePartStartRow - 1 + pageBreak);
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