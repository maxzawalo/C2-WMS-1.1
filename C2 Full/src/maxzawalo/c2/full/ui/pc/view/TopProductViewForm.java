package maxzawalo.c2.full.ui.pc.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;

import maxzawalo.c2.base.os.Run;
import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.base.ui.pc.controls.ComboBoxBizControl;
import maxzawalo.c2.base.ui.pc.controls.DateBizControl;
import maxzawalo.c2.free.bo.Store;
import maxzawalo.c2.full.ui.pc.Main;

public class TopProductViewForm extends JFrame {
	BizControlBase fromDate;
	BizControlBase toDate;
	BizControlBase storeControl;
	JCheckBox byPrice;

	public TopProductViewForm() {
		setBounds(0, 0, 341, 254);
		getContentPane().setLayout(null);
		setTitle("Топ продаж за период");

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

		storeControl = new ComboBoxBizControl();
		storeControl.fieldType = Store.class;
		storeControl.LoadList();
		storeControl.setBo(null);
		// store.setFieldName("store");
		storeControl.setCaption("Склад");
		storeControl.setBounds(0, 57, 328, 56);
		getContentPane().add(storeControl);

		JButton button = new JButton("Сформировать");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Print();
			}
		});
		button.setBounds(82, 176, 137, 29);
		getContentPane().add(button);

		byPrice = new JCheckBox("По цене");
		byPrice.setBounds(10, 122, 131, 29);
		getContentPane().add(byPrice);
	}

	public void Print() {
		UI.Start(this);
		Store store = (Store) storeControl.getBO();

		if (byPrice.isSelected())
			Run.OpenFile(
					Main.httpServer.GetRootUrl() + "report/TopProductByPrice?fromDate=" + fromDate.getDate().getTime()
							+ "&toDate=" + toDate.getDate().getTime() + "&store=" + (store == null ? 0 : store.id));
		else
			Run.OpenFile(Main.httpServer.GetRootUrl() + "report/TopProduct?fromDate=" + fromDate.getDate().getTime()
					+ "&toDate=" + toDate.getDate().getTime() + "&store=" + (store == null ? 0 : store.id));
		UI.Stop(this);
	}
}