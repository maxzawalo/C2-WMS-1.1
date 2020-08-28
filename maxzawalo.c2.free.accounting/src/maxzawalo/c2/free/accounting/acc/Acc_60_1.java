package maxzawalo.c2.free.accounting.acc;

import maxzawalo.c2.free.bo.registry.AccAcc;

//Расчеты с поставщиками и подрядчиками
public class Acc_60_1 extends AccAcc<Acc_60_1> {
	public Acc_60_1() {
		code = "60.1";
		passive = true;
		// typeSubCount1 = Контрагенты
		// typeSubCount2 = Договор
		// typeSubCount3 = Поступление и оплата ТМЦ
	}
}