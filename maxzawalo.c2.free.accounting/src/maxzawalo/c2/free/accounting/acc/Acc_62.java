package maxzawalo.c2.free.accounting.acc;

import maxzawalo.c2.free.bo.registry.AccAcc;

//Расчеты с покупателями и заказчиками
public class Acc_62 extends AccAcc<Acc_62> {
	public Acc_62() {
		active = true;
		passive = true;
		// typeSubCount1 = Контрагенты
		// typeSubCount2 = Договор
		// typeSubCount3 = Отгрузка и оплата ТМЦ
	}
}