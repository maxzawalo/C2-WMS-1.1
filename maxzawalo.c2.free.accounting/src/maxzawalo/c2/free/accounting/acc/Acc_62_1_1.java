package maxzawalo.c2.free.accounting.acc;

import maxzawalo.c2.free.bo.Contract;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.bo.registry.AccAcc;

//Расчеты с покупателями и заказчиками в рублях
public class Acc_62_1_1 extends AccAcc<Acc_62_1_1> {
	public Acc_62_1_1() {
		code = "62.1.1";
		active = true;
		typeSubCount1 = Contractor.class;
		typeSubCount2 = Contract.class;
		// typeSubCount3 = Отгрузка и оплата ТМЦ
	}
}