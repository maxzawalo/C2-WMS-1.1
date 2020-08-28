package maxzawalo.c2.full.el_doc;

import java.io.FileInputStream;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.CatalogueBO;
import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.base.utils.Logger;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.bo.Product;
import maxzawalo.c2.free.bo.Settings;
import maxzawalo.c2.free.bo.Units;
import maxzawalo.c2.free.bo.document.invoice.Invoice;
import maxzawalo.c2.free.bo.document.invoice.InvoiceTablePart;
import maxzawalo.c2.free.bo.store.StoreTP;
import maxzawalo.c2.free.data.factory.catalogue.ContractorFactory;
import maxzawalo.c2.free.data.factory.catalogue.ProductFactory;
import maxzawalo.c2.free.data.factory.catalogue.UnitsFactory;
import maxzawalo.c2.free.data.factory.document.InvoiceFactoryFree;

//import com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper;
//import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

public class ElDoc {

	public static Logger log = Logger.getLogger(ElDoc.class);

	public static Invoice Load(String path) {
		if (!Settings.canCreateEDoc())
			return null;
		try {

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			// invoice-590225275-2017-0000000155.xml
			Document doc = docBuilder.parse(new FileInputStream(path));
			// "D:\\workspace_java\\warehouse\\invoice-590225275-2017-0000000155.xml"
			// normalize text representation
			doc.getDocumentElement().normalize();

			XPath xPath = XPathFactory.newInstance().newXPath();
			// deliveryCondition/contract/documents/document/

			// read a string value
			// String seria =
			// xPath.compile("//deliveryCondition/contract/documents/document/seria").evaluate(doc);
			// String number =
			// xPath.compile("//deliveryCondition/contract/documents/document/number").evaluate(doc);
			// System.out.println(seria + " " + number);

			// String roster_name =
			// xPath.compile("//roster/rosterItem/name").evaluate(doc);
			// System.out.println(roster_name);

			String in_form_number = xPath.compile("concat(//deliveryCondition/contract/documents/document/seria,' ',//deliveryCondition/contract/documents/document/number)").evaluate(doc);
			Date in_form_date = Format.GetDate(xPath.compile("//deliveryCondition/contract/documents/document/date").evaluate(doc), "yyyy-MM-dd");
			Invoice invoice = new InvoiceFactoryFree().getByInDoc(in_form_date, in_form_number);
			if (invoice != null)
				return invoice;

			invoice = new Invoice();
			invoice.in_form_number = in_form_number;
			invoice.in_form_date = in_form_date;
			String unp = xPath.compile("//provider/unp").evaluate(doc);
			Contractor contractor = new ContractorFactory().GetByParam("unp", unp);
			if (contractor == null) {
				// <name>Индивидуальный предприниматель Пожидаева Ольга
				// Викторовна</name>
				// <address>231300, Гродненская, Лида, Ленинская, дом № 7,
				// корпус б, кв.45</address>

				// <deliveryCondition>
				// <contract>
				// <number>193</number>
				// <date>2014-09-03</date>
			}
			invoice.contractor = contractor;
			invoice.doc_contract = contractor.main_contract;
			invoice.DocDate = Format.GetDate(xPath.compile("//general/dateTransaction").evaluate(doc), "yyyy-MM-dd");

			NodeList nodeList = (NodeList) xPath.compile("//roster/rosterItem").evaluate(doc, XPathConstants.NODESET);
			for (int i = 0; i < nodeList.getLength(); i++) {

				Node node = nodeList.item(i);
				String productName = xPath.compile("name").evaluate(node);
				String unitsCode = xPath.compile("units").evaluate(node);
				double count = Double.parseDouble(xPath.compile("count").evaluate(node));
				double price = Double.parseDouble(xPath.compile("price").evaluate(node));
				double sum = Double.parseDouble(xPath.compile("cost").evaluate(node));
				double rateVat = Double.parseDouble(xPath.compile("vat/rate").evaluate(node));
				double sumVat = Double.parseDouble(xPath.compile("vat/summaVat").evaluate(node));
				double total = Double.parseDouble(xPath.compile("costVat").evaluate(node));
				StoreTP newTp = CreateTpRow(InvoiceTablePart.Product.class, productName, unitsCode, count, price, sum, rateVat, sumVat, total);

				invoice.TablePartProduct.add(newTp);
				// System.out.println(nodeList.item(i).getFirstChild().getNodeValue());
			}
			invoice = new InvoiceFactoryFree().Save(invoice);
			return invoice;

		} catch (Exception e) {
			log.ERROR("Load", e);
			Console.I().ERROR(ElDoc.class, "Load", e.getMessage());
		}
		return null;
	}

	public static <T> StoreTP CreateTpRow(Class<T> type, String productName, String unitsCode, double count, double price, double sum, double rateVat, double sumVat, double total)
			throws XPathExpressionException, NoSuchFieldException, Exception {
		StoreTP tp = new StoreTP();
		String name = productName;
		// Первую делаем заглавной
		name = name.substring(0, 1).toUpperCase() + name.substring(1);
		System.out.println(name);

		// Обрезаем Наименование
		String full_name = name;
		int length = Math.min(Product.class.getField(CatalogueBO.fields.NAME).getAnnotation(DatabaseField.class).width(), name.length());
		name = name.substring(0, length).trim();

		Product product = new ProductFactory().GetByParam(CatalogueBO.fields.FULL_NAME, full_name);
		// TODO: сравниваем если есть product

		Units units = new UnitsFactory().GetByCode(unitsCode);
		if (product == null) {
			product = new Product();
			product.name = name;
			product.full_name = full_name;
			product.units = units;
			product = new ProductFactory().Save(product);

			if (!name.equals(full_name)) {
				log.WARN("Load", "Обрезано 'Наименование' - " + product.code + " " + product.name);
				Console.I().WARN(ElDoc.class, "Load", "Обрезано 'Наименование' - " + product.code + " " + product.name);
			}
		}

		tp.product = product;
		tp.count = count;
		tp.price = price;
		tp.price_discount_off = tp.price;
		tp.sum = sum;
		tp.rateVat = rateVat;
		tp.sumVat = sumVat;
		tp.total = total;

		StoreTP newTp = (StoreTP) type.newInstance();
		((BO) tp).copyToObject(newTp);
		return newTp;
	}

	// public static void Save(Invoice invoice) {
	// // if (!Settings.canCreateEDoc())
	// // return;
	//
	// String exp = "/configs/markets/market";
	// // path = "invoice.xml";
	//
	// // Invoice invoice = new Invoice();
	// // invoice.contractor = new Contractor();
	// // invoice.TablePartProduct.add(new InvoiceTablePart.Product());
	// // invoice.TablePartProduct.add(new InvoiceTablePart.Product());
	//
	// try {
	// Document xmlDocument =
	// DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
	//
	// // .parse(path);
	//
	// // XPath xPath = XPathFactory.newInstance().newXPath();
	// // XPathExpression xPathExpression = xPath.compile(exp);
	// // NodeList nodes = (NodeList) xPathExpression.evaluate(xmlDocument,
	// // XPathConstants.NODESET);
	//
	// Document doc =
	// DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
	// doc.setXmlStandalone(false);
	// Element root = doc.createElement("issuance");
	// doc.appendChild(root);
	//
	// // update(doc, "issuance/general", "");
	// update(doc, "issuance/general/number", "590225275-2017-0000000155");
	// update(doc, "issuance/general/dateTransaction", "2017-06-14");
	// update(doc, "issuance/general/documentType", "ORIGINAL");
	//
	// // update(doc, "issuance/provider/providerStatus", "SELLER");
	// // update(doc, "issuance/provider/dependentPerson", false);
	// // update(doc, "issuance/provider/residentsOfOffshore", false);
	// // update(doc, "issuance/provider/specialDealGoods", false);
	// // update(doc, "issuance/provider/bigCompany", false);
	// // update(doc, "issuance/provider/countryCode", 112);
	// // update(doc, "issuance/provider/unp", 590225275);
	// // update(doc, "issuance/provider/branchCode", "");
	// // update(doc, "issuance/provider/name", "Индивидуальный
	// // предприниматель Пожидаева Ольга Викторовна");
	// // update(doc, "issuance/provider/address",
	// // "231300, Гродненская, Лида, Ленинская, дом № 7, корпус б,
	// // кв.45");
	//
	// XModifier modifier = new XModifier(doc);
	// int number = 1;
	// for (TablePartItem item : invoice.TablePartProduct) {
	// InvoiceTablePart.Product tp = (InvoiceTablePart.Product) item;
	// String itemPath = "issuance/roster/rosterItem[" + number + "]/";
	// modifier.addModify(itemPath + "number", "" + number++);
	// modifier.addModify(itemPath + "name", tp.product.name);
	// modifier.addModify(itemPath + "units", tp.product.units.code);
	// modifier.addModify(itemPath + "count", "" + tp.count);
	// modifier.addModify(itemPath + "price", "" + tp.price);
	// modifier.addModify(itemPath + "cost", "" + tp.sum);
	// modifier.addModify(itemPath + "summaExcise", "" + 0);
	// modifier.addModify(itemPath + "vat/rate", "" + tp.rateVat);
	// modifier.addModify(itemPath + "vat/rateType", "DECIMAL");
	// modifier.addModify(itemPath + "vat/summaVat", "" + tp.sumVat);
	// modifier.addModify(itemPath + "costVat", "" + tp.total);
	// }
	// modifier.modify();
	// DOMSource domSource = new DOMSource(doc);
	//
	// // FileOutputStream out = new FileOutputStream("test.xml");
	// // Writer out = new OutputStreamWriter(new
	// // FileOutputStream("test.xml"), "UTF8");
	// Writer out = new OutputStreamWriter(System.out, "UTF8");
	//
	// Transformer transformer =
	// TransformerFactory.newInstance().newTransformer();
	// transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	// transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	// transformer.transform(domSource, new StreamResult(out));
	//
	// out.close();
	//
	// // printXmlDocument(newXmlDocument);
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// }
	// }

	public static void update(Document doc, String path, Object def) {
		String p[] = path.split("/");
		// search nodes or create them if they do not exist
		Node n = doc;
		for (int i = 0; i < p.length; i++) {
			NodeList kids = n.getChildNodes();
			Node nfound = null;
			for (int j = 0; j < kids.getLength(); j++)
				if (kids.item(j).getNodeName().equals(p[i])) {
					nfound = kids.item(j);
					break;
				}
			if (nfound == null) {
				nfound = doc.createElement(p[i]);
				n.appendChild(nfound);
				n.appendChild(doc.createTextNode("\n")); // add whitespace, so
															// the result looks
															// nicer. Not really
															// needed
			}
			n = nfound;
		}
		NodeList kids = n.getChildNodes();
		for (int i = 0; i < kids.getLength(); i++)
			if (kids.item(i).getNodeType() == Node.TEXT_NODE) {
				// text node exists
				kids.item(i).setNodeValue("" + def); // override
				return;
			}

		n.appendChild(doc.createTextNode("" + def));
	}

	public static void printXmlDocument(Document document) {
		DOMImplementationLS domImplementationLS = (DOMImplementationLS) document.getImplementation();
		LSSerializer lsSerializer = domImplementationLS.createLSSerializer();
		String string = lsSerializer.writeToString(document);
		System.out.println(string);
	}

}