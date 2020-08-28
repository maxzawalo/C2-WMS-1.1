package maxzawalo.c2.full.ui.pc.document;

import java.util.Arrays;

import javax.swing.JDialog;

import maxzawalo.c2.free.bo.document.bill.Bill;
import maxzawalo.c2.free.ui.pc.catalogue.LotOfProductListFormFree;
import maxzawalo.c2.free.ui.pc.document.store.BillFormFree;
import maxzawalo.c2.full.report.code.BillReporter;
import maxzawalo.c2.full.reporter.Xlsx;
import maxzawalo.c2.full.ui.pc.catalogue.LotOfProductListFormFull;

public class BillFormFull extends BillFormFree {
	public BillFormFull() {
		super(null);
	}

	public BillFormFull(JDialog parent) {
		super(parent);
	}

	@Override
	protected void FreeTimeLimit() {
		// в полной версии отключаем
	}

	@Override
	protected void FreeSoon() {
		// в полной версии отключаем
	}

	@Override
	protected void LoadReportFromService(String reportName) {
		Bill doc = new Bill().FromDump(reportPath, gson);
		// Bill doc = factory.GetById(elementBO.id);
		// ((StoreDocFactory) factory).LoadTablePart(doc);
		Xlsx.PrintMatrix(reportPath.replace("c2_report", "xlsx"), Arrays.asList(doc), new BillReporter(), 1, 1);
	}

	// FreeSoon();
	// JustFull();
	// GenerateReportData();
	// SendReport2Service();
	// WaitReport();
	// ShowReport();

	@Override
	protected LotOfProductListFormFree CreateSelectForm() {
		return new LotOfProductListFormFull(this, this);
	}
}