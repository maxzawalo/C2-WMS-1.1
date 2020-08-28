package maxzawalo.c2.free.bo.enums;

import java.util.ArrayList;
import java.util.List;

import maxzawalo.c2.base.annotation.BoField;

@BoField(caption = "", type1C = "Перечисления.ВидыДоговоровКонтрагентов")
public class ContractType extends EnumC2<ContractType> {

	@Override
	public List<ContractType> getEnum() {
		if (allEnum == null) {
			allEnum = new ArrayList<>();
			// allEnum.add((EnumC2) CreateEnum(1, "С поставщиком"));
			// allEnum.add((EnumC2) CreateEnum(2, "С покупателем"));
			// allEnum.add((EnumC2) CreateEnum(3, "Прочее"));
			allEnum.add((EnumC2) CreateEnum(1, "С поставщиком"));
			allEnum.add((EnumC2) CreateEnum(2, "С покупателем"));
			allEnum.add((EnumC2) CreateEnum(3, "С комитентом (принципалом) на продажу"));
			allEnum.add((EnumC2) CreateEnum(4, "С комиссионером (агентом) на продажу"));
			allEnum.add((EnumC2) CreateEnum(5, "С комитентом (принципалом) на закупку"));
			allEnum.add((EnumC2) CreateEnum(6, "С комиссионером (агентом) на закупку"));
			allEnum.add((EnumC2) CreateEnum(7, "Прочее"));
			// update contract set contract_type_id = 7 where contract_type_id =
			// 3
		}
		return (List) allEnum;
	}
}