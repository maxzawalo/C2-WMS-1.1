package maxzawalo.c2.free.accounting.acc;

import maxzawalo.c2.free.bo.registry.AccAcc;

//НДС по приобретенным основным средствам (ОС)
public class Acc_18_1 extends AccAcc<Acc_18_1> {
	public Acc_18_1() {
		code = "18.1";
		active = true;
		// typeSubCount1 = Контрагенты
		// typeSubCount2 = Поступление и оплата ТМЦ
		// typeSubCount3 = Основные средства
	}
}