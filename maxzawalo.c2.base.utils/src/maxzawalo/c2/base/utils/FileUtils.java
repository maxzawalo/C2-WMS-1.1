package maxzawalo.c2.base.utils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static maxzawalo.c2.base.utils.FileUtils.GetLogDir;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;

public class FileUtils {

	static Logger log = Logger.getLogger(FileUtils.class);

	static {
		Deploy();
	}

	public static void Deploy() {
		try {
			new File(getAppDir()).mkdirs();
			new File(GetLogDir()).mkdirs();
			new File(GetDataDir()).mkdirs();
			new File(GetCacheDir()).mkdirs();
			new File(GetDumpDir()).mkdirs();
			new File(GetReportDir()).mkdirs();
			new File(GetSettingsDir()).mkdirs();
			new File(Get1cDir()).mkdirs();
			new File(GetBackUpDir()).mkdirs();
			new File(GetSearchContextDir()).mkdirs();
			new File(GetSearchLogDir()).mkdirs();
			new File(GetImgDir()).mkdirs();
			new File(GetTestDir()).mkdirs();
			new File(GetWWWGenDir()).mkdirs();
			new File(GetWWWDir()).mkdirs();
		} catch (Exception e) {
			// TODO: сообщить о недоступности флешки
		}
	}

	static String appDir = null;

	public static void setAppDir(String dir) {
		appDir = dir;
	}

	public static String getAppDir() {
		if (appDir == null)
			// Для standalone
			appDir = Paths.get(".").toAbsolutePath().normalize().toString() + "/";
		// System.out.println("appDir: " + appDir);
		return appDir;
		// return Environment.getExternalStorageDirectory().getPath() + "/2c/";
	}

	public static String GetWWWGenDir() {
		return getAppDir() + "/www_gen/";
	}

	public static String GetWWWDir() {
		return getAppDir() + "/www/";
	}

	public static String GetSearchContextDir() {
		return getAppDir() + "/search_context/";
	}

	public static String GetSearchLogDir() {
		return getAppDir() + "/search_log/";
	}

	public static String GetLogDir() {
		return getAppDir() + "/log/";
	}

	public static String Get1cDir() {
		return getAppDir() + "/1c/";
	}

	public static String GetDataDir() {
		return getAppDir() + "/data/";
	}

	public static String GetBackUpDir() {
		return getAppDir() + "/backup/";
	}

	public static String GetCacheDir() {
		return getAppDir() + "/cache/";
	}

	public static String GetDumpDir() {
		return getAppDir() + "/dump/";
	}

	public static String GetReportDir() {
		return getAppDir() + "/report/";
	}

	public static String GetSettingsDir() {
		return getAppDir() + "/settings/";
	}

	public static String GetImgDir() {
		return getAppDir() + "/img/";
	}

	public static String GetTestDir() {
		return getAppDir() + "/test/";
	}

	public static void Text2File(String path, String text, boolean append) {
		try {
			BufferedWriter out = new BufferedWriter(
			        new OutputStreamWriter(new FileOutputStream(path, append), StandardCharsets.UTF_8));
			out.write(text);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	public static String readFileAsString(String filePath) {
		// String result = "";
		File file = new File(filePath);

		String UTF8 = "utf8";
		int BUFFER_SIZE = 64 * 1024;
		StringBuilder data = new StringBuilder();

		if (file.exists()) {
			// byte[] buffer = new byte[(int) new File(filePath).length()];
			try {
				// Open the file that is the first
				// command line parameter
				FileInputStream fstream = new FileInputStream(file);
				// Get the object of DataInputStream
				InputStreamReader in = new InputStreamReader(new FileInputStream(filePath), UTF8);
				BufferedReader br = new BufferedReader(in, BUFFER_SIZE);
				String line = "";
				while ((line = br.readLine()) != null) {
					// result += line + "\n";
					data.append(line);
					data.append("\n");// TODO:?
				}
				in.close();
				br.close();
				fstream.close();
			} catch (Exception e) {
				log.ERROR("readFileAsString", e);
			}
			// result = new String(buffer);
		}
		return removeUTF8BOM(data.toString());

	}

	public static List<String> readFileAsList(String filePath) {
		String encoding = "utf8";
		return readFileAsList(filePath, encoding);
	}

	public static List<String> readFileAsList(String filePath, String encoding) {
		List<String> list = new ArrayList<>();
		File file = new File(filePath);

		int BUFFER_SIZE = 64 * 1024;
		StringBuilder data = new StringBuilder();

		if (file.exists()) {
			// byte[] buffer = new byte[(int) new File(filePath).length()];
			try {
				// Open the file that is the first
				// command line parameter
				FileInputStream fstream = new FileInputStream(file);
				// Get the object of DataInputStream
				InputStreamReader in = new InputStreamReader(new FileInputStream(filePath), encoding);
				BufferedReader br = new BufferedReader(in, BUFFER_SIZE);
				String line = "";
				while ((line = br.readLine()) != null) {
					list.add(removeUTF8BOM(line));
				}
				in.close();
				br.close();
				fstream.close();
			} catch (Exception e) {
				log.ERROR("readFileAsList", e);
			}
			// result = new String(buffer);
		}
		return list;

	}

	public static final String UTF8_BOM = "\uFEFF";

	static String removeUTF8BOM(String s) {
		if (s.startsWith(UTF8_BOM)) {
			s = s.substring(1);
		}
		return s;
	}

	public static List<String> getFilesFromDir(String directory) {
		List<String> results = new ArrayList<String>();

		File[] files = new File(directory).listFiles();
		if (files == null)
			// TODO: message
			return results;
		// If this pathname does not denote a directory, then listFiles()
		// returns null.

		for (File file : files) {
			if (file.isFile()) {
				results.add(directory + file.getName());
			}
		}
		return results;
	}

	public static void CopyFile(String source, String dest) {
		try {
			// copyFileUsingStream(new File(source), new File(dest));
			Path from = Paths.get(source);
			Path to = Paths.get(dest);
			Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
			System.out.println("CopyFile :" + source + " -> " + dest);
		} catch (Exception e) {
			log.ERROR("CopyFile", e);
		}
	}

	static void copyFileUsingStream(File source, File dest) throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(source);
			os = new FileOutputStream(dest, false);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		} finally {
			is.close();
			os.close();
		}
	}

	public static String getFileExtension(File file) {
		String name = file.getName();
		try {
			return name.substring(name.lastIndexOf(".") + 1);
		} catch (Exception e) {
			return "";
		}
	}

	public static void purgeDirectory(File dir) {
		for (File file : dir.listFiles()) {
			if (file.isDirectory())
				purgeDirectory(file);
			file.delete();
		}
	}

	public static void copyFolder(File src, File dest) throws IOException {

		if (src.isDirectory()) {

			// if directory not exists, create it
			if (!dest.exists()) {
				dest.mkdir();
				System.out.println("Directory copied from " + src + "  to " + dest);
			}

			// list all the directory contents
			String files[] = src.list();

			for (String file : files) {
				// construct the src and dest file structure
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				// recursive copy
				copyFolder(srcFile, destFile);
			}

		} else {
			// if file, then copy it
			// Use bytes stream to support all file types
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];

			int length;
			// copy the file content in bytes
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

			in.close();
			out.close();
			System.out.println("File copied from " + src + " to " + dest);
		}
	}

	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		return dir.delete(); // The directory is empty now and can be deleted.
	}

	public static void deleteFilesByFilter(Path dir, String filter) {
		try {
			Files.list(dir).filter(p -> p.toString().contains(filter)).forEach((p) -> {
				try {
					Files.deleteIfExists(p);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			
			e.printStackTrace();
		}

	}

	public static String GetStringFromResource(Class cl, String url) {
		// List<String> files = IOUtils.readLines(MyClass.class.getClassLoader()
		// .getResourceAsStream("directory/"), Charsets.UTF_8);
		// List<String> files =
		// IOUtils.readLines(Thread.currentThread.getClass().getClassLoader().getResourceAsStream(resourceDir),
		// Charsets.UTF_8);
		InputStream stream = cl.getResourceAsStream(url);
		String result = new BufferedReader(new InputStreamReader(stream)).lines().collect(Collectors.joining("\n"));
		return result;
	}

	public static List<Class> getClasses(ClassLoader cl, String pack) throws Exception {
		String dottedPackage = pack.replaceAll("[/]", ".");
		List<Class> classes = new ArrayList<Class>();
		URL upackage = cl.getResource(pack);

		DataInputStream dis = new DataInputStream((InputStream) upackage.getContent());
		String line = null;
		while ((line = dis.readLine()) != null) {
			if (line.endsWith(".class")) {
				classes.add(Class.forName(dottedPackage + "." + line.substring(0, line.lastIndexOf('.'))));
			}
		}
		return classes;
	}
}