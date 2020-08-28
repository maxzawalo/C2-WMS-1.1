package maxzawalo.c2.full.report.code;

import java.util.HashMap;
import java.util.Map;

import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.free.bo.Contract;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.bo.Settings;
import maxzawalo.c2.full.reporter.DocxReporter;

public class ContractReporter extends DocxReporter<Contract> {
	public ContractReporter() {
		templatePath += "ДоговорПоставки.docx";
	}

	@Override
	protected Map<String, String> GetReplacements(Contract contract) {
		// Бюджетный на основании счета, номер счета, дата сумма в договор
		Map<String, String> replacements = new HashMap<>();

		replacements.put("[МойУнп]", Settings.myFirm.unp);
		replacements.put("[МоеИмя]", Settings.myFirm.name);
		replacements.put("[МойАдрес]", Settings.myFirm.legal_address);

		replacements.put("[Директор]", Settings.Head.toReportShort());
		replacements.put("[НомерДок]", contract.number);
		replacements.put("[ДатаДок]", Format.Show(contract.DocDate));

		Contractor owner = ((Contractor) contract.owner);
		replacements.put("[ПокупательПолноеНаименование]", owner.full_name);

		String ПокупательЦелиПриобретения = "__________________________________________________________________________________";
		ПокупательЦелиПриобретения += " (для собственного производства и (или) потребления, вывоза из РБ,";
		ПокупательЦелиПриобретения += " оптовой и (или) розничной торговли, - нужное Покупатель вписывает самостоятельно)";
		replacements.put("[ПокупательЦелиПриобретения]", ПокупательЦелиПриобретения);

		replacements.put("[ПокупательЮридическийАдрес]", owner.legal_address);
		replacements.put("[ПокупательБанк]", "__________________________________");
		replacements.put("[ПокупательБанкБик]", "________________");
		replacements.put("[ПокупательУнп]", owner.unp);
		replacements.put("[ПокупательТелефон]", owner.phone);

		return replacements;
	}
}