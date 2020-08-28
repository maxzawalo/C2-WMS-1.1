package maxzawalo.c2.free.accounting.acc;

import maxzawalo.c2.free.bo.Contract;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.bo.registry.AccAcc;

//Расчеты с поставщиками и подрядчиками в рублях
public class Acc_60_1_1 extends AccAcc<Acc_60_1_1> {
	public Acc_60_1_1() {
		code = "60.1.1";
		passive = true;
		typeSubCount1 = Contractor.class;
		typeSubCount2 = Contract.class;
		// typeSubCount3 = Поступление и оплата ТМЦ
	}
}