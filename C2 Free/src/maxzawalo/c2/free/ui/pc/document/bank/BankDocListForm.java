package maxzawalo.c2.free.ui.pc.document.bank;

import maxzawalo.c2.base.Actions;
import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.ui.pc.document.DocListForm;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.bo.bank.BankDocBO;
import maxzawalo.c2.free.ui.pc.form.FreeVersionForm;

public class BankDocListForm<TypeBO, ItemForm> extends DocListForm<TypeBO, ItemForm> {
	@Override
	protected void ShowDocTransaction() {
		if (Actions.ShowDocTransactions == null)
			FreeVersionForm.Full();
		else
			Actions.ShowDocTransactions.Do(this, (DocumentBO) GetSelectedItem());
	}

	@Override
	protected void setSearchContext() {
		searchContext = Contractor.class;
	}

	@Override
	protected String SelectItemSetText(BO bo, String text) {
		if (bo instanceof BankDocBO)
			if (((BankDocBO) bo).contractor != null)
				text = ((BankDocBO) bo).contractor.name;
		return super.SelectItemSetText(bo, text);
	}
}