package maxzawalo.c2.free.ui.pc.catalogue;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import maxzawalo.c2.base.Actions;
import maxzawalo.c2.base.ui.pc.catalogue.CatalogueForm;
import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.base.ui.pc.controls.ComboBoxBizControl;
import maxzawalo.c2.free.bo.Product;
import maxzawalo.c2.free.bo.Units;
import maxzawalo.c2.free.data.factory.catalogue.ProductFactory;

public class ProductFormFree extends CatalogueForm<Product> {
	public ProductFormFree() {

		this(null);
	}

	public ProductFormFree(JFrame parent) {
		super(parent);
		factory = new ProductFactory();

		setBounds(0, 0, 704, 471);

		topPanel.setBounds(0, 0, 683, 280);
		bottomPanel.setSize(683, 56);
		bottomPanel.setLocation(0, 281);

		btnCopyToFullName.setSize(25, 25);
		btnCopyToFullName.setLocation(506, 100);

		code.setSize(287, 56);
		code.setLocation(0, 12);

		btnSave.setSize(145, 30);
		btnSave.setLocation(527, 12);

		name.setBounds(0, 76, 506, 56);

		BizControlBase units = new ComboBoxBizControl();
		units.fieldType = Units.class;
		units.LoadList();
		units.setFieldName(Product.fields.UNITS);
		units.setCaption("Ед. изм.");
		units.setBounds(537, 76, 136, 56);
		topPanel.add(units);

		parentControl.setBounds(0, 212, 287, 56);
		full_name.setBounds(0, 144, 673, 56);

		root.setVisible(true);
		root.setBounds(0, 294, 506, 60);
//		root.setFieldType(Product.class);

		JButton showImage = new JButton("");
		showImage.setToolTipText("Показать изображение");
		showImage.setIcon(UI.getImageIcon(30, 25));
		showImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ShowImage();
			}
		});
		showImage.setBounds(616, 233, 46, 35);
		topPanel.add(showImage);

		JButton button = new JButton("Движение");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ShowTransaction();
			}
		});
		button.setBounds(476, 239, 115, 29);
		topPanel.add(button);
	}

	@Override
	public void Load(int id) {
		super.Load(id);
	}

	protected void ShowImage() {
		// TODO: in full
		Product p = ((Product) elementBO);
		// ImageGallery.instance.LoadData(p.name, p.id);
	}

	protected void ShowTransaction() {
		if (Actions.ShowTransactionAction != null)
			Actions.ShowTransactionAction.Do(elementBO);
	}
}