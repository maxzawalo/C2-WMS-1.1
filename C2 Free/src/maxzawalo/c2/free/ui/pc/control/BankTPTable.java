package maxzawalo.c2.free.ui.pc.control;

import javax.swing.table.TableModel;

import maxzawalo.c2.base.ui.pc.controls.PopupPanelTable;

public class BankTPTable extends PopupPanelTable {
//	JPopupMenu productPopupMenu;
//	JPopupMenu lotsPopupMenu;
//	JButton selectLot;
//	JPanel popupPanel;
//	JButton btnSelectProduct;
//	JButton btnOpenBO;

//	public Date docDate;

//	public Action ChangeLotAction;

	public BankTPTable() {
		this(null);
	}

	public BankTPTable(TableModel model) {
		super(model);

//		lotsPopupMenu = new JPopupMenu();
//		lotsPopupMenu.setBorder(BorderFactory.createLineBorder(Color.BLACK));
//
//		productPopupMenu = new JPopupMenu();
//
//		popupPanel = new JPanel();
//		popupPanel.setLayout(null);
//		productPopupMenu.add(popupPanel);
//		productPopupMenu.setOpaque(false);
//
//		btnSelectProduct = new JButton("v");
//		btnSelectProduct.setToolTipText("Выбрать из списка");
//		btnSelectProduct.setMargin(new Insets(0, 0, 0, 0));
//		btnSelectProduct.setSize(btnSize, btnSize);
//		btnSelectProduct.setBackground(Color.WHITE);
//		btnSelectProduct.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				UI.Start(BankTPTable.this);
//				// Class fieldType = (Class) params[0];
//				// BO fieldBO = (BO) params[1];
//				// boolean selectGroupOnly = (boolean) params[2];
//				// BO owner = (BO) params[3];
//				selectListForm = (BoListForm) Actions.ListFormByClassAction.Do(Contract.class, selectedBO, false, null);
//				selectListForm.selectItem(BankTPTable.this.getModel(), selectedBO);
//				UI.Stop(BankTPTable.this);
//			}
//		});
//		popupPanel.add(btnSelectProduct);
//		popupPanel.setOpaque(false);
//
//		btnOpenBO = new JButton("[ ]");
//		btnOpenBO.setToolTipText("Открыть");
//		btnOpenBO.setMargin(new Insets(0, 0, 0, 0));
//		btnOpenBO.setSize(btnSize, btnSize);
//		btnOpenBO.setBackground(Color.WHITE);
//		btnOpenBO.setFont(new Font("Tahoma", Font.PLAIN, 10));
//		btnOpenBO.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				OpenElement();
//			}
//		});
//		popupPanel.add(btnOpenBO);
//
//		selectLot = new JButton("--");
//		selectLot.setToolTipText("Партия");
//		selectLot.setMargin(new Insets(0, 0, 0, 0));
//		selectLot.setSize(btnSize, btnSize);
//		selectLot.setFont(new Font("Tahoma", Font.PLAIN, 10));
//		selectLot.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				// SelectLot();
//			}
//		});
//		popupPanel.add(selectLot);
//
//		popupPanel.setBackground(Color.LIGHT_GRAY);
//		productPopupMenu.setBorder(null);
//		productPopupMenu.setBackground(Color.WHITE);
//
//		productPopupMenu.revalidate();

	}

//	// TODO: переенести в KeyPressedTable
//	public void ShowMenu(BO selectedBO, int x, int y, int rowIndex, int columnIndex) {
//		if (selectListForm == null)
//			selectListForm = (BoListForm) Actions.ListFormByClassAction.Do(Contract.class, selectedBO, false, null);
//
//		selectListForm.rowIndex = rowIndex;
//		selectListForm.columnIndex = columnIndex;
//
//		startLotPopupY = UIManager.getInt("Table.rowHeight") * rowIndex;
//
//		this.selectedBO = selectedBO;
//		selectedTP = (TablePartItem) ((BOEditTableModel) getModel()).getList().get(rowIndex);
//
//		popupPanel.setSize(getColumnModel().getColumn(columnIndex).getWidth() - 5, btnOpenBO.getHeight());
//
//		selectLot.setLocation(0, 0);
//		btnOpenBO.setLocation(popupPanel.getWidth() - btnSize, 0);
//		btnSelectProduct.setLocation(btnOpenBO.getX() - btnSize - 10, 0);
//		productPopupMenu.show(this, 0, y - 2);
//	}

//	@Override
//	protected boolean isBigToolTipColumn(int column) {
//		// return new ArrayList<String>(new String[]{}).contains(column)
//		return (column == ((BOTableModel) getModel())
//				.getColNumByVisibleColumns(BankTP.fields.PRODUCT)
//				|| column == ((BOTableModel) getModel()).getColNumByVisibleColumns("group")
//				|| column == ((BOTableModel) getModel()).getColNumByVisibleColumns("price_bo")
//				|| column == ((BOTableModel) getModel()).getColNumByVisibleColumns("lot")
//				|| column == ((BOTableModel) getModel()).getColNumByVisibleColumns("doc_contract"));
//	}

	// protected List<LotOfProduct> GetLots() {
	// if (Actions.GetLots4TPAction != null)
	// return (List<LotOfProduct>) Actions.GetLots4TPAction.Do(selectedBO,
	// store, docDate);
	//
	// return new ArrayList<>();
	// }

//	protected void OpenElement() {
//		UI.Start(this);
//		// BoForm selectForm = (BoForm)
//		Actions.OpenBoFormByInstanceAction.Do(selectedBO);
//		// selectForm.Load(selectedBO.id);
//		// selectForm.setVisible(true);
//		UI.Stop(this);
//	}
}