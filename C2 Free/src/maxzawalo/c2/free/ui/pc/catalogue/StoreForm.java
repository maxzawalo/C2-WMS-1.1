package maxzawalo.c2.free.ui.pc.catalogue;

import javax.swing.JFrame;

import maxzawalo.c2.base.ui.pc.catalogue.CatalogueForm;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.free.bo.Store;

public class StoreForm extends CatalogueForm<Store> {
	protected BizControlBase address;

	public StoreForm() {
		this(null);
	}

	public StoreForm(JFrame parent) {
		super(parent);
		setBounds(0, 0, 511, 331);

		parentControl.setVisible(false);
		full_name.setVisible(false);

		code.setBounds(12, 12, 154, 56);
		bottomPanel.setSize(299, 64);
		bottomPanel.setLocation(0, 150);
		topPanel.setBounds(0, 0, 299, 149);

		name.setBounds(12, 69, 471, 56);

		address = new BizControlBase();
		address.setCaption("Адрес");
		address.setFieldName(Store.fields.ADDRESS);
		address.setBounds(12, 137, 471, 56);
		topPanel.add(address);
	}

	@Override
	public void Load(int id) {
		elementBO = factory.GetById(id);// TODO: super
		setData();
	}

	@Override
	protected BizControlBase CreateBizControl() {
		return new BizControlBase();
	}
}