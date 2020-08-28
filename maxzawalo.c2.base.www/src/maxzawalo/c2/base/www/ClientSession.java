package maxzawalo.c2.base.www;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
//import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;

import maxzawalo.c2.base.utils.FileUtils;
import maxzawalo.c2.full.reporter.BarcodeGenerator;

/**
 * Обрабатывает запрос клиента.
 */
public class ClientSession implements Runnable {

	@Override
	public void run() {
		try {
			/* Получаем заголовок сообщения от клиента */
			String header = readHeader();
			System.out.println(header + "\n");
			/* Получаем из заголовка указатель на интересующий ресурс */
			String[] url = getURLFromHeader(header);
			System.out.println("URL : " + url[0] + "?" + url[1] + "\n");
			/* Отправляем содержимое ресурса клиенту */
			int code = send(url);
			System.out.println("Result code: " + code + "\n");
//		} catch (IOException e) {
//			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	UUID sid = UUID.randomUUID();
	String location = null;

	public ClientSession(Socket socket) throws Exception {
		this.socket = socket;
		initialize();
	}

	private void initialize() throws Exception {
		/* Получаем поток ввода, в который помещаются сообщения от клиента */
		in = socket.getInputStream();
		/* Получаем поток вывода, для отправки сообщений клиенту */
		out = socket.getOutputStream();
	}

	public void getBytesFromInputStream(InputStream reader, int size, String path) throws Exception {
		// ByteArrayOutputStream os = new ByteArrayOutputStream();
		FileOutputStream os = new FileOutputStream(path);
		try {
			List<Byte> buff = new ArrayList<>();
			int b = 0;
			b = reader.read();
			buff.add((byte) b);
			// ------WebKitFormBoundary

			char[] check_str = "\n------Web".toCharArray();
			int pos = 0;

			boolean stop = false;
			while (b != -1 && !stop && size > 0) {
				b = reader.read();
				buff.add((byte) b);

				if (b == check_str[pos]) {
					pos++;
					if (pos >= check_str.length)
						stop = true;
				} else
					pos = 0;

				if (buff.size() >= 1024) {
					FlushBuff(os, buff);
				}
				size--;
				readed++;

				if (stop) {
					buff = buff.subList(0, buff.size() - pos - 1);
					FlushBuff(os, buff);
					break;
				}
			}
		} finally {
			os.flush();
			os.close();
		}
	}

	public void FlushBuff(FileOutputStream os, List<Byte> buff) throws Exception {
		for (int i = 0; i < buff.size(); i++)
			os.write(buff.get(i));

		buff.clear();
	}

	int readed = 0;
	public boolean post = false;

	/**
	 * Считывает заголовок сообщения от клиента.
	 * 
	 * @return строка с заголовком сообщения от клиента.
	 * @throws Exception
	 */
	private String readHeader() throws Exception {
		InputStreamReader isr = new InputStreamReader(in);
		// BufferedReader reader = new BufferedReader(isr);
		StringBuilder builder = new StringBuilder();

		post = false;
		int ContentLength = 0;
		String ln = null;
		// readed = 0;

		// while (true)
		for (int i = 0; i < 1000; i++) {
			ln = ReadLine(in);

			// ln = reader.readLine();
			if (ln.startsWith("POST"))
				post = true;

			if (post) {
				if (ln.contains("Content-Length:"))
					ContentLength = Integer.parseInt(ln.replace("Content-Length:", "").trim());
				System.out.println(ln);
			}

			if (ln == null || ln.isEmpty()) {
				readed = 0;
				if (post) {
					String l = "";
					for (int p = 0; p < 1000; p++) {
						// while (true) {
						l = ReadLine(in);
						// // for (int p = 0; p < 100; p++)
						// // System.out.println(reader.readLine());
						System.out.println(l);
						if (l.contains("Content-Type") && !l.contains("multipart"))
							break;
					}
					l = ReadLine(in);
					// reader.readLine();
					// reader.readLine();
					getBytesFromInputStream(in, ContentLength - readed - 1,
							"D:\\webserver\\home\\localhost\\www\\c2\\1.jpg");

					System.out.println("");
				}
				break;
			}
			//
			builder.append(ln + System.getProperty("line.separator"));
		}
		return builder.toString();
	}

	public String ReadLine(InputStream reader) throws Exception {
		char[] cbuf = new char[1];
		String ln = "";
		boolean cr = false;
		while (true) {
			int ch = reader.read();
			if (ch == -1)
				break;
			readed++;

			// CR/LF (Hex 0D0A)
			if (ch == '\r') {
				cr = true;
				continue;
			}
			// if (ch == 10)
			// System.out.println("");

			if (cr && ch == '\n')
				break;
			else
				cr = false;

			ln += (char) ch;

		}
		return ln;// .replace("\n", "");
	}

	/**
	 * Вытаскивает идентификатор запрашиваемого ресурса из заголовка сообщения от
	 * клиента.
	 * 
	 * @param header
	 *            заголовок сообщения от клиента.
	 * @return идентификатор ресурса.
	 */
	private String[] getURLFromHeader(String header) {
		System.out.println(header);
		int from = header.indexOf(" ") + 1;
		int to = header.indexOf(" ", from);
		String uri = header.substring(from, to);
		if (uri.equals("/null"))
			uri = "/";
		if (uri.equals("/"))
			uri += "index.html";
		String params = "";
		int paramIndex = uri.indexOf("?");
		if (paramIndex != -1) {
			params = uri.substring(paramIndex + 1, uri.length());
			uri = uri.substring(0, paramIndex);
		}
		return new String[] { uri, params };
	}

	String answerStr = "";
	int code = 200;

	/**
	 * Отправляет ответ клиенту. В качестве ответа отправляется http заголовок и
	 * содержимое указанного ресурса. Если ресурс не указан, отправляется перечень
	 * доступных ресурсов.
	 * 
	 * @param url
	 *            идентификатор запрашиваемого ресурса.
	 * @param isApi
	 * @return код ответа. 200 - если ресурс был найден, 404 - если нет.
	 * @throws Exception
	 */
	private int send(String[] url) throws Exception {
		answerStr = "";
		// InputStream strm = HttpServer.class.getResourceAsStream(url);
		// InputStream strm = new FileInputStream(new FIle))
		// int code = (strm != null) ? 200 : 404;
		code = 200;

		
		
				
		// Content-Type: text/html; charset=utf-8
		// Content-Type: multipart/form-data; boundary=something
		String contentType = "text/html; charset=utf-8";
		if (url[0].endsWith(".css"))
			contentType = "text/css";
		// image/jpeg image/png
		else if (url[0].endsWith(".png"))
			contentType = "image/png";
		else if (url[0].endsWith(".gif"))
			contentType = "image/gif";

		if (url[0].contains("favicon.ico"))
			url[0] = url[0].replace("favicon.ico", "img/app_48_48.png");

		String params = url[1];
		String[] paramValue = (params.isEmpty() ? new String[] {} : params.split("&"));
		Map<String, String> q = new HashMap<>();
		for (String pv : paramValue) {
			String[] v = pv.split("=");
			q.put(v[0], (v.length > 1 ? v[1] : ""));
		}

		BeforeHeader(url, q);
		// http://127.0.0.1:9194/api/bar?code=
		String header = getHeader(code, contentType);
		PrintStream answer = new PrintStream(out, true, "UTF-8");
		answer.print(header);

		
		AfterHeader(url, answer);

		// if (code == 200) {
		// int count = 0;
		// byte[] buffer = new byte[1024];
		// while ((count = strm.read(buffer)) != -1) {
		// out.write(buffer, 0, count);
		// }
		// strm.close();
		// }
		return code;
	}

	public void AfterHeader(String[] url, PrintStream answer) throws Exception {
		if (url[0].contains("/img/")) {
			GetIP();
			String fileName = Paths.get(url[0]).getFileName() + "";
			if (fileName.equals("start.gif")) {
				BufferedImage image = BarcodeGenerator.encodeAsBitmap("http://" + GetIP() + ":9194/" + SCAN_RESULT_PAGE,
						BarcodeFormat.QR_CODE, 300, 300);
				ImageIO.write(image, "png", out);
			} else
				Files.copy(Paths.get(IMG_DIR + fileName), out);
		} else
			answer.print(answerStr);
	}

	public void BeforeHeader(String[] url, Map<String, String> q) throws Exception {
		if (url[0].contains("api")) {
			String func = url[0].replace("api", "").replace("/", "");
			// if (Actions.GetApiAction == null)
			// answer.print("api:" + api + "?" + params);
			// else
			// answer.print(Actions.GetApiAction.Do(api, params));

			answerStr = ApiWww.Get(this, func, q);
		} else if (url[0].contains("/report/")) {
			String api = url[0].replace("report", "").replace("/", "");
			answerStr = Report.Get(api, q);
		} else {
			// answer = print("Привет мир " + sid);
			String tpl = FileUtils.readFileAsString(DEFAULT_FILES_DIR + url[0]);
			for (String p : q.keySet()) {
				tpl = tpl.replace("__" + p, q.get(p));
			}
			if (url[0].replaceAll("/", "").equals("mobile.html"))
			{
				if (post)
					tpl = tpl.replace("__post_message", "Загружено");
				else
					tpl = tpl.replace("__post_message", "");
			}
			
			answerStr = (tpl);
		}
	}

	public String GetIP() {
		List<InetAddress> ips = new ArrayList<>();
		try {
			Enumeration e = NetworkInterface.getNetworkInterfaces();
			while (e.hasMoreElements()) {
				NetworkInterface n = (NetworkInterface) e.nextElement();
				Enumeration ee = n.getInetAddresses();
				while (ee.hasMoreElements()) {
					InetAddress i = (InetAddress) ee.nextElement();
					if (i.getHostAddress().startsWith("192.168."))
						ips.add(i);
				}
			}
			ips = ips.stream()//
					.distinct()//
					.sorted((o1, o2) -> ((Byte) o1.getAddress()[2]).compareTo((Byte) o2.getAddress()[2]))//
					.collect(Collectors.toList());
			for (InetAddress i : ips)
				System.out.println(i.getHostAddress());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ips.get(0).getHostAddress();
	}

	/**
	 * Возвращает http заголовок ответа.
	 * 
	 * @param code
	 *            код результата отправки.
	 * @return http заголовок ответа.
	 */
	@SuppressWarnings("deprecation")
	private String getHeader(int code, String contentType) {
		// "30 Dec 2030 03:04:05 GMT"
		// SimpleDateFormat sdf = new SimpleDateFormat();
		// sdf.setTimeZone(new SimpleTimeZone(0, "GMT"));
		// sdf.applyPattern("dd MMM yyyy HH:mm:ss z");
		// Date testDate = new Date();
		// Print string using recommended method
		// System.out.println(sdf.format(testDate));
		StringBuilder buffer = new StringBuilder();
		buffer.append("HTTP/1.1 " + code + " " + getAnswer(code) + "\n");
		buffer.append("Content-Type: " + contentType + "\n");
		if (location == null) {
			buffer.append("Date: " + new Date().toGMTString() + "\n");
			buffer.append("Accept-Ranges: none\n");
			// TODO: file changed
			buffer.append("Last-Modified: " + new Date().toGMTString() + "\n");
			// TODO:ETag
			buffer.append("Set-Cookie: sid=" + sid + ";\n");
		} else {
			// buffer.append("HTTP/1.1 301 Moved Permanently\n");
			buffer.append("Location: " + location + "\n");
			location = null;
		}
		buffer.append("\n");
		return buffer.toString();
	}

	/**
	 * Возвращает комментарий к коду результата отправки.
	 * 
	 * @param code
	 *            код результата отправки.
	 * @return комментарий к коду результата отправки.
	 */
	private String getAnswer(int code) {
		switch (code) {
		case 200:
			return "OK";
		case 404:
			return "Not Found";
		case 301:
			return "Moved Permanently";
		default:
			return "Internal Server Error";
		}
	}

	private Socket socket;
	private InputStream in = null;
	private OutputStream out = null;
	
	public static String DEFAULT_FILES_DIR = FileUtils.GetWWWDir();
	public static String IMG_DIR = "resource/img/";
	public static final String SCAN_RESULT_PAGE = "mobile.html";

	public void Redirect(String location) {
		code = 301;
		this.location = location;
	}

	// html/DocForm.html
}