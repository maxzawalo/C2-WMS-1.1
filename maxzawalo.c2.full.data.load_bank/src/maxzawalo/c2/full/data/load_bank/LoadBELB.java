package maxzawalo.c2.full.data.load_bank;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import maxzawalo.c2.base.utils.FileUtils;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.base.utils.StringUtils;
import maxzawalo.c2.free.bo.bank.BankTP;
import maxzawalo.c2.free.bo.document.receiptmoney.ReceiptMoney;
import maxzawalo.c2.free.bo.document.receiptmoney.ReceiptMoneyTablePart;
import maxzawalo.c2.free.bo.document.writeoffmoney.WriteOffMoney;
import maxzawalo.c2.free.bo.document.writeoffmoney.WriteOffMoneyTablePart;
import maxzawalo.c2.free.bo.enums.ReceiptMoneyType;
import maxzawalo.c2.free.bo.enums.WriteOffMoneyType;
import maxzawalo.c2.free.data.factory.document.ReceiptMoneyFactory;
import maxzawalo.c2.free.data.factory.document.WriteOffMoneyFactory;

public class LoadBELB extends BankLoader {
	Map<String, String> variables = new HashMap<>();

	@Override
	public void Load(String fileName) throws Exception {
		List<String> list = FileUtils.readFileAsList(fileName, "windows-1251");
		// Запрос выписки по рублевому счету
		String account = "";
		ReceiptMoney receiptMoney = null;// new ReceiptMoney();
		WriteOffMoney writeOffMoney = null;
		ReceiptMoneyTablePart.Payment inTP = null;
		WriteOffMoneyTablePart.Payment outTP = null;

		boolean newDoc = false;
		for (String line : list) {
			if (line.contains("*****") && line.contains("^Acc"))
				account = StringUtils.getBetweenStrings(line, "^Acc=", "^");
			if (line.contains("^DelimD=") || line.contains("^DEBET=ДЕБЕТ^")) {
				newDoc = true;
				if (writeOffMoney != null) {
					writeOffMoney.DocDate = Format.extractDate(variables.get("DateOpD"));
					writeOffMoney.in_date = writeOffMoney.DocDate;
					writeOffMoney.in_number = variables.get("NumDocD");
					writeOffMoney.contractor = GetContractor(variables.get("TPND"));

					double sum = Format.extractDouble(variables.get("AmmRubD"));
					outTP.sum = sum;
					outTP.Calc(BankTP.fields.SUM);

					writeOffMoney.payment_details = variables.get("NaznD");
					writeOffMoney.payment_details += variables.get("Nazn2D");
					writeOffMoney.payment_details += variables.get("Nazn3D");
					writeOffMoney.payment_details += variables.get("Nazn4D");

					switch (variables.get("CD")) {
					case "01":
						writeOffMoney.writeoffmoney_type = new WriteOffMoneyType().getEnumByName("Оплата поставщику");
						break;
					case "06":
						break;
					}
					try {
						writeOffMoney.CalcTotal();
						WriteOffMoneyFactory factory = new WriteOffMoneyFactory();
						allCount++;
						if (!factory.Exists(writeOffMoney)) {
							factory.Save(writeOffMoney);
							newCount++;
						}
					} catch (Exception e) {
						// System.out.println(receiptMoney.payment_details);
						throw e;
					}
					variables.clear();
				}
			} else if (line.contains("^DelimK=") || line.contains("^KREDIT=КРЕДИТ^")) {
				newDoc = true;
				if (receiptMoney != null) {
					receiptMoney.DocDate = Format.extractDate(variables.get("DateOpK"));
					receiptMoney.in_date = receiptMoney.DocDate;
					receiptMoney.in_number = variables.get("NumDocK");
					receiptMoney.contractor = GetContractor(variables.get("TPNK"));
					double sum = Format.extractDouble(variables.get("AmmRubK"));
					inTP.sum = sum;
					inTP.Calc(BankTP.fields.SUM);

					double service_sum = 0;
					for (String v : variables.keySet())
						if (v.contains("Nazn") && variables.get(v).contains("ком.")) {
							service_sum = Format
									.extractDouble(StringUtils.getBetweenStrings(variables.get(v), "ком.", "BYN"));
							// ^Nazn2K=ком.0.96 BYN ;^
							// TODO: test 3%
							((ReceiptMoneyTablePart.Payment) inTP).service_sum = service_sum;
							break;
						}

					receiptMoney.payment_details = variables.get("NaznK");
					receiptMoney.payment_details += variables.get("Nazn2K");
					receiptMoney.payment_details += variables.get("Nazn3K");
					receiptMoney.payment_details += variables.get("Nazn4K");

					switch (variables.get("CK")) {
					case "01":
						receiptMoney.receipt_money_type = new ReceiptMoneyType().getEnumByName("Оплата от покупателя");
						break;
					case "06":
						if (service_sum == 0)
							receiptMoney.receipt_money_type = new ReceiptMoneyType()
									.getEnumByName("Прочее поступление");
						else
							receiptMoney.receipt_money_type = new ReceiptMoneyType()
									.getEnumByName("Поступления от продаж по платежным картам и банковским кредитам");
						break;
					}

					try {
						receiptMoney.CalcTotal();
						ReceiptMoneyFactory factory = new ReceiptMoneyFactory();
						allCount++;
						if (!factory.Exists(receiptMoney)) {
							factory.Save(receiptMoney);
							newCount++;
						}

					} catch (Exception e) {
						// System.out.println(receiptMoney.payment_details);
						throw e;
					}
					variables.clear();
				}
			}

			if (newDoc) {
				if (line.contains("^CK=")) {
					receiptMoney = new ReceiptMoney();
					receiptMoney.bank_account = getBankAccount(account);
					inTP = new ReceiptMoneyTablePart.Payment();
					receiptMoney.TablePartPayment.add(inTP);
					newDoc = false;
				} else if (line.contains("^CD=")) {
					writeOffMoney = new WriteOffMoney();
					writeOffMoney.bank_account = getBankAccount(account);
					outTP = new WriteOffMoneyTablePart.Payment();
					writeOffMoney.TablePartPayment.add(outTP);
					newDoc = false;
				}
			}
			PutVar(line, "CK");
			PutVar(line, "DateOpK");
			PutVar(line, "NumDocK");
			PutVar(line, "NumDocK");
			PutVar(line, "TPNK");
			PutVar(line, "AmmRubK");
			PutVar(line, "NaznK");
			PutVar(line, "Nazn2K");
			PutVar(line, "Nazn3K");
			PutVar(line, "Nazn4K");

			// -------------------- Расход ---------------------------
			PutVar(line, "CD");
			PutVar(line, "DateOpD");
			PutVar(line, "NumDocD");
			PutVar(line, "TPND");
			PutVar(line, "AmmRubD");
			PutVar(line, "NaznD");
			PutVar(line, "Nazn2D");
			PutVar(line, "Nazn3D");
			PutVar(line, "Nazn4D");

			System.out.println(line);
		}
	}

	protected void PutVar(String line, String var) {
		if (line.contains("^" + var + "="))
			variables.put(var, StringUtils.getBetweenStrings(line, "^" + var + "=", "^"));
	}

	@Override
	public String getName() {
		return "БелВЭБ";
	}

}