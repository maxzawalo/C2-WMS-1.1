package maxzawalo.c2.free.accounting.acc;

import maxzawalo.c2.free.bo.BankAccount;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.bo.registry.AccAcc;

//	Код	Наименование	Вид	Валютный	Количественный	Забалансовый	Субконто1	Субконто2	Субконто3	Субконто4	Не используется
//51	Расчетные счета	А	Нет	Нет	Нет	Расчетные счета				Нет

public class Acc_51 extends AccAcc<Acc_51> {
	public Acc_51() {
		code = "51";
		active = true;
		typeSubCount1 = BankAccount.class;
		typeSubCount2 = Contractor.class;
	}
}