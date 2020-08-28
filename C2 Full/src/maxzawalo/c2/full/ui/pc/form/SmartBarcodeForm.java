package maxzawalo.c2.full.ui.pc.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;

import maxzawalo.c2.base.crypto.Hash;
import maxzawalo.c2.base.data.factory.FactoryBO;
import maxzawalo.c2.base.interfaces.TerminalEvent;
import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.ui.pc.form.ConsoleForm;
import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.free.bo.Settings;
import maxzawalo.c2.full.bo.ScannedBarcode;
import maxzawalo.c2.full.hardware.terminal.Terminal;

public class SmartBarcodeForm extends ConsoleForm implements TerminalEvent {
	JButton btnInit;
	JButton btnCreateLinked;

	List<ScannedBarcode> all = new ArrayList<>();

	public SmartBarcodeForm() {
		super();
		setTitle("Сканировать штрихкоды");

		btnClean.setVisible(false);

		btnInit = new JButton("");
		btnInit.setIcon(UI.getBarcodeScannerIcon(30, 30));
		btnInit.setToolTipText("Обновить сканер");
		btnInit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InitScanner();
			}
		});
		btnInit.setBounds(10, 195, 40, 40);
		getContentPane().add(btnInit);

		btnCreateLinked = new JButton("");
		btnCreateLinked.setToolTipText("Сохранить цепочку");
		btnCreateLinked.setIcon(UI.getCommitedIcon(22, 22));
		btnCreateLinked.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CreateLinked();
				Clean();
			}
		});
		btnCreateLinked.setBounds(933, 192, 40, 40);
		getContentPane().add(btnCreateLinked);

		InitScanner();
		onFormResized();
	}

	protected void CreateLinked() {
		Random random = new Random();
		String link = Hash.sha256("" + System.currentTimeMillis() + "" + random.nextLong() + "" + random.nextLong() + "" + random.nextLong());

		FactoryBO factory = new FactoryBO<>().Create(ScannedBarcode.class);
		for (ScannedBarcode sb : all) {
			sb.link = link;
		}
		try {
			factory.BulkSave(all);
		} catch (Exception e) {
			e.printStackTrace();
		}

		all.clear();
	}

	@Override
	protected void onFormResized() {
		super.onFormResized();
		if (btnInit != null)
			btnInit.setLocation(btnInit.getX(), this.getHeight() - btnInit.getHeight() - 50);
		if (btnCreateLinked != null)
			btnCreateLinked.setLocation(this.getWidth() - btnCreateLinked.getWidth() - 25, this.getHeight() - btnCreateLinked.getHeight() - 50);
	}

	protected void InitScanner() {
		Terminal.Uninit();
		Terminal.Init(Settings.startComPort);
		Terminal.callbacks.add(this);
	}

	@Override
	public void onScan(String value, boolean exception) {
		Console.I().INFO(getClass(), "onScan", value);
		ScannedBarcode sb = new ScannedBarcode();
		sb.code = value;
		all.add(sb);
		// barcode = barcode.substring(0, barcode.length() - 1);
		// Price price = new PriceFactory().GetByCode("00-" + barcode);
		// searchText.setText(price.product.name);
		// setSearch(price.product.name);
		// Search();
		// System.out.println(price);
	}
}