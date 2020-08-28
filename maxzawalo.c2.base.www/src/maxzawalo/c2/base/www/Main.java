package maxzawalo.c2.base.www;

import java.util.Scanner;
import java.util.TimeZone;

import maxzawalo.c2.base.os.Run;

//import com.j256.ormlite.logger.LocalLog;

public class Main {
	/**
	 * Первым аргументом может идти номер порта.
	 */
	public static void main(String[] args) {
		/* Если аргументы отсутствуют, порт принимает значение поумолчанию */

		// System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");

		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Minsk"));
//		Settings.Load();
//		DbHelper.connectionString = "jdbc:mysql://" + Settings.get("server") + "/warehouse?user=root&password=root";
//		DbHelper.setConnection();
//		// Logger.setLevel(LogLevel.TEST);
//
//		Initialization.Do();

		// Cache.I().HeatingUp(Global.heatingUpClasses);

		if (args.length > 0) {
//			try {
//				DbHelper.Alter(ClassesFull.dbClasses);
//			} catch (Exception e) {
//				
//				e.printStackTrace();
//			}
			int port = Integer.parseInt(args[0]);
			port = HttpServer.DEFAULT_PORT;
			HttpServer httpServer = new HttpServer();
			httpServer.Start(port);
//			http://localhost:9194/bp/schem.html?id=1
//			Run.OpenFile(httpServer.GetRootUrl() + "img/start.gif");
			Run.OpenFile(httpServer.GetRootUrl() + "bp/schem.html?id=1");

			Scanner scanner = new Scanner(System.in);
			System.out.print("Enter 2 exit ");
			String input = scanner.nextLine();
			// while (true) {
			// try {
			// Thread.sleep(300);
			// } catch (InterruptedException e) {
			// 
			// e.printStackTrace();
			// }
			// }
		}

	}
}