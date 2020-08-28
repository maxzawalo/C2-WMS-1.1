package maxzawalo.c2.full.ui.pc.view;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import maxzawalo.c2.base.Actions;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.data.factory.DocumentFactory;
import maxzawalo.c2.base.interfaces.TerminalEvent;
import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.free.bo.Settings;
import maxzawalo.c2.free.bo.StrictForm;
import maxzawalo.c2.free.data.factory.catalogue.StrictFormFactory;
import maxzawalo.c2.full.data.decoder.BarcodeDecoder;
import maxzawalo.c2.full.hardware.terminal.Terminal;

public class FindDocByBarcodeForm extends JFrame implements TerminalEvent {

	public FindDocByBarcodeForm() {
		getContentPane().setLayout(null);
		setBounds(0, 0, 154, 157);
		InitTerminal();
	}

	protected void InitTerminal() {
		Terminal.Uninit();
		Terminal.Init(Settings.startComPort);
		Terminal.callbacks.add(this);
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
			e.printStackTrace();
			// log.ERROR("onScan", e);
		}
	}

	protected void ProcessScanData(String barcode) {
		StrictFormFactory factory = new StrictFormFactory();
		String[] sfn = BarcodeDecoder.DecodeStrictNumber(barcode);
		if (sfn.length == 0) {
			Console.I().INFO(getClass(), "ProcessScanData", "Штрихкод не является номером БСО");
			return;
		}

		StrictForm form = factory.GetByNumber(sfn[0], sfn[1]);
		if (form == null)
			Console.I().INFO(getClass(), "ProcessScanData", "БСО не найден. Возможно введен некорректно.");
		DocumentFactory documentFactory = ((DocumentFactory) Actions.FactoryByRegTypeAction.Do(form.reg_type));
		DocumentBO doc = (DocumentBO) documentFactory.GetById(form.reg_id);
		if (doc == null)
			Console.I().INFO(getClass(), "ProcessScanData", "Документ не найден");
		Actions.OpenBoFormByInstanceAction.Do(doc);
	}
}