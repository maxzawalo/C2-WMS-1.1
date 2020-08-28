package maxzawalo.c2.free.accounting.acc;

import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.bo.Product;
import maxzawalo.c2.free.bo.registry.AccAcc;

//НДС по приобретенным товарам
public class Acc_18_4 extends AccAcc<Acc_18_4> {
	public Acc_18_4() {
		code = "18.4";
		active = true;
		typeSubCount1 = Contractor.class;
		// typeSubCount2 = Поступление и оплата ТМЦ
		typeSubCount3 = Product.class;
	}
}