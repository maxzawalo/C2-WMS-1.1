package maxzawalo.c2.full.ui.pc.document;

import maxzawalo.c2.base.ui.pc.document.DocForm;
import maxzawalo.c2.free.bo.document.bill.Bill;
import maxzawalo.c2.free.bo.document.bill.BillTablePart;
import maxzawalo.c2.free.bo.document.deliverynote.DeliveryNote;
import maxzawalo.c2.free.bo.document.deliverynote.DeliveryNoteTablePart;
import maxzawalo.c2.free.bo.store.StoreDocBO;
import maxzawalo.c2.free.ui.pc.document.store.BillListFormFree;

public class BillListFormFull extends BillListFormFree {

	public BillListFormFull() {
		typeItemForm = BillFormFull.class;
	}

	@Override
	protected void CreateDeliveryNoteFromBills() {
		DocForm toForm = new DeliveryNoteFormFull();
		StoreDocBO toDoc = new DeliveryNote();

		CreateFromSourceDocFunc(new Bill(), toForm, toDoc, new DeliveryNoteTablePart.Product());

		toForm.elementBO = toDoc;
		toForm.onTablePartChanged.Do(null);
		toForm.setData();
		toForm.setTableModel();
		toForm.setVisible(true);
	}

	@Override
	protected void CreateBillFromBills() {
		DocForm toForm = new BillFormFull();
		StoreDocBO toDoc = new Bill();

		CreateFromSourceDocFunc(new Bill(), toForm, toDoc, new BillTablePart.Product());

		toForm.elementBO = toDoc;
		toForm.onTablePartChanged.Do(null);
		toForm.setData();
		toForm.setTableModel();
		toForm.setVisible(true);
	}
}