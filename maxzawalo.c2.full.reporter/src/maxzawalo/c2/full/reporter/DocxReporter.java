package maxzawalo.c2.full.reporter;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import maxzawalo.c2.base.utils.Logger;

public class DocxReporter<T> {
	Logger log = Logger.getLogger(getClass());
	public String templatePath = "/maxzawalo/c2/full/report/tpl/";

	protected Map<String, String> GetReplacements(T bo) {
		return new HashMap<>();
	}

	public void Print(String reportPath, T bo) {
		try {
			// String reportPath = FileUtils.GetReportDir() + "Contract_" +
			// System.currentTimeMillis() + ".docx";

			// Map<String, String> replacements = new HashMap<>();
			// replacements.put("[ПокупательПолноеИмя]", "ПОКУПАТЕЛЬ ПОКУПАТЕЛЬ
			// ПОКУПАТЕЛЬ ПОКУПАТЕЛЬ");
			// replacements.put("[АдресПокупателя]", "Деревня мухосранка");

			WordReplaceText instance = new WordReplaceText();
			XWPFDocument doc = instance.openDocument(templatePath);// "/maxzawalo/c2/full/report/tpl/ДоговорПоставки.docx");

			if (doc != null) {
				doc = instance.replaceText(doc, GetReplacements(bo));
				instance.saveDocument(doc, reportPath);
				// Run.OpenFile(reportPath);
			}
		} catch (Exception e) {
			log.ERROR("Print", e);
		}
	}
}