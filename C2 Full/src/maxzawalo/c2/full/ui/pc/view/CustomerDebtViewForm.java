package maxzawalo.c2.full.ui.pc.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;

import maxzawalo.c2.base.os.Run;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.base.ui.pc.controls.DateBizControl;
import maxzawalo.c2.full.ui.pc.Main;

public class CustomerDebtViewForm extends JFrame {

	BizControlBase toDateCtrl;

	public CustomerDebtViewForm() {
		setBounds(0, 0, 405, 153);
		getContentPane().setLayout(null);
		setTitle("Задолженность покупателей по срокам долга");

		toDateCtrl = new DateBizControl();
		toDateCtrl.setCaption("Дата");
		toDateCtrl.setBounds(115, 0, 164, 56);
		toDateCtrl.onBOSelected(new Date());
		getContentPane().add(toDateCtrl);

		JButton button = new JButton("Сформировать");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Print();
			}
		});
		button.setBounds(125, 67, 137, 29);
		getContentPane().add(button);
	}

	public void Print() {
		Date toDate = toDateCtrl.getDate();
		Run.OpenFile(Main.httpServer.GetRootUrl() + "report/CustomerDebtView?toDate=" + toDate.getTime());
	}
}