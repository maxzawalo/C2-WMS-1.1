package maxzawalo.c2.free.ui.pc.catalogue;

import javax.swing.JFrame;

import maxzawalo.c2.base.ui.pc.catalogue.CatalogueForm;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.base.ui.pc.controls.CheckBoxBizControl;
import maxzawalo.c2.free.bo.ContactInfo;
import maxzawalo.c2.free.bo.Contract;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.data.factory.catalogue.ContractorFactory;


public class ContractorForm extends CatalogueForm<Contractor> {
	BizControlBase main_contract;

	public ContractorForm(JFrame parent) {
		super(parent);
		factory = new ContractorFactory();

		bottomPanel.setBounds(12, 472, 926, 69);
		topPanel.setBounds(0, 0, 938, 460);

		code.setBounds(12, 12, 287, 56);
		btnSave.setLocation(767, 12);

		name.setBounds(12, 68, 595, 56);

		BizControlBase unp = CreateBizControl();
		unp.setFieldName(Contractor.fields.UNP);
		unp.setCaption("УНП");
		unp.setBounds(619, 68, 287, 56);
		topPanel.add(unp);

		parentControl.setBounds(12, 179, 449, 56);

		main_contract = CreateBizControl();
		main_contract.fieldType = Contract.class;
		main_contract.setBounds(496, 179, 409, 56);
		main_contract.setFieldName(Contractor.fields.MAIN_CONTRACT);
		main_contract.setCaption("Договор");
		// TODO: для всех Справочников владельцев
		main_contract.setEnabled(false);
		topPanel.add(main_contract);

		full_name.setBounds(12, 122, 894, 56);

		BizControlBase legal_address = CreateBizControl();
		legal_address.fieldType = String.class;
		legal_address.setFieldName(Contractor.fields.LEGAL_ADDRESS);
		legal_address.setCaption("Юридический адрес");
		legal_address.setBounds(12, 255, 894, 56);
		topPanel.add(legal_address);

		BizControlBase phone = CreateBizControl();
		phone.selectGroupOnly = true;
		phone.setFieldName(Contractor.fields.PHONE);
		phone.setCaption("Телефон");
		phone.setBounds(12, 323, 449, 56);
		topPanel.add(phone);

		BizControlBase fax = CreateBizControl();
		fax.selectGroupOnly = true;
		fax.setFieldName(Contractor.fields.FAX);
		fax.setCaption("Факс");
		fax.setBounds(496, 323, 413, 56);
		topPanel.add(fax);

		BizControlBase is_resident = new CheckBoxBizControl();
		is_resident.setFieldName(Contractor.fields.IS_RESIDENT);
		is_resident.setCaption("Резидент РБ");
		is_resident.setBounds(12, 394, 148, 56);
		topPanel.add(is_resident);

		CheckBoxBizControl is_individual = new CheckBoxBizControl();
		is_individual.setFieldName(Contractor.fields.IS_INDIVIDUAL);
		is_individual.setCaption("Физ. лицо");
		is_individual.setBounds(170, 391, 148, 56);
		topPanel.add(is_individual);

		BizControlBase main_bank_acccount = CreateBizControl();
		main_bank_acccount.setFieldName("main_bank_acccount");
		main_bank_acccount.setCaption("Основной банковский счет");
		main_bank_acccount.setBounds(12, 478, 449, 56);
		topPanel.add(main_bank_acccount);

		setBounds(0, 0, 966, 688);
	}

	public ContractorForm() {
		this(null);
	}

	@Override
	public void Load(int id) {
		super.Load(id);
		elementBO = ((ContractorFactory) factory).LoadContactInfo(elementBO);
		if (elementBO.contactInfo.size() == 0)
			System.out.println("Нет контактных данных");
		for (ContactInfo info : elementBO.contactInfo)
			System.out.println("" + info);
		// TODO: super.Load(id) setData() тут дублируется
		setData();
		// System.out.println("" +elementBO.main_contract);
	}

	@Override
	public void setData() {
		super.setData();
		// устанавливаем owner
		main_contract.owner = elementBO;
		if (elementBO.id != 0) {
			main_contract.setEnabled(true);
		}
	}

	@Override
	public boolean Save() {
		if (!super.Save())
			return false;
		// Получаем id и заново устанавливаем owner
		// TODO: setData() ?
		main_contract.owner = elementBO;
		main_contract.setEnabled(true);
		return true;
	}

	@Override
	protected BizControlBase CreateBizControl() {
		return new  BizControlBase();
	}
}