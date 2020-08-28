package maxzawalo.c2.full.ui.pc.document;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import maxzawalo.c2.base.bo.Coworker;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.base.ui.pc.controls.ComboBoxBizControl;
import maxzawalo.c2.base.ui.pc.controls.KeyPressedTable;
import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.free.bo.Settings;
import maxzawalo.c2.free.bo.StrictFormType;
import maxzawalo.c2.free.data.factory.catalogue.ContractorFactory;
import maxzawalo.c2.free.ui.pc.catalogue.LotOfProductListFormFree;
import maxzawalo.c2.free.ui.pc.document.store.StoreDocForm;
import maxzawalo.c2.free.ui.pc.model.document.DeliveryNoteTablePartModel;
import maxzawalo.c2.full.bo.document.return_of_goods.ReturnOfGoods;
import maxzawalo.c2.full.bo.document.return_of_goods.ReturnOfGoodsTablePart;
import maxzawalo.c2.full.data.decoder.BarcodeDecoder;
import maxzawalo.c2.full.data.factory.document.ReturnOfGoodsFactory;
import maxzawalo.c2.full.hardware.terminal.Terminal;
import maxzawalo.c2.full.report.code.ReturnOfGoodsReporter;
import maxzawalo.c2.full.reporter.Xlsx;
import maxzawalo.c2.full.ui.pc.catalogue.LotOfProductListFormFull;
import maxzawalo.c2.full.ui.pc.form.AnaliticsForm;

public class ReturnOfGoodsForm extends StoreDocForm<ReturnOfGoods, ReturnOfGoodsTablePart.Product> {
	// BizControlBase out_form_batch;
	// BizControlBase out_form_number;
	// JComboBox<StrictFormType> out_form_type;
	// private KeyPressedTable strictFormTable;

	@Override
	protected void FreeTimeLimit() {
		// в полной версии отключаем
	}

	public ReturnOfGoodsForm() {
		this(null);
	}

	@Override
	protected void InitTerminal() {
		Terminal.Uninit();
		Terminal.Init(Settings.startComPort);
		Terminal.callbacks.add(this);
	}

	public ReturnOfGoodsForm(JDialog parent) {
		super(parent);
		setBounds(0, 0, 1000, 700);
		factory = new ReturnOfGoodsFactory();

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

		final BizControlBase shipment_motive = new BizControlBase();
		shipment_motive.fieldType = String.class;
		shipment_motive.setBounds(12, 12, 442, 62);
		shipment_motive.setFieldName(ReturnOfGoods.fields.SHIPMENT_MOTIVE);
		shipment_motive.setCaption("Основание отпуска");
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
		shipment_permited.setFieldName(ReturnOfGoods.fields.SHIPMENT_PERMITED);
		shipment_permited.setCaption("Отпуск разрешил");
		tabTN.add(shipment_permited);

		BizControlBase shipment_produced = new ComboBoxBizControl();
		shipment_produced.fieldType = Coworker.class;
		shipment_produced.LoadList();
		shipment_produced.setBounds(12, 160, 442, 62);
		shipment_produced.setFieldName(ReturnOfGoods.fields.SHIPMENT_PRODUCED);
		shipment_produced.setCaption("Отпуск произвел");
		tabTN.add(shipment_produced);

		LoadStrictFormTypes();

		Terminal.Uninit();
		Terminal.Init(Settings.startComPort);
		Terminal.callbacks.add(this);
	}

	@Override
	public boolean Print() {
		if (!super.Print())
			return false;
		// UI.Start(this);

		// UI.Stop(this);
		return true;
	}

	@Override
	protected void GenerateReportData(String name) {
		ReturnOfGoods doc = factory.GetById(elementBO.id);
		((ReturnOfGoodsFactory) factory).LoadTablePart(doc);
		new ContractorFactory().setContactInfoFields(doc.contractor);
		reportPath = doc.Dump4Report(name, gson);
	}

	@Override
	protected void LoadReportFromService(String reportName) {
		ReturnOfGoods doc = new ReturnOfGoods().FromDump(reportPath, gson);
		reportPath = reportPath.replace("c2_report", "xlsx");
		Xlsx.PrintMatrix(reportPath, Arrays.asList(doc), new ReturnOfGoodsReporter(), 1, 1);
		// Xlsx.PrintDoc(reportPath, doc, doc.TablePartProduct, new
		// ReturnOfGoodsReporter());
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
	protected LotOfProductListFormFree CreateSelectForm() {
		return new LotOfProductListFormFull(this, this);
	}

	protected void ProcessScanData(String barcode) {
		String[] sfn = BarcodeDecoder.DecodeStrictNumber(barcode);
		if (sfn.length == 0) {
			Console.I().WARN(getClass(), "ProcessScanData", "Штрихкод не является номером БСО");
			return;
		}
		out_form_batch.onBOSelected(sfn[0]);
		out_form_number.onBOSelected(sfn[1]);
	}

	@Override
	protected void ShowTransactions() {
		AnaliticsForm form = new AnaliticsForm(this);
		form.setVisible(true);
		form.setRegistrator((DocumentBO) this.elementBO);
	}
}