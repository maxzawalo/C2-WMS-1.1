package maxzawalo.c2.full.ui.pc.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import maxzawalo.c2.full.ui.pc.view.CustomerDebtViewForm;
import maxzawalo.c2.full.ui.pc.view.MajorContributorReportForm;
import maxzawalo.c2.full.ui.pc.view.OldProductForm;
import maxzawalo.c2.full.ui.pc.view.ProfitReportForm;
import maxzawalo.c2.full.ui.pc.view.TopProductViewForm;

public class PanelDirector extends JPanel {

	public PanelDirector() {
		setLayout(null);
		JButton btnOldProduct = new JButton("Залежалый товар");
		btnOldProduct.setToolTipText(btnOldProduct.getText());
		btnOldProduct.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OldProductForm form = new OldProductForm();
				form.setVisible(true);
			}
		});
		btnOldProduct.setBounds(15, 16, 150, 23);
		add(btnOldProduct);

		JButton btnCustomerDebt = new JButton("Задолженность покупателей");
		btnCustomerDebt.setToolTipText(btnCustomerDebt.getText());
		btnCustomerDebt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				CustomerDebtViewForm form = new CustomerDebtViewForm();
				form.setVisible(true);
			}
		});
		btnCustomerDebt.setBounds(15, 60, 150, 23);
		add(btnCustomerDebt);

		JButton button = new JButton("Топ продаж за период");
		button.setToolTipText(button.getText());
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TopProductViewForm form = new TopProductViewForm();
				form.setVisible(true);
			}
		});
		button.setBounds(15, 109, 150, 23);
		add(button);

		JButton button_1 = new JButton("Крупнейшие плательщики");
		button_1.setToolTipText(button_1.getText());
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MajorContributorReportForm form = new MajorContributorReportForm();
				form.setVisible(true);
			}
		});
		button_1.setBounds(15, 159, 150, 23);
		add(button_1);

		JButton btnProfitReport = new JButton("Прибыль планируемая");
		btnProfitReport.setToolTipText(btnProfitReport.getText());
		btnProfitReport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ProfitReportForm form = new ProfitReportForm();
				form.setVisible(true);
			}
		});
		btnProfitReport.setBounds(15, 209, 150, 23);
		add(btnProfitReport);
	}
}