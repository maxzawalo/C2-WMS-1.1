package maxzawalo.c2.full.ui.pc.form;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.base.bo.registry.Registry;
import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.ui.pc.controls.KeyPressedTable;
import maxzawalo.c2.free.accounting.AccRecord;
import maxzawalo.c2.free.accounting.data.AccFactory;
import maxzawalo.c2.free.bo.LotOfProduct;
import maxzawalo.c2.free.bo.registry.AccAcc;
import maxzawalo.c2.free.bo.registry.RegistryProduct;
import maxzawalo.c2.free.bo.store.StoreDocBO;
import maxzawalo.c2.free.data.factory.catalogue.ProductFactory;
import maxzawalo.c2.free.data.factory.registry.RegistryProductFactory;
import maxzawalo.c2.full.data.factory.catalogue.LotOfProductFactoryFull;
import maxzawalo.c2.full.ui.pc.model.RegistryProductTableModel;

public class AnaliticsForm extends JFrame {

	private final JPanel contentPanel = new JPanel();
	private KeyPressedTable table;
	JTextArea accText;

	public AnaliticsForm() {
		this(null);
	}

	JFrame parent;

	public AnaliticsForm(JFrame parent) {
		// super(parent);
		this.parent = parent;

		UI.SET(this);

		setBounds(100, 100, 1037, 418);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 1021, 336);
		contentPanel.add(tabbedPane);

		JPanel productRegistry = new JPanel();
		tabbedPane.addTab("Номенклатурный регистр", null, productRegistry, null);
		productRegistry.setLayout(new BoxLayout(productRegistry, BoxLayout.X_AXIS));

		JScrollPane scrollPane = new JScrollPane();
		productRegistry.add(scrollPane);

		table = new KeyPressedTable();
		table.settingsKey = getClass().getSimpleName() + ".table";
		table.setFillsViewportHeight(true);
		scrollPane.setViewportView(table);

		JPanel accRegistry = new JPanel();
		tabbedPane.addTab("Бухгалтерский регистр", null, accRegistry, null);
		accRegistry.setLayout(new BoxLayout(accRegistry, BoxLayout.X_AXIS));

		JScrollPane scrollPane_1 = new JScrollPane();
		accRegistry.add(scrollPane_1);
		accText = new JTextArea();
		scrollPane_1.setViewportView(accText);
		// accRegistry.add(accText);
		if (!User.current.isAdmin())
			tabbedPane.remove(accRegistry);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		{
			JButton okButton = new JButton("OK");
			okButton.setActionCommand("OK");
			buttonPane.add(okButton);
			getRootPane().setDefaultButton(okButton);
		}
		{
			JButton cancelButton = new JButton("Cancel");
			cancelButton.setActionCommand("Cancel");
			buttonPane.add(cancelButton);
		}
	}

	DocumentBO registrator;

	public void setRegistrator(DocumentBO registrator) {
		this.registrator = registrator;
		setTitle("" + registrator);
		if (!User.current.isSimple()) {
			new SearchTask().execute();
		}
	}

	List<RegistryProduct> items;

	class SearchTask extends SwingWorker<Void, Void> {
		@Override
		protected Void doInBackground() throws Exception {
			UI.Start(AnaliticsForm.this);

			// TODO: FIFO?
			RegistryProductFactory factory = new RegistryProductFactory();

			Registry registry = new RegistryProduct();
			registry.setRegistrator(registrator);
			items = factory.SelectDocEntries(registry);
			// загружаем Партии, т.к. foreignAutoRefresh = false
			for (RegistryProduct item : items) {
				LotOfProductFactoryFull lotFactory = new LotOfProductFactoryFull();
				item.lotOfProduct = lotFactory.GetById(item.lotOfProduct.id);
				// Фикс для отображения проводок по Услугам
				if (item.lotOfProduct == null) {
					item.lotOfProduct = new LotOfProduct();
					item.lotOfProduct.doc = new StoreDocBO();
					item.lotOfProduct.doc_type = item.reg_type;
					item.lotOfProduct.doc.id = item.reg_id;
					item.lotOfProduct.doc = lotFactory.getDoc(item.lotOfProduct);
					item.lotOfProduct.cost_price = item.price;
				}
				item.product = new ProductFactory().GetById(item.product.id);// .lotOfProduct.product;
				// Берем модуль числа, так как при расходе минуса - не смущаем
				// пользователя?
				item.count = Math.abs(item.count);
			}

			if (User.current.isAdmin()) {
				String txt = "Счет" + "\t" + //
						"Сумма" + "\t" + //
						"Кол-во";
				txt += "\t" + txt + "\n";

				for (int i = 0; i < 120; i++) {
					txt += "-";
				}
				txt += "\n";

				registry = new AccAcc<>();
				registry.setRegistrator(registrator);

				AccFactory accFactory = new AccFactory();

				Map<String, Object> params = new HashMap<>();
				params.put("registrator", registry);
				params.put("selectedAcc", null);
				if (accFactory.getDocRecords.call(params)) {
					for (AccRecord acc : (List<AccRecord>) params.get("docRecords")) {
						txt += acc + "\n";
						System.out.println(acc);
					}
				}
				accText.setText(txt);
			}

			return null;
		}

		@Override
		protected void done() {
			RegistryProductTableModel tableModel = new RegistryProductTableModel();
			tableModel.setList(items);
			table.setModel(tableModel);
			table.setColumnSettings();
			// table.getSelectionModel().clearSelection();
			table.setRowHeight(UIManager.getInt("Table.rowHeight"));
			table.LoadTableSettings();
			table.setHeaderHeight(50);

			table.revalidate();
			table.repaint();

			UI.Stop(AnaliticsForm.this);
		}
	}
}