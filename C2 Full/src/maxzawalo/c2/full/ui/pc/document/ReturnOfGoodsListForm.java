package maxzawalo.c2.full.ui.pc.document;

import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.free.ui.pc.document.store.StoreDocListForm;
import maxzawalo.c2.full.bo.document.return_of_goods.ReturnOfGoods;
import maxzawalo.c2.full.data.factory.document.ReturnOfGoodsFactory;
import maxzawalo.c2.full.ui.pc.form.AnaliticsForm;
import maxzawalo.c2.full.ui.pc.model.document.ReturnOfGoodsTableModel;

public class ReturnOfGoodsListForm extends StoreDocListForm<ReturnOfGoods, ReturnOfGoodsForm> {
	// TODO: super
	public ReturnOfGoodsListForm() {
		factory = new ReturnOfGoodsFactory();
		tableModel = new ReturnOfGoodsTableModel();
		// btnAdd.setEnabled(false);
		// btnDuplicate.setEnabled(false);
	}
	
	@Override
	protected void ShowDocTransaction() {
		AnaliticsForm form = new AnaliticsForm(this);
		form.setVisible(true);
		form.setRegistrator((DocumentBO) GetSelectedItem());
	}
}