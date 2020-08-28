package maxzawalo.c2.base.ui.pc.controls;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import maxzawalo.c2.base.bo.CatalogueBO;

/**
 * 
 * Utility class that can be used to retrieve and/or restore the expansion state
 * of a JTree.
 * 
 * @author G. Cope
 *
 * 
 * 
 */
public class TreeExpansionUtil {

	private final JTree tree;

	/**
	 * 
	 * Constructs a new utility object based upon the parameter JTree
	 * 
	 * @param tree
	 * 
	 */
	public TreeExpansionUtil(JTree tree) {
		this.tree = tree;
	}

	/**
	 * 
	 * Retrieves the expansion state as a String, defined by a comma delimited
	 * list of
	 * 
	 * each row node that is expanded.
	 * 
	 * @return
	 * 
	 */
	public String getExpansionState() {

		StringBuilder sb = new StringBuilder();

		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) model.getRoot();
		Enumeration e = parent.depthFirstEnumeration();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
			if (tree.isExpanded(new TreePath(node.getPath()))) {
				Object nodeInfo = node.getUserObject();
				int id = (nodeInfo instanceof CatalogueBO) ? ((CatalogueBO) nodeInfo).id : 0;
				System.out.println("isExpanded=" + id);
				sb.append(id).append(",");
			}
		}

		// StringBuilder sb = new StringBuilder();
		// for (int i = 0; i < tree.getRowCount(); i++) {
		// if (tree.isExpanded(i)) {
		// sb.append(i).append(",");
		// }
		// }
		if (sb.length() > 0)
			sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	Map<Integer, TreePath> map = new HashMap<>();

	/**
	 * 
	 * Sets the expansion state based upon a comma delimited list of row indexes
	 * that
	 * 
	 * are expanded.
	 * 
	 * @param s
	 * 
	 */
	public void setExpansionState(String s) {
		if (s.equals(""))
			return;

		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) model.getRoot();
		Enumeration e = parent.depthFirstEnumeration();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
			Object nodeInfo = node.getUserObject();
			int id = (nodeInfo instanceof CatalogueBO) ? ((CatalogueBO) nodeInfo).id : 0;
			map.put(id, new TreePath(node.getPath()));
		}

		String[] indexes = s.split(",");
		for (String st : indexes) {
			int id = Integer.parseInt(st);
			ExpandByBoId(id);
		}
	}

	public void ExpandByBoId(int id) {
		System.out.println("ExpandByBoId |" + map.get(id));
		tree.expandPath(map.get(id));
	}

	public void SelectNodeByBoId(int id) {
		System.out.println("SelectNodeByBoId |" + map.get(id));
		tree.setSelectionPath(map.get(id));
		tree.scrollPathToVisible(map.get(id));
	}
}