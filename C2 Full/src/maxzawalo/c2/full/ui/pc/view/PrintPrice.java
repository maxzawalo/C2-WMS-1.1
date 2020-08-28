package maxzawalo.c2.full.ui.pc.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import maxzawalo.c2.base.os.Run;
import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.utils.FileUtils;
import maxzawalo.c2.free.bo.Price;
import maxzawalo.c2.free.bo.PriceState;
import maxzawalo.c2.free.data.factory.catalogue.PriceFactory;
import maxzawalo.c2.full.report.code.PriceReporter;
import maxzawalo.c2.full.reporter.Xlsx;

public class PrintPrice extends JFrame {

	public PrintPrice() {
		getContentPane().setLayout(null);
		setBounds(0, 0, 154, 157);

		JButton button = new JButton("Печать");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UI.Start(PrintPrice.this);

				PriceFactory factory = new PriceFactory();

				List<Price> all = factory.GetByState(PriceState.Новый);
				if (all.size() == 0) {
					JOptionPane.showMessageDialog(PrintPrice.this, "Нет новых ценников.", "Печать ценников",
							JOptionPane.WARNING_MESSAGE);
				} else {
					// all = all.subList(all.size() - 10, all.size() - 1);

					String filename = FileUtils.GetReportDir() + "Price_" + System.currentTimeMillis() + ".xlsx";
					Xlsx.PrintMatrix(filename, all, new PriceReporter(), 3, 9);
					for (Price price : all)
						factory.UpdateState(price, PriceState.Напечатан);
					Run.OpenFile(filename);
				}
				UI.Stop(PrintPrice.this);
			}
		});
		button.setBounds(10, 75, 115, 29);
		getContentPane().add(button);

	}
}
