package maxzawalo.c2.free.ui.pc.catalogue;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import maxzawalo.c2.base.bo.CatalogueBO;
import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.base.data.factory.FactoryBO;
import maxzawalo.c2.base.ui.pc.catalogue.CatalogueListForm;
import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.ui.pc.controls.KeyPressedTable;
import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.base.utils.Profiler;
import maxzawalo.c2.free.bo.LotOfProduct;
import maxzawalo.c2.free.bo.Product;
import maxzawalo.c2.free.bo.Store;
import maxzawalo.c2.free.bo.document.bill.Bill;
import maxzawalo.c2.free.bo.document.invoice.Invoice;
import maxzawalo.c2.free.bo.store.StoreDocBO;
import maxzawalo.c2.free.bo.store.StoreTP;
import maxzawalo.c2.free.cache.Cache;
import maxzawalo.c2.free.data.factory.catalogue.ContractorFactory;
import maxzawalo.c2.free.data.factory.catalogue.LotOfProductFactoryFree;
import maxzawalo.c2.free.data.factory.catalogue.ProductFactory;
import maxzawalo.c2.free.ui.pc.control.LotProductMergedTable;
import maxzawalo.c2.free.ui.pc.document.store.BillFormFree;
import maxzawalo.c2.free.ui.pc.document.store.StoreDocForm;
import maxzawalo.c2.free.ui.pc.form.EnterValueForm;
import maxzawalo.c2.free.ui.pc.model.catalogue.LotOfProductModel;

public class LotOfProductListFormFree extends CatalogueListForm<LotOfProduct, ProductFormFree> {

	protected KeyPressedTable selectedTable;
	List<LotOfProduct> selectedItems = new ArrayList<>();
	protected LotOfProductModel selectedModel = new LotOfProductModel();
	protected StoreDocForm fromForm;
	protected JCheckBox justRest;
	protected JCheckBox withLot;
	protected JScrollPane selectScrollPane;
	protected JButton btnCopy2Doc;
	boolean forInvoice = false;
	protected Store store;
	JButton btnShowImage;

	JLabel loader;
	protected Date docDate;

	public LotOfProductListFormFree() {
		this(null, null);
	}

	public LotOfProductListFormFree(JFrame parent, StoreDocForm fromForm) {
		super(parent);

		factory = new LotOfProductFactoryFree();
		setBounds(0, 0, 1000, 700);

		// Terminal.Uninit();
		// Terminal.Init(Settings.startComPort);
		// Terminal.callbacks.add(this);

		groupForm = new ProductGroupForm();

		loader = new JLabel(UI.getLoaderIcon());
		loader.setVisible(false);
		loader.setLocation(0, 0);
		loader.setSize(160, 160);
		table.add(loader);

		setTitle("Подбор товара");

		btnSelect.setVisible(false);
		btnGroupEdit.setLocation(129, 602);
		btnAddGroup.setLocation(10, 602);
		btnDuplicate.setVisible(false);

		// Создаем объект для общения с Приходной
		elementBO = new LotOfProduct();

		// btnAdd.setVisible(false);
		btnSelect.setVisible(false);
		this.fromForm = fromForm;
		tableModel = new LotOfProductModel();

		selectedTable = new KeyPressedTable();
		selectedTable.setRowHeight(UIManager.getInt("Table.rowHeight"));
		selectedTable.setBounds(109, 352, 606, 74);
		selectedTable.setFillsViewportHeight(true);
		selectedTable.setModel(selectedModel);
		selectedModel.setList(selectedItems);

		selectedTable.beforeDeleteRowAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {

				int row = selectedTable.getSelectedRow();
				// System.out.println("Delete row " + row);
				// TODO: action log

				if (row > -1) {
					// TODO: return to search list (by searchData)

					// Без этого надо обновлять кэш
					if (!forInvoice)
						((LotOfProductModel) LotOfProductListFormFree.this.tableModel).returnToSelectList(
								LotOfProductListFormFree.this.selectedModel.getItem(row), parentGroup);
					// table.revalidate();
					// table.repaint();
				}

				Search();
			}
		};
		selectedTable.EnableRowDeleting();
		getContentPane().setLayout(null);

		selectScrollPane = new JScrollPane();
		selectScrollPane.setViewportView(selectedTable);
		selectScrollPane.setBounds(10, 389, 1348, 259);
		getContentPane().add(selectScrollPane);

		btnInit = new JButton("");
		btnInit.setIcon(UI.getBarcodeScannerIcon(30, 30));
		btnInit.setBounds(10, 661, 40, 40);
		btnInit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InitScanner();
			}
		});
		getContentPane().add(btnInit);

		btnCopy2Doc = new JButton("Перенести в документ");
		btnCopy2Doc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Copy2Doc();
				setVisible(false);
				dispose();
			}
		});

		btnCopy2Doc.setBounds(1106, 677, 252, 29);
		getContentPane().add(btnCopy2Doc);

		btnShowImage = new JButton("");
		btnShowImage.setToolTipText("Показать изображение");
		btnShowImage.setIcon(UI.getImageIcon(30, 25));
		btnShowImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ShowImage();
			}
		});
		btnShowImage.setBounds(270, 600, 46, 40);
		getContentPane().add(btnShowImage);

		justRest = new JCheckBox("Только остатки");
		justRest.setSelected(true);
		justRest.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (justRest.isSelected())
					withLot.setSelected(true);
				withLot.setEnabled(!justRest.isSelected());
				ClearCurrentPage();
				Search();
			}
		});
		justRest.setBounds(690, 5, 150, 20);
		getContentPane().add(justRest);

		withLot = new JCheckBox("С партиями");
		withLot.setSelected(true);
		withLot.setEnabled(false);
		withLot.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				ClearCurrentPage();
				Search();
			}
		});
		withLot.setBounds(690, 35, 150, 20);
		getContentPane().add(withLot);

		JButton button = new JButton("См. контрагент");
		button.setToolTipText("Кто впарил??");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!User.current.isSimple()) {
					LotOfProduct lot = ((LotOfProduct) selectedBO);
					// Подгружаем если нет (3й уровень объекта Партия)
					if (lot.doc.contractor.code.equals(""))
						lot.doc.contractor = new ContractorFactory().GetById(lot.doc.contractor.id);
					Console.I().INFO(getClass(), "button",  lot.doc.contractor + " | " + lot.doc.getRusName() + " " + lot.doc.asLot());
				}
			}
		});
		button.setBounds(854, 17, 119, 29);
		getContentPane().add(button);

		JButton btnShowTransactions = new JButton("Движение");
		btnShowTransactions.setToolTipText("Движение");
		btnShowTransactions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!User.current.isSimple())
					ShowTransactions();
			}
		});
		btnShowTransactions.setBounds(980, 17, 119, 29);
		getContentPane().add(btnShowTransactions);

		// onRowSelected = new AbstractAction() {
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// // Product p = ((LotOfProduct) selectedBO).product;
		// // ImageGallery.instance.LoadData(p.name, p.id);
		// }
		// };
	}

	protected void InitScanner() {
		

	}

	@Override
	protected void setGroupTree() {
		setGroupTree(Product.class);
	}

	// public StoreTP CreateTPByForm(StoreDocForm docForm) {
	// // TODO: вынести в StoreTP, совместить
	// StoreTP tp = null;
	// if (docForm.getClass() == DeliveryNoteFormFree.class)
	// tp = new DeliveryNoteTablePart.Product();
	// else if (docForm.getClass() == BillFormFree.class)
	// tp = new BillTablePart.Product();
	// else if (docForm.getClass() == InvoiceFormFree.class)
	// tp = new InvoiceTablePart.Product();
	//
	// return tp;
	// }

	protected void Copy2Doc() {
		List<StoreTP> allSelected = new ArrayList<>();
		for (LotOfProduct lot : selectedItems) {
			if (lot.id == 0)
				log.DEBUG("Copy2Doc", "lot.id == 0");
			StoreTP tp = null;
			try {
				tp = (StoreTP) itemT.newInstance();
			} catch (Exception e) {
				log.ERROR("Copy2Doc", e);
				return;
			}

			tp.product = lot.product;
			tp.price_discount_off = lot.cost_price;
			// if (fromForm instanceof TakeIntoInventoryForm)
			// tp.price_discount_off = 0;

			tp.price = lot.price;
			// if (tp.sum_contains_vat)
			// tp.price = Format.defaultRound(tp.price * (tp.rateVat + 100) /
			// 100);

			// (Вычисляем наценку)
			// TODO: брать из партии?
			// tp.discount = Format.defaultRound(((tp.price /
			// tp.price_discount_off - 1) * 100));
			tp.CalcDiscount("");
			if (isZeroAddition())
				tp.discount = 0;

			tp.count = lot.count;
			tp.lotOfProduct = lot;
			// Обнуляем партию при приходе
			if (forInvoice && !SkipNullLot())
				tp.lotOfProduct = null;
			tp.Calc("");
			// tp.CalcSum();
			// tp.CalcSumVat();
			// tp.CalcTotal();
			allSelected.add(tp);
		}

		fromForm.CopyFromSelectForm(allSelected);
	}

	protected boolean SkipNullLot() {
		return fromForm instanceof BillFormFree;
	}

	protected boolean isZeroAddition() {
		return false;
	}

	public void valueSelected(double count, double cost_price) {
		if (!forInvoice && count == 0)
			return;

		// Отсекаем некорректно введенные числа после заданной разрядности
		count = Format.countTrunc(count);

		LotOfProduct addLot = (LotOfProduct) selectedBO.cloneObject();
		addLot.count = count;
		if (forInvoice) {

			// В приходной price = cost_price из поля ввода,
			// далее формируется партия и все становится на места
			addLot.cost_price = cost_price;
			if (!(fromForm instanceof BillFormFree)) {
				addLot.doc = elementBO.doc;
				addLot.doc_type = elementBO.doc_type;
				addLot.price = cost_price;
			}
			// addLot.price = TradeAddition.CalcAddition(addLot.product,
			// cost_price, elementBO.getDelivery());
		}

		// Группируем по партиям
		if (!LotOfProductFactoryFree.addGrouped(selectedItems, addLot, 1))
			selectedItems.add(addLot);

		// selectedModel.setList(selectedItems);
		// selectedTable.setModel(selectedModel);
		selectedTable.revalidate();
		selectedTable.repaint();

		table.getSelectionModel().clearSelection();
		if (!forInvoice || SkipMinusLot()) {
			((LotOfProductModel) tableModel).minus(addLot);
			table.revalidate();
			table.repaint();
		}
	}

	protected boolean SkipMinusLot() {
		return fromForm instanceof BillFormFree;
	}

	boolean fuzzy_search = true;
	public Class itemT;
	protected JButton btnInit;

	class SearchTask extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {
			// UI.Start(this);

			tableModel.setList(new ArrayList<>());
			table.setHeaderHeight(50);
			table.revalidate();
			table.repaint();
			loader.setVisible(true);

			// TODO: showZeroBalance, count by Lot group
			// if (searchData.equals(""))
			// pagesCount = BO.GetPagesCount(new
			// Product().GetByParentPageCount(parentGroup), pageSize);
			// else
			Product product = new Product();
			ProductFactory productFactory = new ProductFactory();
			productFactory.DeleteFilterOn();
			product.fuzzy = fuzzy_search;
			pagesCount = FactoryBO
					.GetPagesCount(productFactory.GetCount(product, (CatalogueBO) parentGroup, searchData), pageSize);
			// Иначе пустой список, так как испозуется кэш, а объекты разные
			// if(product.fuzzy_ids.si)
			elementBO.fuzzy_ids = product.fuzzy_ids;
			elementBO.fuzzy = fuzzy_search;
			// pagesCount *= 1.5;// Партий больше чем продуктов
			System.out.println("pagesCount=" + pagesCount);

			if (currentPage == Integer.MAX_VALUE)
				currentPage = getButtonsCount() - 1;

			SetButtons();
			// System.out.println(currentPage);
			// TODO: get page by Lot group
			((LotOfProductModel) tableModel).showZeroBalance = !justRest.isSelected();
			items = ((LotOfProductFactoryFree) factory).GetPageFiltered(elementBO, docDate, currentPage, pageSize,
					parentGroup, searchData, !justRest.isSelected(), !withLot.isSelected(), store, true);// TODO:
			// System.out.println("items.size()="+items.size());// это уже
			// партии их > Товаров
			return null;
		}

		@Override
		protected void done() {
			String key = this.getClass().getSimpleName() + ".SetUI";
			Profiler profiler = new Profiler();
			profiler.Start(key);

			tableModel.setList(items);
			table.getSelectionModel().clearSelection();
			// TODO: selectedModel.getList() is null
			((LotOfProductModel) tableModel).minusList(selectedModel.getList());
			setModel();
			table.setHeaderHeight(50);
			table.revalidate();
			table.repaint();
			SetButtons();

			profiler.Stop(key);
			profiler.PrintElapsed(key);
			// UI.Stop(this);
			loader.setVisible(false);
		}
	}

	@Override
	public void Search() {
		new SearchTask().execute();
	}

	@Override
	protected void CreateTable() {
		table = new LotProductMergedTable();
	}

	public void showDialog() {
		setVisible(true);
	}

	protected void ShowEnterValue() {
		if (!forInvoice && ((LotOfProduct) selectedBO).count <= 0) {
			log.DEBUG("ShowEnterValue", "Нет на складе");
			return;
		}
		if (!forInvoice && ((LotOfProduct) selectedBO).reserve) {
			log.DEBUG("ShowEnterValue", "В резерве");
			return;
		}

		EnterValueForm enterValue = new EnterValueForm(LotOfProductListFormFree.this);
		enterValue.forInvoice = forInvoice;
		enterValue.maxValue = ((LotOfProduct) selectedBO).count;
		enterValue.count = 1;
		enterValue.cost_price = ((LotOfProduct) selectedBO).cost_price;
		enterValue.product = ((LotOfProduct) selectedBO).product;
		enterValue.ShowDialog();
	}

	@Override
	protected void onTableRowDblClick(MouseEvent e) {
		ShowEnterValue();
	}

	public void setDoc(StoreDocBO doc) {
		forInvoice = Is4Invoice(doc);
		if (forInvoice) {
			elementBO.doc = doc;
			elementBO.doc_type = doc.reg_type;
		}
	}

	protected boolean Is4Invoice(StoreDocBO doc) {
		return doc instanceof Invoice || doc instanceof Bill;
	}

	@Override
	protected void setSearchContext() {
		searchContext = Product.class;
	}

	@Override
	protected void onFormResized() {
		super.onFormResized();

		btnShowImage.setLocation(btnShowImage.getX(), buttonsScrollPanel.getY());

		int selectScrollPaneY = buttonsScrollPanel.getY() + buttonsScrollPanel.getHeight() + 20;
		int selectScrollPaneHeight = getHeight() - selectScrollPaneY - 100;
		selectScrollPane.setSize(splitPane.getWidth(), selectScrollPaneHeight);
		selectScrollPane.revalidate();
		selectScrollPane.setLocation(selectScrollPane.getX(), selectScrollPaneY);
		btnCopy2Doc.setLocation(btnCopy2Doc.getParent().getWidth() - btnCopy2Doc.getWidth() - 10,
				selectScrollPane.getY() + selectScrollPane.getHeight() + 20);
		loader.setLocation((int) (scrollPaneGrid.getWidth() / 2 - loader.getWidth() * 1.2),
				scrollPaneGrid.getHeight() / 2 - loader.getHeight());

		btnInit.setLocation(btnInit.getX(), this.getHeight() - btnInit.getHeight() - 50);
	}

	@Override
	protected void btnRefreshClick() {
		Cache.I().clearBySubstr("LotOfProduct.GetPageFiltered");
		Search();
	}

	public void setStore(Store store) {
		this.store = store;
		setTitle(getTitle() + " : " + store + ". На " + Format.Show(docDate));
	}

	public void setDocDate(Date docDate) {
		this.docDate = docDate;
	}

	@Override
	protected void ResizeSplitPane(int parentWidth) {
		int splitPaneHeight = splitPane.getParent().getHeight() - 140;
		// if (BoListForm.this.getClass() == LotOfProductListForm.class)
		splitPaneHeight = (int) (splitPane.getParent().getHeight() * 0.4);

		splitPane.setSize(parentWidth - 20, splitPaneHeight);
	}

	@Override
	protected void PagerSelected(Boolean buttonCountG1) {
		if ((this instanceof LotOfProductListFormFree) && buttonCountG1)
			btnPanel.setBorder(new LineBorder(Color.RED));
	}

	@Override
	protected void CreateAutoSuggestor() {
		// autoSuggestor = new AutoSuggestor(searchText, this, null,
		// Color.WHITE, Color.black, Color.black, 1f);
		// autoSuggestor.context = searchContext;
	}

	protected void ShowImage() {
	}

	protected void ShowTransactions() {
	}

}