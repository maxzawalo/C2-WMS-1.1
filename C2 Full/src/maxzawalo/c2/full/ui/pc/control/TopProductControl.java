package maxzawalo.c2.full.ui.pc.control;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import javafx.application.Platform;
import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.ui.pc.controls.SwingLink;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.free.bo.Product;
import maxzawalo.c2.free.data.factory.catalogue.ProductFactory;
import maxzawalo.c2.free.reporter.HtmlReporter;
import maxzawalo.c2.full.analitics.TopProduct;
import maxzawalo.c2.full.bo.view.TransactionView;
import maxzawalo.c2.full.data.factory.view.TransactionViewFactory;

public class TopProductControl extends JPanel {

	JLabel caption;

	public TopProductControl() {
		setBounds(0, 0, 282, 200);
		setLayout(null);

		caption = new JLabel("Топ 20 товаров");
		caption.setBounds(78, 10, 153, 20);
		add(caption);
		caption.setFont(caption.getFont().deriveFont(20.0f));

	}

	public void setData(final List<TopProduct> all) {
		Platform.runLater(new Runnable() {
			public void run() {

				List<JLabel> labels = new ArrayList<>();
				for (int j = 0; j < TopProductControl.this.getComponentCount(); j++) {
					Component co = TopProductControl.this.getComponent(j);
					if (co instanceof JLabel && co != caption)
						labels.add((JLabel) co);
				}

				for (JLabel l : labels) {
					remove(l);
				}

				int startY = 50;

				for (int pos = 0; pos < 20 & pos < all.size(); pos++) {
					int y = startY + pos * 25;

					TopProduct product = all.get(pos);

					SwingLink name = new SwingLink();
					name.setFont(name.getFont().deriveFont(14.0f));
					name.setBounds(0, y, 200, 20);
					name.setup(product.name);
					name.onClick = new AbstractAction() {
						public void actionPerformed(final ActionEvent evt) {
							// BoForm form = new ProductForm();
							// form.Load((int) getValue(BO.fields.ID));
							// form.setVisible(true);

							Product product = new ProductFactory().GetById((int) getValue(BO.fields.ID));
							HtmlReporter.Create(TransactionView.class, new TransactionViewFactory().get(Format.GetDate("01.01.2000"), new Date(), null, product, null, product.name),
									"Проводки/движение", product.code + " " + product.name, Format.Show(new Date()));

						}
					};
					name.onClick.putValue(BO.fields.ID, product.id);
					add(name);

					JLabel count = new JLabel("" + product.count);
					count.setHorizontalAlignment(SwingConstants.RIGHT);
					count.setBounds(name.getX() + name.getWidth() + 20, y, 50, 20);
					add(count);

					JLabel units = new JLabel(product.units);
					units.setHorizontalAlignment(SwingConstants.CENTER);
					units.setBounds(count.getX() + count.getWidth() + 20, y, 30, 20);
					add(units);
				}

				// revalidate();
				repaint();
			}

		});
	}
}