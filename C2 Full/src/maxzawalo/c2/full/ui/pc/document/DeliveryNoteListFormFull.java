package maxzawalo.c2.full.ui.pc.document;

import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.ui.pc.document.DocForm;
import maxzawalo.c2.free.bo.StrictForm;
import maxzawalo.c2.free.bo.document.deliverynote.DeliveryNote;
import maxzawalo.c2.free.bo.document.invoice.Invoice;
import maxzawalo.c2.free.bo.document.invoice.InvoiceTablePart;
import maxzawalo.c2.free.bo.store.StoreDocBO;
import maxzawalo.c2.free.data.factory.document.DeliveryNoteFactory;
import maxzawalo.c2.free.ui.pc.document.store.DeliveryNoteListFormFree;
import maxzawalo.c2.full.ui.pc.form.AnaliticsForm;

public class DeliveryNoteListFormFull extends DeliveryNoteListFormFree {

	public DeliveryNoteListFormFull() {
		super();
		typeItemForm = DeliveryNoteFormFull.class;
	}

	@Override
	public StoreDocBO LoadExt(StoreDocBO fromDoc) {
		((DeliveryNoteFactory) factory).LoadStrictForm((DeliveryNote) fromDoc);
		return fromDoc;
	}

	@Override
	protected void CreateInvoiceFromDeliveryNote() {
		DocForm toForm = new InvoiceFormFull();
		Invoice toDoc = new Invoice();
		toDoc.meta = DocumentBO.fields.FROM_SOURCE_DOC;

		DeliveryNote dn = new DeliveryNote();
		dn = (DeliveryNote) CreateFromSourceDocFunc(dn, toForm, toDoc, new InvoiceTablePart.Product());
		// for (Object tpp : toDoc.TablePartProduct) {
		// StoreTP tp = (StoreTP) tpp;
		// // Ввели документ на основании
		// LotOfProduct sourceLot = null;
		// Price sourcePrice = null;
		// if (tp.lotOfProduct != null) {
		// sourceLot = new LotOfProduct().GetById(tp.lotOfProduct.id);
		// sourcePrice = sourceLot.price_bo;
		// }
		// }

		StrictForm form = dn.getCurrentStrictForm();
		if (form != null) {
			toDoc.in_form_date = dn.DocDate;
			toDoc.in_form_number = form.form_batch + " " + form.form_number;
		}

		toForm.elementBO = toDoc;
		toForm.onTablePartChanged.Do(null);
		toForm.setData();
		toForm.setTableModel();
		toForm.setVisible(true);
	}

	@Override
	protected void ShowDocTransaction() {
		AnaliticsForm form = new AnaliticsForm(this);
		form.setVisible(true);
		form.setRegistrator((DocumentBO) GetSelectedItem());
	}
}