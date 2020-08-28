package maxzawalo.c2.free.accounting.acc;

import maxzawalo.c2.base.bo.Coworker;
import maxzawalo.c2.free.bo.Product;
import maxzawalo.c2.free.bo.Store;
import maxzawalo.c2.free.bo.registry.AccAcc;

//Специальная оснастка и специальная одежда на складе
public class Acc_10_10 extends AccAcc<Acc_10_10> {
	public Acc_10_10() {
		active = true;
		quantitative = true;
		// Места хранения МОЛ Номенклатура
		typeSubCount1 = Store.class;
		typeSubCount2 = Coworker.class;
		typeSubCount3 = Product.class;
	}
}