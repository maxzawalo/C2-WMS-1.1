package maxzawalo.c2.free.bo.enums;

import java.util.ArrayList;
import java.util.List;

import maxzawalo.c2.base.annotation.BoField;

@BoField(caption = "Виды операций списания денежных средств", type1C = "Перечисления.ВидыОперацийСписаниеДенежныхСредств")
public class WriteOffMoneyType extends EnumC2<WriteOffMoneyType> {

	@Override
	public List<WriteOffMoneyType> getEnum() {
		if (allEnum == null) {
			allEnum = new ArrayList<>();

			allEnum.add((EnumC2) CreateEnum(1, "Оплата поставщику"));
			allEnum.add((EnumC2) CreateEnum(2, "Возврат покупателю"));
			allEnum.add((EnumC2) CreateEnum(3, "Перечисление налога"));
			allEnum.add((EnumC2) CreateEnum(4, "Расчеты по кредитам и займам"));
			allEnum.add((EnumC2) CreateEnum(5, "Прочие расчеты с контрагентами"));
			allEnum.add((EnumC2) CreateEnum(6, "Перевод на другой счет организации"));
			allEnum.add((EnumC2) CreateEnum(7, "Перечисление подотчетному лицу"));
			allEnum.add((EnumC2) CreateEnum(8, "Перечисление заработной платы по ведомостям"));
			allEnum.add((EnumC2) CreateEnum(9, "Перечисление заработной платы работнику"));
			allEnum.add((EnumC2) CreateEnum(10, "Перечисление депонированной заработной платы"));
			allEnum.add((EnumC2) CreateEnum(11, "Выдача займа работнику"));
			allEnum.add((EnumC2) CreateEnum(12, "Прочее списание"));
		}

		return (List) allEnum;
	}
}