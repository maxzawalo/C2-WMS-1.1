package maxzawalo.c2.free.accounting.acc;

import maxzawalo.c2.free.bo.registry.AccAcc;

//000	Вспомогательный (для ввода входящих остатков)
public class Acc_000 extends AccAcc<Acc_000> {
	public Acc_000() {
		code = "000";
		active = true;
		passive = true;
	}
}