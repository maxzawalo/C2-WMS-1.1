package maxzawalo.c2.free.accounting.acc;

import maxzawalo.c2.free.bo.registry.AccAcc;

//Расчеты с покупателями и заказчиками в валюте
public class Acc_62_1_2 extends AccAcc<Acc_62_1_2> {
	public Acc_62_1_2() {
		active = true;
		currency = true;
		// typeSubCount1 = Контрагенты
		// typeSubCount2 = Договор
		// typeSubCount3 = Отгрузка и оплата ТМЦ
	}
}