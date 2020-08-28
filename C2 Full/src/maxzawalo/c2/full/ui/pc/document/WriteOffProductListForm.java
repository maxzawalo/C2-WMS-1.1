package maxzawalo.c2.full.ui.pc.document;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.free.bo.document.invoice.Invoice;
import maxzawalo.c2.free.bo.document.invoice.InvoiceTablePart;
import maxzawalo.c2.free.bo.store.StoreDocBO;
import maxzawalo.c2.free.ui.pc.document.store.InvoiceFormFree;
import maxzawalo.c2.free.ui.pc.document.store.StoreDocForm;
import maxzawalo.c2.free.ui.pc.document.store.StoreDocListForm;
import maxzawalo.c2.full.bo.document.write_off_product.WriteOffProduct;
import maxzawalo.c2.full.data.factory.document.WriteOffProductFactory;
import maxzawalo.c2.full.ui.pc.form.AnaliticsForm;
import maxzawalo.c2.full.ui.pc.model.document.WriteOffProductTableModel;

public class WriteOffProductListForm extends StoreDocListForm<WriteOffProduct, WriteOffProductForm> {

	public WriteOffProductListForm() {
		factory = new WriteOffProductFactory();
		tableModel = new WriteOffProductTableModel();

		fromSourcePopup.add(new JMenuItem(new AbstractAction("Приходная") {
			public void actionPerformed(ActionEvent e) {
				CreateInvoiceFromWriteOffProduct();
			}
		}));
		fromSourcePopup.addSeparator();
	}

	protected void CreateInvoiceFromWriteOffProduct() {
		StoreDocForm toForm = new InvoiceFormFree();
		StoreDocBO toDoc = new Invoice();

		CreateFromSourceDocFunc(new WriteOffProduct(), toForm, toDoc, new InvoiceTablePart.Product());

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