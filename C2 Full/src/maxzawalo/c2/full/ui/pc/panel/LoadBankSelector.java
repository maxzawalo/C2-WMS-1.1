package maxzawalo.c2.full.ui.pc.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.utils.Logger;
import maxzawalo.c2.full.data.load_bank.BankLoader;
import maxzawalo.c2.full.data.load_bank.LoadAKBB;
import maxzawalo.c2.full.data.load_bank.LoadBELB;
import maxzawalo.c2.full.data.load_bank.LoadBank;

public class LoadBankSelector extends JFrame {

	Logger log = Logger.getLogger(LoadBankSelector.class);

	public LoadBankSelector() {
		UI.SET(this);
		getContentPane().setLayout(null);
		setBounds(0, 0, 219, 188);

		JButton btnBELB = new JButton("БелВЭБ");
		btnBELB.setToolTipText(btnBELB.getText());
		btnBELB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Load(new FileNameExtensionFilter("Текстовые файлы", "txt"), new LoadBELB());
			}
		});
		btnBELB.setBounds(10, 11, 183, 23);
		getContentPane().add(btnBELB);

		JButton btnAKBB = new JButton("Беларусбанк");
		btnAKBB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Load(new FileNameExtensionFilter("XML", "xml"), new LoadAKBB());
			}
		});
		btnAKBB.setToolTipText(btnAKBB.getText());
		btnAKBB.setBounds(10, 57, 183, 23);

		getContentPane().add(btnAKBB);

		JButton btnBAPB = new JButton("Белагропромбанк");
		btnBAPB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnBAPB.setToolTipText(btnBAPB.getText());
		btnBAPB.setBounds(10, 105, 183, 23);
		getContentPane().add(btnBAPB);
	}

	public void Load(FileFilter filter, BankLoader loader) {
		JFileChooser fileopen = new JFileChooser();
		fileopen.setFileFilter(filter);
		int ret = fileopen.showDialog(null, "Открыть файл");
		if (ret == JFileChooser.APPROVE_OPTION) {
			File file = fileopen.getSelectedFile();
			LoadBank.Load(loader, file.getAbsolutePath());
		}
	}
}