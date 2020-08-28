package maxzawalo.c2.full.data.load_bank;

import java.io.FileInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.free.bo.bank.BankTP;
import maxzawalo.c2.free.bo.document.receiptmoney.ReceiptMoney;
import maxzawalo.c2.free.bo.document.receiptmoney.ReceiptMoneyTablePart;
import maxzawalo.c2.free.data.factory.document.ReceiptMoneyFactory;

public class LoadAKBB extends BankLoader {
	@Override
	public void Load(String fileName) throws Exception {

		fileName = "D:/workspace_java/Выписка/30072018-01082018.xml";

		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(new FileInputStream(fileName));
		doc.getDocumentElement().normalize();

		// Дата
		NodeList accountinfo = (NodeList) xPath.compile("/TURN/ACCOUNTINFO").evaluate(doc, XPathConstants.NODESET);
		for (int i = 0; i < accountinfo.getLength(); i++) {
			Node ai = accountinfo.item(i);
			String account = getParam(ai, "ACCOUNT");
			String period = getParam(ai, "PERIOD").replace("за", "").trim();
			System.out.println(period);
			NodeList operinfo = getList(ai, "OPERINFO/OPER");
			// операции
			for (int j = 0; j < operinfo.getLength(); j++) {
				Node oper = operinfo.item(j);
				String DOCN = getParam(oper, "DOCN");
				String UNPKORR = getParam(oper, "UNPKORR");
				String DETPAY = getParam(oper, "DETPAY");
				String ed = getParam(oper, "SUMOPER/@ed").trim();
				// ek - расход
				// ek="0.67" /> Плата за кредит.остат.за ОАО "АСБ Беларусбанк"
				String ek = getParam(oper, "SUMOPER/@ek").trim();
				// UMOPER nd. Сумма в номинале по дебету nk. Сумма в номинале по кредиту ed.
				// Сумма в эквиваленте по дебету ek.
				if (ek.isEmpty()) {
					allCount++;
					ReceiptMoneyFactory factory = new ReceiptMoneyFactory();

					ReceiptMoney receiptMoney = new ReceiptMoney();
					ReceiptMoneyTablePart.Payment inTP = new ReceiptMoneyTablePart.Payment();
					receiptMoney.TablePartPayment.add(inTP);

					receiptMoney.DocDate = Format.extractDate(period);
					receiptMoney.in_date = receiptMoney.DocDate;
					receiptMoney.in_number = DOCN;
					receiptMoney.contractor = GetContractor(UNPKORR);
					receiptMoney.bank_account = getBankAccount(account);
					
					double sum = Format.extractDouble(ed);
					inTP.sum = sum;
					inTP.Calc(BankTP.fields.SUM);

					// double service_sum = 0;
					// for (String v : variables.keySet())
					// if (v.contains("Nazn") && variables.get(v).contains("ком.")) {
					// service_sum = Format
					// .extractDouble(StringUtils.getBetweenStrings(variables.get(v), "ком.",
					// "BYN"));
					// // ^Nazn2K=ком.0.96 BYN ;^
					// // TODO: test 3%
					// ((ReceiptMoneyTablePart.Payment) inTP).service_sum = service_sum;
					// break;
					// }

					receiptMoney.payment_details = DETPAY;

					// switch (variables.get("CK")) {
					// case "01":
					// receiptMoney.receipt_money_type = new
					// ReceiptMoneyType().getEnumByName("Оплата от покупателя");
					// break;
					// case "06":
					// if (service_sum == 0)
					// receiptMoney.receipt_money_type = new ReceiptMoneyType()
					// .getEnumByName("Прочее поступление");
					// else
					// receiptMoney.receipt_money_type = new ReceiptMoneyType()
					// .getEnumByName("Поступления от продаж по платежным картам и банковским
					// кредитам");
					// break;
					// }
					receiptMoney.CalcTotal();
					if (!factory.Exists(receiptMoney)) {
						factory.Save(receiptMoney);
						newCount++;
					}
				}

				System.out.println(ed + "|" + ek);
			}
		}
	}

	@Override
	public String getName() {
		return "Беларусбанк";
	}
}