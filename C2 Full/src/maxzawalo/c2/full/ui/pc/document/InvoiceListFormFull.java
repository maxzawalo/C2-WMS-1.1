package maxzawalo.c2.full.ui.pc.document;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.utils.FileUtils;
import maxzawalo.c2.free.bo.document.invoice.Invoice;
import maxzawalo.c2.free.ui.pc.document.store.InvoiceListFormFree;
import maxzawalo.c2.full.el_doc.ElDoc;
import maxzawalo.c2.full.ui.pc.form.AnaliticsForm;

public class InvoiceListFormFull extends InvoiceListFormFree {
	public InvoiceListFormFull() {
		super();
		typeItemForm = InvoiceFormFull.class;
		// TODO: factory = new InvoiceFactoryFull();
	}

	@Override
	protected void FromXml() {
		JFileChooser fileopen = new JFileChooser();
		FileNameExtensionFilter xmlfilter = new FileNameExtensionFilter("xml files (*.xml)", "xml");
		fileopen.setFileFilter(xmlfilter);
		fileopen.setCurrentDirectory(new File(FileUtils.getAppDir()));
		int ret = fileopen.showDialog(null, "Открыть файл");
		if (ret == JFileChooser.APPROVE_OPTION) {
			File file = fileopen.getSelectedFile();
			Invoice invoice = ElDoc.Load(file.getAbsolutePath());
			NewItemForm();
			itemForm.Load(invoice.id);
			itemForm.setVisible(true);
		}
	}

	@Override
	protected void ShowDocTransaction() {
		AnaliticsForm form = new AnaliticsForm(this);
		form.setVisible(true);
		form.setRegistrator((DocumentBO) GetSelectedItem());
	}
}