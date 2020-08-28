package maxzawalo.c2.full.report.code;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.full.bo.catalogue.Place;
import maxzawalo.c2.full.reporter.BarcodeGenerator;
import maxzawalo.c2.full.reporter.XlsxReporter;

public class PlaceReporter extends XlsxReporter {

	public PlaceReporter() {
		templatePath += "place.xlsx";
		tplRangeName = "МестоДлина:МестоВысота";
	}

	@Override
	public void PrintArea(Sheet sheet, CellRangeAddress area, BO bo, int tablePartStartRow) {
		Place place = (Place) bo;

		for (int row = area.getFirstRow(); row < area.getLastRow(); row++) {
			for (int col = area.getFirstColumn(); col < area.getLastColumn(); col++) {
				Cell cell = sheet.getRow(row).getCell(col);
				FindCellsContains(cell, "_Код", place.code);
				// FindCellsContains(cell, "_Наименование", price.product.name +
				// ", " + price.product.units.name);
				// FindCellsContains(cell, "_ЭтоКомплект_",
				// price.product.units.name.contains("компл") ? "К" : "");
				// FindCellsContains(cell, "_Контрагент",
				// price.invoice.contractor.name);
				// FindCellsContains(cell, "_ЦенаНов",
				// Format.Show(price.total));
				// FindCellsContains(cell, "_ЦенаСтарая", Format.get(price.total
				// * 10000));
				// FindCellsContains(cell, "_Накладная",
				// price.invoice.in_form_number + " от " +
				// Format.Show(price.invoice.in_form_date));
			}
		}

		ReplaceCellVariables();

		double margin = 0.4;/* inches */
		sheet.setMargin(Sheet.TopMargin, 0.2);
		sheet.setMargin(Sheet.BottomMargin, margin);
		sheet.setMargin(Sheet.LeftMargin, margin);
		sheet.setMargin(Sheet.RightMargin, margin);
	}

	@Override
	public List<String> CreateAreaImages(BO bo) {
		List<String> path = new ArrayList<>();
		Place place = (Place) bo;
		BarcodeGenerator.SavePlace(place.code);
		// TODO: Доступ к серверному или скачивание
		path.add("D:\\Barcodes\\" + place.code + ".png");
		return path;
	}
}