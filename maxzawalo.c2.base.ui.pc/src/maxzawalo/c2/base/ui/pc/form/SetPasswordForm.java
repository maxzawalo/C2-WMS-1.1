package maxzawalo.c2.base.ui.pc.form;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.base.data.factory.UserFactory;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.base.ui.pc.controls.ComboBoxBizControl;
import maxzawalo.c2.base.utils.Logger;

public class SetPasswordForm extends JFrame {

	protected Logger log = Logger.getLogger(this.getClass().getSimpleName());

	public SetPasswordForm() {
		getContentPane().setLayout(null);
		setBounds(0, 0, 418, 304);

		final BizControlBase user = new ComboBoxBizControl();
		user.fieldType = User.class;
		user.LoadList();
		// user.selectedBO = new User().GetByParam("hdw", Hardware.getP());
		user.setBo(null);
		// coworker.setFieldName("units");
		user.setCaption("Пользователь");
		user.setBounds(12, 0, 383, 53);
		getContentPane().add(user);

		final BizControlBase password = new BizControlBase(true);
		password.setCaption("Пароль");
		password.setBounds(12, 65, 237, 53);
		getContentPane().add(password);

		final BizControlBase passwordConfirm = new BizControlBase(true);
		passwordConfirm.setCaption("Повторить пароль");
		passwordConfirm.setBounds(12, 125, 237, 53);
		getContentPane().add(passwordConfirm);

		JButton button = new JButton("Установить");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				User u = (User) user.getBO();
				if (u == null || u.id == 0) {
					JOptionPane.showMessageDialog(SetPasswordForm.this, "Пользователь не выбран", "Ошибка",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (password.getText().equals(passwordConfirm.getText())) {
					try {
						new UserFactory().SetNewPassword(u, password.getText());
					} catch (Exception e) {
						log.ERROR("btnSetClick", e);
						JOptionPane.showMessageDialog(SetPasswordForm.this, "Ошибка БД. См. лог.", "Ошибка",
								JOptionPane.ERROR_MESSAGE);
					}
					JOptionPane.showMessageDialog(SetPasswordForm.this, "Пароль установлен", ":)",
							JOptionPane.PLAIN_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(SetPasswordForm.this, "Пароли не совпадают", "Ошибка",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		button.setBounds(267, 216, 128, 35);
		getContentPane().add(button);

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - getHeight()) / 2);
		setLocation(x, y);
	}
}