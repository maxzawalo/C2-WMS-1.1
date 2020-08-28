package maxzawalo.c2.free.bo;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.Coworker;
import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.free.bo.document.bill.Bill;
import maxzawalo.c2.free.bo.document.bill.BillTablePart;
import maxzawalo.c2.free.bo.document.deliverynote.DeliveryNote;
import maxzawalo.c2.free.bo.document.deliverynote.DeliveryNoteTablePart;
import maxzawalo.c2.free.bo.document.invoice.Invoice;
import maxzawalo.c2.free.bo.document.invoice.InvoiceTablePart;
import maxzawalo.c2.free.bo.enums.ContractType;
import maxzawalo.c2.free.bo.registry.RegistryProduct;

//@formatter:off
public class ClassesFree {

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
			
			RegistryProduct.class
	};

	public final static Class[] enums = { ContractType.class };

	// Price.class Invoice.class для Подбора(партия)
	// TODO: граф - для последовательности
	// TODO: LotOfProduct.class
	public final static Class[] heatingUpClasses = new Class[] { User.class, Coworker.class, Currency.class,
			Units.class, Store.class, Contractor.class, Contract.class, Product.class, Invoice.class, Price.class };

	// TODO: WriteOffProduct - RemainingStock
	public final static Class[] transactionChains = new Class[] { Invoice.class, DeliveryNote.class };
}
//@formatter:on