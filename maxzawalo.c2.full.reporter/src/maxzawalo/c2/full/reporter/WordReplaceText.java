package maxzawalo.c2.full.reporter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.xwpf.usermodel.PositionInParagraph;
import org.apache.poi.xwpf.usermodel.TextSegement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

public class WordReplaceText {
	public XWPFDocument replaceText(XWPFDocument doc, Map<String, String> replacements) {

		replaceInParagraphs(replacements, doc.getParagraphs());

		List<XWPFTable> tables = doc.getTables();
		for (XWPFTable xwpfTable : tables) {
			List<XWPFTableRow> tableRows = xwpfTable.getRows();
			for (XWPFTableRow xwpfTableRow : tableRows) {
				List<XWPFTableCell> tableCells = xwpfTableRow.getTableCells();
				for (XWPFTableCell xwpfTableCell : tableCells) {
					List<XWPFParagraph> xwpfParagraphs = xwpfTableCell.getParagraphs();
					replaceInParagraphs(replacements, xwpfParagraphs);
				}
			}
		}
		return doc;
	}

	private long replaceInParagraphs(Map<String, String> replacements, List<XWPFParagraph> xwpfParagraphs) {
		long count = 0;
		for (XWPFParagraph paragraph : xwpfParagraphs) {
			List<XWPFRun> runs = paragraph.getRuns();

			for (Map.Entry<String, String> replPair : replacements.entrySet()) {
				String find = replPair.getKey();
				String repl = replPair.getValue();
				TextSegement found = paragraph.searchText(find, new PositionInParagraph());
				if (found != null) {
					count++;
					if (found.getBeginRun() == found.getEndRun()) {
						// whole search string is in one Run
						XWPFRun run = runs.get(found.getBeginRun());
						String runText = run.getText(run.getTextPosition());
						String replaced = runText.replace(find, repl);
						run.setText(replaced, 0);
					} else {
						// The search string spans over more than one Run
						// Put the Strings together
						StringBuilder b = new StringBuilder();
						for (int runPos = found.getBeginRun(); runPos <= found.getEndRun(); runPos++) {
							XWPFRun run = runs.get(runPos);
							b.append(run.getText(run.getTextPosition()));
						}
						String connectedRuns = b.toString();
						String replaced = connectedRuns.replace(find, repl);

						// The first Run receives the replaced String of all
						// connected Runs
						XWPFRun partOne = runs.get(found.getBeginRun());
						partOne.setText(replaced, 0);
						// Removing the text in the other Runs.
						for (int runPos = found.getBeginRun() + 1; runPos <= found.getEndRun(); runPos++) {
							XWPFRun partNext = runs.get(runPos);
							partNext.setText("", 0);
						}
					}
				}
			}
		}
		return count;
	}

	public XWPFDocument openDocument(String file) throws Exception {
		InputStream in = Xlsx.class.getClass().getResourceAsStream(file);
		XWPFDocument document = new XWPFDocument(in);
		return document;
	}

	public void saveDocument(XWPFDocument doc, String file) {
		try (FileOutputStream out = new FileOutputStream(file)) {
			doc.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}