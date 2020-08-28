package maxzawalo.c2.free.bo.enums;

import java.util.ArrayList;
import java.util.List;

import maxzawalo.c2.base.annotation.BoField;

@BoField(caption = "Вид поступления денежных средств", type1C = "Перечисления.ВидыОперацийПоступлениеДенежныхСредств")
public class ReceiptMoneyType extends EnumC2<ReceiptMoneyType> {

	@Override
	public List<ReceiptMoneyType> getEnum() {
		if (allEnum == null) {
			allEnum = new ArrayList<>();

			allEnum.add((EnumC2) CreateEnum(1, "Оплата от покупателя"));
			allEnum.add((EnumC2) CreateEnum(2, "Возврат от поставщика"));
			allEnum.add((EnumC2) CreateEnum(3, "Расчеты по кредитам и займам"));
			allEnum.add((EnumC2) CreateEnum(4, "Прочие расчеты с контрагентами"));
			allEnum.add((EnumC2) CreateEnum(5, "Инкассация"));
			allEnum.add((EnumC2) CreateEnum(6, "Приобретение иностранной валюты"));
			allEnum.add((EnumC2) CreateEnum(7, "Поступления от продажи иностранной валюты"));
			allEnum.add((EnumC2) CreateEnum(8, "Поступления от продаж по платежным картам и банковским кредитам"));
			allEnum.add((EnumC2) CreateEnum(9, "Возврат займа работником"));
			allEnum.add((EnumC2) CreateEnum(10, "Прочее поступление"));
		}

		return (List) allEnum;
	}
}