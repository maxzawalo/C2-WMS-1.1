package maxzawalo.c2.full.ui.pc.document;

import javax.swing.JDialog;

import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.free.ui.pc.catalogue.LotOfProductListFormFree;
import maxzawalo.c2.free.ui.pc.document.store.StoreDocForm;
import maxzawalo.c2.full.bo.document.write_off_product.WriteOffProduct;
import maxzawalo.c2.full.bo.document.write_off_product.WriteOffProductTablePart;
import maxzawalo.c2.full.data.factory.document.WriteOffProductFactory;
import maxzawalo.c2.full.ui.pc.catalogue.LotOfProductListFormFull;
import maxzawalo.c2.full.ui.pc.form.AnaliticsForm;
import maxzawalo.c2.full.ui.pc.model.document.WriteOffProductTablePartModel;

public class WriteOffProductForm extends StoreDocForm<WriteOffProduct, WriteOffProductTablePart.Product> {
	@Override
	protected void FreeTimeLimit() {
		// в полной версии отключаем
	}

	public WriteOffProductForm() {
		this(null);
	}

	public WriteOffProductForm(JDialog parent) {
		super(parent);
		setBounds(0, 0, 1000, 700);
		doc_currency.setVisible(false);
		contractor.setVisible(false);
		doc_contract.setVisible(false);
		sum_contains_vat.setVisible(false);

		factory = new WriteOffProductFactory();

		for (String name : GetTPNames())
			tablePartModels.put(name, new WriteOffProductTablePartModel());
	}

	// @Override
	// protected boolean Print() {
	// if (!super.Print())
	// return false;
	// CashVoucher doc = new CashVoucher().GetById(elementBO.id);
	// doc.LoadTablePart();
	// Xlsx.PrintDoc(doc, doc.TablePartProduct, new CashVoucherReporter());
	//
	// return true;
	// }

	@Override
	protected LotOfProductListFormFree CreateSelectForm() {
		return new LotOfProductListFormFull(this, this);
	}
	
	@Override
	protected void ShowTransactions() {
		AnaliticsForm form = new AnaliticsForm(this);
		form.setVisible(true);
		form.setRegistrator((DocumentBO) this.elementBO);
	}
}