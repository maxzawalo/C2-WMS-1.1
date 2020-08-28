package maxzawalo.c2.full.ui.pc.document;

import java.util.Arrays;

import javax.swing.JDialog;

import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.free.ui.pc.catalogue.LotOfProductListFormFree;
import maxzawalo.c2.free.ui.pc.document.store.StoreDocForm;
import maxzawalo.c2.full.bo.document.cashvoucher.CashVoucher;
import maxzawalo.c2.full.bo.document.cashvoucher.CashVoucherTablePart;
import maxzawalo.c2.full.data.factory.document.CashVoucherFactory;
import maxzawalo.c2.full.report.code.CashVoucherReporter;
import maxzawalo.c2.full.reporter.Xlsx;
import maxzawalo.c2.full.ui.pc.catalogue.LotOfProductListFormFull;
import maxzawalo.c2.full.ui.pc.form.AnaliticsForm;
import maxzawalo.c2.full.ui.pc.model.document.CashVoucherTablePartModel;

public class CashVoucherForm extends StoreDocForm<CashVoucher, CashVoucherTablePart.Product> {

	@Override
	protected void FreeTimeLimit() {
		// в полной версии отключаем
	}

	public CashVoucherForm() {
		this(null);
	}

	public CashVoucherForm(JDialog parent) {
		super(parent);
		setBounds(0, 0, 1000, 700);

		factory = new CashVoucherFactory();
		for (String name : GetTPNames())
			tablePartModels.put(name, new CashVoucherTablePartModel());

		doc_currency.setVisible(false);
		contractor.setVisible(false);
		doc_contract.setVisible(false);
		sum_contains_vat.setVisible(false);

		tabbedPane.remove(tabService);
	}

	@Override
	protected void GenerateReportData(String name) {
		CashVoucher doc = factory.GetById(elementBO.id);
		((CashVoucherFactory) factory).LoadTablePart(doc);
		reportPath = doc.Dump4Report(name, gson);
	}

	@Override
	protected void LoadReportFromService(String reportName) {
		CashVoucher doc = new CashVoucher().FromDump(reportPath, gson);
		reportPath = reportPath.replace("c2_report", "xlsx");
		Xlsx.PrintMatrix(reportPath, Arrays.asList(doc), new CashVoucherReporter(), 1, 1);
	}

	@Override
	protected LotOfProductListFormFree CreateSelectForm() {
		return new LotOfProductListFormFull(CashVoucherForm.this, CashVoucherForm.this);
	}

	@Override
	protected void ShowTransactions() {
		AnaliticsForm form = new AnaliticsForm(this);
		form.setVisible(true);
		form.setRegistrator((DocumentBO) this.elementBO);
	}
}