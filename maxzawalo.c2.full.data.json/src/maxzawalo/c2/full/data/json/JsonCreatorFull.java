package maxzawalo.c2.full.data.json;

import java.util.Map;

import maxzawalo.c2.free.bo.document.invoice.Invoice;
import maxzawalo.c2.free.bo.document.invoice.InvoiceTablePart;
import maxzawalo.c2.free.data.json.BoAdapter;
import maxzawalo.c2.free.data.json.JsonCreatorFree;
import maxzawalo.c2.full.bo.document.cashvoucher.CashVoucher;
import maxzawalo.c2.full.bo.document.cashvoucher.CashVoucherTablePart;
import maxzawalo.c2.full.bo.document.return_of_goods.ReturnOfGoods;
import maxzawalo.c2.full.bo.document.return_of_goods.ReturnOfGoodsTablePart;
import maxzawalo.c2.full.data.json.adapter.CashVoucherAdapter;
import maxzawalo.c2.full.data.json.adapter.CashVoucherTablePartAdapter;
import maxzawalo.c2.full.data.json.adapter.InvoiceAdapter;
import maxzawalo.c2.full.data.json.adapter.InvoiceTablePartAdapter;
import maxzawalo.c2.full.data.json.adapter.ReturnOfGoodsAdapter;
import maxzawalo.c2.full.data.json.adapter.ReturnOfGoodsTablePartAdapter;

public class JsonCreatorFull extends JsonCreatorFree {

	public JsonCreatorFull(boolean web_ui) {
		super(web_ui);
	}

	@Override
	protected void AddAdapters(Map<Class, BoAdapter> adapters) {
		adapters.put(CashVoucher.class, new CashVoucherAdapter());
		adapters.put(CashVoucherTablePart.Product.class, new CashVoucherTablePartAdapter.Product());

		adapters.put(ReturnOfGoods.class, new ReturnOfGoodsAdapter());
		adapters.put(ReturnOfGoodsTablePart.Product.class, new ReturnOfGoodsTablePartAdapter.Product());
		
		adapters.put(Invoice.class, new InvoiceAdapter());
		adapters.put(InvoiceTablePart.Product.class, new InvoiceTablePartAdapter.Product());
		
		
		super.AddAdapters(adapters);

	}
}