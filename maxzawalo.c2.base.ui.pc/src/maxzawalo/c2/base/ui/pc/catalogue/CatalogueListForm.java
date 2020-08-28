package maxzawalo.c2.base.ui.pc.catalogue;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.CatalogueBO;
import maxzawalo.c2.base.data.factory.CatalogueFactory;
import maxzawalo.c2.base.data.factory.FactoryBO;
import maxzawalo.c2.base.ui.pc.controls.TreeExpansionUtil;
import maxzawalo.c2.base.ui.pc.form.BoForm;
import maxzawalo.c2.base.ui.pc.form.BoListForm;
import maxzawalo.c2.base.utils.FileUtils;
import maxzawalo.c2.free.search.SearchContext;

public class CatalogueListForm<TypeBO, ItemForm> extends BoListForm<TypeBO, ItemForm> {

	public boolean selectGroupOnly = false;
	protected JTree tree;
	protected CatalogueBO parentGroup;
	protected CatalogueGroupForm groupForm;// = new
	// CatalogueGroupForm<TypeBO>();

	protected JButton btnAddGroup;
	protected JButton btnGroupEdit;
	TreeExpansionUtil expander;
	String treeSettingsKey = this.getClass().getName() + "_tree.settings";

	public CatalogueListForm() {
		this(null);
	}

	public CatalogueListForm(JFrame parent) {
		super(parent);
		setBounds(0, 0, 1000, 700);
		splitPane.setResizeWeight(0.1);
		searchPanel.setLocation(267, 0);
		btnDuplicate.setLocation(66, 11);

		JScrollPane scrollPane_1 = new JScrollPane();
		splitPane.setLeftComponent(scrollPane_1);

		factory = new CatalogueFactory().Create(typeBO);

		// searchPanel.setLocation(10, 448);

		tree = new JTree();
		scrollPane_1.setViewportView(tree);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				// Returns the last path element of the selection.
				// This method is useful only when the selection model allows a
				// single selection.
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

				if (node == null)
					// Nothing is selected.
					return;

				System.out.println(node.getPath());
				Object nodeInfo = node.getUserObject();

				// setParentFilter(((nodeInfo instanceof CatalogueBO) ?
				// (CatalogueBO) nodeInfo : null));
				CatalogueBO cat = (nodeInfo instanceof CatalogueBO) ? (CatalogueBO) nodeInfo : null;
				setParentFilter(cat);
				System.out.println(nodeInfo);

				if (node.isLeaf()) {
					// BookInfo book = (BookInfo) nodeInfo;
					// displayURL(book.bookURL);
				} else {
					// displayURL(helpURL);
				}

			}
		});

		expander = new TreeExpansionUtil(tree);
		JScrollPane scrollPane = new JScrollPane();
		setGroupTree();

		btnAddGroup = new JButton("+ Группа");
		btnAddGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// CatalogueGroupForm groupForm = new ProductGroupForm();
				groupForm.updateTree = new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent evt) {
						setGroupTree();
						// setExpansionState();
					}
				};

				groupForm.NewBO();
				((CatalogueBO) groupForm.elementBO).is_group = true;
				((CatalogueBO) groupForm.elementBO).parent = parentGroup;
				// Еще раз устанавливаем контролы - для уст. группы
				groupForm.setData();
				// groupForm.setModal(true);
				groupForm.setVisible(true);
			}
		});
		btnAddGroup.setBounds(12, 661, 96, 40);

		getContentPane().add(btnAddGroup);

		btnGroupEdit = new JButton("Ред. группа");
		btnGroupEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (parentGroup != null) {
					// CatalogueGroupForm groupForm = new ProductGroupForm();
					groupForm.updateTree = new AbstractAction() {
						@Override
						public void actionPerformed(ActionEvent evt) {
							setGroupTree();
							// setExpansionState();
						}
					};
					groupForm.Load(parentGroup.id);
					((CatalogueBO) groupForm.elementBO).is_group = true;
					// groupForm.elementBO.parent = parentGroup;
					// Еще раз устанавливаем контролы - для уст. группы
					groupForm.setData();
					// groupForm.setModal(true);
					groupForm.setVisible(true);
				}

			}
		});
		btnGroupEdit.setBounds(133, 661, 128, 40);
		getContentPane().add(btnGroupEdit);
	}

	protected void setExpansionState() {
		String state = FileUtils.readFileAsString(FileUtils.GetSettingsDir() + treeSettingsKey);
		expander.setExpansionState(state.replace("\n", ""));
	}

	@Override
	protected void onClosing() {
		super.onClosing();
		// Сохраняем состояние дерева
		FileUtils.Text2File(FileUtils.GetSettingsDir() + treeSettingsKey, expander.getExpansionState(), false);
	}

	@Override
	protected void SaveUserEnter() {
		SearchContext.SaveUserEnter(searchContext, parentGroup, searchData);
	}

	@Override
	protected void onFormResized() {
		super.onFormResized();
		btnAddGroup.setLocation(btnAddGroup.getX(), buttonsScrollPanel.getY());
		btnGroupEdit.setLocation(btnGroupEdit.getX(), buttonsScrollPanel.getY());
	}

	protected void setGroupTree() {
		setGroupTree(typeBO);
		setExpansionState();
	}

	protected void setParentFilter(CatalogueBO parent) {
		if (selectGroupOnly) {
			selectedBO = parent;
		} else {
			// Первая страница - так как в других может быть больше страниц
			ClearCurrentPage();
			this.parentGroup = parent;
			Search();
			// table.revalidate();
			// table.repaint();
		}
	}

	protected <T> void setGroupTree(Class<T> typeTree) {
		tree.setCellRenderer(new DefaultTreeCellRenderer() {
			private Icon loadIcon = UIManager.getIcon("Tree.openIcon");
			private Icon saveIcon = UIManager.getIcon("Tree.closedIcon");

			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
					boolean isLeaf, int row, boolean focused) {
				Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, isLeaf, row, focused);
				if (selected)
					setIcon(loadIcon);
				else
					setIcon(saveIcon);
				return c;
			}
		});

		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		try {
			root.setUserObject(((BO) typeTree.newInstance()).getRusName());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		root.removeAllChildren();

		// CatalogueBO el = null;
		// try {
		// el = (CatalogueBO) typeTree.newInstance();
		// } catch (Exception e) {
		// log.ERROR("setGroupTree", e);
		// }
		// if (el != null)
		{
			List groups = new CatalogueFactory().Create(typeTree).GetGroups();

			for (Object o : groups) {
				CatalogueBO cat = (CatalogueBO) o;
				if (cat.parent == null) {
					DefaultMutableTreeNode node = new DefaultMutableTreeNode(GetTreeElName(cat));
					node.setUserObject(cat);
					AddChilds(node, groups, 1);
					root.add(node);
				}
			}
			model.reload(root);
		}
	}

	private <T> void AddChilds(DefaultMutableTreeNode parentNode, List<T> groups, int level) {
		if (level >= 10)
			return;
		for (Object o : groups) {
			CatalogueBO cat = (CatalogueBO) o;
			if (cat.parent != null && ((BO) cat.parent).id == ((BO) parentNode.getUserObject()).id) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(GetTreeElName(cat));
				node.setUserObject(cat);
				AddChilds(node, groups, level + 1);
				parentNode.add(node);
			}
		}
	}

	protected String GetTreeElName(CatalogueBO cat) {
		return cat.name;
	}

	@Override
	public void Search() {
		pagesCount = FactoryBO.GetPagesCount(new CatalogueFactory<>().Create(typeBO).GetCount((CatalogueBO) elementBO,
				(CatalogueBO) parentGroup, searchData), pageSize);

		if (currentPage == Integer.MAX_VALUE)
			currentPage = getButtonsCount() - 1;
		// if (currentPage < 0)
		// currentPage = 0;

		// if (selectGroupOnly)
		SetButtons();
		if (!selectGroupOnly) {
			items = new CatalogueFactory<>().Create(typeBO).GetPageByFiltered(currentPage, pageSize,
					((CatalogueBO) elementBO), (CatalogueBO) parentGroup, searchData);
		}

		tableModel.setList(items);
		setModel();
		table.revalidate();
		table.repaint();
		// setGroupTree();
	}

	@Override
	protected void AddNewElement() {
		super.AddNewElement();
		// Сразу устанавливаем выбранную группу
		((CatalogueBO) ((BoForm) itemForm).elementBO).parent = parentGroup;
		// Еще раз устанавливаем контролы - для уст. группы
		((BoForm) itemForm).setData();
	}

	public void ExpandTreeByBo(CatalogueBO cat) {
		// if (cat == null)
		// return;
		CatalogueBO parent = (CatalogueBO) new CatalogueFactory<>().Create(typeBO).getParent(cat);
		if (parent != null && parent.childs.size() == 0)
			parent = (CatalogueBO) new CatalogueFactory<>().Create(typeBO).getParent(parent);
		expander.ExpandByBoId((parent != null) ? parent.id : 0);
	}

	public void SelectTreeNodeByBo(CatalogueBO cat) {
		CatalogueBO parent = (CatalogueBO) new CatalogueFactory<>().Create(typeBO).getParent(cat);
		expander.SelectNodeByBoId((parent != null) ? parent.id : 0);
	}

	public void CollapseTree() {
		int row = tree.getRowCount() - 1;
		// while (row >= 0) { //collapses all nodes
		while (row > 0) { // collapses only child nodes of root node
			tree.collapseRow(row);
			row--;
		}
	}

	@Override
	public void selectItem(Object senderControl, BO boFromTP) {
		if (boFromTP instanceof CatalogueBO) {
			// TODO: test для видимости кнопки
			btnSelect.setVisible(selectGroupOnly);

			String text = ((CatalogueBO) boFromTP).name;
			searchText.setText(text);
			setSearch(text);
			((CatalogueListForm) this).CollapseTree();
			((CatalogueListForm) this).SelectTreeNodeByBo((CatalogueBO) boFromTP);
			Search();
			((CatalogueListForm) this).ExpandTreeByBo((CatalogueBO) boFromTP);
			if (autoSuggestor != null)
				autoSuggestor.TextFieldPressEnter();
		}
		super.selectItem(senderControl, boFromTP);
	}
}