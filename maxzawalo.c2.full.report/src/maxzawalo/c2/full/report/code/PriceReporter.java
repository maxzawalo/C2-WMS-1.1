package maxzawalo.c2.full.report.code;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.free.bo.Price;
import maxzawalo.c2.full.reporter.BarcodeGenerator;
import maxzawalo.c2.full.reporter.XlsxReporter;

public class PriceReporter extends XlsxReporter {

	public PriceReporter() {
		templatePath += "price.xlsx";
		tplRangeName = "ЦенникДлина:ЦенникВысота";
	}

	@Override
	public void PrintArea(Sheet sheet, CellRangeAddress area, BO bo, int tablePartStartRow) {
		Price price = (Price) bo;

		for (int row = area.getFirstRow(); row < area.getLastRow(); row++) {
			for (int col = area.getFirstColumn(); col < area.getLastColumn(); col++) {
				Cell cell = sheet.getRow(row).getCell(col);
				String[] codes = price.code.split("-");
				FindCellsContains(cell, "_Код", codes[1]);
				FindCellsContains(cell, "_Наименование", price.product.name + ", " + price.product.units.name);
				FindCellsContains(cell, "_ЭтоКомплект_", price.product.units.name.contains("компл") ? "К" : "");
				System.out.println("TODO: price.invoice -> InDocs");
				FindCellsContains(cell, "_Контрагент", "К.К. " + price.invoice.contractor.code);
				FindCellsContains(cell, "_ЦенаНов", Format.Show(price.total));
				FindCellsContains(cell, "_ЦенаСтарая", Format.get(price.total * 10000));
				FindCellsContains(cell, "_Накладная",
						price.invoice.in_form_number + " от " + Format.Show(price.invoice.in_form_date));
			}
		}

		ReplaceCellVariables();

		double margin = 0.2;/* inches */
		sheet.setMargin(Sheet.TopMargin, margin);
		sheet.setMargin(Sheet.BottomMargin, margin);
		sheet.setMargin(Sheet.LeftMargin, margin);
		sheet.setMargin(Sheet.RightMargin, margin);
	}

	@Override
	public List<String> CreateAreaImages(BO bo) {
		List<String> path = new ArrayList<>();
		Price price = (Price) bo;
		String[] codes = price.code.split("-");
		// TODO: Доступ к серверному или скачивание
		path.add(BarcodeGenerator.CreatePriceBarcode(codes[1]));
		path.add(BarcodeGenerator.CreatePriceQR(price.code));
		return path;
	}
}