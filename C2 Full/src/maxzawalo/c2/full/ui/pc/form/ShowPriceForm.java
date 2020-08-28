package maxzawalo.c2.full.ui.pc.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.free.bo.Price;
import maxzawalo.c2.free.data.factory.catalogue.PriceFactory;

public class ShowPriceForm extends JFrame {

	JTextArea consoleArea;
	BizControlBase code;

	public ShowPriceForm() {
		setTitle("Найти ценник по коду");

		setBounds(0, 0, 984, 446);
		getContentPane().setLayout(null);

		code = new  BizControlBase();
		code.setCaption("Код ценника");
		code.setBounds(808, 13, 164, 56);
		getContentPane().add(code);
		code.enterPressed.add(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Search();
			}
		});

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 125, 948, 271);
		getContentPane().add(scrollPane);

		consoleArea = new JTextArea();
		scrollPane.setViewportView(consoleArea);

		JButton button = new JButton("Найти");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Search();
			}
		});
		button.setBounds(829, 80, 131, 34);
		getContentPane().add(button);
	}

	protected void Search() {
		consoleArea.setText("");
		List<Price> all = new PriceFactory().GetAllContains(BO.fields.CODE, code.getText());

		if (all.size() == 0)
			consoleArea.setText("Ничего не найдено");
		else {
			for (Price p : all)
				consoleArea.append(p.code + "|" + p.product.name + "|" + p.price + "|" + p.total + "|"
						+ p.invoice.contractor + "\n");
		}
		// log.CONSOLE(message);
	}
}