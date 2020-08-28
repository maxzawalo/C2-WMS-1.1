package maxzawalo.c2.full.ui.pc.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import maxzawalo.c2.base.os.Run;
import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.base.ui.pc.controls.DateBizControl;
import maxzawalo.c2.full.ui.pc.Main;

public class OldProductForm extends JFrame {
	BizControlBase minDays;

	public OldProductForm() {
		setBounds(0, 0, 232, 147);
		getContentPane().setLayout(null);
		setTitle("Залежалый товар");

		minDays = new DateBizControl();
		minDays.setFieldType(Integer.class);
		minDays.setCaption("Дней лежания (min)");
		minDays.setBounds(0, 0, 203, 56);
		minDays.onBOSelected(90);
		getContentPane().add(minDays);

		JButton button = new JButton("Сформировать");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Print();
			}
		});
		button.setBounds(66, 67, 137, 29);
		getContentPane().add(button);
	}

	public void Print() {
		UI.Start(this);
		int days = minDays.getInteger();
		Run.OpenFile(Main.httpServer.GetRootUrl() + "report/OldProductView?days=" + days);
		UI.Stop(this);
	}
}