package maxzawalo.c2.full.ui.pc.form;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.base.ui.pc.controls.DateBizControl;
import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.base.utils.Global;
import maxzawalo.c2.full.data.GroupTransaction;

public class GroupTransactionForm extends JFrame {
	BizControlBase fromDate;
	BizControlBase toDate;
	JCheckBox doSaveTransaction;
	JButton btnStartPause;
	JButton btnSaveTransactions;
	JCheckBox ProductTransactionOnly;
	JCheckBox AccTransactionOnly;

	Thread doThread;
	Thread saveThread;

	public GroupTransactionForm() {
		setBounds(0, 0, 341, 241);
		getContentPane().setLayout(null);
		setTitle("Групповое проведение");

		fromDate = new DateBizControl();
		fromDate.setCaption("C");
		fromDate.setBounds(0, 0, 164, 56);
		fromDate.onBOSelected(new Date());
		getContentPane().add(fromDate);

		toDate = new DateBizControl();
		toDate.setCaption("по");
		toDate.setBounds(164, 0, 164, 56);
		toDate.onBOSelected(new Date());
		getContentPane().add(toDate);

		btnStartPause = new JButton("Старт");
		btnStartPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doSaveTransaction.setEnabled(false);
				AccTransactionOnly.setEnabled(false);
				ProductTransactionOnly.setEnabled(false);
				if (!Global.InMemoryGroupTransaction) {
					if (!doSaveTransaction.isSelected())
						fromDate.setEnabled(false);
					Run();
					btnStartPause.setText("Пауза");
				} else {
					Global.StartPause();
					if (Global.getPaused()) {
						btnStartPause.setText("Старт");
						Console.I().INFO(getClass(), "btnStartPause", "Проведение - Пауза");
					} else {
						btnStartPause.setText("Пауза");
						Console.I().INFO(getClass(), "btnStartPause", "Проведение - Старт");
					}
				}
			}
		});
		btnStartPause.setBounds(90, 164, 137, 29);
		getContentPane().add(btnStartPause);

		doSaveTransaction = new JCheckBox("Сохранять проводки");
		doSaveTransaction.setLocation(10, 62);
		doSaveTransaction.setSize(200, 20);
		doSaveTransaction.setSelected(true);
		doSaveTransaction.setRolloverEnabled(false);
		getContentPane().add(doSaveTransaction);

		ProductTransactionOnly = new JCheckBox("Только Товарный регистр");
		ProductTransactionOnly.setLocation(10, 101);
		ProductTransactionOnly.setSize(303, 20);
		ProductTransactionOnly.setSelected(false);
		ProductTransactionOnly.setRolloverEnabled(false);
		getContentPane().add(ProductTransactionOnly);

		AccTransactionOnly = new JCheckBox("Только Бухгалтерсский регистр");
		AccTransactionOnly.setLocation(10, 128);
		AccTransactionOnly.setSize(303, 20);
		AccTransactionOnly.setSelected(false);
		AccTransactionOnly.setRolloverEnabled(false);
		getContentPane().add(AccTransactionOnly);

		ProductTransactionOnly.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				CheckSelectedRegistry();
			}
		});

		AccTransactionOnly.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				CheckSelectedRegistry();
			}
		});

		btnSaveTransactions = new JButton("Сохранить проводки");
		btnSaveTransactions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Runnable saveTask = () -> {
					GroupTransaction.SaveTransactions(ProductTransactionOnly.isSelected(),
							AccTransactionOnly.isSelected());
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							fromDate.setEnabled(true);
							doSaveTransaction.setEnabled(true);
							AccTransactionOnly.setEnabled(true);
							ProductTransactionOnly.setEnabled(true);
						}
					});
				};
				saveThread = new Thread(saveTask);
				saveThread.start();

			}
		});
		btnSaveTransactions.setBounds(65, 222, 191, 29);
		getContentPane().add(btnSaveTransactions);

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent evt) {
				// Если есть несохраненные проводки - спрашиваем - сохраняем
				System.out.println("close");

				if (doThread != null && doThread.isAlive()) {
					try {
						doThread.interrupt();
//						doThread.join();
					} catch (Exception e1) {
						
						e1.printStackTrace();
					}
					Console.I().WARN(getClass(), "windowClosed", "Проведение отменено");
				}

				if (saveThread != null && saveThread.isAlive()) {
					try {
						saveThread.interrupt();
//						saveThread.join();
					} catch (Exception e1) {
						
						e1.printStackTrace();
					}
					Console.I().WARN(getClass(), "windowClosed", "Сохрание отменено");
				}

				GroupTransaction.ClearGroupTransactionCache();
				// Global.AccTransactionOnly = false;
				Global.setPriority(0);
			}
		});

		JSlider framesPerSecond = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);
		framesPerSecond.setLocation(12, 275);
		framesPerSecond.setSize(301, 56);
		framesPerSecond.setMajorTickSpacing(20);
		framesPerSecond.setMinorTickSpacing(10);
		framesPerSecond.setPaintTicks(true);
		framesPerSecond.setPaintLabels(true);
		framesPerSecond.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				Global.setPriority((framesPerSecond.getMaximum() - framesPerSecond.getValue()) * 20);

			}
		});
		getContentPane().add(framesPerSecond);
	}

	protected void CheckSelectedRegistry() {
		if (ProductTransactionOnly.isSelected() && AccTransactionOnly.isSelected()) {
			Console.I().WARN(getClass(), "CheckSelectedRegistry", "Можно исключить только один регистр");
			ProductTransactionOnly.setSelected(false);
			AccTransactionOnly.setSelected(false);
		}
	}

	public void Run() {
		UI.Start(this);
		Runnable runTask = () -> {
			GroupTransaction.Do(fromDate.getDate(), toDate.getDate(), doSaveTransaction.isSelected(),
					ProductTransactionOnly.isSelected(), AccTransactionOnly.isSelected());// Format.GetDate("01.06.2017"));
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					EndProcess();
				}
			});

		};
		doThread = new Thread(runTask);
		doThread.start();
	}

	protected void EndProcess() {
		// Включаем только после сохранения. Т.к. в ней исп-ся параметры.
		if (doSaveTransaction.isSelected()) {
			doSaveTransaction.setEnabled(true);
			AccTransactionOnly.setEnabled(true);
			ProductTransactionOnly.setEnabled(true);
		}

		System.out.println("EndProcess");
		UI.Stop(GroupTransactionForm.this);
		btnStartPause.setText("Старт");
		JOptionPane.showMessageDialog(GroupTransactionForm.this, "Групповое проведение завершено.", "",
				JOptionPane.INFORMATION_MESSAGE);
	}
}