package maxzawalo.c2.free.ui.pc.form;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import maxzawalo.c2.base.os.Run;
import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.ui.pc.controls.SwingLink;
import maxzawalo.c2.base.utils.Global;

public class FreeVersionForm extends JFrame {
	public FreeVersionForm() {
		UI.SET(this);
		setBounds(0, 0, 351, 206);

		setTitle("Бесплатная версия");
		getContentPane().setLayout(null);

		JLabel label = new JLabel("<html><pre>В бесплатной версии данный \nфункционал не предусмотрен</pre></html>");
		label.setFont(new Font("Tahoma", Font.PLAIN, 15));
		label.setBounds(47, 11, 262, 59);
		getContentPane().add(label);

		JButton btnNewButton = new JButton("ОК");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		btnNewButton.setBounds(127, 117, 89, 23);
		getContentPane().add(btnNewButton);

		SwingLink swingLink = new SwingLink();
		swingLink.setup("Скачать полную версию");
		swingLink.setFont(swingLink.getFont().deriveFont(14f));
		swingLink.setBounds(93, 81, 170, 20);
		swingLink.onClick = new AbstractAction() {
			public void actionPerformed(final ActionEvent evt) {
				Run.OpenFile(Global.downloadFullPage);
			}
		};
		getContentPane().add(swingLink);
	}

	public static void Full() {
		FreeVersionForm form = new FreeVersionForm();
		form.setVisible(true);
	}

	public static void Soon() {
		JOptionPane.showMessageDialog(null, "Уже скоро...", "Функция в разработке", JOptionPane.INFORMATION_MESSAGE);
	}

	public static void Limit() {
		if (new java.util.Date().getTime() >= maxzawalo.c2.base.utils.Format.GetDate("01.05.2018").getTime()) {
			javax.swing.JOptionPane.showMessageDialog(null, "Закончился срок эксплуатации данной версии.", "Скачать.",
					javax.swing.JOptionPane.INFORMATION_MESSAGE);
			maxzawalo.c2.base.os.Run.OpenFile(maxzawalo.c2.base.utils.Global.downloadPage);
			System.exit(0);
		}
	}
}