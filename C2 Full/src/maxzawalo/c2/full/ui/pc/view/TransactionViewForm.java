package maxzawalo.c2.full.ui.pc.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;

import maxzawalo.c2.base.os.Run;
import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.base.ui.pc.controls.ComboBoxBizControl;
import maxzawalo.c2.base.ui.pc.controls.DateBizControl;
import maxzawalo.c2.free.bo.Product;
import maxzawalo.c2.free.bo.Store;
import maxzawalo.c2.full.ui.pc.Main;

public class TransactionViewForm extends JFrame {
	BizControlBase fromDate;
	BizControlBase toDate;
	public BizControlBase product;
	JCheckBox isGroup;
	BizControlBase storeControl;

	public TransactionViewForm() {
		setBounds(0, 0, 341, 276);
		getContentPane().setLayout(null);
		setTitle("Проводки/движение");

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
		button.setBounds(87, 205, 137, 29);
		getContentPane().add(button);

		isGroup = new JCheckBox("Группа");
		// TODO: в бизконтрол
		isGroup.setBounds(240, 125, 85, 29);
		isGroup.setRolloverEnabled(false);
		isGroup.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				product.selectGroupOnly = isGroup.isSelected();
			}
		});
		getContentPane().add(isGroup);

		product = new BizControlBase();
		product.setFieldType(Product.class);
		product.setBo(new Product());
		product.setCaption("Номенклатура");
		product.setBounds(0, 138, 328, 56);
		getContentPane().add(product);
	}

	public void Print() {
		UI.Start(this);
		Store store = (Store) storeControl.getBO();
		Product p = (Product) product.getBO();
		String searchData = product.getText();

		Run.OpenFile(Main.httpServer.GetRootUrl() + "report/TransactionView?fromDate=" + fromDate.getDate().getTime()
				+ "&toDate=" + toDate.getDate().getTime() + "&store=" + (store == null ? 0 : store.id) + "&product="
				+ (p == null ? 0 : p.id) + "&searchData=" + searchData);

		UI.Stop(this);
	}
}