package maxzawalo.c2.free.ui.pc;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.AbstractAction;
import javax.swing.Action;

import com.j256.ormlite.logger.LocalLog;

import maxzawalo.c2.base.bo.Coworker;
import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.base.data.DbHelper;
import maxzawalo.c2.base.data.factory.UserFactory;
import maxzawalo.c2.base.ui.pc.form.LoginForm;
import maxzawalo.c2.base.utils.FileUtils;
import maxzawalo.c2.base.utils.Global;
import maxzawalo.c2.base.utils.Logger;
import maxzawalo.c2.base.utils.Logger.LogLevel;
import maxzawalo.c2.free.bo.ClassesFree;
import maxzawalo.c2.free.bo.Settings;
import maxzawalo.c2.free.cache.Cache;
import maxzawalo.c2.free.data.factory.GlobalConstantsFactory;
import maxzawalo.c2.free.ui.pc.form.FreeVersionForm;
import maxzawalo.c2.free.ui.pc.form.MainFormFree;

public class Main {
	static {
		Global.VERSION = "1.1.8.27";
		Logger.setLevel(LogLevel.TEST);
		System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");
	}

	static Logger log = Logger.getLogger(Main.class);
	// public static String connectionString =
	// "jdbc:mysql://localhost/warehouse?user=root&password=root";
	// public static String connectionString =
	// "jdbc:mysql://192.168.1.100/warehouse?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&autoReconnect=true&user=root&password=root";
	// public static String connectionString = "jdbc:sqlite:C2.db";

	public static void main(String[] args) {

		FreeVersionForm.Limit();

		Global.heatingUpClasses = ClassesFree.heatingUpClasses;
		Global.dbClasses = ClassesFree.dbClasses;
		Global.enums = ClassesFree.enums;
		Global.transactionChains = ClassesFree.transactionChains;
		Settings.isDesignTime = false;

		// Устанавливаем тут, потому что функция развертывания использует при
		// сохранении
		Cache.I().SetHeatingUpClasses(Global.heatingUpClasses);

		// System.exit(1);

		// -Duser.timezone=Europe/Minsk
		System.out.println("" + new Date().getTime());
		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Minsk"));
		log.INFO("main", "Start " + Global.VERSION);

		if (!DbHelper.CheckEnums())
			System.exit(0);

//		Settings.Load();
		{
			// connectionString = "jdbc:mysql://" + Settings.get("server")
			// +
			// "/warehouse?allowMultiQueries=true&autoReconnect=true&user=root&password=root";

			DbHelper.connectionString = "jdbc:sqlite:" + FileUtils.getAppDir() + "C2.db";

			// single connection source example for a database URI
			try {
				DbHelper.setConnection();
			} catch (Exception e) {
				log.FATAL("main", e);
				System.exit(1);
			}

			// Cache.I().GetFromDbAction = new GetFromDbAction();
			// Cache.I().GetByIdFromDbAction = new GetByIdFromDbAction();

			try {
				DbHelper.Alter(new Class[] { Coworker.class, User.class });

				if (new UserFactory().GetCount() == 0) {
					User user = new UserFactory().CreateNew("Администратор", "", "Администратор");
				}
			} catch (Exception e) {
				log.FATAL("main", e);
			}

			LoginForm login = new LoginForm();
			login.setVisible(true);
			login.onLogin = onLogin;

		}

	}

	static Action onLogin = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent evt) {
			try {
				GlobalConstantsFactory.Load();
			} catch (Exception e) {
				log.FATAL("main", e);
				// System.exit(1);
			}

			Cache.I().HeatingUp(Global.heatingUpClasses);

			MainFormFree form = new MainFormFree();
			form.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});
			form.setVisible(true);
		}
	};
}
