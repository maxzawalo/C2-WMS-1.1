package maxzawalo.c2.base.ui.pc.catalogue;

import javax.swing.JFrame;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.SlaveCatalogueBO;
import maxzawalo.c2.base.data.factory.SlaveCatalogueFactory;
import maxzawalo.c2.base.ui.pc.form.BoForm;

public class SlaveCatalogueListForm<TypeBO, ItemForm> extends CatalogueListForm<TypeBO, ItemForm> {

	BO owner;

	public SlaveCatalogueListForm() {
		this(null);
	}

	public SlaveCatalogueListForm(JFrame parent) {
		super(parent);
	}
	// TODO: search by owner filter

	public boolean loadOwner = false;

	@Override
	public void Search() {

		pagesCount = 1;

		// if (selectGroupOnly)
		SetButtons();

		items = ((SlaveCatalogueFactory) factory).GetByOwner(owner, loadOwner, searchData);

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
		((SlaveCatalogueBO) ((BoForm) itemForm).elementBO).owner = owner;
		// // Еще раз устанавливаем контролы - для уст. группы
		// ((BoForm) itemForm).setData();
	}

	public void SetOwner(BO owner) {
		this.owner = owner;
		if (elementBO != null && owner != null) {
			boCaption = ((BO) elementBO).getRusName() + " - " + owner;
			setTitle(boCaption);
		}
	}
}