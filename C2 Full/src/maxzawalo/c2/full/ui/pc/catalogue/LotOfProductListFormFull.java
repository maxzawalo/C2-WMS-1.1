package maxzawalo.c2.full.ui.pc.catalogue;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import maxzawalo.c2.base.interfaces.TerminalEvent;
import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.free.bo.LotOfProduct;
import maxzawalo.c2.free.bo.Price;
import maxzawalo.c2.free.bo.Product;
import maxzawalo.c2.free.bo.Settings;
import maxzawalo.c2.free.bo.store.StoreDocBO;
import maxzawalo.c2.free.data.factory.catalogue.PriceFactory;
import maxzawalo.c2.free.reporter.HtmlReporter;
import maxzawalo.c2.free.ui.pc.catalogue.LotOfProductListFormFree;
import maxzawalo.c2.free.ui.pc.document.store.StoreDocForm;
import maxzawalo.c2.full.bo.document.remaining_stock.RemainingStock;
import maxzawalo.c2.full.bo.document.return_from_customer.ReturnFromCustomer;
import maxzawalo.c2.full.bo.view.TransactionView;
import maxzawalo.c2.full.data.factory.catalogue.LotOfProductFactoryFull;
import maxzawalo.c2.full.data.factory.view.TransactionViewFactory;
import maxzawalo.c2.full.hardware.terminal.Terminal;
import maxzawalo.c2.full.ui.pc.control.ImageGallery;
import maxzawalo.c2.full.ui.pc.document.ReturnFromCustomerForm;
import maxzawalo.c2.full.ui.pc.document.ReturnOfGoodsForm;
import maxzawalo.c2.full.ui.pc.document.TakeIntoInventoryForm;
import maxzawalo.c2.full.ui.pc.document.WriteOffProductForm;

public class LotOfProductListFormFull extends LotOfProductListFormFree implements TerminalEvent {

	JLabel labelInfo;

	public LotOfProductListFormFull(JFrame parent, StoreDocForm fromForm) {
		super(parent, fromForm);
		setBounds(0, 0, 1000, 700);

		factory = new LotOfProductFactoryFull();

		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		selectedTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		// JButton btnShowTransactions = new JButton("Количество");
		// btnShowTransactions.setToolTipText("Количество выделенных");
		// btnShowTransactions.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent arg0) {
		// ShowSelectedCount();
		// }
		// });
		// btnShowTransactions.setBounds(1100, 17, 119, 29);
		// getContentPane().add(btnShowTransactions);

		onRowSelected = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ShowSelectedCount();
			}
		};

		selectedTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				// if (e.getClickCount() == 2) {
				// onTableRowDblClick(e);
				// }
				if (e.getClickCount() == 1) {
					// onTableRowClick(e);
					ShowSelectedCount();
				}
			}
		});

		labelInfo = new JLabel("Инфо");
		labelInfo.setFont(new Font("Tahoma", Font.PLAIN, 20));
		InfoLabelPos();
		getContentPane().add(labelInfo);

		table.rowPopup.removeAll();
		JMenuItem clearPriceItem = new JMenuItem("Распечатать ценник заново");
		clearPriceItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<LotOfProduct> selected = new ArrayList<>();
				for (int row : table.getSelectedRows()) {
					LotOfProduct lot = ((LotOfProduct) tableModel.getList().get(row));
					selected.add(lot);
				}

				if (((LotOfProductFactoryFull) factory).ClearPriceState(selected))
					Console.I().INFO(getClass(), "LotOfProductListFormFull", "Можно печатать ценник(и).");
				else
					Console.I().ERROR(getClass(), "LotOfProductListFormFull", "Ошибка изменения состояния ценника.");
			}
		});
		table.rowPopup.add(clearPriceItem);
		InitScanner();
	}

	@Override
	protected void InitScanner() {
		Terminal.Uninit();
		Terminal.Init(Settings.startComPort);
		Terminal.callbacks.add(this);
	}

	protected void InfoLabelPos() {
		labelInfo.setBounds(btnInit.getX() + btnInit.getWidth() + 10, btnCopy2Doc.getY() - btnCopy2Doc.getHeight() / 2, 400, 30);
	}

	@Override
	protected void onFormResized() {
		super.onFormResized();
		InfoLabelPos();
	}

	@Override
	protected void ShowImage() {
		Product p = ((LotOfProduct) selectedBO).product;
		ImageGallery.instance.LoadData(p.name, p.id);
	}

	@Override
	protected void ShowTransactions() {
		LotOfProduct lot = ((LotOfProduct) selectedBO);

		HtmlReporter.Create(TransactionView.class, new TransactionViewFactory().get(Format.GetDate("01.01.2000"), docDate, store, lot.product, null, lot.product.name), "Проводки/движение",
				lot.product.code + " " + lot.product.name, Format.Show(docDate));
		// log.CONSOLE("" + lot.doc.contractor + " | " +
		// lot.doc.getRusName() + " " + lot.doc.asLot());
	}

	@Override
	public void onScan(final String value, boolean exc) {
		try {
			if (SwingUtilities.isEventDispatchThread()) {
				ProcessScanData(value);
			} else {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						ProcessScanData(value);
					}
				});
			}
		} catch (Exception e) {
			log.ERROR("onScan", e);
		}
	}

	protected void ProcessScanData(String barcode) {
		barcode = barcode.substring(0, barcode.length() - 1);
		Price price = new PriceFactory().GetByCode("00-" + barcode);
		searchText.setText(price.product.name);
		setSearch(price.product.name);
		Search();
		System.out.println(price);
	}

	// @Override
	// public StoreTP CreateTPByForm(StoreDocForm docForm) {
	// // TODO: вынести в StoreTP, совместить
	// StoreTP tp = null;
	// if (tp == null) {
	// if (docForm.getClass() == DeliveryNoteFormFull.class)
	// tp = new DeliveryNoteTablePart.Product();
	// else if (docForm.getClass() == BillFormFull.class)
	// tp = new BillTablePart.Product();
	// else if (docForm.getClass() == InvoiceFormFull.class)
	// tp = new InvoiceTablePart.Product();
	// else if (docForm.getClass() == OrderForm.class)
	// tp = new OrderTablePart.Product();
	// else if (docForm.getClass() == CashVoucherForm.class)
	// tp = new CashVoucherTablePart.Product();
	// else if (docForm.getClass() == ReturnOfGoodsForm.class)
	// tp = new ReturnOfGoodsTablePart.Product();
	// else if (docForm.getClass() == WriteOffProductForm.class)
	// tp = new WriteOffProductTablePart.Product();
	// else if (docForm.getClass() == TakeIntoInventoryForm.class)
	// tp = new RemainingStockTablePart.Product();
	// else if (docForm.getClass() == ReturnFromCustomerForm.class)
	// tp = new ReturnFromCustomerTablePart.Product();
	// }
	//
	// return tp;
	// }

	@Override
	protected boolean isZeroAddition() {
		return fromForm instanceof WriteOffProductForm || fromForm instanceof TakeIntoInventoryForm;
	}

	@Override
	protected boolean SkipNullLot() {
		return super.SkipNullLot() || fromForm instanceof ReturnFromCustomerForm;
	}

	@Override
	protected boolean SkipMinusLot() {
		return super.SkipMinusLot() || fromForm instanceof ReturnOfGoodsForm;
	}

	protected boolean Is4Invoice(StoreDocBO doc) {

		return super.Is4Invoice(doc) || doc instanceof RemainingStock || doc instanceof ReturnFromCustomer;
	}

	protected void ShowSelectedCount() {
		double count = 0;
		for (int row : table.getSelectedRows()) {
			count += ((LotOfProduct) tableModel.getList().get(row)).count;
			count = Format.countRound(count);
		}
		double countSelected = 0;
		for (int row : selectedTable.getSelectedRows()) {
			countSelected += ((LotOfProduct) selectedModel.getList().get(row)).count;
			countSelected = Format.countRound(countSelected);
		}

		labelInfo.setText("Выделенное количество: " + count + " | " + countSelected);
	}
}