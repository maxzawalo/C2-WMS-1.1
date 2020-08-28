package maxzawalo.c2.full.ui.pc.daybook;

import javax.swing.JButton;
import javax.swing.JFrame;

import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.base.ui.pc.form.BoForm;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.bo.Product;
import maxzawalo.c2.full.bo.StoreDaybook;

public class StoreDaybookForm extends BoForm<StoreDaybook> {

	BizControlBase contractor;

	public StoreDaybookForm() {
		this(null);
	}

	public StoreDaybookForm(JFrame parent) {
		super(parent);

		setBounds(0, 0, 428, 494);

		topPanel.setBounds(0, 0, 683, 280);
		bottomPanel.setSize(683, 56);
		bottomPanel.setLocation(0, 281);

		code.setVisible(false);
		code.setSize(119, 56);
		code.setLocation(683, 49);

		btnSave.setSize(145, 30);
		btnSave.setLocation(527, 12);

		contractor = new  BizControlBase();
		contractor.fieldType = Contractor.class;
		contractor.setFieldName("contractor");
		contractor.setCaption("Контрагент");
		contractor.setBounds(12, 12, 379, 56);
		contractor.setEnabled(false);
		topPanel.add(contractor);

		JButton showImage = new JButton("");
		showImage.setToolTipText("Показать изображение");
		showImage.setIcon(UI.getImageIcon(30, 25));
		// showImage.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent arg0) {
		// Product p = ((Product) elementBO);
		// ImageGallery.instance.LoadData(p.name, p.id);
		// }
		// });
		showImage.setBounds(355, 357, 46, 35);
		topPanel.add(showImage);

		BizControlBase product = new  BizControlBase();
		product.fieldType = Product.class;
		product.setFieldName("product");
		product.setCaption("Номенклатура");
		product.setBounds(12, 80, 379, 56);
		topPanel.add(product);

		BizControlBase price = new  BizControlBase();
		price.setFieldName("price");
		price.setCaption("Цена");
		price.setBounds(234, 148, 157, 56);
		topPanel.add(price);

		BizControlBase count = new  BizControlBase();
		count.setFieldName("count");
		count.setCaption("Количество");
		count.setBounds(12, 148, 180, 56);
		topPanel.add(count);

		BizControlBase who_recieve = new  BizControlBase();
		who_recieve.setFieldName("who_recieve");
		who_recieve.setCaption("Кто получил");
		who_recieve.setBounds(12, 216, 379, 56);
		topPanel.add(who_recieve);

		BizControlBase comment = new  BizControlBase();
		comment.setFieldName(DocumentBO.fields.COMMENT);
		comment.setCaption("Комментарий");
		comment.setBounds(12, 290, 379, 56);
		topPanel.add(comment);
	}

	@Override
	public void Load(int id) {
		super.Load(id);
	}

	@Override
	public boolean Save() {

		if (super.Save()) {
			setVisible(false);
			dispose();
			return true;
		}
		return false;
	}

	public void SetContractor(Contractor contractor) {
		elementBO.contractor = contractor;
		setData();
	}
}