package maxzawalo.c2.full.ui.pc.document;

import java.util.List;

import javax.swing.JDialog;

import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.ui.pc.controls.KeyPressedTable;
import maxzawalo.c2.base.ui.pc.utils.TextTransfer;
import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.free.bo.Settings;
import maxzawalo.c2.free.bo.document.deliverynote.DeliveryNote;
import maxzawalo.c2.free.bo.document.invoice.InvoiceTablePart;
import maxzawalo.c2.free.bo.store.StoreDocBO;
import maxzawalo.c2.free.bo.store.StoreTP;
import maxzawalo.c2.free.data.factory.catalogue.UnitsFactory;
import maxzawalo.c2.free.data.factory.document.DeliveryNoteFactory;
import maxzawalo.c2.free.data.factory.document.StoreDocFactory;
import maxzawalo.c2.free.ui.pc.catalogue.LotOfProductListFormFree;
import maxzawalo.c2.free.ui.pc.document.store.InvoiceFormFree;
import maxzawalo.c2.full.data.decoder.BarcodeDecoder;
import maxzawalo.c2.full.data.factory.document.InvoiceFactoryFull;
import maxzawalo.c2.full.el_doc.ElDoc;
import maxzawalo.c2.full.hardware.terminal.Terminal;
import maxzawalo.c2.full.ui.pc.catalogue.LotOfProductListFormFull;
import maxzawalo.c2.full.ui.pc.form.AnaliticsForm;

public class InvoiceFormFull extends InvoiceFormFree {
	@Override
	protected void FreeTimeLimit() {
		// в полной версии отключаем
	}

	public InvoiceFormFull() {
		this(null);
	}

	public InvoiceFormFull(JDialog parent) {
		super(parent);
		factory = new InvoiceFactoryFull();
	}

	@Override
	protected void InitTerminal() {
		Terminal.Uninit();
		Terminal.Init(Settings.startComPort);
		Terminal.callbacks.add(this);
	}

	@Override
	protected void FromClipboard(String tpName) {
		try {
			String txt = new TextTransfer().getClipboardContents();
			// System.out.println(txt);
			for (String line : txt.split("[\\r\\n]+")) {

				String parts[] = line.split("[\\t\\t]+");

				String productName = parts[1].trim();
				double count = Format.extractDouble(parts[2]);
				String unitsCode = new UnitsFactory().GetByParam("name", parts[3].trim()).code;

				double price = Format.extractDouble(parts[4]);
				double rateVat = Format.extractDouble(parts[5]);
				double sum = Format.extractDouble(parts[6]);
				double sumVat = Format.extractDouble(parts[7]);
				double total = 0;
				if (parts.length == 9)
					total = Format.extractDouble(parts[8].trim().replace(",", "."));
				// TODO: type by name
				StoreTP newTp = ElDoc.CreateTpRow(InvoiceTablePart.Product.class, productName, unitsCode, count, price,
						sum, rateVat, sumVat, total);
				newTp.Calc("");

				elementBO.GetTPByName(tpName).add(newTp);
			}
			elementBO.CalcTotal();

		} catch (Exception e) {
			log.ERROR("FromClipboard", e);
			Console.I().ERROR(getClass(), "FromClipboard", e.getMessage());
		}

		TPChanged();
		grids.get(tpName).Refresh();
	}

	@Override
	protected LotOfProductListFormFree CreateSelectForm() {
		return new LotOfProductListFormFull(this, this);
	}

	protected void ProcessScanData(String barcode) {
		String[] sfn = BarcodeDecoder.DecodeStrictNumber(barcode);
		if (sfn.length == 0) {
			Console.I().INFO(getClass(), "ProcessScanData", "Штрихкод не является номером БСО");
			return;
		}

		in_form_number.onBOSelected(sfn[0] + sfn[1]);
	}

	@Override
	protected void ShowTransactions() {
		AnaliticsForm form = new AnaliticsForm(this);
		form.setVisible(true);
		form.setRegistrator((DocumentBO) this.elementBO);
	}

	@Override
	public void AddFromSourceDoc() {
		// TODO: унифицировать
		Console.I().INFO(getClass(), "AddFromSourceDoc", "=== Добавление из источника");
		if (elementBO.source_doc_id == 0) {
			Console.I().INFO(getClass(), "AddFromSourceDoc", "Документ не имеет источника");
			return;
		}
		StoreDocBO toDoc = elementBO;

		// TODO:elementBO.source_doc_type
		StoreDocFactory factory = new DeliveryNoteFactory();
		StoreDocBO fromDoc = (DeliveryNote) factory.GetById(elementBO.source_doc_id);
		factory.LoadTablePart(fromDoc);
		List[] ret = StoreTP.AppendTP(fromDoc, toDoc, new InvoiceTablePart.Product(), true);
		List<StoreTP> newList = ret[0];
		List<StoreTP> deletedList = ret[1];

		for (StoreTP n : newList)
			Console.I().INFO(getClass(), "AddFromSourceDoc", "Добавлено: " + n.toConsole());

		for (StoreTP del : deletedList)
			Console.I().INFO(getClass(), "AddFromSourceDoc", "В источнике нет: " + del.toConsole());

		for (Object tp : toDoc.TablePartProduct)
			((StoreTP) tp).onChanged = onTablePartChanged;

		for (KeyPressedTable g : grids.values())
			g.Refresh();
	}
}