package maxzawalo.c2.free.ui.pc.model.catalogue;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JLabel;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.base.ui.pc.model.BOTableModel;
import maxzawalo.c2.base.ui.pc.model.ColumnSettings;
import maxzawalo.c2.base.ui.pc.renderer.CheckBoxRenderer;
import maxzawalo.c2.base.ui.pc.renderer.CountCellRenderer;
import maxzawalo.c2.base.ui.pc.renderer.CustomCellRenderer;
import maxzawalo.c2.base.ui.pc.renderer.FuzzyCellRenderer;
import maxzawalo.c2.free.bo.LotOfProduct;
import maxzawalo.c2.free.bo.comparator.LotOfProductComparator;
import maxzawalo.c2.free.data.factory.catalogue.LotOfProductFactoryFree;

public class LotOfProductModel extends BOTableModel<LotOfProduct> {

	public List<Integer> groups = new ArrayList<>();

	public LotOfProductModel() {
		ColumnSettings column = AddVisibleColumns();
		column.renderer = new FuzzyCellRenderer();
		column.horizontalAlignment = JLabel.LEFT;
		column.caption = "Группа";
		column.name = LotOfProduct.fields.GROUP;

		column = AddVisibleColumns();
		column.renderer = new FuzzyCellRenderer();
		column.horizontalAlignment = JLabel.LEFT;
		column.name = LotOfProduct.fields.PRODUCT;

		column = AddVisibleColumns();
		// column.addCol = true;
		column.renderer = new CheckBoxRenderer();
		column.name = LotOfProduct.fields.RESERVE;

		if (!User.current.isSimple()) {
			column = AddVisibleColumns();
			column.name = LotOfProduct.fields.COST_PRICE;
		}

		column = AddVisibleColumns();
		// column.renderer.fontStyle = Font.BOLD;
		column.name = LotOfProduct.fields.PRICE;

		column = AddVisibleColumns();
		column.caption = "Цена с НДС";
		((CustomCellRenderer) column.renderer).fontStyle = Font.BOLD;
		column.name = LotOfProduct.fields.PRICE_WITH_VAT;

		// column = AddVisibleColumns();
		// column.caption = "Цена (расчетная)";
		// column.name = LotOfProduct.fields.CALC_PRICE;

		if (!User.current.isSimple()) {
			column = AddVisibleColumns();
			column.caption = "Наценка (факт)";
			column.name = LotOfProduct.fields.ADD;
		}

		column = AddVisibleColumns();
		column.renderer = new CountCellRenderer();
		((CustomCellRenderer) column.renderer).fontStyle = Font.BOLD;
		column.name = LotOfProduct.fields.COUNT;
		column.format = "0.000";

		column = AddVisibleColumns();
		column.horizontalAlignment = JLabel.CENTER;
		column.caption = "Ед. изм.";
		column.name = LotOfProduct.fields.UNITS;

		// column = AddVisibleColumns();
		// column.horizontalAlignment = JLabel.CENTER;
		// column.addCol = true;
		// column.name = "price_bo";

		column = AddVisibleColumns();
		column.caption = "Партия";
		column.name = LotOfProduct.fields.LOT;

		setColumnCaptions();
	}

	// Используем при поиске, выборе новой страницы
	public void minusList(List<LotOfProduct> minusList) {
		for (LotOfProduct minus : minusList)
			minus(minus, false);

		// Удаляем из списка выбранный остаток
		removeZeroBalance();
		CreateProductGroups();
	}

	public void minus(LotOfProduct minus) {
		minus(minus, true);
	}

	public void minus(LotOfProduct minus, boolean doRemoveZeroBalance) {
		LotOfProductFactoryFree.addGrouped(list, minus, -1);

		// Удаляем из списка выбранный остаток
		if (doRemoveZeroBalance) {
			removeZeroBalance();
			CreateProductGroups();
		}
	}

	public void returnToSelectList(LotOfProduct returnLot, BO parentGroup) {

		// TODO: куда возвращать если фильтр по группам с ветвями?

		// int parent_id = new
		// Product().GetById(returnLot.product.id).parent.id;
		// if (parentGroup == null || parentGroup.id != parent_id)// TODO:!!!
		// return;// Переключили группу - возвращаем не сюда

		if (LotOfProductFactoryFree.addGrouped(list, returnLot, 1))
			return;

		list.add(returnLot);
		Collections.sort(list, new LotOfProductComparator());
		CreateProductGroups();
	}

	public boolean showZeroBalance = false;

	void removeZeroBalance() {
		if (showZeroBalance)
			return;

		List<LotOfProduct> newList = new ArrayList<>();
		for (LotOfProduct lot : list)
			if (lot.count != 0)
				newList.add(lot);

		list.clear();
		list.addAll(newList);
	}

	// @Override
	// public void setList(List<LotOfProduct> list) {
	// super.setList(list);
	// removeZeroBalance();
	//// CreateProductGroups();
	// }

	protected void CreateProductGroups() {
		groups.clear();
		int count = 0;
		int startPos = 0;
		int productId = -1;
		for (int pos = 0; pos < list.size(); pos++) {
			groups.add(0);
			LotOfProduct lot = list.get(pos);
			if (productId != lot.product.id) {
				productId = lot.product.id;
				groups.set(startPos, count);
				startPos = pos;
				count = 1;
			} else {
				count++;
			}
		}
		// Устанавливаем последнюю группу
		if (groups.size() != 0)
			groups.set(startPos, count);

		// System.out.println(Arrays.toString(groups.toArray()));
		// int size = groups.size();

	}
}