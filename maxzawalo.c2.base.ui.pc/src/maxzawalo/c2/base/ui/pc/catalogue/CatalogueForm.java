package maxzawalo.c2.base.ui.pc.catalogue;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import maxzawalo.c2.base.bo.CatalogueBO;
import maxzawalo.c2.base.data.factory.CatalogueFactory;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.base.ui.pc.form.BoForm;

public class CatalogueForm<T> extends BoForm<T> {

	protected BizControlBase name;
	protected BizControlBase parentControl;
	protected BizControlBase full_name;
	protected BizControlBase root;

	protected JButton btnCopyToFullName;

	public CatalogueForm() {
		this(null);
	}

	public CatalogueForm(JFrame parent) {
		super(parent);

		factory = new CatalogueFactory<>().Create(typeBO);

		name = CreateBizControl();
		name.setCaption("Наименование");
		name.setFieldName(CatalogueBO.fields.NAME);
		topPanel.add(name);

		parentControl = CreateBizControl();
		parentControl.selectGroupOnly = true;
		parentControl.setFieldType(typeBO);
		parentControl.setFieldName(CatalogueBO.fields.PARENT);
		parentControl.setCaption("Группа");
		topPanel.add(parentControl);

		full_name = CreateBizControl();
		full_name.setCaption("Полное наименование");
		full_name.setFieldName(CatalogueBO.fields.FULL_NAME);
		topPanel.add(full_name);

		btnCopyToFullName = new JButton("v");
		btnCopyToFullName.setToolTipText("Копировать в полное наименование");
		btnCopyToFullName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String value = full_name.getText().trim();
				if (value.equals("")) {
					full_name.onBOSelected(name.getText());
					// full_name.revalidate();
					// full_name.setText(name.getText());
				}
			}
		});
		btnCopyToFullName.setBounds(-100, -100, 25, 25);
		btnCopyToFullName.setMargin(new Insets(0, 0, 0, 0));
		topPanel.add(btnCopyToFullName);

		root = CreateBizControl();
		root.setVisible(false);
		root.setFieldType(typeBO);
		root.setFieldName(CatalogueBO.fields.ROOT);
		root.setCaption("Корневой элемент");
		topPanel.add(root);
	}

	@Override
	protected BizControlBase CreateBizControl() {
		return super.CreateBizControl();
	}
}