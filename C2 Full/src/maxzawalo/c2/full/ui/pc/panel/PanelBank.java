package maxzawalo.c2.full.ui.pc.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.base.utils.Logger;
import maxzawalo.c2.free.ui.pc.document.bank.ReceiptMoneyListForm;
import maxzawalo.c2.free.ui.pc.document.bank.WriteOffMoneyListForm;

public class PanelBank extends JPanel {

	Logger log = Logger.getLogger(PanelBank.class);

	public PanelBank() {
		setLayout(null);

		JButton button_30 = new JButton("Поступление на расчетный счет");
		button_30.setToolTipText(button_30.getText());
		button_30.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ReceiptMoneyListForm form = new ReceiptMoneyListForm();
				form.Search();
				form.setVisible(true);
			}
		});
		button_30.setBounds(191, 25, 150, 23);
		add(button_30);

		JButton writeOffMoney = new JButton("Списание с расчетного счета");
		writeOffMoney.setToolTipText(writeOffMoney.getText());
		writeOffMoney.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WriteOffMoneyListForm form = new WriteOffMoneyListForm();
				form.Search();
				form.setVisible(true);
			}
		});
		writeOffMoney.setBounds(10, 25, 150, 23);
		add(writeOffMoney);

		JButton button = new JButton("Загрузка из файла");
		if (User.current.isAdmin()) {
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					LoadBankSelector selector = new LoadBankSelector();
					selector.setVisible(true);
				}
			});
		}
		button.setToolTipText(button.getText());
		button.setBounds(383, 25, 150, 23);
		add(button);
	}
}