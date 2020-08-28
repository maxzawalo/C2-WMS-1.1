package maxzawalo.c2.free.accounting.acc;

import maxzawalo.c2.free.bo.LotOfProduct;
import maxzawalo.c2.free.bo.Product;
import maxzawalo.c2.free.bo.Store;
import maxzawalo.c2.free.bo.registry.AccAcc;

public class Acc_41_1 extends AccAcc<Acc_41_1> {
	public Acc_41_1() {
		code = "41.1";
		active = true;
		quantitative = true;
		typeSubCount1 = Product.class;
		typeSubCount2 = LotOfProduct.class;
		typeSubCount3 = Store.class;
	}
}