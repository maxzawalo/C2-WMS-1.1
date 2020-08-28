package maxzawalo.c2.full.ui.pc.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;

import maxzawalo.c2.base.Actions;
import maxzawalo.c2.base.os.Run;
import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.base.ui.pc.controls.DateBizControl;
import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.bo.Product;
import maxzawalo.c2.free.bo.document.bill.Bill;
import maxzawalo.c2.free.bo.document.bill.BillTablePart;
import maxzawalo.c2.free.data.factory.document.BillFactory;
import maxzawalo.c2.full.bo.view.ContractorTransactionView;
import maxzawalo.c2.full.data.factory.view.TransactionViewFactory;
import maxzawalo.c2.full.ui.pc.Main;

public class ContractorTransactionViewForm extends JFrame {
	BizControlBase fromDate;
	BizControlBase toDate;
	public BizControlBase contractorCtrl;

	public ContractorTransactionViewForm() {
		setBounds(0, 0, 341, 203);
		getContentPane().setLayout(null);
		setTitle("Отгрузка по Контрагенту");

		fromDate = new DateBizControl();
		fromDate.setCaption("C");
		fromDate.setBounds(0, 0, 164, 56);
		fromDate.onBOSelected(new Date());
		getContentPane().add(fromDate);

		toDate = new DateBizControl();
		toDate.setCaption("по");
		toDate.setBounds(164, 0, 164, 56);
		toDate.onBOSelected(new Date());
		getContentPane().add(toDate);

		JButton button = new JButton("Сформировать");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Print();
			}
		});
		button.setBounds(177, 122, 137, 29);
		getContentPane().add(button);

		contractorCtrl = new BizControlBase();
		contractorCtrl.setFieldType(Contractor.class);
		contractorCtrl.setBo(new Product());
		contractorCtrl.setCaption("Контрагент");
		contractorCtrl.setBounds(0, 55, 328, 56);
		getContentPane().add(contractorCtrl);

		JButton button_1 = new JButton("Счет");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				UI.Start(ContractorTransactionViewForm.this);
				Contractor contractor = (Contractor) contractorCtrl.getBO();
				if (contractor == null)
					Console.I().INFO(getClass(), "button_1", "Выберите Контрагента");
				else {
					List<ContractorTransactionView> list = new TransactionViewFactory().getByContractor(fromDate.getDate(), toDate.getDate(), contractor);

					Bill bill = new Bill();
					bill.contractor = contractor;
					bill.doc_contract = contractor.main_contract;
					bill.comment = Bill.fields.OFFER;
					bill.meta = Bill.fields.OFFER;

					for (ContractorTransactionView item : list) {
						BillTablePart.Product tp = new BillTablePart.Product();
						tp.product = item.product;
						tp.price_discount_off = item.cost_price;
						tp.price = item.reg_price;
						tp.count = item.reg_count;
						tp.CalcDiscount("");
						tp.Calc("");
						bill.TablePartProduct.add(tp);
					}

					bill.CalcTotal();

					try {
						new BillFactory().Save(bill);
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (Actions.OpenBoFormByInstanceAction != null)
						Actions.OpenBoFormByInstanceAction.Do(bill);

					UI.Stop(ContractorTransactionViewForm.this);
				}
			}
		});
		button_1.setBounds(10, 122, 90, 29);
		getContentPane().add(button_1);
	}

	public void Print() {
		UI.Start(this);
		Contractor contractor = (Contractor) contractorCtrl.getBO();
		if (contractor == null)
			Console.I().INFO(getClass(), "Print","Выберите Контрагента");
		else {
			Run.OpenFile(Main.httpServer.GetRootUrl() + "report/ContractorTransactionView?fromDate=" + fromDate.getDate().getTime() + "&toDate=" + toDate.getDate().getTime() + "&contractor="
					+ contractor.id);
		}
		UI.Stop(this);
	}
}