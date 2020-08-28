package maxzawalo.c2.free.ui.pc.document.store;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import maxzawalo.c2.base.bo.Coworker;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.base.ui.pc.controls.ComboBoxBizControl;
import maxzawalo.c2.base.ui.pc.controls.KeyPressedTable;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.bo.Settings;
import maxzawalo.c2.free.bo.Store;
import maxzawalo.c2.free.bo.StrictFormType;
import maxzawalo.c2.free.bo.document.deliverynote.DeliveryNote;
import maxzawalo.c2.free.bo.document.deliverynote.DeliveryNoteTablePart;
import maxzawalo.c2.free.data.factory.catalogue.ContractorFactory;
import maxzawalo.c2.free.data.factory.document.DeliveryNoteFactory;
import maxzawalo.c2.free.data.factory.document.StoreDocFactory;
import maxzawalo.c2.free.ui.pc.form.FreeVersionForm;
import maxzawalo.c2.free.ui.pc.model.document.DeliveryNoteTablePartModel;

public class DeliveryNoteFormFree extends StoreDocForm<DeliveryNote, DeliveryNoteTablePart.Product> {

	public static final String REPORT_TN2 = "TN2";
	public static final String REPORT_TTNVert = "TTNVert";
	public static final String REPORT_TTNVertWithAdd = "TTNVertWithAdd";

	BizControlBase procuration_number;
	BizControlBase procuration_date;
	BizControlBase procuration_name;
	BizControlBase rows_per_page;
	protected BizControlBase pageBreak;

	JPopupMenu menu = new JPopupMenu();

	public DeliveryNoteFormFree() {
		this(null);
	}

	public DeliveryNoteFormFree(JDialog parent) {
		super(parent);
		FreeTimeLimit();
		setBounds(0, 0, 1000, 700);

		factory = new DeliveryNoteFactory();
		for (String name : GetTPNames())
			tablePartModels.put(name, new DeliveryNoteTablePartModel());

		JPanel tabStrictForms = new JPanel();
		tabbedPane.addTab("БСО", null, tabStrictForms, null);
		tabStrictForms.setLayout(null);

		out_form_batch = new BizControlBase();
		out_form_batch.setBounds(164, 12, 140, 56);
		tabStrictForms.add(out_form_batch);
		out_form_batch.setCaption("Исх. серия");

		out_form_number = new BizControlBase();
		out_form_number.setBounds(304, 12, 140, 56);
		tabStrictForms.add(out_form_number);
		out_form_number.setCaption("Исх. номер");

		out_form_type = new JComboBox<StrictFormType>();
		out_form_type.setBounds(12, 30, 140, 30);
		tabStrictForms.add(out_form_type);

		JLabel label = new JLabel("Исх. тип");
		label.setBounds(12, 12, 69, 20);
		tabStrictForms.add(label);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 96, 840, 116);
		tabStrictForms.add(scrollPane);

		strictFormTable = new KeyPressedTable();
		strictFormTable.EnableMarkDeleteRow();
		strictFormTable.settingsKey = getClass().getSimpleName() + ".strictFormTable";
		strictFormTable.setFillsViewportHeight(true);
		scrollPane.setViewportView(strictFormTable);

		JButton button_1 = new JButton("Испортить");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				RuinStrictForm();
			}
		});
		button_1.setBounds(485, 30, 115, 38);
		tabStrictForms.add(button_1);

		JPanel tabTN = new JPanel();
		tabbedPane.addTab("ТН", null, tabTN, null);
		tabTN.setLayout(null);

		JPanel box = new JPanel();
		box.setLayout(null);
		box.setBounds(578, 12, 370, 166);
		box.setBorder(BorderFactory.createTitledBorder("Доверенность"));
		tabTN.add(box);

		procuration_number = new BizControlBase();
		procuration_number.setBounds(12, 31, 218, 62);
		box.add(procuration_number);
		procuration_number.setCaption("Номер");
		procuration_number.setFieldName(DeliveryNote.fields.PROCURATION_NUMBER);

		procuration_date = new BizControlBase();
		procuration_date.setBounds(231, 31, 129, 62);
		box.add(procuration_date);
		procuration_date.setCaption("Дата");
		procuration_date.setFieldName(DeliveryNote.fields.PROCURATION_DATE);

		procuration_name = new BizControlBase();
		procuration_name.setBounds(12, 92, 348, 62);
		box.add(procuration_name);
		procuration_name.setCaption("ФИО");
		procuration_name.setFieldName(DeliveryNote.fields.PROCURATION_NAME);

		final BizControlBase shipment_motive = new BizControlBase();
		shipment_motive.fieldType = String.class;
		shipment_motive.setBounds(12, 12, 348, 62);
		shipment_motive.setCaption("Основание отпуска");
		shipment_motive.setFieldName(DeliveryNote.fields.SHIPMENT_MOTIVE);
		tabTN.add(shipment_motive);

		doc_contract.afterBOSelected.add(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (elementBO.shipment_motive == null || elementBO.shipment_motive.isEmpty())
					elementBO.shipment_motive = "" + ((elementBO.doc_contract != null) ? elementBO.doc_contract : "");
				shipment_motive.setBo(elementBO);
				shipment_motive.revalidate();
			}
		});

		BizControlBase shipment_permited = new ComboBoxBizControl();
		shipment_permited.fieldType = Coworker.class;
		shipment_permited.LoadList();
		shipment_permited.setBounds(12, 86, 442, 62);
		shipment_permited.setCaption("Отпуск разрешил");
		shipment_permited.setFieldName(DeliveryNote.fields.SHIPMENT_PERMITED);
		tabTN.add(shipment_permited);

		BizControlBase shipment_produced = new ComboBoxBizControl();
		shipment_produced.fieldType = Coworker.class;
		shipment_produced.LoadList();
		shipment_produced.setBounds(12, 160, 442, 62);
		shipment_produced.setCaption("Отпуск произвел");
		shipment_produced.setFieldName(DeliveryNote.fields.SHIPMENT_PRODUCED);
		tabTN.add(shipment_produced);

		pageBreak = new BizControlBase();
		pageBreak.setCaption("Разрыв страницы");
		pageBreak.setBounds(12, 234, 152, 62);
		tabTN.add(pageBreak);

		JPanel tabTTN = new JPanel();
		tabbedPane.addTab("ТТН", null, tabTTN, null);
		tabTTN.setLayout(null);

		final BizControlBase client = new BizControlBase();
		client.setCaption("Заказчик");
		client.setFieldName(DeliveryNote.fields.CLIENT);
		client.fieldType = Contractor.class;
		client.setBounds(12, 12, 428, 56);
		tabTTN.add(client);

		contractor.afterBOSelected.add(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (elementBO.client == null) {
					elementBO.client = elementBO.contractor;
					client.onBOSelected(elementBO.client);
					client.revalidate();
				}
			}
		});

		BizControlBase shipper_hand_in = new ComboBoxBizControl();
		shipper_hand_in.fieldType = Coworker.class;
		shipper_hand_in.LoadList();
		shipper_hand_in.setCaption("Сдал отправитель");
		shipper_hand_in.setFieldName(DeliveryNote.fields.SHIPPER_HAND_IN);
		shipper_hand_in.setBounds(498, 12, 428, 56);
		tabTTN.add(shipper_hand_in);

		BizControlBase waybill = new BizControlBase();
		waybill.setCaption("Путевой лист");
		waybill.setFieldName(DeliveryNote.fields.WAYBILL);
		waybill.setBounds(12, 80, 428, 56);
		tabTTN.add(waybill);

		BizControlBase car = new BizControlBase();
		car.setCaption("Автомобиль");
		car.setFieldName(DeliveryNote.fields.CAR);
		car.setBounds(12, 148, 428, 56);
		tabTTN.add(car);

		BizControlBase driver = new BizControlBase();
		driver.setCaption("Водитель");
		driver.setFieldName(DeliveryNote.fields.DRIVER);
		driver.setBounds(12, 216, 428, 56);
		tabTTN.add(driver);

		final BizControlBase lading_place = new BizControlBase();
		lading_place.setCaption("Пункт погрузки");
		lading_place.setFieldName("lading_place");
		lading_place.setBounds(498, 80, 428, 56);
		tabTTN.add(lading_place);

		final BizControlBase discharge_place = new BizControlBase();
		discharge_place.setCaption("Пункт разгрузки");
		discharge_place.setFieldName(DeliveryNote.fields.DISCHARGE_PLACE);
		discharge_place.setBounds(498, 148, 428, 56);
		tabTTN.add(discharge_place);

		client.afterBOSelected.add(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				elementBO.client = new ContractorFactory().LoadContactInfo(elementBO.client);
				if (elementBO.discharge_place == null || elementBO.discharge_place.isEmpty())
					elementBO.discharge_place = ""
							+ ((elementBO.client.legal_address != null) ? elementBO.client.legal_address : "");
				discharge_place.onBOSelected(elementBO.discharge_place);
				discharge_place.revalidate();
			}
		});

		rows_per_page = new BizControlBase();
		// bizControl.setFieldName("discharge_place");
		rows_per_page.setCaption("Строк на странице");
		rows_per_page.setBounds(498, 226, 146, 56);
		tabTTN.add(rows_per_page);

		store.afterBOSelected.add(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// elementBO.lading_place = ((Store) store.selectedBO).adress;
				lading_place.onBOSelected(((Store) store.selectedBO).address);
				lading_place.revalidate();
			}
		});

		LoadStrictFormTypes();

		menu.add(new JMenuItem(new AbstractAction("ТН-2") {
			public void actionPerformed(ActionEvent e) {
				PrintTN2();
			}
		}));

		menu.addSeparator();
		menu.add(new JMenuItem(new AbstractAction("ТТН вертикальная") {
			public void actionPerformed(ActionEvent e) {
				PrintTTNVert();
			}
		}));
		menu.addSeparator();
		menu.add(new JMenuItem(new AbstractAction("ТТН вертик. с приложением") {
			public void actionPerformed(ActionEvent e) {
				PrintTTNVertWithAdd();
			}
		}));
	}

	@Override
	public boolean Print() {
		if (!Check4Print(""))
			return false;
		menu.show(btnPrint, btnPrint.getX(), btnPrint.getY() - btnPrint.getHeight() - menu.getPreferredSize().height);
		return true;
	}

	@Override
	public void Load(int id) {
		super.Load(id);
		setStrictFormTable();
		setStrictForm();
		System.out.println(elementBO.CalcSumTotal());
	}

	// @Override
	// public boolean Save() {
	// if (!super.Save())
	// return false;
	// ChangeStrictForm();
	// return true;
	// }

	@Override
	public void setData() {
		{
			if (elementBO.lading_place == null || elementBO.lading_place.isEmpty())
				elementBO.lading_place = Settings.myFirm.legal_address;
		}
		super.setData();
		rows_per_page.setText("1");
		pageBreak.setText("0");
	}

	@Override
	protected void GenerateReportData(String name) {
		DeliveryNote doc = factory.GetById(elementBO.id);
		((StoreDocFactory) factory).LoadTablePart(doc);
		new ContractorFactory().setContactInfoFields(doc.contractor);
		new ContractorFactory().setContactInfoFields(doc.client);
		doc.rowsPerPage = Integer.parseInt(rows_per_page.getText());
		reportPath = doc.Dump4Report(name, gson);
	}

	@Override
	protected void FreeSoon() {
		FreeVersionForm.Soon();
	}

	public void PrintTN2() {
		Print(REPORT_TN2);
	}

	public void PrintTTNVert() {
		Print(REPORT_TTNVert);
	}

	public void PrintTTNVertWithAdd() {
		FreeVersionForm.Full();
	}
}