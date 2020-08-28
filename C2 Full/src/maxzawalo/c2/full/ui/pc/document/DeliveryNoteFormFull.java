package maxzawalo.c2.full.ui.pc.document;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenuItem;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.os.Run;
import maxzawalo.c2.base.ui.pc.controls.KeyPressedTable;
import maxzawalo.c2.base.ui.pc.form.BoListForm;
import maxzawalo.c2.base.ui.pc.model.BOTableModel;
import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.base.utils.Global;
import maxzawalo.c2.free.bo.Settings;
import maxzawalo.c2.free.bo.document.bill.Bill;
import maxzawalo.c2.free.bo.document.deliverynote.DeliveryNote;
import maxzawalo.c2.free.bo.document.deliverynote.DeliveryNoteTablePart;
import maxzawalo.c2.free.bo.store.StoreDocBO;
import maxzawalo.c2.free.bo.store.StoreTP;
import maxzawalo.c2.free.data.factory.document.BillFactory;
import maxzawalo.c2.free.data.factory.document.StoreDocFactory;
import maxzawalo.c2.free.ui.pc.catalogue.LotOfProductListFormFree;
import maxzawalo.c2.free.ui.pc.document.store.DeliveryNoteFormFree;
import maxzawalo.c2.full.data.decoder.BarcodeDecoder;
import maxzawalo.c2.full.hardware.terminal.Terminal;
import maxzawalo.c2.full.report.code.DeliveryNoteReporterTN2;
import maxzawalo.c2.full.report.code.DeliveryNoteReporterTTN;
import maxzawalo.c2.full.report.code.DeliveryNoteReporterTTNAddition;
import maxzawalo.c2.full.reporter.Xlsx;
import maxzawalo.c2.full.ui.pc.catalogue.LotOfProductListFormFull;
import maxzawalo.c2.full.ui.pc.form.AnaliticsForm;

public class DeliveryNoteFormFull extends DeliveryNoteFormFree {
	@Override
	protected void FreeTimeLimit() {
		// в полной версии отключаем
	}

	public DeliveryNoteFormFull() {
		this(null);
	}

	@Override
	protected void InitTerminal() {
		Terminal.Uninit();
		Terminal.Init(Settings.startComPort);
		Terminal.callbacks.add(this);
	}

	public DeliveryNoteFormFull(JDialog parent) {
		super(parent);

		for (String name : GetTPNames()) {
			KeyPressedTable grid = grids.get(name);
			if (grid == null)
				continue;
			grid.rowPopup.removeAll();
			JMenuItem setZeroAdditionItem = new JMenuItem("Установить наценку в >0<");
			setZeroAdditionItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SetZeroAddition(grid);
				}
			});
			grid.rowPopup.add(setZeroAdditionItem);
		}

		JButton btnAdd2TP = new JButton("Д");
		btnAdd2TP.setToolTipText("Дополнить ТЧ из Счета");
		btnAdd2TP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BoListForm selectListForm = new BillListFormFull();
				selectListForm.selectItem(DeliveryNoteFormFull.this, elementBO);
			}
		});
		btnAdd2TP.setBounds(359, 152, 51, 40);
		topPanel.add(btnAdd2TP);
	}

	@Override
	protected void FreeSoon() {
		// в полной версии отключаем
	}

	@Override
	protected void LoadReportFromService(String reportName) {
		DeliveryNote doc = new DeliveryNote().FromDump(reportPath, gson);

		// doc = factory.GetById(elementBO.id);
		// ((StoreDocFactory) factory).LoadTablePart(doc);
		// doc.calcFields.put("rowsPerPage", 5);

		reportPath = reportPath.replace("c2_report", "xlsx");
		// int rowsPerPage = doc.rowsPerPage;// Integer.parseInt("" +
		// doc.calcFields.get("rowsPerPage"));
		switch (reportName) {
		case REPORT_TN2:
			Xlsx.PrintMatrix(reportPath, Arrays.asList(doc), new DeliveryNoteReporterTN2(Integer.parseInt(pageBreak.getText())), 1, 1);
			// (reportPath, doc, doc.TablePartProduct, new
			// DeliveryNoteReporterTN2());
			break;
		case REPORT_TTNVert:
			// Xlsx.PrintDoc(reportPath, doc, doc.TablePartProduct, new
			// DeliveryNoteReporterTTN(false, doc.rowsPerPage),
			// true);
			Xlsx.PrintMatrix(reportPath, Arrays.asList(doc), new DeliveryNoteReporterTTN(), 1, 1);
			break;
		case REPORT_TTNVertWithAdd:
			Xlsx.PrintMatrix(reportPath, Arrays.asList(doc), new DeliveryNoteReporterTTN(true, doc.rowsPerPage), 1, 1);
			// Xlsx.PrintDoc(reportPath, doc, doc.TablePartProduct, new
			// DeliveryNoteReporterTTN(true, doc.rowsPerPage),
			// false);
			Xlsx.PrintDocAddition(AdditionPath(), doc, new DeliveryNoteReporterTTNAddition(), doc.rowsPerPage);
			break;
		}
	}

	@Override
	protected void ShowReport(String reportName) {
		super.ShowReport(reportName);
		switch (reportName) {
		case REPORT_TTNVertWithAdd:
			String pathname = AdditionPath().replace("c2_report", "xlsx");
			if (!Global.RunInTest)
				Run.OpenFile(pathname);
			else if (!new File(pathname).exists())
				throw new UnsupportedOperationException("File not exists" + pathname);
			break;
		}
	}

	protected String AdditionPath() {
		return reportPath.replace(REPORT_TTNVertWithAdd, REPORT_TTNVertWithAdd + "_Add");
	}

	@Override
	public void PrintTTNVertWithAdd() {
		Print(REPORT_TTNVertWithAdd);
	}

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

	void SetZeroAddition(KeyPressedTable grid) {
		for (int row : grid.getSelectedRows()) {
			StoreTP tp = (StoreTP) ((BOTableModel) grid.getModel()).getList().get(row);
			tp.discount = 0;
			tp.Calc(StoreTP.fields.DISCOUNT);
		}
		grid.Refresh();
	}

	@Override
	protected void ShowTransactions() {
		AnaliticsForm form = new AnaliticsForm(this);
		form.setVisible(true);
		form.setRegistrator((DocumentBO) this.elementBO);
	}

	@Override
	public void onBOSelected(BO selectedBO) {
		// TODO: унифицировать
		Console.I().INFO(getClass(), "onBOSelected", "=== Добавление из Счета");
		StoreDocBO toDoc = elementBO;

		StoreDocFactory factory = new BillFactory();
		StoreDocBO fromDoc = (Bill) factory.GetById(selectedBO.id);
		factory.LoadTablePart(fromDoc);

		List[] ret = StoreTP.AppendTP(fromDoc, toDoc, new DeliveryNoteTablePart.Product(), true);
		List<StoreTP> newList = ret[0];
		List<StoreTP> deletedList = ret[1];
		List<StoreTP> existList = ret[2];

		for (StoreTP n : newList)
			Console.I().INFO(getClass(), "onBOSelected", "Добавлено: " + n.toConsole());

		for (StoreTP el : existList)
			Console.I().INFO(getClass(), "onBOSelected", "Есть в ТЧ: " + el.toConsole());

		for (Object tp : toDoc.TablePartProduct)
			((StoreTP) tp).onChanged = onTablePartChanged;

		for (KeyPressedTable g : grids.values())
			g.Refresh();
	}
}