package maxzawalo.c2.full.data.load_bank;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import maxzawalo.c2.base.data.factory.SlaveCatalogueFactory;
import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.free.bo.BankAccount;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.data.factory.catalogue.ContractorFactory;

public class BankLoader {

	protected XPath xPath = XPathFactory.newInstance().newXPath();

	public int newCount = 0;
	public int allCount = 0;

	public String getName() {
		return "";
	}

	public void Load(String fileName) throws Exception {
	}

	protected Contractor GetContractor(String unp) {
		ContractorFactory f = new ContractorFactory();
		f.DeleteFilterOn();
		Contractor contractor = f.GetByUnp(unp);

		if (contractor == null)
			//TODO: add event
			Console.I().ERROR(this.getClass(), "GetContractor", "Ошибка. Не найден Контрагент c УНП=" + unp);
		return contractor;
	}

	public BankAccount getBankAccount(String account) {
		BankAccount acc = (BankAccount) new SlaveCatalogueFactory().Create(BankAccount.class)
				.GetByParam(BankAccount.fields.NUMBER, account);

		if (acc == null)
//			TODO: add event
			Console.I().ERROR(this.getClass(), "getBankAccount", "Ошибка. Не найден Банковский счет " + account);
		return acc;
	}

	protected String getParam(Node n, String name) throws XPathExpressionException {
		return xPath.compile(name).evaluate(n);
	}

	protected NodeList getList(Node n, String name) throws XPathExpressionException {
		return (NodeList) xPath.compile(name).evaluate(n, XPathConstants.NODESET);
	}
}