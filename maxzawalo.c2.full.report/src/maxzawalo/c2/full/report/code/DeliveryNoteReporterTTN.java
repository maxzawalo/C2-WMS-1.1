package maxzawalo.c2.full.report.code;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.data.factory.FactoryBO;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.base.utils.MoneyInWords;
import maxzawalo.c2.free.bo.Settings;
import maxzawalo.c2.free.bo.StrictForm;
import maxzawalo.c2.free.bo.document.deliverynote.DeliveryNote;
import maxzawalo.c2.free.bo.store.StoreTP;
import maxzawalo.c2.full.reporter.XlsxReporter;

public class DeliveryNoteReporterTTN extends XlsxReporter {
	boolean withAddition = false;
	int rowsPerPage = 0;

	public DeliveryNoteReporterTTN() {
		this(false, 0);
	}

	public DeliveryNoteReporterTTN(boolean withAddition, int rowsPerPage) {
		this.withAddition = withAddition;
		hasTablePart = !withAddition;
		this.rowsPerPage = rowsPerPage;
		tplRangeName = "ТТНДлина:ТТНВысота";
		templatePath += "ttn.xlsx";
	}

	@Override
	public <Doc> void PrintDocParams(Doc doc, Cell cell) {
		super.PrintDocParams(doc, cell);

		DeliveryNote note = (DeliveryNote) doc;
		// if (core == null)
		// core = new Core(note.id);

		// FindCellsContains(cell, "ПутевойЛист",
		// core.get("deliverynote.waybill"));
		FindCellsContains(cell, "ПутевойЛист", note.waybill);
		// FindCellsContains(cell, "_Автомобиль", core.get("deliverynote.car"));
		FindCellsContains(cell, "_Автомобиль", note.car);
		// FindCellsContains(cell, "_Водитель",
		// core.get("deliverynote.driver"));
		FindCellsContains(cell, "_Водитель", note.driver);

		// TODO: Settings.myFirm from json
		FindCellsContains(cell, "ГрузоотправительУнп", "" + Settings.myFirm.unp);
		FindCellsContains(cell, "ГрузоотправительПолноеИмя", "" + Settings.myFirm.full_name);
		FindCellsContains(cell, "ГрузоотправительИмя", "" + Settings.myFirm.name);
		// FindCellsContains(cell, "ГрузоотправительАдрес",
		// core.get("deliverynote.store.address"));
		FindCellsContains(cell, "ГрузоотправительАдрес", (note.store == null ? "" : note.store.address));
		// FindCellsContains(cell, "ЗаказчикПеревозкиУнп",
		// core.get("deliverynote.client.unp"));
		FindCellsContains(cell, "ЗаказчикПеревозкиУнп", (note.client == null ? "" : note.client.unp));

		// new ContractorFactory().setContactInfoFields(note.contractor);
		// FindCellsContains(cell, "ГрузополучательУнп",
		// core.get("deliverynote.contractor.unp"));
		FindCellsContains(cell, "ГрузополучательУнп", (note.contractor == null ? "" : note.contractor.unp));
		// FindCellsContains(cell, "ГрузополучательИмя",
		// core.get("deliverynote.contractor.full_name"));
		FindCellsContains(cell, "ГрузополучательИмя", (note.contractor == null ? "" : note.contractor.full_name));
		FindCellsContains(cell, "ГрузополучательАдрес", (note.contractor == null ? "" : note.contractor.legal_address));
		// FindCellsContains(cell, "ОснованиеОтпуска",
		// core.get("deliverynote.shipment_motive"));
		FindCellsContains(cell, "ОснованиеОтпуска", note.shipment_motive);

		// FindCellsContains(cell, "ЗаказчикИмя",
		// core.get("deliverynote.client.full_name"));
		FindCellsContains(cell, "ЗаказчикИмя", (note.client == null ? "" : note.client.full_name));
		// new ContractorFactory().setContactInfoFields(note.client);
		FindCellsContains(cell, "ЗаказчикАдрес", (note.client == null ? "" : note.client.legal_address));

		// FindCellsContains(cell, "ПунктПогрузки",
		// core.get("deliverynote.lading_place"));
		FindCellsContains(cell, "ПунктПогрузки", note.lading_place);
		// FindCellsContains(cell, "ПунктРазгрузки",
		// core.get("deliverynote.discharge_place"));
		FindCellsContains(cell, "ПунктРазгрузки", note.discharge_place);

		FindCellsContains(cell, "ОтпускРазрешил",
				(note.shipment_permited == null ? "" : note.shipment_permited.toReport()));
		FindCellsContains(cell, "ОтпускПроизвел",
				(note.shipment_produced == null ? "" : note.shipment_produced.toReport()));
		FindCellsContains(cell, "СдалГрузоотправитель",
				(note.shipper_hand_in == null ? "" : note.shipper_hand_in.toReport()));

		String procuration = Format.Show("dd.MM.yy", note.procuration_date);
		// По печати
		if (note.procuration_number != null && !note.procuration_number.trim().equals(""))
			procuration = "№ " + note.procuration_number + " от " + procuration;
		FindCellsContains(cell, "ДоверенностьНомерДата", procuration);

		// FindCellsContains(cell, "ДоверенностьФИО",
		// core.get("deliverynote.procuration_name"));
		FindCellsContains(cell, "ДоверенностьФИО", note.procuration_name);
		// FindCellsContains(cell, "ДоверенностьОрганизация",
		// core.get("deliverynote.contractor.full_name"));
		FindCellsContains(cell, "ДоверенностьОрганизация", (note.contractor == null ? "" : note.contractor.full_name));

		StrictForm form = note.getCurrentStrictForm();
		String sf = "";
		if (form != null)
			sf = form.form_type_name.replace("нов", "").trim() + " " + form.form_batch + " № " + form.form_number;
		FindCellsContains(cell, "_БСО", sf);

		if (withAddition) {
			int pageCount = (int) FactoryBO.GetPagesCount(note.getTablePart4Rep().size(), rowsPerPage);
			FindCellsContains(cell, "_Наименование",
					"Товар (продукция) согласно приложения на " + pageCount + " листах");
			FindCellsContains(cell, "_ЕдИзм", "x");
			FindCellsContains(cell, "_Количество", Format.Show(note.CalcCount(), 3));
			FindCellsContains(cell, "_Цена", "x");
			FindCellsContains(cell, "_Сумма", Format.Show(note.CalcSum()));
			FindCellsContains(cell, "_СтавкаНДС", "x");
			FindCellsContains(cell, "_СуммаНДС", Format.Show(note.CalcSumVat()));
			FindCellsContains(cell, "_Всего", Format.Show(note.CalcSumTotal()));
			FindCellsContains(cell, "_Примечание", "x");
		}

		FindCellsContains(cell, "ГрузовыхМест", MoneyInWords.JustNum(note.CalcCount()));

		FindCellsContains(cell, "ИтогоКоличество", Format.Show(note.CalcCount(), 3));
		FindCellsContains(cell, "ИтогоСумма", Format.Show(note.CalcSum()));
		FindCellsContains(cell, "ИтогоНДС", Format.Show(note.CalcSumVat()));
		FindCellsContains(cell, "ИтогоВсего", Format.Show(note.CalcSumTotal()));

		FindCellsContains(cell, "СуммаНДСПрописьРуб", MoneyInWords.Rub(note.CalcSumVat()));
		FindCellsContains(cell, "СуммаНДСПрописьКоп", MoneyInWords.Kop(note.CalcSumVat()));

		FindCellsContains(cell, "ВсегоПрописьРуб", MoneyInWords.Rub(note.CalcSumTotal()));
		FindCellsContains(cell, "ВсегоПрописьКоп", MoneyInWords.Kop(note.CalcSumTotal()));
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
		sheet.setMargin(Sheet.TopMargin, 0.6);
		sheet.setMargin(Sheet.BottomMargin, margin);
		sheet.setMargin(Sheet.LeftMargin, 0.7);
		sheet.setMargin(Sheet.RightMargin, margin);
	}

	@Override
	protected <Item> void PrintRowExt(Item tp, Cell cell) {
		super.PrintRowExt(tp, cell);
		StoreTP tpe = (StoreTP) tp;

		FindCellsContains(cell, "_Наименование", tpe.product.full_name);
		FindCellsContains(cell, "_ЕдИзм", (tpe.product.units == null ? "" : tpe.product.units.name));
		FindCellsContains(cell, "_Количество", Format.Show(tpe.count, 3));
		FindCellsContains(cell, "_Цена", Format.Show(tpe.price));
		FindCellsContains(cell, "_Сумма", Format.Show(tpe.sum));
		FindCellsContains(cell, "_СтавкаНДС", Format.Show(tpe.rateVat));
		FindCellsContains(cell, "_СуммаНДС", Format.Show(tpe.sumVat));
		FindCellsContains(cell, "_Всего", Format.Show(tpe.total));
		FindCellsContains(cell, "_Примечание", "");
	}
}