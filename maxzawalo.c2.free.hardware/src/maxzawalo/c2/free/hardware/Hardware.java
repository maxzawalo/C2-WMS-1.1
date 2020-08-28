package maxzawalo.c2.free.hardware;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;

import maxzawalo.c2.base.crypto.Hash;
import maxzawalo.c2.base.os.Validator;

public class Hardware {
	// If you want to quickly find out what’s the CPU clock speed, you can do
	// the following, also if you have turbo boost CPUs you can find out what’s
	// the Max Clock Speed your system is capable of for the current
	// configuration. Of course you can always overclock your CPU and that will
	// too reflect the change.
	//
	// wmic cpu get name,CurrentClockSpeed,MaxClockSpeed /every:1

	public static String getMotherboardSN() {

		// File file = File.createTempFile("realhowto", ".vbs");
		// file.deleteOnExit();
		// FileWriter fw = new java.io.FileWriter(file);
		//
		// String vbs = "Set objWMIService =
		// GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
		// + "Set colItems = objWMIService.ExecQuery _ \n" + " (\"Select *
		// from Win32_BaseBoard\") \n"
		// + "For Each objItem in colItems \n" + " Wscript.Echo
		// objItem.SerialNumber \n"
		// + " exit for ' do the first cpu only! \n" + "Next \n";
		//
		// fw.write(vbs);
		// fw.close();
		// Process p = Runtime.getRuntime().exec("cscript //NoLogo " +
		// file.getPath());
		// wmic baseboard get product,Manufacturer,version,serialnumber
		// String cmd = null;
		// wmic bios get name,serialnumber,version
		// wmic csproduct get name,identifyingnumber,uuid
		// wmic cpu get name,CurrentClockSpeed,MaxClockSpeed
		return RunCmd("wmic baseboard get *");
	}

	protected static String RunCmd(String cmd) {
		String result = "";
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = input.readLine()) != null) {
				result += line;
			}
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.trim();
	}

	public static String getCpuId() {
		return RunCmd("wmic cpu get name, ProcessorId");
		// String result = "";
		// try {
		// File file = File.createTempFile("realhowto", ".vbs");
		// file.deleteOnExit();
		// FileWriter fw = new java.io.FileWriter(file);
		//
		// // Private Function CpuId() As String
		// // Dim computer As String
		// // Dim wmi As Variant
		// // Dim processors As Variant
		// // Dim cpu As Variant
		// // Dim cpu_ids As String
		// //
		// // computer = "."
		// // Set wmi = GetObject("winmgmts:" & _
		// // "{impersonationLevel=impersonate}!\\" & _
		// // computer & "\root\cimv2")
		// // Set processors = wmi.ExecQuery("Select * from " & _
		// // "Win32_Processor")
		// //
		// // For Each cpu In processors
		// // cpu_ids = cpu_ids & ", " & cpu.ProcessorId
		// // Next cpu
		// // If Len(cpu_ids) > 0 Then cpu_ids = Mid$(cpu_ids, 3)
		// //
		// // CpuId = cpu_ids
		// // End Function
		//
		// String vbs = "Set objWMIService =
		// GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
		// + "Set colItems = objWMIService.ExecQuery _ \n" + " (\"Select * from
		// Win32_Processor\") \n"
		// + "For Each objItem in colItems \n" + " Wscript.Echo
		// objItem.ProcessorId \n"
		// + " exit for ' do the first cpu only! \n" + "Next \n";
		//
		// fw.write(vbs);
		// fw.close();
		// Process p = Runtime.getRuntime().exec("cscript //NoLogo " +
		// file.getPath());
		// BufferedReader input = new BufferedReader(new
		// InputStreamReader(p.getInputStream()));
		// String line;
		// while ((line = input.readLine()) != null) {
		// result += line;
		// }
		// input.close();
		// } catch (Exception e) {
		// // e.printStackTrace();
		// }
		// return result.trim();
	}

	public static String getCpuIdLinux() {
		String result = "";
		try {
			Process p = Runtime.getRuntime().exec("grep -iE \"serial\" /proc/cpuinfo");
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = input.readLine()) != null) {
				result += line;
			}
			input.close();
			result = result.split(":")[1];
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return result.trim();
	}

	public static String getDriveSerialNumber(String drive) {
		String result = "";
		try {
			File file = File.createTempFile("realhowto", ".vbs");
			file.deleteOnExit();
			FileWriter fw = new java.io.FileWriter(file);

			String vbs = "Set objFSO = CreateObject(\"Scripting.FileSystemObject\")\n"
					+ "Set colDrives = objFSO.Drives\n" + "Set objDrive = colDrives.item(\"" + drive + "\")\n"
					+ "Wscript.Echo objDrive.SerialNumber"; // see note
			fw.write(vbs);
			fw.close();
			Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = input.readLine()) != null) {
				result += line;
			}
			input.close();
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return result.trim();
	}

	static String p = "bad";

	public static String getP() {
		if (p.equals("bad")) {
			if (Validator.isLinux()) {
				p = Hash.sha256(getCpuIdLinux());
			} else if (Validator.isWindows()) {
				p = Hash.sha256(getCpuId() + getMotherboardSN() + getBios());
			}
		}
		return p;
	}

	private static String getBios() {
		return RunCmd("wmic csproduct get name,identifyingnumber,uuid");
	}
}