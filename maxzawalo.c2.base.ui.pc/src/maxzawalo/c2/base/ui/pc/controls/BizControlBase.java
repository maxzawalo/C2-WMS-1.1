package maxzawalo.c2.base.ui.pc.controls;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import maxzawalo.c2.base.Actions;
import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.CatalogueBO;
import maxzawalo.c2.base.data.factory.CatalogueFactory;
import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.ui.pc.form.BoListForm;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.base.utils.Logger;
import maxzawalo.c2.free.bo.enums.EnumC2;

public class BizControlBase extends JPanel {

	protected Logger log = Logger.getLogger(this.getClass().getSimpleName());

	JTextField textField;
	JLabel label;
	JButton btnSearch;
	JComboBox comboBox;
	BO item;
	protected BO fieldBO;
	String fieldName = "";
	public Class fieldType;
	protected BoListForm selectListForm;
	public boolean selectGroupOnly = false;
	public CatalogueBO owner;
	JCheckBox checkBox;
	JButton btnClear;

	public List<Action> afterBOSelected = new ArrayList<>();
	public List<Action> enterPressed = new ArrayList<>();

	Action localEnterPressed = new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (btnSearch.isVisible())
				SelectClick();
		}
	};

	public int text_limit = 0;

	public Map<String, Object> filter = new HashMap<>();

	public BizControlBase() {
		this(false);
	}

	public BizControlBase(boolean password) {
		UI.SET(this);
		setBounds(0, 0, 232, 60);

		setLayout(null);

		if (password) {
			fieldType = String.class;
			textField = new JPasswordField();
		} else
			textField = new JTextField();
		JTextFieldRegularPopupMenu.addTo(textField);
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					onEnterPressed();
					for (Action a : enterPressed)
						a.actionPerformed(null);
				}
			}
		});

		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (text_limit == 0)
					return;
				if (textField.getText().length() >= text_limit) // limit to 3
																// characters
					e.consume();
			}
		});
		textField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				textFieldChanged();
			}

			public void removeUpdate(DocumentEvent e) {
				textFieldChanged();
			}

			public void insertUpdate(DocumentEvent e) {
				textFieldChanged();
			}

		});
		textField.setColumns(10);
		textField.setBounds(10, 25, 146, 26);
		add(textField);

		// JTextArea textArea = new JTextArea(5, 20);
		// JScrollPane scrollPane = new JScrollPane(textArea);

		// Чтобы показывался список по Enter
		enterPressed.add(localEnterPressed);

		btnSearch = new JButton();
		btnSearch.setMargin(new Insets(0, 0, 0, 0));
		btnSearch.setIcon(UI.getSearchIcon(20, 20));
		btnSearch.setToolTipText("Найти");
		btnSearch.setVisible(false);

		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SelectClick();
			}
		});

		btnSearch.setBounds(162, 25, 26, 26);
		add(btnSearch);

		label = new JLabel("ControlCaption");
		label.setBounds(15, 0, 106, 26);
		add(label);

		comboBox = new JComboBox();
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (comboBox.getSelectedIndex() == 0)
					onBOSelected(null);
				else
					onBOSelected((BO) comboBox.getSelectedItem());
			}
		});
		comboBox.setVisible(false);
		comboBox.setBounds(10, 10, 213, 41);
		add(comboBox);

		checkBox = new JCheckBox("");
		checkBox.setRolloverEnabled(false);
		checkBox.setVisible(false);
		checkBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				Field f;
				try {

					f = item.getClass().getField(fieldName);
					f.set(item, checkBox.isSelected());
				} catch (Exception e) {
					log.ERROR("checkBoxStateChanged", e);
				}

				for (Action action : afterBOSelected)
					action.actionPerformed(null);
				// System.out.println("Changed: " + checkBox.isSelected());
				// System.err.println(e.getStateChange());
			}
		});

		checkBox.setBounds(10, 16, 50, 26);
		add(checkBox);

		btnClear = new JButton("x");
		btnClear.setToolTipText("Найти");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onBOSelected(null);
			}
		});
		btnClear.setMargin(new Insets(0, 0, 2, 0));
		btnClear.setBounds(197, 25, 26, 26);
		btnClear.setVisible(false);
		add(btnClear);

		// this.getRootPane()
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				onResized();
			}
		});
	}

	private void onResized() {
		checkBox.setLocation(10, checkBox.getY());
		label.setBounds(10, label.getY(), this.getWidth() - 20, label.getHeight());
		textField.setLocation(10, textField.getY());
		btnClear.setLocation(this.getWidth() - btnClear.getWidth() - 10, btnClear.getY());
		btnSearch.setLocation(btnClear.getX() - btnSearch.getWidth() - 5, btnSearch.getY());

		int textFieldW = btnSearch.getX() - textField.getX() - 5;
		if (!btnClear.isVisible())
			textFieldW = this.getWidth() - 2 * 10;
		textField.setSize(textFieldW, textField.getHeight());

		checkBox.setSize(textFieldW, this.getHeight() - 2 * 10);
		comboBox.setBounds(textField.getBounds());
	}

	protected void onEnterPressed() {
		if (fieldType == Date.class) {
			Date date = Format.extractDate(textField.getText());
			if (date != null) {
				textField.setText(Format.Show(date));
			}
		}
	}

	public void textFieldChanged() {
		if (setTextFunc)
			return;
		String data = textField.getText();

		try {
			if (fieldName.isEmpty())
				return;
			Field f = item.getClass().getField(fieldName);
			Class cl = f.getType();
			if (cl == Date.class) {
				Date date = Format.GetDate(data);
				if (date != null) {
					log.DEBUG("textFieldChanged", "Установлена дата " + Format.Show(date));
					f.set(item, date);
				}
			} else if (cl == Double.class || double.class == cl)
				f.set(item, Format.extractDouble(data));
			else
				f.set(item, data);// TODO: if BO - not set from string
			for (Action action : afterBOSelected)
				action.actionPerformed(null);
		} catch (Exception e) {
			log.ERROR("textFieldChanged", e);
		}
	}

	protected void SelectClick() {
		// TODO: авто типизация контрола
		if (fieldBO != null || fieldType != null) {
			if (fieldBO == null)
				try {
					fieldBO = (BO) fieldType.newInstance();
				} catch (Exception e) {
					log.ERROR("SelectClick", e);
				}

			if (fieldBO instanceof CatalogueBO)
				((CatalogueBO) fieldBO).name = textField.getText();

			ListFormSelect();

			selectListForm.filter = this.filter;

			// ((JDialog)
			Component parent = BizControlBase.this.getParent();
			while (!(parent instanceof JFrame))
				parent = parent.getParent();
			// ((JFrame) parent).setModal(false);

			// selectListForm.setSearch(textField.getText());
			// selectListForm.Search();
			// selectListForm.setModal(true);
			selectListForm.selectItem(BizControlBase.this, fieldBO);
		}
	}

	protected void ListFormSelect() {
		selectListForm = (BoListForm) Actions.ListFormByClassAction.Do(fieldType, fieldBO, selectGroupOnly, owner);
	}

	public void setCaption(String caption) {
		label.setVisible(!caption.isEmpty());
		if (fieldType == Boolean.class)
			label.setText("");
		else
			label.setText(caption);
		checkBox.setText(caption);
	}

	public void setToolTipText(String text) {
		textField.setToolTipText(text);
		checkBox.setToolTipText(text);
	}

	public void setBo(BO item) {
		if (!fieldName.equals(""))
			this.item = item;

		try {
			checkBox.setVisible(false);
			Object obj = getBO();

			// Контрол используется для выбора из списка
			// Справочник, Документ, Расчетная таблица (алгорим, выборка)
			if (obj == null && fieldType != String.class)
				btnSearch.setVisible(!comboBox.isVisible());
			if (obj instanceof BO) {
				fieldType = obj.getClass();
				btnSearch.setVisible(!comboBox.isVisible());
				fieldBO = (BO) obj;
				if (comboBox.isVisible()) {
					for (int i = 0; i < comboBox.getItemCount(); i++)
						if (((BO) comboBox.getItemAt(i)).id == fieldBO.id) {
							comboBox.setSelectedIndex(i);
							break;
						}
				}
			} else if (fieldType == Boolean.class) {
				textField.setVisible(false);
				btnSearch.setVisible(false);
				checkBox.setVisible(true);
				checkBox.setSelected((boolean) obj);
			}

			setText(obj);
		} catch (Exception e) {
			log.ERROR("setBo", e);
		}
		btnClear.setVisible(btnSearch.isVisible());
	}

	public Object getBO() {
		Object obj = null;

		try {

			if (item == null)
				return selectedBO;

			// Для контролов, которые исп-ся для ввода без сохранения данных
			if (fieldName.trim().equals(""))
				return null;

			System.out.println("getBO.fieldName: " + fieldName);
			Field f = item.getClass().getField(fieldName);
			obj = f.get(item);
		} catch (Exception e) {
			log.ERROR("getBO", e);
		}
		return obj;
	}

	public void LoadList() {
		try {
			// если установлен тип принудительно - пытаемся получить объект
			if (fieldType != null) {
				BO obj = ((BO) fieldType.newInstance());
				if (obj instanceof CatalogueBO)
					((CatalogueBO) obj).name = "======";
				comboBox.removeAllItems();
				comboBox.addItem(obj);
				// Arrays.asList(Global.enums).contains(fieldType)
				for (Object bo : (obj instanceof EnumC2 ? ((EnumC2) obj).getEnum()
						: new CatalogueFactory<>().Create(fieldType).GetAll())) {
					comboBox.addItem(bo);
				}
			}
		} catch (Exception e) {
			log.ERROR("LoadList", e);
		}
	}

	boolean setTextFunc = false;

	public void setText(Object obj) {
		setTextFunc = true;

		if (obj instanceof Date) {
			if (obj == null)
				obj = Format.Show(new Date());
			else
				obj = Format.Show((Date) obj);
		}

		if (obj == null)
			this.textField.setText("");
		else
			this.textField.setText("" + obj);

		textField.setCaretPosition(0);
		setTextFunc = false;
	}

	public String getText() {
		return textField.getText().trim();
	}

	public Date getDate() {
		return Format.GetDate(getText());
	}

	public Integer getInteger() {
		return Integer.parseInt(getText());
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName.replace("_id", "");
		setCaption();
	}

	protected void setCaption() {
		try {
			if (fieldType != null) {
				BoField field = (BoField) fieldType.getAnnotation(BoField.class);
				if (field != null)
					setCaption(field.caption());
			}
		} catch (Exception e) {
			log.ERROR("setFieldName", e);
		}
	}

	public String getFieldName() {
		return this.fieldName;
	}

	public Object selectedBO;

	// Выбран элемент из списка BO
	public void onBOSelected(Object selectedBO) {
		try {
			this.selectedBO = selectedBO;
			setText(selectedBO);
			if (item != null) {
				Field f = item.getClass().getField(fieldName);
				f.set(item, selectedBO);
			}
			for (Action action : afterBOSelected)
				action.actionPerformed(null);
		} catch (Exception e) {
			log.ERROR("onBOSelected", e);
		}
	}

	protected void setComboType() {
		textField.setVisible(false);
		btnSearch.setVisible(false);
		checkBox.setVisible(false);
		comboBox.setVisible(true);
	}

	protected void setCheckBoxType() {
		fieldType = Boolean.class;
		textField.setVisible(false);
		btnSearch.setVisible(false);
		comboBox.setVisible(false);
		checkBox.setVisible(true);
	}

	public void setReadOnly() {
		textField.setEditable(false);
	}

	@Override
	public void setEnabled(boolean arg0) {
		textField.setEnabled(arg0);
		btnSearch.setEnabled(arg0);
		checkBox.setEnabled(arg0);
		comboBox.setEnabled(arg0);
		btnClear.setEnabled(arg0);
	}

	public void setFieldType(Class fieldType) {
		this.fieldType = fieldType;

		try {
			btnSearch.setVisible(fieldType.newInstance() instanceof BO);
			onResized();
		} catch (Exception e) {
			log.ERROR("setFieldType", e);
		}

	}

	public void SetFocus() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				textField.grabFocus();
				textField.requestFocus();// or inWindow
			}
		});

	}
}