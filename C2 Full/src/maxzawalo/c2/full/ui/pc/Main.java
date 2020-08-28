package maxzawalo.c2.full.ui.pc;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import com.j256.ormlite.logger.LocalLog;

import maxzawalo.c2.base.bo.Coworker;
import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.base.bo.registry.RegType;
import maxzawalo.c2.base.data.DbHelper;
import maxzawalo.c2.base.data.factory.CoworkerFactory;
import maxzawalo.c2.base.data.factory.UserFactory;
import maxzawalo.c2.base.ui.pc.form.ConsoleForm;
import maxzawalo.c2.base.ui.pc.form.LoginForm;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.base.utils.Global;
import maxzawalo.c2.base.utils.Logger;
import maxzawalo.c2.base.utils.Logger.LogLevel;
import maxzawalo.c2.base.www.HttpServer;
import maxzawalo.c2.free.bo.Product;
import maxzawalo.c2.free.bo.Settings;
import maxzawalo.c2.free.cache.Cache;
import maxzawalo.c2.free.data.factory.GlobalConstantsFactory;
import maxzawalo.c2.free.search.SearchContext;
import maxzawalo.c2.free.ui.pc.catalogue.ContractForm;
import maxzawalo.c2.full.data.GroupTransaction;
import maxzawalo.c2.full.data.json.net.CurrencyFromNet;
import maxzawalo.c2.full.synchronization.Synchronization;
import maxzawalo.c2.full.ui.pc.action.ContractPrintAction;
import maxzawalo.c2.full.ui.pc.form.MainFormFull;
import maxzawalo.c2.full.ui.pc.form.SimpleMainForm;

public class Main {
	static {
		Global.VERSION = "1.1.34.150";
		Logger.setLevel(LogLevel.TEST);
		System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");
	}

	static Logger log = Logger.getLogger(Main.class);

	public static HttpServer httpServer = new HttpServer();

	public static void main(String[] args) throws Exception {
		System.out.println("==== Тестируем RegType ====");
		if (!RegType.Check()) {
			log.FATAL("main", "Дубликаты RegType");
			System.exit(0);
		}

		if (!DbHelper.CheckEnums())
			System.exit(0);

		Settings.isDesignTime = false;

		// -Duser.timezone=Europe/Minsk
		System.out.println("" + new Date().getTime());
		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Minsk"));
		log.INFO("main", "Start " + Global.VERSION);

		System.out.println("" + new Date().getTime());

		Settings.Load();

		String strDbUser = "root"; // database loging username
		String strDbPassword = "root"; // database login password

		Initialization.Do();
		
		//
		// if (false)
		{
			// preparedstatement = con.prepareStatement(query);
			// preparedstatement.setQueryTimeout(seconds);
			
			int nLocalPort = 3306;
			DbHelper.connectionString = "jdbc:mysql://" + Settings.get("server") + ":" + nLocalPort 
					+ "/warehouse?connectTimeout=60000&socketTimeout=60000&allowMultiQueries=true&autoReconnect=true&user=root&password=root";

			System.out.println("license");
			// log.FATAL("license","У вас нет лицензии на данное програмное обеспечение.
			// Обратитесь к разработчикам. https://vk.com/c2_wms");
			try {
				DbHelper.setConnection();
			} catch (Exception e) {
				log.FATAL("main", e);
				System.exit(1);
			}

			try {
				DbHelper.Alter(new Class[] { Coworker.class, User.class });
			} catch (SQLException e) {
				log.FATAL("main", e);
				JOptionPane.showMessageDialog(null, "Нет связи с сервером.", "Ошибки", JOptionPane.ERROR_MESSAGE);
				if (e.getMessage().contains("invalid database address"))
					System.exit(0);
			} catch (Exception e) {
				log.FATAL("main", e);
			}

			if (false) {
				{
					Synchronization.Do();
					for (Coworker c : new CoworkerFactory().GetAll()) {
						User user = new UserFactory().GetByParam("coworker_id", c.id);
						if (user == null) {
							try {
								user = new User();
								user.coworker = c;
								user.password = UserFactory.CreatePasswordHash("");// UUID.randomUUID().toString()
								new UserFactory().Save(user);
							} catch (Exception e) {
								log.ERROR("main", e);
							}
						}
					}
				}
				try {
					DbHelper.Alter(Global.dbClasses);
				} catch (Exception e1) {
					
					e1.printStackTrace();
				}
			}

			if (args.length > 0) {
				// TODO: check params
				AfterLogin();

				Global.canBalanceBeMinus = false;
				GroupTransaction.Do(Format.GetDate(args[1]), Format.GetDate(args[2]), true, false, false);
			} else {
				// port = Integer.parseInt(args[0]);
				LoginForm login = new LoginForm();
				login.setVisible(true);
				login.onLogin = onLogin;
				login.EnterSimpleNCheck();
			}
		}
	}

	public static void SetActions() {
		ContractForm.printAction = new ContractPrintAction();
	}

	public static void AfterLogin() {
		Cache.I().HeatingUp(Global.heatingUpClasses);

		try {
			GlobalConstantsFactory.Load();
		} catch (Exception e) {
			log.FATAL("main", e);
			// System.exit(1);
		}
		// TODO: во free
		CurrencyFromNet.Load();

		SearchContext.GetPhrases(Product.class);
		ConsoleForm.console.Init();
	}

	static Action onLogin = new AbstractAction() {
		@Override
		public void actionPerformed(ActionEvent evt) {
			SetActions();
			AfterLogin();

			if (User.current.isSimple()) {
				SimpleMainForm form = new SimpleMainForm();
				form.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						System.exit(0);
					}
				});
				form.setVisible(true);
			} else {
				MainFormFull form = new MainFormFull();
				form.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						// freeLock();
						System.exit(0);
					}
				});
				form.LoadData();
				form.setVisible(true);

				httpServer.Start();
				// Run.OpenFile(httpServer.GetRootUrl());
			}
		}
	};
}