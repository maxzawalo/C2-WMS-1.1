package maxzawalo.c2.free.ui.pc.document.store;

import javax.swing.JDialog;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.free.bo.document.bill.Bill;
import maxzawalo.c2.free.bo.document.bill.BillTablePart;
import maxzawalo.c2.free.data.factory.catalogue.ContractorFactory;
import maxzawalo.c2.free.data.factory.document.BillFactory;
import maxzawalo.c2.free.data.factory.document.StoreDocFactory;
import maxzawalo.c2.free.ui.pc.form.FreeVersionForm;
import maxzawalo.c2.free.ui.pc.model.document.BillTablePartModel;

public class BillFormFree extends StoreDocForm<Bill, BillTablePart.Product> {
	public BillFormFree() {
		this(null);
	}

	public BillFormFree(JDialog parent) {
		super(parent);
		FreeTimeLimit();
		factory = new BillFactory();
		setBounds(0, 0, 1000, 700);
		for (String name : GetTPNames())
			tablePartModels.put(name, new BillTablePartModel());
	}

	@Override
	protected boolean Check4Print(String name) {
		return !((BO) elementBO).code.equals("");
	}

	@Override
	protected void GenerateReportData(String name) {
		Bill doc = factory.GetById(elementBO.id);
		((StoreDocFactory) factory).LoadTablePart(doc);
		new ContractorFactory().setContactInfoFields(doc.contractor);
		// doc.calcFields.put("ReportPaymentData",
		// ContractorFactory.ReportPaymentData(doc.contractor));
		reportPath = doc.Dump4Report(name, gson);
	}

	@Override
	protected void FreeSoon() {
		FreeVersionForm.Soon();
	}
}