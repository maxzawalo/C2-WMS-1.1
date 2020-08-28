package maxzawalo.c2.full.ui.pc.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.os.Run;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.base.ui.pc.controls.DateBizControl;
import maxzawalo.c2.base.utils.FileUtils;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.full.bo.view.DocCommitView;
import maxzawalo.c2.full.data.factory.view.DocCommitViewFactory;

public class DocCommitViewForm extends JFrame {

	BizControlBase fromDate;
	BizControlBase toDate;
	BizControlBase hourCount;

	public DocCommitViewForm() {
		setBounds(0, 0, 341, 257);
		getContentPane().setLayout(null);
		setTitle("Документы измененные");

		fromDate = new DateBizControl();
		fromDate.setCaption("C");
		fromDate.setBounds(0, 0, 164, 56);
		fromDate.onBOSelected(new Date());
		getContentPane().add(fromDate);

		toDate = new DateBizControl();
		toDate.setCaption("по");
		toDate.setBounds(164, 0, 164, 56);
		toDate.onBOSelected(new Date());
		getContentPane().add(toDate);

		JButton button = new JButton("Сформировать");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Print();
			}
		});
		button.setBounds(82, 173, 137, 29);
		getContentPane().add(button);

		hourCount = new  BizControlBase();
		hourCount.setCaption("Разница, Ч");
		hourCount.setBounds(0, 68, 128, 56);
		hourCount.setText("1");
		getContentPane().add(hourCount);
	}

	public void Print() {
		int count = Integer.parseInt(hourCount.getText());
		List<DocCommitView> list = new DocCommitViewFactory().get(fromDate.getDate(), toDate.getDate(), count);
		for (DocCommitView item : list)
			System.out.println(item);
		String title = "Документы измененные > " + count + "ч после создания";
		String data = "<title>" + title + "</title>";
		data += "<h1>" + title + "</h1>";
		data += "<h3>с " + Format.Show(fromDate.getDate()) + " по " + Format.Show(toDate.getDate()) + "</h3>";
		data += "<style>" + "table {\r\n" + "border:solid 1px black;\r\n" + "border-collapse: collapse;}\r\n";
		data += "td {\r\n" + "border:solid 1px black;\r\n" + "border-collapse: collapse;}\r\n";
		data += "th {\r\n" + "border:solid 1px black;\r\n" + "border-collapse: collapse; font-weight: bold;}\r\n";
		data += "</style>";
		data += "<table>";
		data += "<tr>";
		for (Field f : DocCommitView.class.getFields()) {
			data += "<th>";
			try {
				data += "" + f.getAnnotation(BoField.class).caption();
			} catch (Exception e) {
				e.printStackTrace();
			}
			data += "</th>";
		}
		data += "</tr>";

		String filename = FileUtils.GetReportDir() + "DocCommitView_" + System.currentTimeMillis() + ".html";

		StringBuilder builder = new StringBuilder();
		builder.append(data);

		for (DocCommitView item : list) {
			builder.append("<tr>");
			for (Field f : DocCommitView.class.getFields()) {
				builder.append("<td>");
				try {
					builder.append(f.get(item));
				} catch (Exception e) {
					e.printStackTrace();
				}
				builder.append("</td>");
			}
			builder.append("</tr>");
		}
		builder.append("</table>");

		FileUtils.Text2File(filename, builder.toString(), false);
		Run.OpenFile(filename);
	}
}