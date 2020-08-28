package maxzawalo.c2.base.ui.pc.catalogue;

import javax.swing.Action;
import javax.swing.JFrame;

import maxzawalo.c2.base.bo.CatalogueBO;
import maxzawalo.c2.base.data.factory.CatalogueFactory;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.base.ui.pc.form.BoForm;

public class CatalogueGroupForm<T> extends BoForm<T> {

	protected BizControlBase name;
	protected BizControlBase parentCtrl;

	public CatalogueGroupForm() {
		this(null);
	}

	public CatalogueGroupForm(JFrame parent) {
		super(parent);

		topPanel.setBounds(0, 0, 683, 280);
		bottomPanel.setSize(683, 56);
		bottomPanel.setLocation(0, 281);

		code.setSize(287, 56);
		code.setLocation(0, 12);

		btnSave.setSize(145, 30);
		btnSave.setLocation(527, 12);

		name = new BizControlBase();
		name.setCaption("Наименование");
		name.setFieldName(CatalogueBO.fields.NAME);
		name.setBounds(0, 76, 597, 56);
		topPanel.add(name);

		parentCtrl = new BizControlBase();
		parentCtrl.selectGroupOnly = true;
		parentCtrl.setFieldName(CatalogueBO.fields.PARENT);
		parentCtrl.setCaption("Группа");
		parentCtrl.fieldType = typeBO;
		parentCtrl.setBounds(0, 144, 287, 56);
		topPanel.add(parentCtrl);
		setBounds(0, 0, 631, 307);
	}

	@Override
	public boolean Save() {
		if (!super.Save())
			return false;

		((CatalogueFactory) factory).ClearGroups();

		if (updateTree != null)
			updateTree.actionPerformed(null);
		return true;
	}

	public Action updateTree;
}