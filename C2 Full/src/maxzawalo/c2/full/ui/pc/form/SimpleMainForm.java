package maxzawalo.c2.full.ui.pc.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.ui.pc.form.BoListForm;
import maxzawalo.c2.base.utils.Global;
import maxzawalo.c2.base.utils.Logger;
import maxzawalo.c2.free.ui.pc.form.AboutFormFree;
import maxzawalo.c2.full.ui.pc.document.BillListFormFull;
import maxzawalo.c2.full.ui.pc.document.CashVoucherListForm;
import maxzawalo.c2.full.ui.pc.document.DeliveryNoteListFormFull;
import maxzawalo.c2.full.ui.pc.document.InvoiceListFormFull;
import maxzawalo.c2.full.ui.pc.view.PrintPrice;

public class SimpleMainForm extends JFrame {
	protected Logger log = Logger.getLogger(this.getClass().getSimpleName());

	public SimpleMainForm() {
		System.out.println("license");
		UI.SET(this);
		setTitle("C2 " + Global.VERSION);
		setBounds(0, 0, 609, 159);

		JButton button_1 = new JButton("Счета на оплату");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BoListForm form = new BillListFormFull();
				form.Search();
				form.setVisible(true);
			}
		});
		getContentPane().setLayout(null);
		button_1.setBounds(10, 11, 150, 23);
		getContentPane().add(button_1);

		JButton button_3 = new JButton("Приходная накладная");
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BoListForm form = new InvoiceListFormFull();
				form.Search();
				form.setVisible(true);
			}
		});
		button_3.setBounds(206, 11, 150, 23);
		getContentPane().add(button_3);

		JButton about = new JButton("?");
		about.setToolTipText("О программе");
		about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				AboutFormFree form = new AboutFormFree();
				form.setVisible(true);
			}
		});
		about.setBounds(543, 11, 40, 23);
		getContentPane().add(about);

		JButton button = new JButton("Расходная накладная");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BoListForm form = new DeliveryNoteListFormFull();
				form.Search();
				form.setVisible(true);
			}
		});
		button.setBounds(10, 45, 150, 23);
		getContentPane().add(button);

		JButton btnPrint = new JButton("Печать ценников");
		btnPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PrintPrice form = new PrintPrice();
				form.setVisible(true);
			}
		});
		btnPrint.setBounds(378, 45, 150, 23);
		getContentPane().add(btnPrint);

		JButton button_2 = new JButton("Чеки");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BoListForm form = new CashVoucherListForm();
				form.Search();
				form.setVisible(true);
			}
		});
		button_2.setBounds(10, 87, 150, 23);
		getContentPane().add(button_2);
	}
}