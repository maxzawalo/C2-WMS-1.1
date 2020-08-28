package maxzawalo.c2.base.www;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Обрабатывает запросы от клиентов, возвращая файлы, указанные в url-path или
 * ответ с кодом 404, если такой файл не найден.
 *
 */
public class HttpServer {

	int port = 0;

	Thread runner = new Thread(new Runnable() {

		public synchronized void run() {
			/* Создаем серверный сокет на полученном порту */
			ServerSocket serverSocket = null;
			try {
				serverSocket = new ServerSocket(port);
				System.out.println("Server started on port: " + serverSocket.getLocalPort() + "\n");
			} catch (IOException e) {
				System.out.println("Port " + port + " is blocked.");
				// System.exit(-1);
				return;
			}
			/*
			 * Если порт был свободен и сокет был успешно создан, можно
			 * переходить к следующему шагу - ожиданию клинтов
			 */
			while (true) {
				try {
					Socket clientSocket = serverSocket.accept();
					/*
					 * Для обработки запроса от каждого клиента создается
					 * отдельный объект и отдельный поток
					 */
					ClientSession session = new ClientSession(clientSocket);
					new Thread(session).start();
				} catch (Exception e) {
					System.out.println("Failed to establish connection.");
					System.out.println(e.getMessage());
					// System.exit(-1);
					return;
				}
			}

		}
	});

	public void Start(int port) {
		this.port = port;
		runner.setDaemon(true);
		runner.start();
	}

	public void Start() {
		Start(DEFAULT_PORT);
	}

	public void Stop() {
		runner.interrupt();
	}

	public String GetRootUrl() {
		return "http://localhost:" + this.port + "/";
	}

	public static final int DEFAULT_PORT = 9194;
}