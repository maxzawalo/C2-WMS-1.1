package maxzawalo.c2.base.ui.pc.form;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.data.factory.FactoryBO;
import maxzawalo.c2.base.interfaces.AutoSuggestorI;
import maxzawalo.c2.base.ui.pc.catalogue.CatalogueListForm;
import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.base.ui.pc.controls.ClipboardKeyAdapter;
import maxzawalo.c2.base.ui.pc.controls.KeyPressedTable;
import maxzawalo.c2.base.ui.pc.controls.TreeExpansionUtil;
import maxzawalo.c2.base.ui.pc.document.DocForm;
import maxzawalo.c2.base.ui.pc.model.BOTableModel;
import maxzawalo.c2.base.ui.pc.model.TablePartModel;
import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.base.utils.Logger;
import maxzawalo.c2.free.search.SearchContext;

public class BoListForm<TypeBO, ItemForm> extends JFrame {

	protected Logger log = Logger.getLogger(this.getClass().getSimpleName());

	protected FactoryBO<TypeBO> factory;

	protected Class<TypeBO> typeBO;
	protected Class typeItemForm;// Class<ItemForm>
	protected TypeBO elementBO;
	protected ItemForm itemForm;

	private int X = 0;
	private int Y = 0;
	protected KeyPressedTable table;
	protected JScrollPane scrollPaneGrid;

	public List<TypeBO> items = new ArrayList<>();
	protected long pagesCount = 0;
	protected int currentPage = 0;
	protected int pageSize = 30;
	protected String searchData = "";

	int maxButtonCount = 1000;
	protected JPanel searchPanel;
	protected BizControlBase searchText;
	protected JButton btnRefresh;

	protected JButton btnDuplicate;

	protected JButton btnSelect;
	protected JScrollPane buttonsScrollPanel;

	public boolean selectMode = false;
	protected BO selectedBO;
	protected JButton btnAdd;

	protected String boCaption = "";

	protected BOTableModel tableModel;

	JCheckBox enableSuggestor;
	protected AutoSuggestorI autoSuggestor;// = new AutoSuggestor(searchText,
											// this, null,
	// Color.WHITE, Color.black, Color.black, 1f);

	TreeExpansionUtil expander;
	String treeSettingsKey = this.getClass().getName() + "_tree.settings";

	public Map<String, Object> filter = new HashMap<>();

	public BoListForm() {
		this(null);
	}

	JFrame parent;

	protected Class searchContext;

	public BoListForm(JFrame parent) {
		this.parent = parent;
		setBounds(0, 0, 1000, 700);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				onClosing();
			}
		});

		ClearCurrentPage();
		UI.SET(this);
		// this.setIconImage(UI.getAppIcon());

		Class clazz = this.getClass();
		// Рекировочка
		if (clazz.getSuperclass().getName().contains("Free"))
			clazz = clazz.getSuperclass();

		Object sc = clazz.getGenericSuperclass();
		if (sc != null && sc instanceof ParameterizedType) {
			java.lang.reflect.Type[] gParams = ((ParameterizedType) sc).getActualTypeArguments();
			if (gParams.length == 2)// для dao - иначе не создает BO
			{
				typeBO = (Class<TypeBO>) gParams[0];
				typeItemForm = (Class<ItemForm>) gParams[1];
				try {
					elementBO = typeBO.newInstance();
					// NewItemForm();
				} catch (Exception e) {
					log.ERROR("BoListForm", e);
				}
			}
		}

		setSearchContext();
		this.setIconImage(UI.getAppIcon());
		getContentPane().setLayout(null);

		// Search();

		buttonsScrollPanel = new JScrollPane();
		buttonsScrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		buttonsScrollPanel.setBounds(271, 663, 722, 51);
		// SetButtons();
		getContentPane().add(buttonsScrollPanel);

		btnPanel = new JPanel();
		btnPanel.setBounds(0, 0, 300, 40);
		buttonsScrollPanel.setViewportView(btnPanel);

		btnSelect = new JButton("Выбрать");
		btnSelect.setVisible(false);
		btnSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onSelectItem();
			}
		});
		btnSelect.setBounds(868, 11, 101, 40);
		getContentPane().add(btnSelect);

		btnAdd = new JButton("+");
		btnAdd.setToolTipText("Добавить новый");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Add();
			}

		});
		btnAdd.setBounds(10, 11, 45, 40);
		getContentPane().add(btnAdd);

		splitPane = new JSplitPane();
		splitPane.setLocation(10, 60);
		splitPane.setSize(983, 587);
		getContentPane().add(splitPane);

		scrollPaneGrid = new JScrollPane();
		scrollPaneGrid.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		splitPane.setRightComponent(scrollPaneGrid);

		CreateTable();

		if (table != null) {
			// TODO разобраться - фикс для подбора чтобы не падал
			table.setFillsViewportHeight(true);
			table.setRowHeight(UIManager.getInt("Table.rowHeight"));
			table.settingsKey = getClass().getSimpleName() + ".table";
			table.EnableMarkDeleteRow();

			// TODO: вынести в table
			table.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						onTableRowDblClick(e);
					}
					if (e.getClickCount() == 1) {
						onTableRowClick(e);
					}
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					// int r = table.rowAtPoint(e.getPoint());
					// if (r >= 0 && r < table.getRowCount()) {
					// table.setRowSelectionInterval(r, r);
					// } else {
					// table.clearSelection();
					// }
					//
					// if (table.getSelectedRow() < 0)
					// return;
				}
			});
			scrollPaneGrid.setViewportView(table);
			table.doDeleteQuestion = true;
		}

		// table.setSize(new Dimension(100,100));

		panel = new JPanel();
		splitPane.setLeftComponent(panel);

		searchPanel = new JPanel();
		searchPanel.setBounds(429, 0, 424, 67);
		getContentPane().add(searchPanel);
		searchPanel.setLayout(null);

		searchText = new BizControlBase();
		searchText.setCaption("");
		searchText.enterPressed.add(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnSearchClick();
			}
		});

		searchText.setFieldType(String.class);
		CreateAutoSuggestor();
		// Каждый раз при открытии очищаем, потому что долгий кэш
		SearchContext.Clear(searchContext);
		if (autoSuggestor != null) {
			List<String> words = SearchContext.FromPhrases(searchContext, "");
			autoSuggestor.setDictionary(words);

		}
		// searchText.addKeyListener(new KeyAdapter() {
		// @Override
		// public void keyPressed(KeyEvent e) {
		// if (e.getKeyCode() == KeyEvent.VK_ENTER) {
		// if (autoSuggestor != null)
		// autoSuggestor.TextFieldPressEnter();
		// btnSearchClick();
		// }
		// }
		// });

		searchText.setBounds(98, -7, 312, 62);
		searchPanel.add(searchText);
		// searchText.setColumns(10);

		if (autoSuggestor != null)
			addComponentListener(new ComponentAdapter() {
				public void componentMoved(ComponentEvent e) {
					autoSuggestor.Move();
				}
			});

		btnRefresh = new JButton();
		btnRefresh.setIcon(UI.getRefreshIcon(20, 20));
		btnRefresh.setToolTipText("Обновить");
		btnRefresh.setBounds(10, 10, 40, 40);
		searchPanel.add(btnRefresh);

		enableSuggestor = new JCheckBox("");
		enableSuggestor.setVisible(false);
		// enableSuggestor.setSelected(Settings.enableSuggestor);
		enableSuggestor.setToolTipText("Включить автозаполнение");
		enableSuggestor.setBounds(61, 16, 27, 29);
		if (autoSuggestor != null)
			enableSuggestor.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					autoSuggestor.setEnabled(enableSuggestor.isSelected());
				}
			});
		searchPanel.add(enableSuggestor);
		if (autoSuggestor != null)
			autoSuggestor.setEnabled(enableSuggestor.isSelected());

		btnDuplicate = new JButton("Дубликат");
		btnDuplicate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DuplicateSelected();
			}
		});
		btnDuplicate.setBounds(285, 11, 103, 40);
		getContentPane().add(btnDuplicate);

		// textArea.setvie
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnRefreshClick();
			}
		});

		if (typeBO != null) {
			try {
				boCaption = ((BO) typeBO.newInstance()).getRusName();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			setTitle(boCaption);
		}

		searchText.addFocusListener(new CustomFocusListener());

		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				searchText.setFocusable(true);
				searchText.requestFocusInWindow();
			}
		});

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				onFormResized();
			}
		});

		table.editRowPopupItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				OpenSelected();
			}
		});

	}

	protected void CreateAutoSuggestor() {
		// autoSuggestor = new AutoSuggestor(searchText, this, null,
		// Color.WHITE, Color.black, Color.black, 1f);
		// autoSuggestor.context = searchContext;
	}

	protected void setSearchContext() {
		searchContext = typeBO;
	}

	protected void NewItemForm() {
		try {
			itemForm = (ItemForm) typeItemForm.newInstance();// .getDeclaredConstructor(JFrame.class).newInstance(null);
		} catch (Exception e) {
			log.ERROR("NewItemForm", e);
		}
	}

	public TypeBO GetSelectedItem() {
		int row = table.getSelectedRow();
		return (TypeBO) ((BOTableModel) table.getModel()).getItem(row);
	}

	protected void CreateTable() {
		table = new KeyPressedTable();
		table.addKeyListener(new ClipboardKeyAdapter(table));
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	}

	protected void onSelectItem() {
		if (senderControl instanceof BizControlBase)
			((BizControlBase) senderControl).onBOSelected(selectedBO);
		else if (senderControl instanceof TablePartModel)
			((TablePartModel) senderControl).setValueAt(selectedBO, rowIndex, columnIndex);
		else if (senderControl instanceof DocForm)
			((DocForm) senderControl).onBOSelected(selectedBO);

		if (selectedBO == null && !(this instanceof CatalogueListForm && ((CatalogueListForm) this).selectGroupOnly))
			return;
		setVisible(false);
		selectMode = false;
	}

	// protected void setColumnModel() {
	// DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
	// rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
	// table.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
	// }

	protected void AddNewElement() {
		NewItemForm();
		((BoForm) itemForm).NewBO();
	}

	protected JScrollPane SetButtons() {
		int buttonCount = getButtonsCount();

		btnPanel.removeAll();
		btnPanel.setBorder(null);

		for (int i = buttonCount - 1; i >= 0; i--) {
			JButton b = new JButton((i + 1) + "");
			if (currentPage == i)
				b.setEnabled(false);

			Font f = new Font(b.getFont().getFontName(), b.getFont().getStyle(), 10);
			b.setFont(f);
			b.setMargin(new Insets(0, 0, 0, 0));
			b.setPreferredSize(new Dimension(20, 20));
			b.putClientProperty("page", i);
			b.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent evt) {
					JButton b = (JButton) evt.getSource();
					currentPage = (int) b.getClientProperty("page");
					Search();
					table.revalidate();
					table.repaint();
					System.out.println(currentPage);
				}
			});
			btnPanel.add(b);
		}
		PagerSelected(buttonCount > 1);

		// int scrollbarSize = UIManager.getInt("ScrollBar.width");
		// buttonPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0,
		// (int) (scrollbarSize * 0.7)));
		//
		btnPanel.revalidate();
		btnPanel.repaint();
		return buttonsScrollPanel;
	}

	protected void PagerSelected(Boolean buttonCountG1) {
		// if ((BoListForm.this instanceof LotOfProductListForm) &&
		// buttonCountG1)
		// btnPanel.setBorder(new LineBorder(Color.RED));
	}

	protected int getButtonsCount() {
		return Math.min((int) pagesCount, maxButtonCount);
	}

	protected void ClearCurrentPage() {
		currentPage = Integer.MAX_VALUE;
	}

	public void setSearch(String value) {
		ClearCurrentPage();
		searchData = value;
	}

	// TODO: async
	public void Search() {
		pagesCount = FactoryBO.GetPagesCount(new FactoryBO<>().Create(typeBO).GetCount(), pageSize);
	}

	public void setModel() {
		table.setModel(tableModel);
		// setColumnModel();
		table.LoadTableSettings();
		table.setColumnSettings();
	}

	Object senderControl;

	// public void selectItem(BizControl senderControl, BO item) {
	// this.senderControl = senderControl;
	// selectMode = true;
	//// btnSelect.setVisible(selectMode);
	// setVisible(true);
	// }

	public int rowIndex = 0;
	public int columnIndex = 0;
	protected JSplitPane splitPane;
	private JPanel panel;
	protected JPanel btnPanel;

	public void selectItem(Object senderControl, BO boFromTP) {
		this.senderControl = senderControl;
		selectMode = true;
		setVisible(true);
	}

	protected void onTableRowDblClick(MouseEvent e) {
		if (selectMode) {
			BO bo = (BO) ((BOTableModel) table.getModel()).getItem(table.getSelectedRow());
			if (bo.deleted) {
				log.WARN("onTableRowDblClick", "Элемент удален");
				Console.I().WARN(getClass(), "onTableRowDblClick", "Элемент не может быть выбран, т.к. удален.");
				return;
			}
			onSelectItem();
		} else {
			System.out.println("dbl click " + table.getSelectedRow());
			System.out.println("" + ((BOTableModel) table.getModel()).getItem(table.getSelectedRow()));

			OpenSelected();
		}
	}

	protected void OpenSelected() {
		BO bo = (BO) ((BOTableModel) table.getModel()).getItem(table.getSelectedRow());
		if (bo.deleted) {
			log.WARN("OpenSelected", "Элемент удален");
			Console.I().INFO(getClass(), "OpenSelected", "Элемент удален");
			return;
		}

		NewItemForm();
		// ((BoForm) itemForm).setModal(true);
		((BoForm) itemForm).Load(bo.id);
		((BoForm) itemForm).setVisible(true);
	}

	protected void DuplicateSelected() {
		BO bo = (BO) ((BOTableModel) table.getModel()).getItem(table.getSelectedRow());
		if (bo.deleted) {
			log.WARN("DuplicateSelected", "Элемент удален");
			return;
		}

		NewItemForm();
		// ((BoForm) itemForm).setModal(true);
		((BoForm) itemForm).Duplicate(bo.id);
		((BoForm) itemForm).setVisible(true);
	}

	protected Action onRowSelected;

	protected void onTableRowClick(MouseEvent e) {
		int row = table.rowAtPoint(e.getPoint());
		// int col = table.columnAtPoint(e.getPoint());

		System.out.println("click " + row);
		selectedBO = (BO) ((BOTableModel) table.getModel()).getItem(row);
		if (selectedBO == null)
			table.clearSelection();
		else if (onRowSelected != null)
			onRowSelected.actionPerformed(null);

	}

	protected void btnSearchClick() {
		setSearch(searchText.getText());
		SaveUserEnter();
		Search();
		SetButtons();
	}

	protected void SaveUserEnter() {
		SearchContext.SaveUserEnter(searchContext, null, searchData);
	}

	protected void onFormResized() {
		// System.out.println("BoListForm resize");
		int parentWidth = splitPane.getParent().getWidth();
		ResizeSplitPane(parentWidth);

		System.out.println(scrollPaneGrid.getParent());
		int rightPanelWidth = (int) (splitPane.getWidth() * (1 - splitPane.getResizeWeight())) - 10;
		splitPane.revalidate();
		scrollPaneGrid.setSize(rightPanelWidth, splitPane.getHeight());
		scrollPaneGrid.revalidate();
		if (table != null)
			table.LoadTableSettings();

		ResizeBtnPanel();
	}

	protected void ResizeBtnPanel() {
		int btnPanelWidth = (int) (splitPane.getWidth() * 0.65);
		// if (BoListForm.this instanceof DocListForm)
		// btnPanelWidth = buttonsScrollPanel.getParent().getWidth() - 20;
		buttonsScrollPanel.setSize(btnPanelWidth, buttonsScrollPanel.getHeight());
		buttonsScrollPanel.setLocation(buttonsScrollPanel.getParent().getWidth() - buttonsScrollPanel.getWidth() - 10,
				splitPane.getY() + splitPane.getHeight() + 20);

		btnPanel.setSize(buttonsScrollPanel.getWidth(), buttonsScrollPanel.getHeight());
	}

	protected void ResizeSplitPane(int parentWidth) {
		int splitPaneHeight = splitPane.getParent().getHeight() - 140;
		// if (BoListForm.this.getClass() == LotOfProductListForm.class)
		// splitPaneHeight = (int) (splitPane.getParent().getHeight() * 0.4);
		splitPane.setSize(parentWidth - 20, splitPaneHeight);
	}

	protected void btnRefreshClick() {
		Search();
		SetButtons();
	}

	protected void onClosing() {
		// int i=JOptionPane.showConfirmDialog(null, "Seguro que quiere
		// salir?");
		// if(i==0)
		// System.exit(0);//cierra aplicacion
	}

	protected void Add() {
		AddNewElement();
		// ((BoForm) itemForm).setModal(true);
		((BoForm) itemForm).setVisible(true);
	}

	public class CustomFocusListener implements FocusListener {
		public void focusGained(FocusEvent e) {
			System.out.println(e.getSource() + " — focusGained()");
		}

		public void focusLost(FocusEvent e) {
			System.out.println(e.getSource() + " — focusLost()");
		}
	}
}