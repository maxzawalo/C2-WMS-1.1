package maxzawalo.c2.full.ui.pc.view;

import java.awt.event.MouseEvent;
import java.util.Date;

import javax.swing.JFrame;

import maxzawalo.c2.full.ui.pc.catalogue.LotOfProductListFormFull;

public class ViewPriceForm extends LotOfProductListFormFull {

	public ViewPriceForm() {
		this(null);
	}

	public ViewPriceForm(JFrame parent) {
		super(parent, null);
		btnAdd.setVisible(false);
		btnGroupEdit.setVisible(false);
		btnAddGroup.setVisible(false);
		setTitle("Цены");
		setDocDate(new Date());
	}

	@Override
	protected void onTableRowDblClick(MouseEvent e) {
		// Отключаем выбор партии
	}

	@Override
	protected void onFormResized() {
		super.onFormResized();

		int selectScrollPaneY = 0;
		int selectScrollPaneHeight = getHeight() - selectScrollPaneY - 100;
		selectScrollPane.setVisible(false);
		btnCopy2Doc.setVisible(false);
	}
}