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
import maxzawalo.c2.free.bo.store.StoreDocBO;
import maxzawalo.c2.free.bo.store.StoreTP;
import maxzawalo.c2.free.data.factory.catalogue.ContractorFactory;
import maxzawalo.c2.full.bo.document.return_of_goods.ReturnOfGoods;
import maxzawalo.c2.full.reporter.XlsxReporter;

public class ReturnOfGoodsReporter extends XlsxReporter {

	public ReturnOfGoodsReporter() {
		tplRangeName = "ТН2Длина:ТН2Высота";
		templatePath += "tn2.xlsx";
		hasTablePart = true;
	}

	@Override
	public <Doc> void PrintDocParams(Doc doc, Cell cell) {
		super.PrintDocParams(doc, cell);

		ReturnOfGoods rog = (ReturnOfGoods) doc;

		FindCellsContains(cell, "ГрузоотправительУнп", "" + Settings.myFirm.unp);
		FindCellsContains(cell, "ГрузоотправительИмя", "" + Settings.myFirm.name);
		FindCellsContains(cell, "ГрузоотправительАдрес", "" + rog.store.address);// Settings.myFirm.legal_address

		new ContractorFactory().setContactInfoFields(rog.contractor);
		FindCellsContains(cell, "ГрузополучательУнп", "" + rog.contractor.unp);
		FindCellsContains(cell, "ГрузополучательИмя", "" + rog.contractor.full_name);
		FindCellsContains(cell, "ГрузополучательАдрес", "" + rog.contractor.legal_address);
		FindCellsContains(cell, "ОснованиеОтпуска", "" + rog.shipment_motive);

		FindCellsContains(cell, "ОтпускРазрешил", "" + rog.shipment_permited.toReport());
		FindCellsContains(cell, "ОтпускПроизвел", "" + rog.shipment_produced.toReport());
		FindCellsContains(cell, "ДоверенностьНомерДата", "");
		FindCellsContains(cell, "ДоверенностьФИО", "");
		FindCellsContains(cell, "ДоверенностьОрганизация", "");

		// StrictForm form = note.getCurrentStrictForm();
		// if (form != null) {
		// FindCellsContains(cell, "_БСО", form.form_type_name + " " +
		// form.form_batch + " № " + form.form_number);
		// }
		StrictForm form = rog.getCurrentStrictForm();
		String sf = "";
		if (form != null)
			sf = form.form_type_name + " " + form.form_batch + " № " + form.form_number;
		FindCellsContains(cell, "_БСО", sf);

		FindCellsContains(cell, "ИтогоКоличество", Format.Show(rog.CalcCount(), 3));
		FindCellsContains(cell, "ИтогоСумма", Format.Show(rog.CalcSum()));
		FindCellsContains(cell, "ИтогоНДС", Format.Show(rog.CalcSumVat()));
		FindCellsContains(cell, "ИтогоВсего", Format.Show(rog.CalcSumTotal()));

		FindCellsContains(cell, "СуммаНДСПропись", MoneyInWords.Full(rog.CalcSumVat()));
		FindCellsContains(cell, "ВсегоПропись", MoneyInWords.Full(rog.CalcSumTotal()));
	}

	// TODO: super
	@Override
	public void PrintArea(Sheet sheet, CellRangeAddress area, BO bo, int tablePartStartRow) {
		StoreDocBO doc = (StoreDocBO) bo;

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