package maxzawalo.c2.free.ui.pc.catalogue;

import javax.swing.JFrame;

import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.base.ui.pc.catalogue.CatalogueGroupForm;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.free.bo.Product;
import maxzawalo.c2.free.data.factory.catalogue.ProductFactory;

public class ProductGroupForm extends CatalogueGroupForm<Product> {
	public ProductGroupForm() {
		this(null);
	}

	public ProductGroupForm(JFrame parent) {
		super(parent);
		factory = new ProductFactory();
		setBounds(0, 0, 626, 383);

		topPanel.setBounds(0, 0, 683, 280);
		bottomPanel.setSize(683, 56);
		bottomPanel.setLocation(0, 281);

		code.setSize(277, 56);
		code.setLocation(0, 8);

		btnSave.setSize(145, 30);
		btnSave.setLocation(527, 12);

		parentCtrl.setLocation(0, 212);

		if (!User.current.isSimple()) {
			BizControlBase additionControl = CreateBizControl();
			additionControl.setFieldName(Product.fields.ADDITION);
			additionControl.setCaption("Наценка");
			additionControl.setBounds(0, 144, 130, 56);
			topPanel.add(additionControl);
		}
		if (User.current.isAdmin()) {
			BizControlBase web_cat = CreateBizControl();
			web_cat.setFieldName(Product.fields.WEB_CAT);
			web_cat.setCaption("Каталог на сайте");
			web_cat.setBounds(320, 212, 277, 56);
			topPanel.add(web_cat);
		}
	}

	@Override
	protected BizControlBase CreateBizControl() {
		return new BizControlBase();
	}
}