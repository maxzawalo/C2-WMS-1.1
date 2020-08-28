package maxzawalo.c2.full.ui.pc.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Date;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import com.google.zxing.BarcodeFormat;

import maxzawalo.c2.base.Actions;
import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.base.data.DbHelper;
import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.ui.pc.form.BoListForm;
import maxzawalo.c2.base.ui.pc.form.SetPasswordForm;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.base.utils.Global;
import maxzawalo.c2.base.utils.Logger;
import maxzawalo.c2.free.bo.Settings;
import maxzawalo.c2.free.bo.document.deliverynote.DeliveryNote;
import maxzawalo.c2.free.cache.Cache;
import maxzawalo.c2.free.reporter.HtmlReporter;
import maxzawalo.c2.free.ui.pc.catalogue.AllContractListForm;
import maxzawalo.c2.free.ui.pc.catalogue.ContractorListForm;
import maxzawalo.c2.free.ui.pc.catalogue.CoworkerListForm;
import maxzawalo.c2.free.ui.pc.catalogue.ProductListForm;
import maxzawalo.c2.free.ui.pc.catalogue.StoreListForm;
import maxzawalo.c2.free.ui.pc.catalogue.UnitsListForm;
import maxzawalo.c2.free.ui.pc.form.AboutFormFree;
import maxzawalo.c2.full.analitics.TransactionByDocPeriod;
import maxzawalo.c2.full.bo.ScannedBarcode;
import maxzawalo.c2.full.bo.document.cashvoucher.CashVoucher;
import maxzawalo.c2.full.bo.view.RemainingStockView;
import maxzawalo.c2.full.data.Adapter1C;
import maxzawalo.c2.full.data.factory.MobileLoginFactory;
import maxzawalo.c2.full.data.factory.document.InvoiceFactoryFull;
import maxzawalo.c2.full.data.factory.document.ReturnFromCustomerFactory;
import maxzawalo.c2.full.data.factory.view.RemainingStockViewFactory;
import maxzawalo.c2.full.reporter.BarcodeGenerator;
import maxzawalo.c2.full.synchronization.Synchronization;
import maxzawalo.c2.full.ui.pc.control.Chart;
import maxzawalo.c2.full.ui.pc.control.ImageGallery;
import maxzawalo.c2.full.ui.pc.control.TopProductControl;
import maxzawalo.c2.full.ui.pc.daybook.StoreDaybookListForm;
import maxzawalo.c2.full.ui.pc.document.BillListFormFull;
import maxzawalo.c2.full.ui.pc.document.CashVoucherListForm;
import maxzawalo.c2.full.ui.pc.document.DeliveryNoteListFormFull;
import maxzawalo.c2.full.ui.pc.document.InvoiceListFormFull;
import maxzawalo.c2.full.ui.pc.document.OrderListForm;
import maxzawalo.c2.full.ui.pc.document.ReturnFromCustomerListForm;
import maxzawalo.c2.full.ui.pc.document.ReturnOfGoodsListForm;
import maxzawalo.c2.full.ui.pc.document.TakeIntoInventoryListForm;
import maxzawalo.c2.full.ui.pc.document.Warrant4ReceiptListForm;
import maxzawalo.c2.full.ui.pc.document.WriteOffProductListForm;
import maxzawalo.c2.full.ui.pc.panel.PanelBank;
import maxzawalo.c2.full.ui.pc.panel.PanelDirector;
import maxzawalo.c2.full.ui.pc.view.ContractorTransactionViewForm;
import maxzawalo.c2.full.ui.pc.view.CostPriceViewForm;
import maxzawalo.c2.full.ui.pc.view.CustomerDebtViewForm;
import maxzawalo.c2.full.ui.pc.view.DocCommitViewForm;
import maxzawalo.c2.full.ui.pc.view.FindDocByBarcodeForm;
import maxzawalo.c2.full.ui.pc.view.PrintPrice;
import maxzawalo.c2.full.ui.pc.view.ReconciliationReportForm;
import maxzawalo.c2.full.ui.pc.view.RemainingStockReportForm;
import maxzawalo.c2.full.ui.pc.view.TransactionViewForm;
import maxzawalo.c2.full.ui.pc.view.ViewPriceForm;

public class MainFormFull extends JFrame {

	protected Logger log = Logger.getLogger(this.getClass().getSimpleName());

	private JTextField textField;

	Chart dnChart;
	Chart cvChart;
	TopProductControl topProduct;

	public MainFormFull() {
		System.out.println("license");
		UI.SET(this);

		setTitle("C2 " + Global.VERSION);
		// setIconImage(UI.getAppIcon());

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		// tabbedPane.setAlignmentY(1.0f);
		// tabbedPane.setAlignmentX(1.0f);

		JPanel panelStatistics = new JPanel();
		panelStatistics.setLayout(null);

		dnChart = new Chart("Отгружено на сумму, руб");
		dnChart.setBounds(0, 48, 600, 250);
		panelStatistics.add(dnChart);

		cvChart = new Chart("Выручка по кассе, руб");
		cvChart.setBounds(0, 310, 600, 250);
		panelStatistics.add(cvChart);

		topProduct = new TopProductControl();
		topProduct.setBounds(651, 47, 328, 581);
		panelStatistics.add(topProduct);

		JButton btnRefresh = new JButton();
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LoadData();
			}
		});
		btnRefresh.setIcon(UI.getRefreshIcon(20, 20));
		btnRefresh.setToolTipText("Обновить");
		btnRefresh.setBounds(5, 5, 40, 40);
		panelStatistics.add(btnRefresh);

		JButton btnMobileLogin = new JButton("");
		btnMobileLogin.setToolTipText("Вход с мобильного");
		btnMobileLogin.setIcon(UI.getMobileIcon());
		btnMobileLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					String loginHash = new MobileLoginFactory().Create();
					BufferedImage image = BarcodeGenerator.encodeAsBitmap("login_" + loginHash, BarcodeFormat.QR_CODE,
							300, 300);
					ImageGallery.instance.LoadData("Сканируйте на мобильном", image);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		btnMobileLogin.setBounds(10, 577, 40, 40);
		panelStatistics.add(btnMobileLogin);

		JButton about = new JButton("?");
		about.setToolTipText("О программе");
		about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				AboutFormFree form = new AboutFormFree();
				form.setVisible(true);
			}
		});
		about.setBounds(939, 5, 40, 40);
		panelStatistics.add(about);

		JButton button_9 = new JButton("");
		button_9.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FindDocByBarcodeForm form = new FindDocByBarcodeForm();
				form.setVisible(true);
			}
		});
		button_9.setToolTipText("Вход с мобильного");
		button_9.setBounds(85, 577, 40, 40);
		panelStatistics.add(button_9);

		JPanel panelDocs = new JPanel();
		panelDocs.setLayout(null);

		JPanel panelAdmin = new JPanel();
		panelAdmin.setLayout(null);

		JButton btnSync = new JButton("Синхронизация");
		btnSync.setBounds(15, 61, 145, 29);
		panelAdmin.add(btnSync);

		JButton btnTest = new JButton("Загрузка новых данных");
		btnTest.setBounds(15, 226, 145, 29);
		panelAdmin.add(btnTest);

		JButton btnAlterDb = new JButton("Обновить БД");
		btnAlterDb.setBounds(15, 106, 145, 29);
		panelAdmin.add(btnAlterDb);
		// btnAlterDb.setVisible(show_admin_buttons);
		btnAlterDb.setToolTipText("Обновить структуру БД");

		JButton groupTransaction = new JButton("Групповое проведение ВСЕХ");
		groupTransaction.setBounds(207, 16, 145, 29);
		panelAdmin.add(groupTransaction);

		JButton button_10 = new JButton("Аналит. тест");
		button_10.setBounds(15, 181, 145, 29);
		panelAdmin.add(button_10);

		JButton btnBackup = new JButton("BackUp");
		btnBackup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				UI.Start(MainFormFull.this);
				try {
					Synchronization.Backup();
					UI.Stop(MainFormFull.this);
					JOptionPane.showMessageDialog(MainFormFull.this, "Резервное копирование завершено.", "BackUp",
							JOptionPane.PLAIN_MESSAGE);

				} catch (Exception e) {
					log.ERROR("Backup", e);
					UI.Stop(MainFormFull.this);
					JOptionPane.showMessageDialog(MainFormFull.this, "Ошибки при резервном копировании. См. лог.",
							"BackUp", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnBackup.setBounds(15, 16, 145, 29);
		panelAdmin.add(btnBackup);
	
		button_10.setVisible(User.current.isAdmin());
		button_10.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				UI.Start(MainFormFull.this);
				// MainForm.this.setEnabled(false);
				try {
					// Test test = new Test();
					// test.setUp();
					// test.AfterLoad();
				} catch (Exception e) {
					log.ERROR("AnalitTestClick", e);
				}
				// MainForm.this.setEnabled(true);
				UI.Stop(MainFormFull.this);
			}

		});
		groupTransaction.setVisible(User.current.isAdmin());
		groupTransaction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				GroupTransactionForm form = new GroupTransactionForm();
				form.setVisible(true);
			}
		});
		btnAlterDb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				UI.Start(MainFormFull.this);
				// MainForm.this.setEnabled(false);
				try {
					DbHelper.Alter(Global.dbClasses);
					DbHelper.AlerScript();
					Adapter1C.Create();

					Class t = ScannedBarcode.class;
					// if (t.getName().contains("ScannedBarcode"))
					{
						// Field codeField = t.getField(BO.fields.CODE);
						// codeField.setAccessible(true);
						// DatabaseField anno =
						// codeField.getAnnotation(DatabaseField.class);
						// anno = (DatabaseField)
						// AnnotationInvocationHandler.setAttrValue(anno,
						// DatabaseField.class, "width", 50);

						// Annotation newAnnotation = new DatabaseField();
						//
						// Field field =
						// Class.class.getDeclaredField("annotations");
						// field.setAccessible(true);
						// Map<Class<? extends Annotation>, Annotation>
						// annotations = (Map<Class<? extends Annotation>,
						// Annotation>) field.get(ScannedBarcode.class);
						// annotations.put(DatabaseField.class, newAnnotation);

					}
					// DbHelper.Alter(t);
				} catch (Exception e) {
					log.ERROR("btnAlterDbClick", e);
				}
				// MainForm.this.setEnabled(true);
				UI.Stop(MainFormFull.this);
				JOptionPane.showMessageDialog(MainFormFull.this, "Обновление структуры БД завершена", "Обновление БД",
						JOptionPane.PLAIN_MESSAGE);
			}
		});
		btnTest.setVisible(User.current.isAdmin());
		btnTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new LoadDataTask().execute();
			}
		});
		btnSync.setVisible(User.current.isAdmin());
		btnSync.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SyncTask().execute();
			}
		});

		JPanel panelCats = new JPanel();

		panelCats.setLayout(null);

		JPanel panelReport = new JPanel();
		panelReport.setLayout(null);

		JButton button_18 = new JButton("Печать ценников");
		button_18.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PrintPrice form = new PrintPrice();
				form.setVisible(true);
			}
		});
		button_18.setBounds(15, 130, 150, 23);
		panelReport.add(button_18);

		JButton button = new JButton("Контрагенты");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BoListForm form = new ContractorListForm();
				form.Search();
				form.setVisible(true);
			}
		});
		button.setBounds(15, 55, 150, 23);
		panelCats.add(button);

		JButton button_1 = new JButton("Счета на оплату");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BoListForm form = new BillListFormFull();
				form.Search();
				form.setVisible(true);
			}
		});
		button_1.setBounds(15, 55, 150, 23);
		panelDocs.add(button_1);

		JButton button_2 = new JButton("Номенклатура");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BoListForm form = new ProductListForm();
				form.Search();
				form.setVisible(true);
			}
		});
		button_2.setBounds(15, 16, 150, 23);
		panelCats.add(button_2);

		JButton button_3 = new JButton("Приходная накладная");
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BoListForm form = new InvoiceListFormFull();
				form.Search();
				form.setVisible(true);
			}
		});
		button_3.setBounds(206, 16, 150, 23);
		panelDocs.add(button_3);

		JButton button_4 = new JButton("Ед. изм.");
		button_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BoListForm form = new UnitsListForm();
				form.Search();
				form.setVisible(true);
			}
		});
		button_4.setBounds(15, 94, 150, 23);
		panelCats.add(button_4);

		JButton button_19 = new JButton("Договоры");
		button_19.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BoListForm form = new AllContractListForm();
				form.Search();
				form.setVisible(true);
			}
		});
		button_19.setBounds(206, 16, 150, 23);
		panelCats.add(button_19);

		JButton button_23 = new JButton("Сотрудники");
		button_23.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!User.current.isAdmin())
					return;
				BoListForm form = new CoworkerListForm();
				form.Search();
				form.setVisible(true);
			}
		});
		button_23.setBounds(397, 16, 150, 23);
		panelCats.add(button_23);

		JButton button_33 = new JButton("Склады");
		button_33.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BoListForm form = new StoreListForm();
				form.Search();
				form.setVisible(true);
			}
		});
		button_33.setBounds(15, 159, 150, 23);
		panelCats.add(button_33);

		JButton button_5 = new JButton("Расходная накладная");
		button_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BoListForm form = new DeliveryNoteListFormFull();
				form.Search();
				form.setVisible(true);
			}
		});
		button_5.setBounds(15, 94, 150, 23);
		panelDocs.add(button_5);

		JButton button_6 = new JButton("Цены");
		button_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ViewPriceForm selectForm = new ViewPriceForm();
				selectForm.Search();
				selectForm.showDialog();
			}
		});
		button_6.setBounds(15, 11, 150, 23);
		panelReport.add(button_6);

		JButton button_7 = new JButton("Заказы");
		button_7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BoListForm form = new OrderListForm();
				form.Search();
				form.setVisible(true);
			}
		});
		button_7.setBounds(15, 16, 150, 23);
		panelDocs.add(button_7);

		JButton button_8 = new JButton("Чеки");
		button_8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BoListForm form = new CashVoucherListForm();
				form.Search();
				form.setVisible(true);
			}
		});
		button_8.setBounds(15, 150, 150, 23);
		panelDocs.add(button_8);

		JButton button_11 = new JButton("Остатки на складе");
		button_11.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				HtmlReporter.Create(RemainingStockView.class, new RemainingStockViewFactory().get(),
						"Остатки на складе");
			}
		});
		button_11.setBounds(197, 11, 150, 23);
		panelReport.add(button_11);

		JButton button_12 = new JButton("Проводки/движение");
		button_12.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				TransactionViewForm report = new TransactionViewForm();
				report.setVisible(true);
			}

		});
		button_12.setBounds(197, 89, 150, 23);
		panelReport.add(button_12);

		JButton button_13 = new JButton("Возврат поставщику");
		button_13.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BoListForm form = new ReturnOfGoodsListForm();
				form.Search();
				form.setVisible(true);
			}
		});
		button_13.setBounds(15, 200, 150, 23);
		panelDocs.add(button_13);

		JButton button_14 = new JButton("Остатки по группам");
		button_14.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				RemainingStockReportForm report = new RemainingStockReportForm();
				report.setVisible(true);
			}
		});
		button_14.setBounds(197, 50, 150, 23);
		panelReport.add(button_14);

		JButton button_15 = new JButton("Ценник по коду");
		button_15.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ShowPriceForm form = new ShowPriceForm();
				form.setVisible(true);
			}
		});
		button_15.setBounds(15, 89, 150, 23);
		panelReport.add(button_15);

		JButton button_26 = new JButton("Документы измененные");
		button_26.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DocCommitViewForm form = new DocCommitViewForm();
				form.setVisible(true);
			}
		});
		button_26.setBounds(376, 11, 150, 23);
		panelReport.add(button_26);

		JButton button_27 = new JButton("Себестоимость");
		button_27.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				CostPriceViewForm form = new CostPriceViewForm();
				form.setVisible(true);
			}
		});
		button_27.setBounds(197, 130, 150, 23);
		panelReport.add(button_27);

		JButton button_28 = new JButton("Задолженность покупателей");
		button_28.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				CustomerDebtViewForm form = new CustomerDebtViewForm();
				form.setVisible(true);
			}
		});
		button_28.setBounds(197, 263, 150, 23);
		panelReport.add(button_28);

		JButton button_29 = new JButton("Акт сверки");
		button_29.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ReconciliationReportForm form = new ReconciliationReportForm();
				form.setVisible(true);
			}
		});
		button_29.setBounds(197, 302, 150, 23);
		panelReport.add(button_29);

		JButton button_32 = new JButton("Отгрузка по Контрагенту");
		button_32.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ContractorTransactionViewForm form = new ContractorTransactionViewForm();
				form.setVisible(true);
			}
		});
		button_32.setBounds(197, 386, 150, 23);
		panelReport.add(button_32);

		JButton button_16 = new JButton("Списание товаров");
		button_16.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BoListForm form = new WriteOffProductListForm();
				form.Search();
				form.setVisible(true);
			}
		});
		button_16.setBounds(15, 239, 150, 23);
		panelDocs.add(button_16);

		JButton button_17 = new JButton("Оприходование товара");
		button_17.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BoListForm form = new TakeIntoInventoryListForm();
				form.Search();
				form.setVisible(true);
			}
		});
		button_17.setBounds(206, 55, 150, 23);
		panelDocs.add(button_17);

		JButton button_20 = new JButton("Возврат от покупателя");
		button_20.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BoListForm form = new ReturnFromCustomerListForm();
				form.Search();
				form.setVisible(true);
			}
		});
		button_20.setBounds(206, 200, 150, 23);
		panelDocs.add(button_20);

		JButton button_22 = new JButton("Доверенности");
		button_22.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BoListForm form = new Warrant4ReceiptListForm();
				form.Search();
				form.setVisible(true);
			}
		});
		button_22.setBounds(409, 13, 150, 23);
		panelDocs.add(button_22);

		JButton button_25 = new JButton("Складской журнал");
		button_25.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BoListForm form = new StoreDaybookListForm();
				form.Search();
				form.setVisible(true);
			}
		});
		button_25.setBounds(15, 326, 150, 23);
		panelDocs.add(button_25);

		JButton button_31 = new JButton("Сканировать ВСЁ");
		button_31.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SmartBarcodeForm form = new SmartBarcodeForm();
				form.setVisible(true);
			}
		});
		button_31.setBounds(10, 411, 150, 23);
		panelDocs.add(button_31);

		JButton button_21 = new JButton("Сравнение данных");
		button_21.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// TestDiff.ContractDiff();
				// TestIDs.ContractIDs();
			}
		});
		button_21.setBounds(401, 16, 167, 29);
		panelAdmin.add(button_21);

		JButton button_24 = new JButton("Новый пароль");
		button_24.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SetPasswordForm form = new SetPasswordForm();
				form.setVisible(true);
			}
		});
		button_24.setBounds(606, 16, 145, 29);
		panelAdmin.add(button_24);

		JButton btnNewButton = new JButton("БСО Приходных док.");
		btnNewButton.setToolTipText("БСО Приходных док.");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UI.Start(MainFormFull.this);
				Actions.SaveSFFromInDocAction.Do(new InvoiceFactoryFull());
				Actions.SaveSFFromInDocAction.Do(new ReturnFromCustomerFactory());
				UI.Stop(MainFormFull.this);
			}
		});
		btnNewButton.setBounds(15, 302, 145, 37);
		panelAdmin.add(btnNewButton);

		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.TRAILING).addComponent(tabbedPane,
				Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 984, Short.MAX_VALUE));
		groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup()
						.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE).addGap(6)));
		getContentPane().setLayout(groupLayout);

		setBounds(0, 0, 1000, 700);

		tabbedPane.addTab("Статистика", null, panelStatistics, null);
		if (User.current.isAdmin() || User.current.isDirector())
			tabbedPane.addTab("Директору", null, new PanelDirector(), null);
		tabbedPane.addTab("Склад", null, panelDocs, null);
		tabbedPane.addTab("Банк", null, new PanelBank(), null);
		// tabbedPane.addTab("Прокат", null, new RentPanel(), null);
		tabbedPane.addTab("Справочники", null, panelCats, null);
		tabbedPane.addTab("Отчеты", null, panelReport, null);
		if (Settings.isDesignTime || User.current.isAdmin()) {
			tabbedPane.addTab("Админ", null, panelAdmin, null);
		}
	}

	public void LoadData() {
		dnChart.setData(TransactionByDocPeriod.name(Format.AddDay(new Date(), -30), new Date(), new DeliveryNote()));
		cvChart.setData(TransactionByDocPeriod.name(Format.AddDay(new Date(), -30), new Date(), new CashVoucher()));
		topProduct.setData(TransactionByDocPeriod.TopProduct(Format.AddDay(new Date(), -30), new Date(), null, 20));
	}

	class LoadDataTask extends SwingWorker<Void, Void> {
		@Override
		protected Void doInBackground() throws Exception {
			UI.Start(MainFormFull.this);
			try {
				// Test test = new Test();
				// test.setUp();
				// test.test();
			} catch (Exception e) {
				log.ERROR("btnTestClick", e);
			}
			return null;
		}

		@Override
		protected void done() {
			UI.Stop(MainFormFull.this);
			JOptionPane.showMessageDialog(MainFormFull.this, "Загрузка новых данных завершена");
			// TODO:
			// JOptionPane.showMessageDialog(MainForm.this, "Eggs are not
			// supposed to be green.", "Inane error",
			// JOptionPane.ERROR_MESSAGE);
		}
	}

	class SyncTask extends SwingWorker<Void, Void> {

		boolean SyncRes = false;

		@Override
		protected Void doInBackground() throws Exception {
			UI.Start(MainFormFull.this);
			// MainForm.this.setEnabled(false);
			// TODO:UI
			Global.canBalanceBeMinus = true;
			SyncRes = Synchronization.Do();
			Global.canBalanceBeMinus = false;

			// Разогреваем кэш так как Synchronization.Do() вызывает
			// Cache.I().clearAllCache();
			Cache.I().HeatingUp(Global.heatingUpClasses);

			return null;
		}

		@Override
		protected void done() {
			UI.Stop(MainFormFull.this);
			if (SyncRes)
				JOptionPane.showMessageDialog(MainFormFull.this, "Синхронизация завершена успешно.", "Синхронизация",
						JOptionPane.PLAIN_MESSAGE);
			else
				JOptionPane.showMessageDialog(MainFormFull.this, "Ошибки при синхронизации. См. лог.", "Синхронизация",
						JOptionPane.ERROR_MESSAGE);
		}
	}
}
