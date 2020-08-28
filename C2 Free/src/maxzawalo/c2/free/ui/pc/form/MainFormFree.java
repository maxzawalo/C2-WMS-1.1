package maxzawalo.c2.free.ui.pc.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.ui.pc.form.BoListForm;
import maxzawalo.c2.base.utils.Global;
import maxzawalo.c2.base.utils.Logger;
import maxzawalo.c2.free.ui.pc.document.store.BillListFormFree;
import maxzawalo.c2.free.ui.pc.document.store.DeliveryNoteListFormFree;
import maxzawalo.c2.free.ui.pc.document.store.InvoiceListFormFree;

public class MainFormFree extends JFrame {
	protected Logger log = Logger.getLogger(this.getClass().getSimpleName());

	public MainFormFree() {
		UI.SET(this);
		setTitle("C2 " + Global.VERSION);
		setBounds(0, 0, 478, 133);

		JButton button_1 = new JButton("Счет");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BoListForm form = new BillListFormFree();
				form.Search();
				form.setVisible(true);
			}
		});
		getContentPane().setLayout(null);
		button_1.setBounds(10, 11, 124, 23);
		getContentPane().add(button_1);

		JButton button_3 = new JButton("Приходная");
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BoListForm form = new InvoiceListFormFree();
				form.Search();
				form.setVisible(true);
			}
		});
		button_3.setBounds(170, 11, 150, 23);
		getContentPane().add(button_3);

		JButton about = new JButton("?");
		about.setToolTipText("О программе");
		about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				AboutFormFree form = new AboutFormFree();
				form.setVisible(true);
			}
		});
		about.setBounds(410, 11, 40, 23);
		getContentPane().add(about);
		
		JButton button = new JButton("Расходная");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BoListForm form = new DeliveryNoteListFormFree();
				form.Search();
				form.setVisible(true);
			}
		});
		button.setBounds(10, 56, 124, 23);
		getContentPane().add(button);
	}
}