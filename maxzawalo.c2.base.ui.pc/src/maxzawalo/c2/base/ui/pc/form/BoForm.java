package maxzawalo.c2.base.ui.pc.form;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import com.google.gson.Gson;
import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.base.data.factory.FactoryBO;
import maxzawalo.c2.base.interfaces.TerminalEvent;
import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.base.utils.Logger;

public class BoForm<TypeBO> extends JFrame implements TerminalEvent {
	public static Gson gson;
	protected Logger log = Logger.getLogger(this.getClass().getSimpleName());
	protected FactoryBO<TypeBO> factory;

	protected Class<TypeBO> typeBO;
	// TODO: protected
	public TypeBO elementBO;

	protected JPanel bottomPanel;
	protected JPanel topPanel;

	protected BizControlBase code;

	public BoForm() {
		this(null);
	}

	JFrame parent;
	protected JButton btnSave;

	public BoForm(JFrame parent) {
		this.parent = parent;
		setBounds(0, 0, 1000, 700);
		// super(parent);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				System.out.println("close");
				// BoForm.this.dispose();
			}
		});

		this.getRootPane().addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				onFormResized();
			}
		});

		UI.SET(this);

		try {
			Class clazz = this.getClass();
			java.lang.reflect.Type[] gParams = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments();
			if (gParams.length == 1)// для dao - иначе не создает BO
			{
				typeBO = (Class<TypeBO>) gParams[0];
				elementBO = typeBO.newInstance();
			}
		} catch (Exception e) {
			// log.ERROR("BoForm", e);
			e.printStackTrace();
		}

		// this.setIconImage(UI.getAppIcon());

		setBounds(0, 0, 1000, 700);

		topPanel = new JPanel();
		// topPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		topPanel.setBounds(0, 0, 1008, 657);

		code = CreateBizControl();
		code.setBounds(12, 12, 302, 56);
		code.setCaption("Код");
		code.setFieldName(BO.fields.CODE);

		bottomPanel = new JPanel();
		bottomPanel.setBounds(0, 656, 1008, 74);
		topPanel.setLayout(null);
		topPanel.add(code);
		ImageIcon icon = (ImageIcon) UIManager.getIcon("FileView.computerIcon");
		getContentPane().setLayout(null);
		getContentPane().add(topPanel);
		getContentPane().add(bottomPanel);
		bottomPanel.setLayout(null);

		btnSave = new JButton("Сохранить");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Save();
			}
		});
		btnSave.setBounds(891, 12, 106, 42);
		btnSave.setAlignmentX(Component.CENTER_ALIGNMENT);
		bottomPanel.add(btnSave);

		InitTerminal();
	}

	protected void InitTerminal() {
	}

	protected BizControlBase CreateBizControl() {
		return new BizControlBase();
	}

	public void Load(int id) {
		try {
			elementBO = factory.GetById(id, 0, false);
		} catch (Exception e) {
			log.ERROR("Load", e);
		}
		setData();
	}

	public void NewBO() {
		try {
			elementBO = typeBO.newInstance();
		} catch (Exception e) {
			log.ERROR("NewBO", e);
		}
		setData();
	}

	public void Duplicate(int id) {
		try {
			// TODO: test null fields foreignAutoRefresh = false
			elementBO = (TypeBO) ((BO) factory.GetById(id, 0, false)).cloneBO();
		} catch (Exception e) {
			log.ERROR("Duplicate", e);
		}
		setData();
	}

	/**
	 * Рекурсивная ф-я. Находит все BizControl на форме.
	 * 
	 * @param component
	 */
	public void setBizControlsInComponent(Component component) {
		if (component instanceof BizControlBase) {
			try {
				String fieldName = (((BizControlBase) component).getFieldName());
				if (fieldName.equals(""))
					return;
				Field f = typeBO.getField(fieldName);
				DatabaseField df = f.getAnnotation(DatabaseField.class);
				((BizControlBase) component).text_limit = df.width();
			} catch (Exception e) {
				log.ERROR("setBizControlsInComponent", e);
			}
			((BizControlBase) component).setBo((BO) elementBO);
		} else if (component instanceof Container)
			for (int j = 0; j < ((Container) component).getComponentCount(); j++) {
				Component co = ((Container) component).getComponent(j);
				setBizControlsInComponent(co);
			}
	}

	protected void beforeSetData() {
	}

	public void setData() {
		beforeSetData();
		if (elementBO != null) {
			setTitle(getRusTitle());
		}
		setBizControlsInComponent(this);
		setEvents();
		AfterSetData();
	}

	protected void AfterSetData() {
	}

	public String getRusTitle() {
		return ((BO) elementBO).getRusName();
	}

	protected void setEvents() {
	}

	public boolean Save() {
		try {
			BO bo = ((BO) elementBO);
			if (bo.id == 0) {
				factory.Save(elementBO);
			} else {
				// TODO: проверять статус и видимость по роли. перенести в ядро?
				bo = (BO) factory.GetById(bo.id, 0, false);
				// TODO:chech changed
				if (bo.locked_by == null || bo.locked_by.id == User.zero.id) {
					factory.Save(elementBO);
				} else {
					Console.I().INFO(getClass(), "Save", "Объект заблокирован пользователем: " + bo.locked_by);
					return false;
				}
			}
			// Обновляем визуально Код при первом сохранении
			code.setBo((BO) elementBO);
			code.revalidate();
			Console.I().INFO(getClass(), "Save", "Сохранен: " + ((BO) elementBO).getRusName());
		} catch (Exception e) {
			((BO) elementBO).Dump();
			log.ERROR("Save", e);
			Console.I().INFO(getClass(), "Save", e.getMessage());
			return false;
		}
		return true;
	}

	protected void onFormResized() {
		topPanel.setBounds(0, 0, BoForm.this.getContentPane().getWidth(), BoForm.this.getContentPane().getHeight() - bottomPanel.getHeight());

		bottomPanel.setBounds(0, BoForm.this.getContentPane().getHeight() - bottomPanel.getHeight(), BoForm.this.getContentPane().getWidth(), bottomPanel.getHeight());
		btnSave.setLocation(btnSave.getParent().getWidth() - btnSave.getWidth() - 10, btnSave.getY());
	}

	@Override
	public void onScan(final String value, boolean exc) {
		try {
			if (SwingUtilities.isEventDispatchThread()) {
				ProcessScanData(value);
			} else {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						ProcessScanData(value);
					}
				});
			}
		} catch (Exception e) {
			log.ERROR("onScan", e);
		}
	}

	protected void ProcessScanData(String barcode) {

	}
}