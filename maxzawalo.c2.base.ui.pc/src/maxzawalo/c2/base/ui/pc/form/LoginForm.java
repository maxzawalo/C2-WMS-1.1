package maxzawalo.c2.base.ui.pc.form;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.base.data.factory.UserFactory;
import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.base.ui.pc.controls.ComboBoxBizControl;
import maxzawalo.c2.base.utils.Global;
import maxzawalo.c2.base.utils.Logger;
import maxzawalo.c2.free.hardware.Hardware;

public class LoginForm extends JFrame {

	BizControlBase user;
	BizControlBase password;
	public Action onLogin;

	public LoginForm() {
		setTitle("C2 " + Global.VERSION);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		UI.SET(this);
		setBounds(0, 0, 421, 200);
		getContentPane().setLayout(null);

		user = new ComboBoxBizControl();
		user.fieldType = User.class;
		user.LoadList();
		user.selectedBO = new UserFactory().GetByParam(User.fields.HARDWARE, Hardware.getP());
		user.setBo(null);
		// coworker.setFieldName("units");
		user.setCaption("Пользователь");
		user.setBounds(12, 0, 383, 53);
		getContentPane().add(user);

		password = new BizControlBase(true);
		password.enterPressed.add(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DoEnter();
			}
		});

		// coworker.LoadList()-;
		// coworker.setFieldName("units");
		password.setCaption("Пароль");
		password.setBounds(12, 50, 383, 53);
		getContentPane().add(password);

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - getHeight()) / 2);
		setLocation(x, y);

		JButton button = new JButton("Вход");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DoEnter();
			}
		});
		button.setBounds(174, 122, 97, 29);
		getContentPane().add(button);

		JButton button_1 = new JButton("Отменить");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		button_1.setBounds(281, 122, 114, 29);
		getContentPane().add(button_1);
	}

	public void EnterSimpleNCheck() {
		boolean DevUser = true;
		if (!DevUser) {
			User u = new UserFactory().GetByHardware(Hardware.getP());
			if (u == null) {
				Logger log = Logger.getLogger(System.class);
				log.FATAL("",
				        "У вас нет лицензии на данное програмное обеспечение. Обратитесь к разработчикам. https://vk.com/c2_wms");
				// log.FATAL("main", Hardware.getP());
				System.exit(ABORT);
			}
			if (u.isSimple()) {
				user.onBOSelected(u);
				DoEnter();
			}
		}
	}

	protected void DoEnter() {
		User u = (User) user.getBO();
		if (!u.isSimple()) {
			if (!UserFactory.CheckPassword(u, password.getText())) {
				JOptionPane.showMessageDialog(LoginForm.this, "Неверный пароль", "Ошибка", JOptionPane.ERROR_MESSAGE);
				password.setText("");
				return;
			}
		}
		User.current = u;
		setVisible(false);
		onLogin.actionPerformed(null);
	}
}