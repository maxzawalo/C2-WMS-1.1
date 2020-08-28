package maxzawalo.c2.free.accounting.acc;

import maxzawalo.c2.free.bo.registry.AccAcc;

//НДС по приобретенным нематериальным активам (НМА)
public class Acc_18_2 extends AccAcc<Acc_18_2> {
	public Acc_18_2() {
		code = "18.2";
		active = true;
		// typeSubCount1 = Контрагенты
		// typeSubCount2 = Поступление и оплата ТМЦ
		// typeSubCount3 = Нематериальные активы
	}
}