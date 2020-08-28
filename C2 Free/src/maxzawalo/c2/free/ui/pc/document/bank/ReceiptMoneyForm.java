package maxzawalo.c2.free.ui.pc.document.bank;

import javax.swing.JDialog;

import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.base.ui.pc.controls.ComboBoxBizControl;
import maxzawalo.c2.base.ui.pc.controls.DateBizControl;
import maxzawalo.c2.free.bo.BankAccount;
import maxzawalo.c2.free.bo.Settings;
import maxzawalo.c2.free.bo.bank.BankDocBO;
import maxzawalo.c2.free.bo.document.receiptmoney.ReceiptMoney;
import maxzawalo.c2.free.bo.document.receiptmoney.ReceiptMoneyTablePart;
import maxzawalo.c2.free.bo.enums.ReceiptMoneyType;
import maxzawalo.c2.free.data.factory.document.ReceiptMoneyFactory;
import maxzawalo.c2.free.ui.pc.model.document.bank.ReceiptMoneyTablePartModel;

public class ReceiptMoneyForm extends BankDocForm<ReceiptMoney, ReceiptMoneyTablePart.Payment> {
	protected BizControlBase in_form_number;
	protected BizControlBase in_form_date;

	public ReceiptMoneyForm() {

		this(null);
	}

	public ReceiptMoneyForm(JDialog parent) {
		super(parent);
		FreeTimeLimit();
		factory = new ReceiptMoneyFactory();
		setBounds(0, 0, 1000, 700);

		contractor.setSize(297, 56);
		contractor.setLocation(630, 80);

		for (String name : GetTPNames())
			tablePartModels.put(name, new ReceiptMoneyTablePartModel());

		in_form_number = new BizControlBase();
		in_form_number.setFieldName(BankDocBO.fields.IN_NUMBER);
		in_form_number.setCaption("Вх. номер");
		in_form_number.setBounds(739, 12, 128, 56);
		topPanel.add(in_form_number);

		in_form_date = new DateBizControl();
		in_form_date.setFieldName(BankDocBO.fields.IN_DATE);
		in_form_date.setCaption("Вх. дата");
		in_form_date.setBounds(630, 12, 116, 56);
		topPanel.add(in_form_date);

		ComboBoxBizControl receiptMoneyType = new ComboBoxBizControl();
		receiptMoneyType.fieldType = ReceiptMoneyType.class;
		receiptMoneyType.LoadList();
		receiptMoneyType.setFieldName(ReceiptMoney.fields.RECEIPT_MONEY_TYPE);
		receiptMoneyType.setCaption("Вид операции");
		receiptMoneyType.setBounds(12, 80, 248, 56);
		topPanel.add(receiptMoneyType);

		ComboBoxBizControl bank_account = new ComboBoxBizControl();
		bank_account.fieldType = BankAccount.class;
		// TODO:?
		bank_account.owner = Settings.myFirm;
		bank_account.LoadList();
		bank_account.setFieldName(BankDocBO.fields.BANK_ACCOUNT);
		bank_account.setCaption("Банковский счет");
		bank_account.setBounds(272, 80, 338, 56);
		topPanel.add(bank_account);
		
		BizControlBase bizControlBase = new BizControlBase();
		bizControlBase.setFieldName(ReceiptMoney.fields.PAYMENT_DETAILS);
		bizControlBase.setCaption("Назначение платежа");
		bizControlBase.setBounds(311, 137, 463, 70);
		topPanel.add(bizControlBase);

		// RefreshTP();
	}
}