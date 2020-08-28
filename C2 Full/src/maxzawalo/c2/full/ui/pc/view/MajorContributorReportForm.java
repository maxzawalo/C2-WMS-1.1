package maxzawalo.c2.full.ui.pc.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;

import maxzawalo.c2.base.os.Run;
import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.base.ui.pc.controls.DateBizControl;
import maxzawalo.c2.full.ui.pc.Main;

public class MajorContributorReportForm extends JFrame {
	BizControlBase fromDate;
	BizControlBase toDate;

	public MajorContributorReportForm() {
		setBounds(0, 0, 341, 203);
		getContentPane().setLayout(null);
		setTitle("Крупнейшие плательщики");

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
		button.setBounds(87, 124, 137, 29);
		getContentPane().add(button);
	}

	public void Print() {
		UI.Start(this);
		Run.OpenFile(Main.httpServer.GetRootUrl() + "report/MajorContributorReport?fromDate="
				+ fromDate.getDate().getTime() + "&toDate=" + toDate.getDate().getTime());

		UI.Stop(this);
	}
}