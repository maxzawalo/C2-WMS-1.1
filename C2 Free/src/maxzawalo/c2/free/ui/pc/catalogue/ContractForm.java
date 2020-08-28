package maxzawalo.c2.free.ui.pc.catalogue;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.CatalogueBO;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.bo.SlaveCatalogueBO;
import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.base.interfaces.ActionC2;
import maxzawalo.c2.base.ui.pc.catalogue.CatalogueForm;
import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.base.ui.pc.controls.CheckBoxBizControl;
import maxzawalo.c2.base.ui.pc.controls.ComboBoxBizControl;
import maxzawalo.c2.base.ui.pc.controls.DateBizControl;
import maxzawalo.c2.free.bo.Contract;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.bo.Currency;
import maxzawalo.c2.free.bo.document.bill.Bill;
import maxzawalo.c2.free.bo.enums.ContractType;
import maxzawalo.c2.free.data.factory.catalogue.ContractFactory;
import maxzawalo.c2.free.data.factory.catalogue.ContractorFactory;
import maxzawalo.c2.free.ui.pc.form.FreeVersionForm;

public class ContractForm extends CatalogueForm<Contract> {

	protected ComboBoxBizControl contract_type;

	protected BizControlBase bill;
	protected BizControlBase docDate;
	protected BizControlBase number;

	public static ActionC2 printAction;

	public ContractForm(JFrame parent) {
		super(parent);
		setBounds(0, 0, 533, 339);

		factory = new ContractFactory();
		bottomPanel.setBounds(12, 374, 926, 69);
		topPanel.setBounds(0, 0, 938, 362);

		btnSave.setLocation(388, 12);

		JButton button = new JButton("Печать");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Print();
			}
		});
		button.setBounds(12, 12, 115, 42);
		bottomPanel.add(button);
		code.setBounds(12, 12, 184, 56);

		full_name.setLocation(664, 100);
		full_name.setVisible(false);
		parentControl.setLocation(545, 100);
		parentControl.setVisible(false);
		name.setBounds(12, 235, 493, 56);

		bill = CreateBizControl();
		bill.setFieldName(Contract.fields.BILL);
		bill.setCaption("Счет");
		bill.setFieldType(Bill.class);
		bill.setBounds(12, 80, 380, 56);
		topPanel.add(bill);
		bill.afterBOSelected.add(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				docDate.onBOSelected(((Bill) bill.getBO()).DocDate);
			}
		});

		docDate = new DateBizControl();
		docDate.setFieldName(DocumentBO.fields.DOC_DATE);
		docDate.setCaption("Дата");
		docDate.setBounds(208, 80, 184, 56);
		topPanel.add(docDate);

		number = CreateBizControl();
		if (!User.current.isAdmin())
			number.setReadOnly();
		number.setFieldName(Contract.fields.NUMBER);
		number.setCaption("Номер");
		number.setBounds(12, 80, 184, 56);
		topPanel.add(number);

		CheckBoxBizControl is_bill = new CheckBoxBizControl();
		is_bill.setFieldName(Contract.fields.IS_BILL);
		is_bill.setCaption("Это счет");
		is_bill.setBounds(220, 12, 184, 56);
		topPanel.add(is_bill);
		is_bill.afterBOSelected.add(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ChangeVisibility();
				SetBill();
			}
		});

		BizControlBase doc_currency = new ComboBoxBizControl();
		doc_currency.fieldType = Currency.class;
		doc_currency.LoadList();
		doc_currency.setFieldName(Contract.fields.DOC_CURRENCY);
		// doc_currency.setCaption("Валюта");
		doc_currency.setBounds(404, 80, 101, 56);
		topPanel.add(doc_currency);

		contract_type = new ComboBoxBizControl();
		contract_type.fieldType = ContractType.class;
		contract_type.LoadList();
		contract_type.setFieldName(Contract.fields.CONTRACT_TYPE);
		contract_type.setCaption("Вид договора");
		contract_type.setBounds(12, 148, 286, 56);
		topPanel.add(contract_type);

		CheckBoxBizControl return_with_sign = new CheckBoxBizControl();
		return_with_sign.setFieldName(Contract.fields.RETURN_WITH_SIGN);
		return_with_sign.setCaption("<html>&nbsp;Договор вернули<br>&nbsp;с подписи</html>");
		return_with_sign.setBounds(321, 148, 184, 75);
		topPanel.add(return_with_sign);
	}

	protected void Print() {
		if (printAction == null) {
			FreeVersionForm.Soon();
		} else {
			ContractType ctFilter = new ContractType().getEnumByName("С покупателем");
			if (!ctFilter.equals(elementBO.contract_type)) {
				JOptionPane.showMessageDialog(this, "Печатаются только договоры с Покупателем", "Печать",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			UI.Start(this);
			Object[] params = { elementBO };
			printAction.Do(params);
			UI.Stop(this);
		}
	}

	public ContractForm() {
		this(null);
	}

	@Override
	public void Load(int id) {
		super.Load(id);
		elementBO.owner = new ContractorFactory().GetById(elementBO.owner.id);
		ShowOwner();
		SetBill();
	}
	// @Override
	// public void NewBO() {
	// super.NewBO();
	// ShowOwner();
	// }
	//

	protected void ShowOwner() {
		setTitle("Договор - " + ((CatalogueBO) elementBO.owner).name);
	}

	@Override
	public void setData() {
		super.setData();
		ChangeVisibility();
	}

	protected void ChangeVisibility() {
		bill.setVisible(elementBO.is_bill);
		docDate.setVisible(!elementBO.is_bill);
		number.setVisible(!elementBO.is_bill);
	}

	@Override
	public boolean Save() {
		if (!super.Save())
			return false;
		// Обновляем визуально при первом сохранении
		number.setBo((BO) elementBO);
		number.revalidate();

		name.setBo((BO) elementBO);
		name.revalidate();
		return true;
	}

	@Override
	protected BizControlBase CreateBizControl() {
		return new  BizControlBase();
	}

	protected void SetBill() {
		if (elementBO.is_bill) {
			Bill b = (Bill) bill.getBO();
			if (b == null)
				b = new Bill();
			// b.meta = "Не выбран";
			// Почему то Контрагент не ставится если счет уже установлен из БД
			b.contractor = (Contractor) ((SlaveCatalogueBO) elementBO).owner;
			// bill.setBo(b);
			elementBO.bill = b;
			// !!! setBo - elementBO формы
			bill.setBo(elementBO);
			// bill.onBOSelected(b);
		}
	}
}