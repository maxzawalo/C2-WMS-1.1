package maxzawalo.c2.full.ui.pc.document;

import java.util.Arrays;

import javax.swing.JDialog;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.Coworker;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.base.ui.pc.controls.ComboBoxBizControl;
import maxzawalo.c2.base.ui.pc.controls.DateBizControl;
import maxzawalo.c2.base.utils.FileUtils;
import maxzawalo.c2.free.ui.pc.catalogue.LotOfProductListFormFree;
import maxzawalo.c2.free.ui.pc.document.store.StoreDocForm;
import maxzawalo.c2.full.bo.document.warrant_4_receipt.Warrant4Receipt;
import maxzawalo.c2.full.bo.document.warrant_4_receipt.Warrant4ReceiptTablePart;
import maxzawalo.c2.full.data.factory.document.Warrant4ReceiptFactory;
import maxzawalo.c2.full.report.code.Warrant4ReceiptM2Reporter;
import maxzawalo.c2.full.reporter.Xlsx;
import maxzawalo.c2.full.ui.pc.catalogue.LotOfProductListFormFull;
import maxzawalo.c2.full.ui.pc.model.document.Warrant4ReceiptTablePartModel;

public class Warrant4ReceiptForm extends StoreDocForm<Warrant4Receipt, Warrant4ReceiptTablePart.Product> {
	@Override
	protected void FreeTimeLimit() {
		// в полной версии отключаем
	}

	public Warrant4ReceiptForm() {
		this(null);
	}

	public Warrant4ReceiptForm(JDialog parent) {
		super(parent);
		setBounds(0, 0, 1000, 700);

		doc_currency.setLocation(953, 67);
		store.setLocation(955, 12);
		store.setVisible(false);
		doc_currency.setVisible(false);

		factory = new Warrant4ReceiptFactory();

		for (String name : GetTPNames())
			tablePartModels.put(name, new Warrant4ReceiptTablePartModel());

		BizControlBase in_form_date = new DateBizControl();
		in_form_date.setFieldName(Warrant4Receipt.fields.END_DATE);
		in_form_date.setCaption("Дата действия");
		in_form_date.setBounds(452, 12, 124, 56);
		topPanel.add(in_form_date);

		BizControlBase coworker = new ComboBoxBizControl();
		coworker.fieldType = Coworker.class;
		coworker.LoadList();
		coworker.setFieldName(Warrant4Receipt.fields.COWORKER);
		coworker.setCaption("Подотчетное лицо");
		coworker.setBounds(453, 135, 336, 56);
		topPanel.add(coworker);
	}

	@Override
	protected boolean Check4Print(String reportName) {
		return !((BO) elementBO).code.equals("");
	}

	@Override
	protected void LoadReportFromService(String name) {
		Warrant4Receipt doc = factory.GetById(elementBO.id);

		((Warrant4ReceiptFactory) factory).LoadTablePart(doc);
		// reportPath = reportPath.replace("c2_report", "xlsx");
		// TODO: json
		reportPath = FileUtils.GetReportDir() + "Warrant4Receipt_" + System.currentTimeMillis() + ".xlsx";
		Xlsx.PrintMatrix(reportPath, Arrays.asList(doc), new Warrant4ReceiptM2Reporter(), 1, 1);
		// Xlsx.PrintDoc(reportPath, doc, doc.TablePartProduct, new
		// Warrant4ReceiptМ2Reporter());
	}

	@Override
	protected LotOfProductListFormFree CreateSelectForm() {
		return new LotOfProductListFormFull(this, this);
	}
}