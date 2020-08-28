package maxzawalo.c2.base.ui.pc.form;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import maxzawalo.c2.base.Actions;
import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.ui.pc.controls.SwingLink;
import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.base.utils.Console.LogItem;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.base.utils.Global;
import maxzawalo.c2.base.utils.Logger.LogLevel;

public class ConsoleForm extends JFrame {

	final public static ConsoleForm console = new ConsoleForm();

	JTextPane consoleArea;
	JScrollPane scrollPane;
	protected JButton btnClean;
	SwingLink link2Doc;

	PrintTask printTask;

	public ConsoleForm() {
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		UI.SET(this);
		getContentPane().setLayout(null);
		setBounds(0, 200, 1000, 304);

		scrollPane = new JScrollPane();
		scrollPane.setLocation(0, 0);
		scrollPane.setSize(984, 246);
		getContentPane().add(scrollPane);

		consoleArea = new JTextPane();
		scrollPane.setViewportView(consoleArea);

		btnClean = new JButton("Очистить");
		btnClean.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Clean();
			}
		});
		btnClean.setBounds(10, 269, 125, 23);
		getContentPane().add(btnClean);

		link2Doc = new SwingLink();
		link2Doc.setVisible(false);
		link2Doc.setFont(link2Doc.getFont().deriveFont(14f));
		link2Doc.setBounds(179, 232, 397, 20);
		link2Doc.setup("Ссылка на док");
		link2Doc.onClick = new AbstractAction() {
			public void actionPerformed(ActionEvent evt) {
				if (Actions.OpenBadDocAction != null)
					Actions.OpenBadDocAction.Do(Global.GetBadTransactionDoc());
			}
		};
		getContentPane().add(link2Doc);

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				onFormResized();
			}
		});

		onFormResized();

		// printTask = new PrintTask();
		// printTask.execute();

		Runnable printTask = new Runnable() {
			public void run() {
				Date lastRead = new Date();
				while (true) {
					try {
						// TODO: регулируется в зависимости от
						// наполнения/сек
						// TODO:
						if (Global.InMemoryGroupTransaction)
							Thread.sleep(1000);
						else
							Thread.sleep(300);

						for (LogItem item : Console.I().getMessages(lastRead)) {
							lastRead = item.time;
							EventQueue.invokeLater(new Runnable() {
								public void run() {
									String msg = Format.Show("HH:mm:ss", item.time) + "| " + item.message + "\n";
									PrintLocal(msg, item.level);
								}
							});
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};

		t = new Thread(printTask);
		t.start();
	}

	Thread t;

	protected void onFormResized() {
		btnClean.setLocation(btnClean.getX(), this.getHeight() - btnClean.getHeight() - 50);
		scrollPane.setBounds(10, 10, this.getWidth() - 35, btnClean.getY() - 30);
		link2Doc.setLocation(link2Doc.getX(), this.getHeight() - link2Doc.getHeight() - 50);
	}

	// public void Print(String message) {
	// Print(message, LogLevel.INFO);
	// }

	// public void Print(String message, int level) {
	// final String msg = Format.Show("HH:mm:ss", new Date()) + "| " + message +
	// "\n";
	//
	// EventQueue.invokeLater(new Runnable() {
	// public void run() {
	// PrintLocal(msg, level);
	// }
	// });
	//
	// // if (SwingUtilities.isEventDispatchThread()) {
	// // PrintLocal(msg, level);
	// // } else {
	// // SwingUtilities.invokeLater(new Runnable() {
	// // public void run() {
	// // PrintLocal(msg, level);
	// // }
	// // });
	// // }
	// }

	private void appendToPane(JTextPane tp, String msg, Color c, boolean bold) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
		aset = sc.addAttribute(aset, StyleConstants.Bold, bold);

		// aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida
		// Console");
		// aset = sc.addAttribute(aset, StyleConstants.Alignment,
		// StyleConstants.ALIGN_JUSTIFIED);

		int len = tp.getDocument().getLength();
		tp.setCaretPosition(len);
		tp.setCharacterAttributes(aset, false);
		tp.replaceSelection(msg);
	}

	protected void PrintLocal(String msg, int level) {
		// link2Doc.setVisible(false);
		Color color = Color.BLACK;
		if (level >= LogLevel.ERROR)
			color = Color.RED;

		boolean bold = (level == LogLevel.WARN);

		try {
			appendToPane(consoleArea, msg, color, bold);
			setVisible(true);
			if (Global.groupTransaction) {
				if (Global.GetBadTransactionDoc() != null) {
					link2Doc.setVisible(true);
					link2Doc.setup("" + Global.GetBadTransactionDoc());
				} else
					link2Doc.setVisible(false);
			} else
				setState(Frame.NORMAL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void Clean() {
		consoleArea.setText("");
		link2Doc.setVisible(false);
	}

	class PrintTask extends SwingWorker<Void, Void> {
		@Override
		protected Void doInBackground() throws Exception {
			{
				Date lastRead = new Date();
				while (true) {
					try {
						// TODO: регулируется в зависимости от
						// наполнения/сек
						Thread.sleep(100);

						for (LogItem item : Console.I().getMessages(lastRead)) {
							lastRead = item.time;
							String msg = Format.Show("HH:mm:ss", item.time) + "| " + item.message + "\n";
							PrintLocal(msg, item.level);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		@Override
		protected void done() {

		}
	}

	public void Init() {
	}
}