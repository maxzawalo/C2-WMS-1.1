package maxzawalo.c2.full.ui.pc.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JRadioButton;

import maxzawalo.c2.base.os.Run;
import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.base.ui.pc.controls.DateBizControl;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.full.ui.pc.Main;

public class ProfitReportForm extends JFrame {
	BizControlBase fromDate;
	BizControlBase toDate;
	private JRadioButton radioButton;

	public ProfitReportForm() {
		setBounds(0, 0, 341, 203);
		getContentPane().setLayout(null);
		setTitle("Прибыль планируемая");

		fromDate = new DateBizControl();
		fromDate.setCaption("C");
		fromDate.setBounds(0, 0, 164, 56);
		fromDate.onBOSelected(Format.AddDay(new Date(), -30));
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

		isShipment = new JRadioButton("По отгрузке");
		isShipment.setBounds(0, 67, 149, 29);
		isShipment.setSelected(true);
		getContentPane().add(isShipment);

		isCashe = new JRadioButton("По кассе");
		isCashe.setBounds(174, 67, 149, 29);
		getContentPane().add(isCashe);

		ButtonGroup btnGroup = new ButtonGroup();
		btnGroup.add(isShipment);
		btnGroup.add(isCashe);
	}

	JRadioButton isShipment;
	JRadioButton isCashe;

	public void Print() {
		UI.Start(this);
		if (isShipment.isSelected())
			Run.OpenFile(Main.httpServer.GetRootUrl() + "report/ProfitReport?fromDate=" + fromDate.getDate().getTime()
					+ "&toDate=" + toDate.getDate().getTime());
		else
			Run.OpenFile(Main.httpServer.GetRootUrl() + "report/ProfitReportCache?fromDate="
					+ fromDate.getDate().getTime() + "&toDate=" + toDate.getDate().getTime());
		UI.Stop(this);
	}
}