package maxzawalo.c2.full.ui.pc.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.os.Run;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.base.ui.pc.controls.ComboBoxBizControl;
import maxzawalo.c2.free.bo.Product;
import maxzawalo.c2.free.bo.Store;
import maxzawalo.c2.full.ui.pc.Main;

public class RemainingStockReportForm extends JFrame {
	public RemainingStockReportForm() {
		setTitle("Остатки по группам");
		final BizControlBase parentControl = new BizControlBase();
		parentControl.fieldType = Product.class;
		parentControl.selectGroupOnly = true;
		parentControl.setFieldName("parent");
		parentControl.setCaption("Группа");
		parentControl.setBounds(12, 85, 589, 56);
		parentControl.setBo(new Product());
		getContentPane().add(parentControl);
		setBounds(0, 0, 623, 240);
		getContentPane().setLayout(null);

		BizControlBase storeControl = new ComboBoxBizControl();
		storeControl.fieldType = Store.class;
		storeControl.LoadList();
		storeControl.setBo(null);
		// store.setFieldName("store");
		storeControl.setCaption("Склад");
		storeControl.setBounds(12, 12, 314, 56);
		getContentPane().add(storeControl);

		JButton button = new JButton("Сформировать");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				BO parent = (BO) parentControl.getBO();
				Store store = (Store) storeControl.getBO();
				Run.OpenFile(Main.httpServer.GetRootUrl() + "report/RemainingStockView?store="
						+ (store == null ? 0 : store.id) + "&parent=" + (parent == null ? 0 : parent.id));
			}
		});
		button.setBounds(206, 152, 143, 34);
		getContentPane().add(button);
	}
}