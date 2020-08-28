package maxzawalo.c2.free.ui.pc.control;

import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;

public class LotProductMergedTable extends StoreTPTable {
	public LotProductMergedTable() {
		this(null);
	}

	private int disabled_col = 0, cur_col = 0;

	public LotProductMergedTable(TableModel model) {
		super(model);
		setUI(new LotProductMergedTableUI());
		getTableHeader().setReorderingAllowed(false);
		// setCellSelectionEnabled(true);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	@Override
	public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
		super.changeSelection(rowIndex, columnIndex, toggle, extend);
		repaint();
	}
}