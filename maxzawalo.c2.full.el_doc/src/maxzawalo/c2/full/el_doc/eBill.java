package maxzawalo.c2.full.el_doc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import maxzawalo.c2.base.utils.FileUtils;
import maxzawalo.c2.base.utils.Format;

public class eBill {
	public String unp = "";
	public Date date;
	public double sum = 0;
	public double sumVat = 0;
	public String number = "";

	@Override
	public String toString() {
		return unp + "\t" + Format.Show(date) + "\t" + sum + "\t" + sumVat + "\t" + number;
	}

	public static void Checker() {
		CheckDoc("ПоступлениеТоваровУслуг");
		CheckDoc("РеализацияТоваровУслуг");
	}

	protected static void CheckDoc(String docName) {
		List<eBill> all1c = new ArrayList<>();
		List<eBill> allPortal = new ArrayList<>();

		String[] lines1c = FileUtils.readFileAsString(FileUtils.getAppDir() + docName + ".csv").split("[\\r\\n]+");
		for (String line : lines1c) {
			String[] params = line.split(";");
			eBill b = new eBill();
			b.date = Format.GetDate(params[0].trim());
			b.unp = params[1].trim();
			b.number = params[4].trim();
			b.sum = Double.parseDouble(params[2].replace(" ", "").replace("" + (char) 160, "").replace(",", "."));
			b.sumVat = Double.parseDouble(params[3].replace(" ", "").replace("" + (char) 160, "").replace(",", "."));
			all1c.add(b);
		}

		String[] linesPortal = FileUtils.readFileAsString(FileUtils.getAppDir() + docName + "Портал.csv")
				.split("[\\r\\n]+");

		for (String line : linesPortal) {
			String[] params = line.split(";");
			eBill b = new eBill();
			b.date = Format.GetDate(params[4].trim());
			b.unp = params[0].trim();
			b.number = params[3].trim();
			b.sum = Double.parseDouble(params[6].replace(" ", "").replace(",", "."));
			b.sumVat = Double.parseDouble(params[7].replace(" ", "").replace(",", "."));
			allPortal.add(b);
		}

		List<eBill> foundPortal = new ArrayList<>();
		List<eBill> found1c = new ArrayList<>();
		for (eBill bill1c : all1c) {
			for (eBill billPortal : allPortal) {
				if (bill1c.date.equals(billPortal.date) && bill1c.unp.equals(billPortal.unp)
						&& bill1c.sum == billPortal.sum && bill1c.sumVat == billPortal.sumVat) {
					foundPortal.add(billPortal);
					found1c.add(bill1c);
					break;
				}
			}
			// System.out.println("");
		}
		for (eBill found : foundPortal)
			allPortal.remove(found);

		for (eBill found : found1c)
			all1c.remove(found);

		String diff = "";
		for (eBill bill : allPortal) {
			diff += bill + "\r\n";
			System.out.println(bill);
		}

		FileUtils.Text2File(FileUtils.getAppDir() + docName + "ОтличияПортал.txt", diff, false);

		diff = "";
		for (eBill bill : all1c) {
			diff += bill + "\r\n";
			System.out.println(bill);
		}

		FileUtils.Text2File(FileUtils.getAppDir() + docName + "Отличия1с.txt", diff, false);
	}
}