package maxzawalo.c2.full.bo;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.Coworker;
import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.free.bo.BankAccount;
import maxzawalo.c2.free.bo.ContactInfo;
import maxzawalo.c2.free.bo.Contract;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.bo.Currency;
import maxzawalo.c2.free.bo.LotOfProduct;
import maxzawalo.c2.free.bo.Price;
import maxzawalo.c2.free.bo.Product;
import maxzawalo.c2.free.bo.Store;
import maxzawalo.c2.free.bo.StrictForm;
import maxzawalo.c2.free.bo.Units;
import maxzawalo.c2.free.bo.document.bill.Bill;
import maxzawalo.c2.free.bo.document.bill.BillTablePart;
import maxzawalo.c2.free.bo.document.deliverynote.DeliveryNote;
import maxzawalo.c2.free.bo.document.deliverynote.DeliveryNoteTablePart;
import maxzawalo.c2.free.bo.document.invoice.Invoice;
import maxzawalo.c2.free.bo.document.invoice.InvoiceTablePart;
import maxzawalo.c2.free.bo.document.receiptmoney.ReceiptMoney;
import maxzawalo.c2.free.bo.document.receiptmoney.ReceiptMoneyTablePart;
import maxzawalo.c2.free.bo.document.writeoffmoney.WriteOffMoney;
import maxzawalo.c2.free.bo.document.writeoffmoney.WriteOffMoneyTablePart;
import maxzawalo.c2.free.bo.enums.ContractType;
import maxzawalo.c2.free.bo.enums.ReceiptMoneyType;
import maxzawalo.c2.free.bo.registry.RegistryProduct;
import maxzawalo.c2.full.bo.document.cashvoucher.CashVoucher;
import maxzawalo.c2.full.bo.document.cashvoucher.CashVoucherTablePart;
import maxzawalo.c2.full.bo.document.order.Order;
import maxzawalo.c2.full.bo.document.order.OrderTablePart;
import maxzawalo.c2.full.bo.document.remaining_stock.RemainingStock;
import maxzawalo.c2.full.bo.document.remaining_stock.RemainingStockTablePart;
import maxzawalo.c2.full.bo.document.return_from_customer.ReturnFromCustomer;
import maxzawalo.c2.full.bo.document.return_from_customer.ReturnFromCustomerTablePart;
import maxzawalo.c2.full.bo.document.return_of_goods.ReturnOfGoods;
import maxzawalo.c2.full.bo.document.return_of_goods.ReturnOfGoodsTablePart;
import maxzawalo.c2.full.bo.document.warrant_4_receipt.Warrant4Receipt;
import maxzawalo.c2.full.bo.document.warrant_4_receipt.Warrant4ReceiptTablePart;
import maxzawalo.c2.full.bo.document.write_off_product.WriteOffProduct;
import maxzawalo.c2.full.bo.document.write_off_product.WriteOffProductTablePart;
import maxzawalo.c2.full.bo.registry.RegistryAccounting;
import maxzawalo.c2.full.bo.registry.RegistryInventory;
import maxzawalo.c2.full.bo.registry.RegistryProductFIFO;

//@formatter:off
public class ClassesFull {

	public final static Class[] dbClasses = { 
			BO.class, 
			
			User.class, 
			Coworker.class, 
			Units.class, 
			Product.class,
			Store.class, 
			Currency.class, 
			Contractor.class, 
			Contract.class, 
			
			ContactInfo.class, 
			StrictForm.class, 
			Price.class,
			LotOfProduct.class, 
			
			Bill.class,
			BillTablePart.Product.class, 
			BillTablePart.Service.class, 
			BillTablePart.Equipment.class, 
			
			Invoice.class, 
			InvoiceTablePart.Product.class,
			InvoiceTablePart.Service.class, 
			InvoiceTablePart.Equipment.class, 
			
			DeliveryNote.class,
			DeliveryNoteTablePart.Product.class, 
			DeliveryNoteTablePart.Service.class,
			DeliveryNoteTablePart.Equipment.class, 
			
			RegistryProduct.class,
//========= Full			
			MobileLogin.class,
			TradeAddition.class,
			ScannedBarcode.class, 
			
			Order.class, 
			OrderTablePart.Product.class, 
			OrderTablePart.Service.class,
			OrderTablePart.Equipment.class, 
			
			RemainingStock.class, 
			RemainingStockTablePart.Product.class,
			RemainingStockTablePart.Service.class, 
			RemainingStockTablePart.Equipment.class, 
			
			CashVoucher.class, 
			CashVoucherTablePart.Product.class, 
			CashVoucherTablePart.Service.class,
			CashVoucherTablePart.Equipment.class, 
			
			ReturnOfGoods.class, 
			ReturnOfGoodsTablePart.Product.class,
			ReturnOfGoodsTablePart.Service.class, 
			ReturnOfGoodsTablePart.Equipment.class,
			
			WriteOffProduct.class, 
			WriteOffProductTablePart.Product.class, 
			WriteOffProductTablePart.Service.class,
			WriteOffProductTablePart.Equipment.class,

			ReturnFromCustomer.class, 
			ReturnFromCustomerTablePart.Product.class,
			ReturnFromCustomerTablePart.Service.class, 
			ReturnFromCustomerTablePart.Equipment.class, 
			
			Warrant4Receipt.class, 
			Warrant4ReceiptTablePart.Product.class, 
			Warrant4ReceiptTablePart.Service.class,
			Warrant4ReceiptTablePart.Equipment.class, 
			
			StoreDaybook.class, 
			
			RegistryInventory.class, 
			RegistryAccounting.class,
			
			BankAccount.class,
			
			ReceiptMoney.class,
			ReceiptMoneyTablePart.Payment.class,
			
			WriteOffMoney.class,
			WriteOffMoneyTablePart.Payment.class
			
			,RegistryProductFIFO.class
			
//			,FromMarketToRent.class, 
//			,FromMarketToRentTP.Product.class
	};

	public final static Class[] enums = { ContractType.class,ReceiptMoneyType.class };

	// Price.class Invoice.class для Подбора(партия)
	// TODO: граф - для последовательности
	// TODO: LotOfProduct.class
	public final static Class[] heatingUpClasses = new Class[] { User.class, Coworker.class, Currency.class,
			Units.class, Store.class, Contractor.class, Contract.class, Product.class, Invoice.class, Price.class, TradeAddition.class ,BankAccount.class};

	// TODO: учесть что ВводНачальныхОстатков здесь = RemainingStock - т.к. остатки самые первые, 
	//то в цепочке не будет в тот день ничего (!!!!! не вводить в тот день больше ничего)
	public final static Class[] transactionChains = new Class[] {  
			Invoice.class, Order.class, DeliveryNote.class, 
			CashVoucher.class, WriteOffProduct.class, 
			RemainingStock.class, ReturnFromCustomer.class, ReturnOfGoods.class,
			ReceiptMoney.class, WriteOffMoney.class};
}
//@formatter:on