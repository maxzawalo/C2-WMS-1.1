package maxzawalo.c2.free.ui.pc.form;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import maxzawalo.c2.base.os.Run;
import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.ui.pc.controls.SwingLink;
import maxzawalo.c2.base.utils.Global;

public class AboutFormFree extends JFrame {

	public AboutFormFree() {
		UI.SET(this);
		setTitle("О программе C2 " + Global.VERSION);
		setBounds(0, 0, 319, 234);

		SwingLink site = new SwingLink();
		site.setFont(site.getFont().deriveFont(14f));
		site.setBounds(30, 11, 200, 20);
		site.setup("Сайт программы");
		site.onClick = new AbstractAction() {
			public void actionPerformed(final ActionEvent evt) {
				 Run.OpenFile(Global.site);
			}
		};
		getContentPane().add(site);

		SwingLink history = new SwingLink();
		history.setFont(site.getFont().deriveFont(14f));
		history.setBounds(30, 42, 200, 20);
		history.setup("История изменений");
		history.onClick = new AbstractAction() {
			public void actionPerformed(final ActionEvent evt) {
				Run.OpenFile(Global.changesHistory);
			}
		};
		getContentPane().add(history);

		JButton btnUpdateC2 = new JButton();
		btnUpdateC2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UpdateC2();
			}
		});
		getContentPane().setLayout(null);
		btnUpdateC2.setIcon(UI.getUpdaterIcon(30, 30));
		btnUpdateC2.setToolTipText("Обновление С2");
		btnUpdateC2.setBounds(255, 25, 38, 39);
		getContentPane().add(btnUpdateC2);
		
		SwingLink mzLink = new SwingLink();
		mzLink.setup("Завало Максим");
			mzLink.onClick = new AbstractAction() {
			public void actionPerformed(final ActionEvent evt) {
				Run.OpenFile("https://vk.com/maxzawalo");
			}
		};
		mzLink.setFont(mzLink.getFont().deriveFont(14f));
		mzLink.setBounds(30, 126, 200, 20);
		getContentPane().add(mzLink);
		
		
		JLabel label = new JLabel("Авторы");
		label.setFont(new Font("Tahoma", Font.PLAIN, 14));
		label.setBounds(10, 101, 108, 14);
		getContentPane().add(label);
	}

	protected void UpdateC2() {
		FreeVersionForm.Full();
		// // TODO: тип С2 (free,1 user Итп)
		// try {
		// Process process = new ProcessBuilder(FileUtils.GetAppDir() +
		// "c2_updater.exe", Global.VERSION)
		// .start();
		// } catch (IOException e1) {
		// e1.printStackTrace();
		// }
		// System.exit(0);
	}
}