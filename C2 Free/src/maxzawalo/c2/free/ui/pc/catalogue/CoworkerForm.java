package maxzawalo.c2.free.ui.pc.catalogue;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;

import maxzawalo.c2.base.bo.Coworker;
import maxzawalo.c2.base.data.factory.CoworkerFactory;
import maxzawalo.c2.base.ui.pc.catalogue.CatalogueForm;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;


public class CoworkerForm extends CatalogueForm<Coworker> {
	public CoworkerForm() {
		this(null);
	}

	public CoworkerForm(JFrame parent) {
		super(parent);
		factory = new CoworkerFactory();
		
		setBounds(0, 0, 489, 411);

		bottomPanel.setSize(299, 64);
		bottomPanel.setLocation(0, 150);
		topPanel.setBounds(0, 0, 299, 149);

		name.setBounds(12, 69, 441, 56);
		name.setCaption("ФИО");

		code.setBounds(12, 12, 132, 56);
		full_name.setLocation(506, 104);
		full_name.setVisible(false);
		parentControl.setLocation(506, 22);
		parentControl.setVisible(false);

		BizControlBase position = CreateBizControl();
		position.fieldType = String.class;
		position.setFieldName(Coworker.fields.POSITION);
		position.setCaption("Должность");
		position.setSize(297, 60);
		position.setLocation(156, 8);
		topPanel.add(position);

		JPanel passportPanel = new JPanel();
		Border passportBorder = BorderFactory.createTitledBorder("Паспорт");
		passportPanel.setBorder(passportBorder);
		passportPanel.setBounds(12, 137, 441, 160);
		topPanel.add(passportPanel);
		passportPanel.setLayout(null);

		BizControlBase passport_batch = CreateBizControl();
		passport_batch.fieldType = String.class;
		passport_batch.setFieldName(Coworker.fields.PASSPORT_BATCH);
		passport_batch.setCaption("Серия");
		passport_batch.setBounds(12, 31, 65, 60);
		passportPanel.add(passport_batch);

		BizControlBase passport_number = CreateBizControl();
		passport_number.fieldType = String.class;
		passport_number.setFieldName(Coworker.fields.PASSPORT_NUMBER);
		passport_number.setCaption("Номер");
		passport_number.setBounds(78, 31, 118, 60);
		passportPanel.add(passport_number);

		BizControlBase passport_issued_by = CreateBizControl();
		passport_issued_by.fieldType = String.class;
		passport_issued_by.setFieldName(Coworker.fields.PASSPORT_ISSUED_BY);
		passport_issued_by.setCaption("Кем выдан");
		passport_issued_by.setBounds(12, 88, 417, 60);
		passportPanel.add(passport_issued_by);

		BizControlBase passport_issued_date = CreateBizControl();
		passport_issued_date.setFieldName(Coworker.fields.PASSPORT_ISSUED_DATE);
		passport_issued_date.setCaption("Дата выдачи");
		passport_issued_date.setBounds(311, 31, 118, 60);
		passportPanel.add(passport_issued_date);
	}

	@Override
	public void Load(int id) {
		elementBO = factory.GetById(id, 0, false);// TODO: super
		setData();
	}
	
	@Override
	protected BizControlBase CreateBizControl() {
		return new  BizControlBase();
	}
}