package maxzawalo.c2.full.ui.pc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;

import com.j256.ormlite.logger.LocalLog;

import maxzawalo.c2.base.Actions;
import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.bo.registry.RegType;
import maxzawalo.c2.base.bo.registry.Registry;
import maxzawalo.c2.base.data.factory.DocumentFactory;
import maxzawalo.c2.base.data.factory.FactoryBO;
import maxzawalo.c2.base.interfaces.ActionC2;
import maxzawalo.c2.base.ui.pc.catalogue.CatalogueListForm;
import maxzawalo.c2.base.ui.pc.catalogue.SlaveCatalogueListForm;
import maxzawalo.c2.base.ui.pc.form.BoForm;
import maxzawalo.c2.base.ui.pc.form.BoListForm;
import maxzawalo.c2.base.utils.Global;
import maxzawalo.c2.base.utils.ListUtils;
import maxzawalo.c2.base.utils.Logger;
import maxzawalo.c2.free.accounting.AccList;
import maxzawalo.c2.free.bo.Contract;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.bo.Product;
import maxzawalo.c2.free.bo.Store;
import maxzawalo.c2.free.bo.StrictForm;
import maxzawalo.c2.free.bo.Units;
import maxzawalo.c2.free.bo.document.bill.Bill;
import maxzawalo.c2.free.bo.document.deliverynote.DeliveryNote;
import maxzawalo.c2.free.bo.document.invoice.Invoice;
import maxzawalo.c2.free.bo.store.StoreDocBO;
import maxzawalo.c2.free.data.factory.catalogue.StrictFormFactory;
import maxzawalo.c2.free.data.factory.document.BillFactory;
import maxzawalo.c2.free.data.factory.document.DeliveryNoteFactory;
import maxzawalo.c2.free.data.factory.document.ReceiptMoneyFactory;
import maxzawalo.c2.free.data.factory.document.StoreDocFactory;
import maxzawalo.c2.free.data.factory.document.WriteOffMoneyFactory;
import maxzawalo.c2.free.ui.pc.catalogue.ContractForm;
import maxzawalo.c2.free.ui.pc.catalogue.ContractListForm;
import maxzawalo.c2.free.ui.pc.catalogue.ContractorForm;
import maxzawalo.c2.free.ui.pc.catalogue.ContractorListForm;
import maxzawalo.c2.free.ui.pc.catalogue.ProductFormFree;
import maxzawalo.c2.free.ui.pc.catalogue.ProductListForm;
import maxzawalo.c2.free.ui.pc.catalogue.UnitsListForm;
import maxzawalo.c2.free.ui.pc.document.store.BillListFormFree;
import maxzawalo.c2.full.ai.ml.Rule;
import maxzawalo.c2.full.bo.ClassesFull;
import maxzawalo.c2.full.bo.document.cashvoucher.CashVoucher;
import maxzawalo.c2.full.bo.document.return_from_customer.ReturnFromCustomer;
import maxzawalo.c2.full.bo.document.return_of_goods.ReturnOfGoods;
import maxzawalo.c2.full.data.factory.TradeAdditionFactory;
import maxzawalo.c2.full.data.factory.document.CashVoucherFactory;
import maxzawalo.c2.full.data.factory.document.InvoiceFactoryFull;
import maxzawalo.c2.full.data.factory.document.OrderFactory;
import maxzawalo.c2.full.data.factory.document.RemainingStockFactory;
import maxzawalo.c2.full.data.factory.document.ReturnFromCustomerFactory;
import maxzawalo.c2.full.data.factory.document.ReturnOfGoodsFactory;
import maxzawalo.c2.full.data.factory.document.WriteOffProductFactory;
import maxzawalo.c2.full.data.json.JsonCreatorFull;
import maxzawalo.c2.full.ui.pc.document.BillFormFull;
import maxzawalo.c2.full.ui.pc.document.CashVoucherForm;
import maxzawalo.c2.full.ui.pc.document.DeliveryNoteFormFull;
import maxzawalo.c2.full.ui.pc.document.InvoiceFormFull;
import maxzawalo.c2.full.ui.pc.document.ReturnFromCustomerForm;
import maxzawalo.c2.full.ui.pc.document.ReturnOfGoodsForm;
import maxzawalo.c2.full.ui.pc.form.AnaliticsForm;
import maxzawalo.c2.full.ui.pc.view.TransactionViewForm;

public class Initialization {

	private static final String NOT_IMPLEMENTED = "NOT_IMPLEMENTED_ERROR";
	protected static Logger log = Logger.getLogger(Initialization.class);

	public static void Do() {
		System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");
		// Global.heatingUpClasses = new Class[] { Product.class };
		Global.heatingUpClasses = ClassesFull.heatingUpClasses;

		Global.dbClasses = ListUtils.joinArrayGeneric(ClassesFull.dbClasses, AccList.classes);
		Global.enums = ClassesFull.enums;
		Global.transactionChains = ClassesFull.transactionChains;

		Actions.getAccRegisters = new ActionC2() {
			@Override
			public Object Do(Object... params) {
				List<Registry> acc = new ArrayList<>();
				Arrays.asList(AccList.classes).forEach((r) -> {
					try {
						acc.add((Registry) r.newInstance());
					} catch (Exception e) {
						log.ERROR("Actions.getAccRegisters", e);
					}
				});
				return acc;
			}
		};

		Actions.FactoryByTypeAction = new ActionC2() {

			@Override
			public Object Do(Object... params) {
				DocumentFactory factory = null;
				try {
					factory = (DocumentFactory) Actions.FactoryByRegTypeAction
							.Do(((DocumentBO) ((Class) params[0]).newInstance()).reg_type);
				} catch (Exception e) {
					log.FATAL("FactoryByTypeAction", e);
				}
				if (factory != null)
					return factory;
				log.FATAL("FactoryByTypeAction", NOT_IMPLEMENTED);
				return null;
			}
		};

		Actions.GetLots4TPAction = new ActionC2() {
			@Override
			public Object Do(Object[] params) {
				return new maxzawalo.c2.full.data.factory.catalogue.LotOfProductFactoryFull()
						.Ge4TP((Product) params[0], (Store) params[1], (Date) params[2]);
			}
		};

		Actions.ShowTransactionAction = new ActionC2() {
			@Override
			public Object Do(Object... params) {
				TransactionViewForm report = new TransactionViewForm();
				report.product.onBOSelected(params[0]);
				report.setVisible(true);

				return true;
			}
		};

		Actions.OpenBoFormByInstanceAction = new ActionC2() {
			@Override
			public Object Do(Object... params) {
				// SwingUtilities.invokeLater(new Runnable() {
				// @Override
				// public void run() {
				BO bo = (BO) params[0];
				BoForm form = null;
				if (bo instanceof Invoice) {
					form = new InvoiceFormFull();
					form.Load(bo.id);
				} else if (bo instanceof DeliveryNote) {
					form = new DeliveryNoteFormFull();
					form.Load(bo.id);
				} else if (bo instanceof CashVoucher) {
					form = new CashVoucherForm();
					form.Load(bo.id);
				} else if (bo instanceof ReturnOfGoods) {
					form = new ReturnOfGoodsForm();
					form.Load(bo.id);
				} else if (bo instanceof ReturnFromCustomer) {
					form = new ReturnFromCustomerForm();
					form.Load(bo.id);
				} else if (bo instanceof Bill) {
					form = new BillFormFull();
					form.Load(bo.id);
				}
				// ==== Справочники
				else if (bo instanceof Product) {
					form = new ProductFormFree();
					form.Load(bo.id);
				} else if (bo instanceof Contractor) {
					form = new ContractorForm();
					form.Load(bo.id);
				} else if (bo instanceof Contract) {
					form = new ContractForm();
					form.Load(bo.id);
				}
				form.setVisible(true);
				// }
				// });
				return true;
			}
		};
		Actions.OpenBadDocAction = Actions.OpenBoFormByInstanceAction;

		Actions.FactoryByRegTypeAction = new ActionC2() {
			@Override
			public Object Do(Object... params) {
				switch ((int) params[0]) {
				case RegType.Invoice:
					return new InvoiceFactoryFull();
				case RegType.DeliveryNote:
					return new DeliveryNoteFactory();
				case RegType.Bill:
					return new BillFactory();
				case RegType.Order:
					return new OrderFactory();
				case RegType.RemainingStock:
					return new RemainingStockFactory();
				case RegType.CashVoucher:
					return new CashVoucherFactory();
				case RegType.ReturnOfGoods:
					return new ReturnOfGoodsFactory();
				case RegType.WriteOffProduct:
					return new WriteOffProductFactory();
				case RegType.ReturnFromCustomer:
					return new ReturnFromCustomerFactory();
				// Банк
				case RegType.ReceiptMoney:
					return new ReceiptMoneyFactory();
				case RegType.WriteOffMoney:
					return new WriteOffMoneyFactory();
				}

				log.FATAL("FactoryByRegTypeAction", NOT_IMPLEMENTED);
				return null;
			}
		};

		Actions.DocByRegTypeAction = new ActionC2() {
			@Override
			public Object Do(Object... params) {
				// switch ((int) params[0]) {
				// case RegType.Invoice:
				// return new InvoiceFactoryFull();
				// case RegType.DeliveryNote:
				// return new DeliveryNoteFactory();
				// case RegType.Bill:
				// return new BillFactory();
				// case RegType.Order:
				// return new OrderFactory();
				// case RegType.RemainingStock:
				// return new RemainingStockFactory();
				// case RegType.CashVoucher:
				// return new CashVoucherFactory();
				// case RegType.ReturnOfGoods:
				// return new ReturnOfGoodsFactory();
				// case RegType.WriteOffProduct:
				// return new WriteOffProductFactory();
				// case RegType.ReturnFromCustomer:
				// return new ReturnFromCustomerFactory();
				// }

				log.FATAL("DocByRegTypeAction", NOT_IMPLEMENTED);
				return null;
			}
		};

		Actions.GetFromDbAction = new ActionC2() {
			@Override
			public Object Do(Object... params) {
				FactoryBO factory = new FactoryBO().Create((Class) params[0]);
				factory.DeleteFilterOff();
				return factory.GetAll4Cache();
			}
		}; // GetFromDbAction();
		Actions.GetByIdFromDbAction = new ActionC2() {
			@Override
			public Object Do(Object... params) {
				// public Object Do(Class type, int id, int level) {
				FactoryBO factory = new FactoryBO().Create((Class) params[0]);
				factory.DeleteFilterOff();
				// factory.mapper = new GenericRowMapper(type);
				return factory.GetById((int) params[1], (int) params[2] + 1, false);
			}
		}; // GetByIdFromDbAction();

		Actions.ListFormByClassAction = new ActionC2() {
			@Override
			public Object Do(Object... params) {
				// .ListFormByClass.Do(fieldType, fieldBO);

				Class fieldType = (Class) params[0];
				BO fieldBO = (BO) params[1];
				boolean selectGroupOnly = (boolean) params[2];
				BO owner = (BO) params[3];
				BoListForm selectListForm = null;

				if (fieldBO instanceof Units || fieldType == Units.class) {
					return new UnitsListForm();
				} else if (fieldBO instanceof Contractor || fieldType == Contractor.class) {
					selectListForm = new ContractorListForm();
					((CatalogueListForm) selectListForm).selectGroupOnly = selectGroupOnly;
				} else if (fieldBO instanceof Product || fieldType == Product.class) {
					selectListForm = new ProductListForm();
					((CatalogueListForm) selectListForm).selectGroupOnly = selectGroupOnly;
				} else if (fieldBO instanceof Contract || fieldType == Contract.class) {
					selectListForm = new ContractListForm();
					((SlaveCatalogueListForm) selectListForm).SetOwner(owner);
					((CatalogueListForm) selectListForm).selectGroupOnly = selectGroupOnly;
				} else if (fieldBO instanceof Bill || fieldType == Bill.class) {
					selectListForm = new BillListFormFree();
					// TODO: owner filter
				}
				if (selectListForm != null)
					return selectListForm;

				log.FATAL("ListFormByClassAction", NOT_IMPLEMENTED);

				return null;
			}
		};

		Actions.CalcAdditionAction = new ActionC2() {
			@Override
			public Object Do(Object... params) {
				// .Do(lot.product, priceNormByCurrency, lot.getDelivery());
				return new TradeAdditionFactory().CalcAddition((Product) params[0], (double) params[1],
						(boolean) params[2]);
			}
		};

		Actions.CheckSFAction = new ActionC2() {
			@Override
			public Object Do(Object... params) {
				String in_form_number = params[0].toString().toUpperCase().trim();
				in_form_number = in_form_number.replace(" ", "");

				Rule rule = new Rule();
				rule.AddRule(
						"length=9;[0]=isletter;[1]=isletter;[2]=isDigit;[3]=isDigit;[4]=isDigit;[5]=isDigit;[6]=isDigit;[7]=isDigit;[8]=isDigit;");
				rule.AddRule(
						"length=7;[0]=isletter;[1]=isletter;[2]=isDigit;[3]=isDigit;[4]=isDigit;[5]=isDigit;[6]=isDigit;");
				rule.AddRule(
						"length=8;[0]=isletter;[1]=isletter;[2]=isDigit;[3]=isDigit;[4]=isDigit;[5]=isDigit;[6]=isDigit;[7]=isDigit;");

				return rule.Check(in_form_number);
			}
		};

		Actions.SaveSFFromInDocAction = new ActionC2() {
			@Override
			public Object Do(Object... params) {
				try {
					StoreDocFactory factory = (StoreDocFactory) params[0];
					// factory.DeleteFilterOn();
					StrictFormFactory sfFactory = new StrictFormFactory();
					for (Object d : factory.GetAll()) {
						StoreDocBO doc = (StoreDocBO) d;
						StrictForm form = new StrictForm();
						form.reg_type = doc.reg_type;
						form.reg_id = doc.id;

						String in_form_number = "";
						if (doc instanceof Invoice) {
							in_form_number = ((Invoice) doc).in_form_number;
						} else if (doc instanceof ReturnFromCustomer) {
							in_form_number = ((ReturnFromCustomer) doc).in_form_number;
						}

						in_form_number = in_form_number.toUpperCase().trim();
						in_form_number = in_form_number.replace(" ", "");

						// String all = in_form_number.toUpperCase().trim();
						if ((boolean) Actions.CheckSFAction.Do(in_form_number)) {
							String form_batch = in_form_number.substring(0, 2);
							String form_number = in_form_number.substring(2, in_form_number.length());
							form.form_batch = form_batch;
							form.form_number = form_number;
						} else {
							System.out.println("Неверный БСО: " + in_form_number + "\t\t" + doc.toString() + "|"
									+ doc.contractor.name);
							continue;
							// assertEquals(true, false);
						}

						form.form_type_code = "2";
						form.form_type_name = "ТТН-1";
						form.write_off_type = "Списание";

						if (!sfFactory.Exists(form))
							sfFactory.Save(form);
					}

				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}
		};

		Actions.ShowDocTransactions = new ActionC2() {

			@Override
			public Object Do(Object... params) {
				AnaliticsForm form = new AnaliticsForm((JFrame) params[0]);
				form.setVisible(true);
				form.setRegistrator((DocumentBO) params[1]);
				return null;
			}
		};

		BoForm.gson = new JsonCreatorFull(false).CreateGson();
	}
}