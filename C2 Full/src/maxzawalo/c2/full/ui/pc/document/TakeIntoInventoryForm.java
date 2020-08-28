package maxzawalo.c2.full.ui.pc.document;

import javax.swing.JDialog;

import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.base.ui.pc.controls.CheckBoxBizControl;
import maxzawalo.c2.free.ui.pc.catalogue.LotOfProductListFormFree;
import maxzawalo.c2.free.ui.pc.document.store.StoreDocForm;
import maxzawalo.c2.full.bo.document.remaining_stock.RemainingStock;
import maxzawalo.c2.full.bo.document.remaining_stock.RemainingStockTablePart;
import maxzawalo.c2.full.data.factory.document.RemainingStockFactory;
import maxzawalo.c2.full.ui.pc.catalogue.LotOfProductListFormFull;
import maxzawalo.c2.full.ui.pc.form.AnaliticsForm;
import maxzawalo.c2.full.ui.pc.model.document.TakeIntoInventoryTablePartModel;

public class TakeIntoInventoryForm extends StoreDocForm<RemainingStock, RemainingStockTablePart.Product> {
	@Override
	protected void FreeTimeLimit() {
		// в полной версии отключаем
	}

	public TakeIntoInventoryForm() {
		this(null);
	}

	public TakeIntoInventoryForm(JDialog parent) {
		super(parent);
		setBounds(0, 0, 1000, 700);
		doc_currency.setVisible(false);
		contractor.setVisible(false);
		doc_contract.setVisible(false);
		
		BizControlBase is_remaining = new CheckBoxBizControl();
		is_remaining.setFieldName(RemainingStock.fields.IS_REMAINING);
		is_remaining.setCaption("Остатки");
		is_remaining.setBounds(329, 12, 109, 56);
		topPanel.add(is_remaining);
		
		// sum_contains_vat.setVisible(false);
		factory = new RemainingStockFactory();

		for (String name : GetTPNames())
			tablePartModels.put(name, new TakeIntoInventoryTablePartModel());
	}

	@Override
	public boolean Print() {
		if (!super.Print())
			return false;
		// Xlsx.PrintDoc(elementBO, elementBO.TablePart, new
		// DeliveryNoteReporterTN2());
		return true;
	}

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