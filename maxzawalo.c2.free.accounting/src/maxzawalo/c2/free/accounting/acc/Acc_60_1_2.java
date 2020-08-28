package maxzawalo.c2.free.accounting.acc;

import maxzawalo.c2.free.bo.Contract;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.bo.registry.AccAcc;

//Расчеты с поставщиками и подрядчиками в валюте
public class Acc_60_1_2 extends AccAcc<Acc_60_1_2> {
	public Acc_60_1_2() {
		code = "60.1.2";
		passive = true;
		currency = true;
		typeSubCount1 = Contractor.class;
		typeSubCount2 = Contract.class;
		// typeSubCount3 = Поступление и оплата ТМЦ
	}
}