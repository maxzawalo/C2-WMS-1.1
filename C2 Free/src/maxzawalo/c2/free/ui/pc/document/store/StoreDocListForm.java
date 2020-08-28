package maxzawalo.c2.free.ui.pc.document.store;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.data.factory.DocumentFactory;
import maxzawalo.c2.base.ui.pc.document.DocForm;
import maxzawalo.c2.base.ui.pc.document.DocListForm;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.bo.store.StoreDocBO;
import maxzawalo.c2.free.bo.store.StoreTP;
import maxzawalo.c2.free.ui.pc.form.FreeVersionForm;

public class StoreDocListForm<TypeBO, ItemForm> extends DocListForm<TypeBO, ItemForm> {

	// TODO:
	// enableSuggestor.setVisible(true);
	// enableSuggestor.setSelected(Settings.enableSuggestor);

	@Override
	protected void ShowDocTransaction() {
		FreeVersionForm.Full();
		// TODO: full
		// AnaliticsForm form = new AnaliticsForm(StoreDocListForm.this);
		// form.setVisible(true);
		// form.setRegistrator((DocumentBO) GetSelectedItem());
	}

	public StoreDocBO CreateFromSourceDocFunc(StoreDocBO fromDoc, DocForm toForm, StoreDocBO toDoc, StoreTP toTp) {
		// CreateFromSourceDoc
		// if (!toDoc.meta.contains("reg_type="))
		// toDoc.meta += " reg_type=" + fromDoc.reg_type;
		for (int row : table.getSelectedRows()) {
			int id = ((BO) tableModel.getList().get(row)).id;
			fromDoc = (StoreDocBO) factory.GetById(id);
			((DocumentFactory) factory).LoadTablePart(fromDoc);

			// DeliveryNote->Invoice - загружаем БСО напр.
			fromDoc = LoadExt(fromDoc);

			// toDoc.DocDate = fromDoc.DocDate;
			toDoc.doc_currency = fromDoc.doc_currency;
			toDoc.contractor = fromDoc.contractor;
			toDoc.doc_contract = fromDoc.doc_contract;

			toDoc.source_doc_type = fromDoc.reg_type;
			toDoc.source_doc_id = fromDoc.id;

			StoreTP.AddTP(fromDoc, toDoc, toTp);
			for (Object tp : toDoc.TablePartProduct)
				((StoreTP) tp).onChanged = toForm.onTablePartChanged;
		}

		// toDoc.EnumTP();
		// TODO: list
		return fromDoc;
	}

	public StoreDocBO LoadExt(StoreDocBO fromDoc) {
		return fromDoc;

	}

	@Override
	protected void setSearchContext() {
		searchContext = Contractor.class;
	}

	@Override
	protected String SelectItemSetText(BO bo, String text) {
		if (bo instanceof StoreDocBO)
			if (((StoreDocBO) bo).contractor != null)
				text = ((StoreDocBO) bo).contractor.name;
		return super.SelectItemSetText(bo, text);
	}
}