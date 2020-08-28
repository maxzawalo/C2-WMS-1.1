//https://code.google.com/archive/p/java-simple-serial-connector/issues/14
//sudo chmod -R 777 /dev/ttyUSB0
//sudo adduser max dialout
//sudo reboot

package maxzawalo.c2.full.hardware.terminal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import maxzawalo.c2.base.interfaces.TerminalEvent;
import maxzawalo.c2.base.os.Validator;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class Terminal {

	static Thread myThready = new Thread(new Runnable() {
		public void run() {
			while (true) {
				System.out.println("run " + (new Date().getTime() - lastReadTime));
				if (new Date().getTime() - lastReadTime > timeout)
					for (TerminalEvent a : callbacks) {
						a.onScan("", true);
					}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
			}
		}
	});

	static boolean ComPortTech = false;

	public static void Start4TEch() {
		myThready.setDaemon(true);
		myThready.start();
		ComPortTech = true;
	}

	private static SerialPort serialPort;

	public static List<TerminalEvent> callbacks = new ArrayList<>();

	public static boolean Init(int startComPort) {
		try {
			ResetTimer();
			String portName = "";
			if (Validator.isWindows()) {

				String[] portNames = SerialPortList.getPortNames();
				for (int i = 0; i < portNames.length; i++) {
					portName = portNames[i];
					System.out.println(portName);
					if (Integer.parseInt(portName.replace("COM", "")) >= startComPort)
						break;
					portName = "none";
				}
			} else if (Validator.isLinux()) {
				// TODO: startComPort
				portName = "/dev/ttyUSB0";
			}

			serialPort = new SerialPort(portName);
			serialPort.openPort();
			serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
			// serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN);
			serialPort.purgePort(SerialPort.PURGE_RXCLEAR);
			serialPort.purgePort(SerialPort.PURGE_TXCLEAR);
			serialPort.setEventsMask(SerialPort.MASK_RXCHAR);
			serialPort.addEventListener(new EventListener());

		} catch (SerialPortException ex) {
			System.out.println(ex.getMessage());
			// Exception type - Port busy
			return false;
		} catch (Exception ex) {
			System.out.println(ex);
			return false;
		}
		return true;
	}

	public static boolean Uninit() {
		try {
			callbacks.clear();
			// if (serialPort != null)
			serialPort.closePort();
		} catch (Exception e) {
			
			e.printStackTrace();
			return false;
		}
		return true;
	}

	protected static void ResetTimer() {
		lastReadTime = new Date().getTime();
	}

	static ArrayList<Byte> data = new ArrayList<>();

	final static int timeout = 5000;
	static long lastReadTime = new Date().getTime();

	static class EventListener implements SerialPortEventListener {
		public void serialEvent(SerialPortEvent event) {
			try {
				if (ComPortTech) {
					System.out.println("serialEvent " + (new Date().getTime() - lastReadTime));
					if (new Date().getTime() - lastReadTime > timeout)
						throw new Exception("timeout");
					ResetTimer();
				}

				if (event.isRXCHAR() && event.getEventValue() > 0) {
					byte[] buffer = serialPort.readBytes(serialPort.getInputBufferBytesCount());
					// System.out.println(buffer.length + " " + new
					// String(buffer, "US-ASCII"));

					for (int pos = 0; pos < buffer.length; pos++) {
						byte b = buffer[pos];
						if (b == 13) {
							byte[] locBuff = new byte[data.size()];
							for (int i = 0; i < locBuff.length; i++)
								locBuff[i] = data.get(i);
							data.clear();
							// System.out.println(locBuff.length + " " + new
							// String(locBuff, "US-ASCII"));
							for (TerminalEvent a : callbacks) {
								a.onScan(new String(locBuff, "US-ASCII"), false);
							}
							// break;
						} else
							data.add(b);
					}
					// System.out.println("EventValue=" +
					// event.getEventValue());
					// System.out.println(new String(buffer, "US-ASCII"));
					// serialPort.closePort();
				}
			} catch (Exception ex) {
				System.out.println(ex);
				for (TerminalEvent a : callbacks) {
					a.onScan(ex.getMessage(), true);
				}
			}

		}
	}
}