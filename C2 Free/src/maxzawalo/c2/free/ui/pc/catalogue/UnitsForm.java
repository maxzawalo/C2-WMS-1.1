package maxzawalo.c2.free.ui.pc.catalogue;

import javax.swing.JFrame;

import maxzawalo.c2.base.ui.pc.catalogue.CatalogueForm;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.free.bo.Units;
import maxzawalo.c2.free.data.factory.catalogue.UnitsFactory;


public class UnitsForm extends CatalogueForm<Units> {

	public UnitsForm() {
		this(null);
	}

	public UnitsForm(JFrame parent) {
		super(parent);
		factory = new UnitsFactory();

		bottomPanel.setSize(299, 64);
		bottomPanel.setLocation(0, 150);
		topPanel.setBounds(0, 0, 299, 149);

		name.setBounds(12, 69, 287, 56);

		setBounds(0, 0, 315, 251);
	}

	@Override
	public void Load(int id) {
		elementBO = factory.GetById(id);// TODO: super
		setData();
	}
	
	@Override
	protected BizControlBase CreateBizControl() {
		return new  BizControlBase();
	}
}