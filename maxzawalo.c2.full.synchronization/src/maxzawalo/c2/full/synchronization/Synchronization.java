//TODO: new FactoryBO<>().BulkSave(newItems); Create
package maxzawalo.c2.full.synchronization;

import java.io.File;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.misc.TransactionManager;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.CatalogueBO;
import maxzawalo.c2.base.bo.Coworker;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.bo.SlaveCatalogueBO;
import maxzawalo.c2.base.data.DbHelper;
import maxzawalo.c2.base.data.factory.CatalogueFactory;
import maxzawalo.c2.base.data.factory.DocumentFactory;
import maxzawalo.c2.base.data.factory.FactoryBO;
import maxzawalo.c2.base.data.factory.SlaveCatalogueFactory;
import maxzawalo.c2.base.data.factory.TablePartItemFactory;
import maxzawalo.c2.base.interfaces.CompareT;
import maxzawalo.c2.base.utils.FileUtils;
import maxzawalo.c2.base.utils.Global;
import maxzawalo.c2.base.utils.ListUtils;
import maxzawalo.c2.base.utils.Logger;
import maxzawalo.c2.base.utils.Profiler;
import maxzawalo.c2.free.bo.ContactInfo;
import maxzawalo.c2.free.bo.Contract;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.bo.Currency;
import maxzawalo.c2.free.bo.ListUtilsBO;
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
import maxzawalo.c2.free.bo.store.StoreDocBO;
import maxzawalo.c2.free.cache.Cache;
import maxzawalo.c2.free.data.factory.GlobalConstantsFactory;
import maxzawalo.c2.free.data.factory.catalogue.ContactInfoFactory;
import maxzawalo.c2.free.data.factory.catalogue.ContractFactory;
import maxzawalo.c2.free.data.factory.catalogue.ContractorFactory;
import maxzawalo.c2.free.data.factory.catalogue.CurrencyFactory;
import maxzawalo.c2.free.data.factory.catalogue.PriceFactory;
import maxzawalo.c2.free.data.factory.catalogue.ProductFactory;
import maxzawalo.c2.free.data.factory.catalogue.StrictFormFactory;
import maxzawalo.c2.free.data.factory.catalogue.UnitsFactory;
import maxzawalo.c2.free.data.factory.document.BillFactory;
import maxzawalo.c2.free.data.factory.document.DeliveryNoteFactory;
import maxzawalo.c2.free.data.factory.document.StoreDocFactory;
import maxzawalo.c2.free.data.json.BoAdapter;
import maxzawalo.c2.free.data.json.adapter.BillAdapter;
import maxzawalo.c2.free.data.json.adapter.BillTablePartAdapter;
import maxzawalo.c2.free.data.json.adapter.ContractAdapter;
import maxzawalo.c2.free.data.json.adapter.ContractorAdapter;
import maxzawalo.c2.free.data.json.adapter.CoworkerAdapter;
import maxzawalo.c2.free.data.json.adapter.CurrencyAdapter;
import maxzawalo.c2.free.data.json.adapter.DeliveryNoteAdapter;
import maxzawalo.c2.free.data.json.adapter.DeliveryNoteTablePartAdapter;
import maxzawalo.c2.free.data.json.adapter.ProductAdapter;
import maxzawalo.c2.free.data.json.adapter.StoreAdapter;
import maxzawalo.c2.free.data.json.adapter.StrictFormAdapter;
import maxzawalo.c2.free.data.json.adapter.UnitsAdapter;
import maxzawalo.c2.full.bo.TradeAddition;
import maxzawalo.c2.full.bo.comparer.RegistryAccountingCompare;
import maxzawalo.c2.full.bo.document.cashvoucher.CashVoucher;
import maxzawalo.c2.full.bo.document.cashvoucher.CashVoucherTablePart;
import maxzawalo.c2.full.bo.document.remaining_stock.RemainingStock;
import maxzawalo.c2.full.bo.document.remaining_stock.RemainingStockTablePart;
import maxzawalo.c2.full.bo.document.return_of_goods.ReturnOfGoods;
import maxzawalo.c2.full.bo.document.return_of_goods.ReturnOfGoodsTablePart;
import maxzawalo.c2.full.bo.registry.RegistryAccounting;
import maxzawalo.c2.full.data.factory.TradeAdditionFactory;
import maxzawalo.c2.full.data.factory.document.CashVoucherFactory;
import maxzawalo.c2.full.data.factory.document.InvoiceFactoryFull;
import maxzawalo.c2.full.data.factory.document.RemainingStockFactory;
import maxzawalo.c2.full.data.factory.document.ReturnOfGoodsFactory;
import maxzawalo.c2.full.data.factory.registry.RegistryAccountingFactory;
import maxzawalo.c2.full.data.json.adapter.CashVoucherAdapter;
import maxzawalo.c2.full.data.json.adapter.CashVoucherTablePartAdapter;
import maxzawalo.c2.full.data.json.adapter.ContactInfoAdapter;
import maxzawalo.c2.full.data.json.adapter.InvoiceAdapter;
import maxzawalo.c2.full.data.json.adapter.InvoiceTablePartAdapter;
import maxzawalo.c2.full.data.json.adapter.PriceAdapter;
import maxzawalo.c2.full.data.json.adapter.RegistryAccountingAdapter;
import maxzawalo.c2.full.data.json.adapter.RemainingStockAdapter;
import maxzawalo.c2.full.data.json.adapter.RemainingStockTablePartAdapter;
import maxzawalo.c2.full.data.json.adapter.ReturnOfGoodsAdapter;
import maxzawalo.c2.full.data.json.adapter.ReturnOfGoodsTablePartAdapter;
import maxzawalo.c2.full.data.json.adapter.TradeAdditionAdapter;
import maxzawalo.c2.full.data.json.net.CurrencyFromNet;

public class Synchronization {

	static Logger log = Logger.getLogger(Synchronization.class);
	static Gson gson;

	public static Gson getGson() {
		if (gson == null) {
			Map<Class, BoAdapter> adapters = new HashMap<>();
			GsonBuilder gsonBuilder = new GsonBuilder();
			adapters.put(Product.class, new ProductAdapter());
			adapters.put(Contractor.class, new ContractorAdapter());
			adapters.put(Units.class, new UnitsAdapter());
			adapters.put(Price.class, new PriceAdapter());

			adapters.put(Coworker.class, new CoworkerAdapter());

			adapters.put(Contract.class, new ContractAdapter());
			adapters.put(ContactInfo.class, new ContactInfoAdapter());
			adapters.put(TradeAddition.class, new TradeAdditionAdapter());
			adapters.put(Currency.class, new CurrencyAdapter());
			adapters.put(StrictForm.class, new StrictFormAdapter());
			adapters.put(Store.class, new StoreAdapter());

			adapters.put(Bill.class, new BillAdapter());
			adapters.put(BillTablePart.Product.class, new BillTablePartAdapter.Product());
			adapters.put(BillTablePart.Service.class, new BillTablePartAdapter.Service());
			adapters.put(BillTablePart.Equipment.class, new BillTablePartAdapter.Equipment());

			adapters.put(Invoice.class, new InvoiceAdapter());
			// adapters.put(InvoiceTablePart.class, new
			// InvoiceTablePartAdapter());
			adapters.put(InvoiceTablePart.Product.class, new InvoiceTablePartAdapter.Product());
			adapters.put(InvoiceTablePart.Service.class, new InvoiceTablePartAdapter.Service());
			adapters.put(InvoiceTablePart.Equipment.class, new InvoiceTablePartAdapter.Equipment());

			adapters.put(RemainingStock.class, new RemainingStockAdapter());
			// adapters.put(InvoiceTablePart.class, new
			// InvoiceTablePartAdapter());
			adapters.put(RemainingStockTablePart.Product.class, new RemainingStockTablePartAdapter.Product());
			adapters.put(RemainingStockTablePart.Service.class, new RemainingStockTablePartAdapter.Service());
			adapters.put(RemainingStockTablePart.Equipment.class, new RemainingStockTablePartAdapter.Equipment());

			adapters.put(DeliveryNote.class, new DeliveryNoteAdapter());
			adapters.put(DeliveryNoteTablePart.Product.class, new DeliveryNoteTablePartAdapter.Product());
			adapters.put(DeliveryNoteTablePart.Service.class, new DeliveryNoteTablePartAdapter.Service());
			adapters.put(DeliveryNoteTablePart.Equipment.class, new DeliveryNoteTablePartAdapter.Equipment());

			adapters.put(ReturnOfGoods.class, new ReturnOfGoodsAdapter());
			adapters.put(ReturnOfGoodsTablePart.Product.class, new ReturnOfGoodsTablePartAdapter.Product());
			adapters.put(ReturnOfGoodsTablePart.Service.class, new ReturnOfGoodsTablePartAdapter.Service());
			adapters.put(ReturnOfGoodsTablePart.Equipment.class, new ReturnOfGoodsTablePartAdapter.Equipment());

			adapters.put(CashVoucher.class, new CashVoucherAdapter());
			adapters.put(CashVoucherTablePart.Product.class, new CashVoucherTablePartAdapter.Product());
			adapters.put(CashVoucherTablePart.Service.class, new CashVoucherTablePartAdapter.Service());
			adapters.put(CashVoucherTablePart.Equipment.class, new CashVoucherTablePartAdapter.Equipment());

			adapters.put(RegistryAccounting.class, new RegistryAccountingAdapter());

			for (Class cl : adapters.keySet())
				gsonBuilder.registerTypeAdapter(cl, adapters.get(cl));

			// adapters.put(BillsProduct.class, new
			// DocConnectAdapter());
			gsonBuilder.excludeFieldsWithoutExposeAnnotation();
			gsonBuilder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
			gsonBuilder.serializeNulls();
			gson = gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();

			for (Class cl : adapters.keySet()) {
				adapters.get(cl).gson = gson;
				adapters.get(cl).mode1c = true;
				adapters.get(cl).SetSkipFields();
			}
		}
		return gson;
	}

	static Object locker = new Object();
	static boolean isProcess = false;

	public static void LoadDeliveryNote(Map<Class, SyncInfo> info) throws Exception {
		Profiler profiler = new Profiler();
		profiler.Start("LoadDeliveryNote");

		List<DeliveryNote> all = new DeliveryNoteFactory().getActualTable();
		List<DeliveryNote> intersection = new ArrayList<>();
		List<DeliveryNote> newItems = LoadDocument(DeliveryNote[].class, all, intersection, "DeliveryNote.JSON");
		info.put(DeliveryNote.class, new SyncInfo(newItems.size()));
		if (newItems.size() != 0) {
			new DeliveryNoteFactory().BulkSave(newItems);

			List<DeliveryNoteTablePart.Product> intersectionTP = new ArrayList<>();
			List<DeliveryNoteTablePart.Product> allTP = LoadDocTP(DeliveryNoteTablePart.Product[].class, new FactoryBO<>().Create(DeliveryNoteTablePart.Product.class).getActualTable(), intersectionTP,
					"DeliveryNoteTablePartProduct.JSON");
			int size = allTP.size();
			new TablePartItemFactory<DeliveryNoteTablePart.Product>().BulkSave(allTP);
			BulkUpdateDocTotal(newItems);

			// new Registry().GroupTransactionT(newItems);
		}

		profiler.Stop("LoadDeliveryNote");
		profiler.PrintElapsed("LoadDeliveryNote");
	}

	public static void LoadReturnOfGoods(Map<Class, SyncInfo> info) throws Exception {
		Profiler profiler = new Profiler();
		profiler.Start("LoadReturnOfGoods");

		List<ReturnOfGoods> all = new ReturnOfGoodsFactory().getActualTable();
		List<ReturnOfGoods> intersection = new ArrayList<>();
		List<ReturnOfGoods> newItems = LoadDocument(ReturnOfGoods[].class, all, intersection, "ReturnOfGoods.JSON");
		info.put(ReturnOfGoods.class, new SyncInfo(newItems.size()));
		if (newItems.size() != 0) {
			new ReturnOfGoodsFactory().BulkSave(newItems);

			List<ReturnOfGoodsTablePart.Product> intersectionTP = new ArrayList<>();
			List<ReturnOfGoodsTablePart.Product> allTP = LoadDocTP(ReturnOfGoodsTablePart.Product[].class, new FactoryBO<>().Create(ReturnOfGoodsTablePart.Product.class).getActualTable(),
					intersectionTP, "ReturnOfGoodsTablePartProduct.JSON");
			int size = allTP.size();
			new TablePartItemFactory<ReturnOfGoodsTablePart.Product>().BulkSave(allTP);
			BulkUpdateDocTotal(newItems);

			// new Registry().GroupTransactionT(newItems);
		}

		profiler.Stop("LoadReturnOfGoods");
		profiler.PrintElapsed("LoadReturnOfGoods");
	}

	public static void LoadCashVoucher(Map<Class, SyncInfo> info) throws Exception {
		Profiler profiler = new Profiler();
		profiler.Start("LoadCashVoucher");

		List<CashVoucher> all = new CashVoucherFactory().getActualTable();
		List<CashVoucher> intersection = new ArrayList<>();
		List<CashVoucher> newItems = LoadDocument(CashVoucher[].class, all, intersection, "CashVoucher.JSON");
		info.put(DeliveryNote.class, new SyncInfo(newItems.size()));
		if (newItems.size() != 0) {
			new CashVoucherFactory().BulkSave(newItems);

			List<CashVoucherTablePart.Product> intersectionTP = new ArrayList<>();
			List<CashVoucherTablePart.Product> allTP = LoadDocTP(CashVoucherTablePart.Product[].class, new FactoryBO<>().Create(CashVoucherTablePart.Product.class).getActualTable(), intersectionTP,
					"CashVoucherTablePartProduct.JSON");
			int size = allTP.size();
			new TablePartItemFactory<CashVoucherTablePart.Product>().BulkSave(allTP);
			BulkUpdateDocTotal(newItems);

			// new Registry().GroupTransactionT(newItems);
		}

		profiler.Stop("LoadCashVoucher");
		profiler.PrintElapsed("LoadCashVoucher");
	}

	public static void LoadBill(Map<Class, SyncInfo> info) throws Exception {
		Profiler profiler = new Profiler();
		profiler.Start("LoadBill");

		List<Bill> all = new BillFactory().getActualTable();
		List<Bill> intersection = new ArrayList<>();
		List<Bill> newItems = LoadDocument(Bill[].class, all, intersection, "Bill.JSON");
		info.put(Bill.class, new SyncInfo(newItems.size()));
		if (newItems.size() != 0) {
			new BillFactory().BulkSave(newItems);

			List<BillTablePart.Product> intersectionTP = new ArrayList<>();
			List<BillTablePart.Product> allTP = LoadDocTP(BillTablePart.Product[].class, new FactoryBO<>().Create(BillTablePart.Product.class).getActualTable(), intersectionTP,
					"BillTablePartProduct.JSON");
			int size = allTP.size();
			new TablePartItemFactory<BillTablePart.Product>().BulkSave(allTP);

			BulkUpdateDocTotal(newItems);
		}

		profiler.Stop("LoadBill");
		profiler.PrintElapsed("LoadBill");
	}

	protected static void LoadTradeAddition(Map<Class, SyncInfo> info) throws Exception {
		Profiler profiler = new Profiler();
		profiler.Start("LoadTradeAddition");

		List<TradeAddition> all = new TradeAdditionFactory().getActualTable();
		List<TradeAddition> intersection = new ArrayList<>();
		List<TradeAddition> newItems = LoadCatalogue(TradeAddition[].class, all, intersection, "TradeAddition.JSON");
		info.put(TradeAddition.class, new SyncInfo(newItems.size()));
		if (newItems.size() != 0)
			new TradeAdditionFactory().BulkSave(newItems);

		// Очищаем кэш данной таблицы
		// TODO: у всех таблиц так делаем
		Cache.I().clearAllCache();// Cache("TradeAddition.GetAll");

		profiler.Stop("LoadTradeAddition");
		profiler.PrintElapsed("LoadTradeAddition");
	}

	protected static void LoadCurrency(Map<Class, SyncInfo> info) throws Exception {

		CurrencyFromNet.Load();

		Profiler profiler = new Profiler();
		profiler.Start("LoadCurrency");

		List<Currency> all = new CurrencyFactory().getActualTable();
		List<Currency> intersection = new ArrayList<>();
		List<Currency> newItems = LoadCatalogue(Currency[].class, all, intersection, "Currency.JSON");
		info.put(Currency.class, new SyncInfo(newItems.size()));
		if (newItems.size() != 0)
			new CurrencyFactory().BulkSave(newItems);

		profiler.Stop("LoadCurrency");
		profiler.PrintElapsed("LoadCurrency");
	}

	protected static void LoadStrictForm(Map<Class, SyncInfo> info) throws Exception {
		Profiler profiler = new Profiler();
		profiler.Start("LoadStrictForm");

		List<StrictForm> all = new StrictFormFactory().getActualTable();
		List<StrictForm> intersection = new ArrayList<>();
		LoadStrictFormByDoc(info, all, intersection, new DeliveryNote(), "StrictFormDeliveryNote.JSON");
		LoadStrictFormByDoc(info, all, intersection, new ReturnOfGoods(), "StrictFormReturnOfGoods.JSON");
		profiler.Stop("LoadStrictForm");
		profiler.PrintElapsed("LoadStrictForm");
	}

	protected static void LoadStrictFormByDoc(Map<Class, SyncInfo> info, List<StrictForm> all, List<StrictForm> intersection, DocumentBO doc, String jsonName) throws Exception {
		List<StrictForm> newItems = LoadCatalogue(StrictForm[].class, all, intersection, jsonName);
		info.put(StrictForm.class, new SyncInfo(newItems.size()));
		if (newItems.size() != 0) {
			// Устанавливаем ссылку на док
			for (StrictForm form : newItems) {
				String uuid = (String) form.calcFields.get("doc_uuid");
				doc = (DocumentBO) new DocumentFactory<>().Create(doc.getClass()).GetByUUID(uuid);
				if (doc == null) {
					log.WARN("LoadStrictForm", doc.getClass().getName() + " not found " + uuid);
					continue;
				}
				form.reg_id = doc.id;
				form.reg_type = doc.reg_type;
			}

			new StrictFormFactory().BulkSave(newItems);
		}
	}

	protected static void ClearSyncFlag() throws InstantiationException, IllegalAccessException {
		// Все ок - снимаем флаг - видим новые данные
		for (Class cl : Global.dbClasses) {
			if (cl == BO.class)
				continue;
			log.INFO("ClearSyncFlag", cl.getName() + " count=" + (new FactoryBO<>().Create(cl).ClearSyncFlag(Global.sync_flag)));
		}
	}

	protected static List<Contractor> LoadContractor(Map<Class, SyncInfo> info) throws Exception {
		Profiler profiler = new Profiler();
		profiler.Start("LoadContractor");

		List<Contractor> all = new ContractorFactory().getActualTable();
		List<Contractor> intersection = new ArrayList<>();
		List<Contractor> newItems = LoadCatalogue(Contractor[].class, all, intersection, "Contractor.JSON");
		info.put(Contractor.class, new SyncInfo(newItems.size()));
		if (newItems.size() != 0) {
			new ContractorFactory().BulkSave(newItems);
			BulkUpdateParent(newItems, all);
		}

		if (intersection.size() != 0) {
			List<Contractor> updated = new ArrayList<>();
			// TODO: как то тут надо прицепить sync_flag
			for (Contractor pi : intersection)
				for (Contractor pa : all)
					// или code
					if (pa.uuid.equals(pi.uuid)) {
						if (pi.name != null && pi.name.equals(pa.name)) {
							pa.name = pi.name;
							updated.add(pa);
						}
						if (pi.full_name != null && pi.full_name.equals(pa.full_name)) {
							pa.full_name = pi.full_name;
							updated.add(pa);
						}
						break;
					}
			new ContractorFactory().BulkSave(updated);
			log.INFO("LoadContractor", "обновлено " + updated.size());
		}

		List<Contractor> merged = new ArrayList<>();
		merged.addAll(all);
		merged.addAll(newItems);

		profiler.Stop("LoadContractor");
		profiler.PrintElapsed("LoadContractor");

		return merged;
	}

	public static void LoadProduct(Map<Class, SyncInfo> info) throws Exception {
		Profiler profiler = new Profiler();
		profiler.Start("LoadProduct");

		List<Product> all = new ProductFactory().getActualTable();
		List<Product> intersection = new ArrayList<>();
		List<Product> newItems = LoadCatalogue(Product[].class, all, intersection, "Product.JSON");
		info.put(Product.class, new SyncInfo(newItems.size()));
		if (newItems.size() != 0) {
			new ProductFactory().BulkSave(newItems);
			BulkUpdateParent(newItems, all);
		}

		if (intersection.size() != 0) {
			List<Product> updated = new ArrayList<>();
			// TODO: как то тут надо прицепить sync_flag
			for (Product pi : intersection)
				for (Product pa : all)
					// или code
					if (pa.uuid.equals(pi.uuid)) {
						// TODO:сравниваем все поля кроме BO. если отличается -
						// обновляем их. reflection. field list.
						// TODO: deleted, изменение иерархии
						if (pa.addition != pi.addition) {
							pa.addition = pi.addition;
							updated.add(pa);
						}
						if (pi.name != null && pi.name.equals(pa.name)) {
							pa.name = pi.name;
							updated.add(pa);
						}
						if (pi.full_name != null && pi.full_name.equals(pa.full_name)) {
							pa.full_name = pi.full_name;
							updated.add(pa);
						}
						break;
					}
			new ProductFactory().BulkSave(updated);
			log.INFO("LoadProduct", "обновлено " + updated.size());
		}

		profiler.Stop("LoadProduct");
		profiler.PrintElapsed("LoadProduct");

	}

	public static void LoadPrice(Map<Class, SyncInfo> info) throws Exception {
		Profiler profiler = new Profiler();
		profiler.Start("LoadPrice");

		List<Price> all = new PriceFactory().getActualTable();
		List<Price> intersection = new ArrayList<>();
		List<Price> newItems = LoadCatalogue(Price[].class, all, intersection, "Price.JSON");
		info.put(Price.class, new SyncInfo(newItems.size()));
		if (newItems.size() != 0) {
			new PriceFactory().BulkSave(newItems);
			// BulkUpdateParent(newItems, all);
		}
		profiler.Stop("LoadPrice");
		profiler.PrintElapsed("LoadPrice");
	}

	protected static void LoadUnits(Map<Class, SyncInfo> info) throws Exception {
		Profiler profiler = new Profiler();
		profiler.Start("LoadUnits");

		List<Units> all = new UnitsFactory().getActualTable();
		List<Units> intersection = new ArrayList<>();
		List<Units> newItems = LoadCatalogue(Units[].class, all, intersection, "Units.JSON");
		info.put(Units.class, new SyncInfo(newItems.size()));
		if (newItems.size() != 0)
			new UnitsFactory().BulkSave(newItems);

		profiler.Stop("LoadUnits");
		profiler.PrintElapsed("LoadUnits");
	}

	protected static void LoadCoworker(Map<Class, SyncInfo> info) throws Exception {
		Profiler profiler = new Profiler();
		profiler.Start("LoadCoworker");

		List<Coworker> all = new FactoryBO<>().Create(Coworker.class).getActualTable();
		List<Coworker> intersection = new ArrayList<>();
		List<Coworker> newItems = LoadCatalogue(Coworker[].class, all, intersection, "Coworkers.JSON");
		info.put(Coworker.class, new SyncInfo(newItems.size()));
		if (newItems.size() != 0)
			new FactoryBO<>().Create(Coworker.class).BulkSave(newItems);

		profiler.Stop("LoadCoworker");
		profiler.PrintElapsed("LoadCoworker");
	}

	public static List<ContactInfo> LoadContactInfo(Map<Class, SyncInfo> info) throws Exception {
		Profiler profiler = new Profiler();
		profiler.Start("LoadContactInfo");

		List<ContactInfo> all = new ContactInfoFactory().getActualTable();
		List<ContactInfo> intersection = new ArrayList<>();
		List<ContactInfo> newItems = LoadCatalogue(ContactInfo[].class, all, intersection, "ContactInfo.JSON");
		info.put(ContactInfo.class, new SyncInfo(newItems.size()));
		if (newItems.size() != 0)
			new ContactInfoFactory().BulkSave(newItems);

		List<ContactInfo> merged = new ArrayList<>();
		merged.addAll(all);
		merged.addAll(newItems);

		profiler.Stop("LoadContactInfo");
		profiler.PrintElapsed("LoadContactInfo");

		return merged;
	}

	public static List<Contract> LoadContract(Map<Class, SyncInfo> info) throws Exception {
		Profiler profiler = new Profiler();
		profiler.Start("LoadContract");

		List<Contract> all = new ContractFactory().getActualTable();
		List<Contract> intersection = new ArrayList<>();
		List<Contract> newItems = LoadCatalogue(Contract[].class, all, intersection, "Contract.JSON");
		info.put(Contract.class, new SyncInfo(newItems.size()));
		if (newItems.size() != 0)
			new ContractFactory().BulkSave(newItems);

		List<Contract> merged = new ArrayList<>();
		merged.addAll(all);
		merged.addAll(newItems);

		profiler.Stop("LoadContract");
		profiler.PrintElapsed("LoadContract");
		return merged;
	}

	public static void LoadInvoice(Map<Class, SyncInfo> info) throws Exception {
		Profiler profiler = new Profiler();
		profiler.Start("LoadInvoice");

		List<Invoice> all = new InvoiceFactoryFull().getActualTable();
		List<Invoice> intersection = new ArrayList<>();
		// Приходные
		List<Invoice> newItems = LoadDocument(Invoice[].class, all, intersection, "Invoice.JSON");
		// allInvoice = Except(allInvoice, new Invoice().GetAll(),
		// DocumentBOCompare);

		info.put(Invoice.class, new SyncInfo(newItems.size()));
		if (newItems.size() != 0) {

			new InvoiceFactoryFull().BulkSave(newItems);

			List<InvoiceTablePart.Product> intersectionTpProduct = new ArrayList<>();
			List<InvoiceTablePart.Product> allInvoiceTpProduct = LoadDocTP(InvoiceTablePart.Product[].class, new TablePartItemFactory<InvoiceTablePart.Product>().getActualTable(),
					intersectionTpProduct, "InvoiceTablePartProduct.JSON");
			int size = allInvoiceTpProduct.size();
			new TablePartItemFactory<InvoiceTablePart.Product>().BulkSave(allInvoiceTpProduct);

			List<InvoiceTablePart.Equipment> intersectionTpEquipment = new ArrayList<>();
			List<InvoiceTablePart.Equipment> allInvoiceTablePartEquipment = LoadDocTP(InvoiceTablePart.Equipment[].class, new TablePartItemFactory<InvoiceTablePart.Equipment>().getActualTable(),
					intersectionTpEquipment, "InvoiceTablePartEquipment.JSON");
			size += allInvoiceTablePartEquipment.size();
			new TablePartItemFactory<InvoiceTablePart.Equipment>().BulkSave(allInvoiceTablePartEquipment);

			List<InvoiceTablePart.Service> intersectionTpService = new ArrayList<>();
			List<InvoiceTablePart.Service> allInvoiceTablePartService = LoadDocTP(InvoiceTablePart.Service[].class, new TablePartItemFactory<InvoiceTablePart.Service>().getActualTable(),
					intersectionTpService, "InvoiceTablePartService.JSON");
			info.put(InvoiceTablePart.class, new SyncInfo(allInvoiceTablePartService.size() + size));
			new TablePartItemFactory<InvoiceTablePart.Service>().BulkSave(allInvoiceTablePartService);
			BulkUpdateDocTotal(newItems);

			// TODO: actual table
			Cache.I().putList("Invoice.All.Sync", newItems, 600);

			LoadPrice(info);

			// new Registry().GroupTransactionT(newItems);
		} else
			LoadPrice(info);

		Cache.I().clearAllCache();
		profiler.Stop("LoadInvoice");
		profiler.PrintElapsed("LoadInvoice");
	}

	public static void LoadRemainingStock(Map<Class, SyncInfo> info) throws Exception {
		Profiler profiler = new Profiler();
		profiler.Start("LoadRemainingStock");

		List<RemainingStock> all = new RemainingStockFactory().getActualTable();
		List<RemainingStock> intersection = new ArrayList<>();
		// Ввод первоначальных остатков
		List<RemainingStock> newItems = LoadDocument(RemainingStock[].class, all, intersection, "VvodOstatkov.JSON");

		info.put(RemainingStock.class, new SyncInfo(newItems.size()));
		if (newItems.size() != 0) {
			new RemainingStockFactory().BulkSave(newItems);
			List<RemainingStockTablePart.Product> intersectionTpProduct = new ArrayList<>();
			List<RemainingStockTablePart.Product> allTpProduct = LoadDocTP(RemainingStockTablePart.Product[].class, new FactoryBO<>().Create(RemainingStockTablePart.Product.class).getActualTable(),
					intersectionTpProduct, "VvodOstatkovTablePartProduct.JSON");
			new TablePartItemFactory<RemainingStockTablePart.Product>().BulkSave(allTpProduct);
			BulkUpdateDocTotal(newItems);

			List<RemainingStock> merged = new ArrayList<>();
			merged.addAll(newItems);
			merged.addAll(all);

			// new Registry().GroupTransactionT(merged);
		}

		Cache.I().clearAllCache();
		profiler.Stop("LoadRemainingStock");
		profiler.PrintElapsed("LoadRemainingStock");
	}

	protected static void LoadRegistryAccounting(Map<Class, SyncInfo> info) throws Exception {
		Profiler profiler = new Profiler();
		profiler.Start("LoadRegistryAccounting");

		List<RegistryAccounting> all = new RegistryAccountingFactory().getActualTable();
		List<RegistryAccounting> intersection = new ArrayList<>();
		List<RegistryAccounting> newItems = new ArrayList<>();

		File folder = new File(FileUtils.Get1cDir());
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles)
			if (file.getName().contains("ПроводкиСчет") && FileUtils.getFileExtension(file).toLowerCase().equals("json")) {
				newItems.addAll(LoadRegistryAccounting(RegistryAccounting[].class, all, intersection, file.getName()));
				Files.move(Paths.get(file.getAbsolutePath()), Paths.get(file.getAbsolutePath() + "_" + Global.sync_flag));
			}
		info.put(RegistryAccounting.class, new SyncInfo(newItems.size()));

		// for (RegistryAccounting item : newItems)
		// if (item.contractor == null)
		// System.out.println(item);

		if (newItems.size() != 0) {
			// TODO: Clear by newItems period
			new RegistryAccountingFactory().ClearTable();// .ClearLastMonth();
			new RegistryAccountingFactory().BulkSave(newItems);
		}

		profiler.Stop("LoadRegistryAccounting");
		profiler.PrintElapsed("LoadRegistryAccounting");
	}

	private static void EndProcess() {
		synchronized (locker) {
			isProcess = false;
		}
	}

	protected static void LoadStore(Map<Class, SyncInfo> info) throws Exception {
		Profiler profiler = new Profiler();
		profiler.Start("LoadStore");

		List<Store> all = new FactoryBO<>().Create(Store.class).getActualTable();
		List<Store> intersection = new ArrayList<>();
		List<Store> newItems = LoadCatalogue(Store[].class, all, intersection, "Store.JSON");
		info.put(Currency.class, new SyncInfo(newItems.size()));
		if (newItems.size() != 0)
			new FactoryBO<>().Create(Store.class).BulkSave(newItems);

		profiler.Stop("LoadStore");
		profiler.PrintElapsed("LoadStore");
	}

	static void Load() {
		new Thread(new Runnable() {
			public void run() {
				try {
					// LoadCatalogue(Units.class, "Units.JSON");
					// LoadCatalogue(Product.class, "Products.JSON");
					// LoadTable(Contractor[].class, "Contractors.JSON");
				} catch (Exception e) {
					log.ERROR("Load", e);
				}

				EndProcess();
			}
		});
	}

	public static <T> List<T> LoadCatalogue(final Class<T[]> clazz, List<T> all, List<T> intersection, final String jsonName) {
		return LoadTable(clazz, all, intersection, ListUtilsBO.CatalogueBOCompare, jsonName);
	}

	public static <T> List<T> LoadRegistryAccounting(final Class<T[]> clazz, List<T> all, List<T> intersection, final String jsonName) {
		return LoadTable(clazz, all, intersection, new RegistryAccountingCompare(), jsonName);
	}

	static <T> List<T> LoadDocument(final Class<T[]> clazz, List<T> all, List<T> intersection, final String jsonName) {
		return LoadTable(clazz, all, intersection, ListUtilsBO.DocumentBOCompare, jsonName);
	}

	static <T> List<T> LoadDocTP(final Class<T[]> clazz, List<T> all, List<T> intersection, final String jsonName) {
		List<T> tp = LoadTable(clazz, all, intersection, ListUtilsBO.CommonTablePartCompare, jsonName);
		// List<?> shallowCopy = tp.subList(0, tp.size());
		tp = ListUtils.reverse(tp);

		// shallowCopy = tp.subList(0, tp.size());
		intersection = ListUtils.reverse(intersection);

		return tp;
	}

	public static <T> List<T> LoadTable(final Class<T[]> clazz, List<T> all, List<T> intersection, CompareT comparer, final String jsonName) {
		Profiler profiler = new Profiler();
		profiler.Start("LoadTable");

		List<T> newList = new ArrayList<>();
		try {
			profiler.Start("LoadTable.readFileAsString");
			String str = FileUtils.readFileAsString(FileUtils.Get1cDir() + jsonName);
			profiler.Stop("LoadTable.readFileAsString");
			profiler.PrintElapsed("LoadTable.readFileAsString");

			profiler.Start("LoadTable.fromJson");
			T[] arr = getGson().fromJson(str, clazz);
			newList = Arrays.asList(arr);
			profiler.Stop("LoadTable.fromJson");
			profiler.PrintElapsed("LoadTable.fromJson");

			// TODO: !!! очистка табчасти при Update
			{
				profiler.Start("LoadTable.Except");
				// В объединение попадают новые объекты
				newList = ListUtils.Except(newList, all, intersection, comparer);
				profiler.Stop("LoadTable.Except");
				profiler.PrintElapsed("LoadTable.Except");
			}
		} catch (Exception e) {
			log.ERROR("LoadTable", e);
		}

		log.INFO("LoadTable", clazz.getName() + " new count = " + newList.size());
		log.INFO("LoadTable", clazz.getName() + " for update count = " + intersection.size());

		profiler.Stop("LoadTable");
		profiler.PrintElapsed("LoadTable");
		return newList;
	}

	private static <T> void BulkUpdateParent(final List<T> newItems, final List<T> all) throws Exception {
		Profiler profiler = new Profiler();
		profiler.Start("BulkUpdateParent");

		// Слияние обоих списков
		all.addAll(newItems);
		// устанавливаем parent
		for (T o : newItems) {
			for (T parent : all) {
				if (((CatalogueBO) o).parent == null)
					continue;
				if (((BO) parent).uuid.equals(((CatalogueBO) o).parent.uuid)) {
					((CatalogueBO) o).parent = (BO) parent;
					break;
				}
			}
		}
		int pagesCount = (int) FactoryBO.GetPagesCount(newItems.size(), FactoryBO.maxPerTransation);

		for (int p = 0; p < pagesCount; p++) {
			final int page = p;
			TransactionManager.callInTransaction(DbHelper.getConnectionSource(), new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					CatalogueFactory factory = new CatalogueFactory();
					int end = Math.min(newItems.size(), (page + 1) * FactoryBO.maxPerTransation);
					for (int i = page * FactoryBO.maxPerTransation; i < end; i++) {
						factory.UpdateParent(((CatalogueBO) newItems.get(i)));
					}
					return null;
				}
			});
		}

		profiler.Stop("BulkUpdateParent");
		profiler.PrintElapsed("BulkUpdateParent");
	}

	private static <T> void BulkUpdateDocTotal(final List<T> all) throws Exception {
		Profiler profiler = new Profiler();
		profiler.Start("BulkUpdateDocTotal");

		if (all.size() == 0) {
			profiler.Stop("BulkUpdateDocTotal");
			profiler.PrintElapsed("BulkUpdateDocTotal");
			return;
		}
		int pagesCount = (int) FactoryBO.GetPagesCount(all.size(), FactoryBO.maxPerTransation);

		for (int p = 0; p < pagesCount; p++) {
			final int page = p;
			TransactionManager.callInTransaction(DbHelper.getConnectionSource(), new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					StoreDocFactory factory = new StoreDocFactory<>();
					int end = Math.min(all.size(), (page + 1) * FactoryBO.maxPerTransation);
					for (int i = page * FactoryBO.maxPerTransation; i < end; i++) {
						StoreDocBO doc = ((StoreDocBO) all.get(i));
						factory.LoadTablePart(doc);
						doc.total = doc.CalcSumTotal();
						doc.totalVat = doc.CalcSumVat();
						factory.UpdateTotal(doc);
					}
					return null;
				}
			});
		}
		profiler.Stop("BulkUpdateDocTotal");
		profiler.PrintElapsed("BulkUpdateDocTotal");
	}

	private static <I, O> void BulkUpdateOwner(final List<I> items, final List<O> owners) throws Exception {
		Profiler profiler = new Profiler();
		profiler.Start("BulkUpdateOwner");

		// устанавливаем Owner
		for (I item : items) {
			for (O owner : owners) {
				if (((SlaveCatalogueBO) item).owner == null)
					continue;
				if (((SlaveCatalogueBO) item).owner.uuid.equals(((BO) owner).uuid)) {
					((SlaveCatalogueBO) item).owner = (BO) owner;
					break;
				}
			}
		}

		int pagesCount = (int) FactoryBO.GetPagesCount(items.size(), FactoryBO.maxPerTransation);

		for (int p = 0; p < pagesCount; p++) {
			final int page = p;
			TransactionManager.callInTransaction(DbHelper.getConnectionSource(), new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					SlaveCatalogueFactory factory = new SlaveCatalogueFactory<>();
					int end = Math.min(items.size(), (page + 1) * FactoryBO.maxPerTransation);
					for (int i = page * FactoryBO.maxPerTransation; i < end; i++) {
						factory.UpdateOwner(((SlaveCatalogueBO) items.get(i)));
					}
					return null;
				}
			});
		}
		profiler.Stop("BulkUpdateOwner");
		profiler.PrintElapsed("BulkUpdateOwner");
	}

	public static void Clear() {
		// ClearTable(new Units());
		// ClearTable(new Contractor());
		// ClearTable(new Product());
		// ClearTable(new Price());
		// // ClearTable(new BillTablePart());
		// ClearTable(new Bill());
		//
		// // ClearTable(new Barcode());
	}

	public static void FillData() {
		// for (int i = 0; i < 20; i++) {
		// Bill b = new Bill();
		// b.code = "test";
		// // if(i< 5)
		// b.contractor = new Contractor().GetById(i + 1);
		// b.TablePartProduct.add(
		// (BillTablePart.Product) new BillTablePart.Product().setProduct(new
		// Product().GetById(i * 4 + 2)));
		// b.TablePartProduct.add(
		// (BillTablePart.Product) new BillTablePart.Product().setProduct(new
		// Product().GetById(i * 4 + 3)));
		// b.TablePartProduct.add(
		// (BillTablePart.Product) new BillTablePart.Product().setProduct(new
		// Product().GetById(i * 4 + 4)));
		// b.TablePartProduct.add(
		// (BillTablePart.Product) new BillTablePart.Product().setProduct(new
		// Product().GetById(i * 4 + 5)));
		// for (CommonTablePart item : b.TablePartProduct) {
		// item.code = "test";
		// item.count = Format.defaultRound(new Random().nextDouble() * new
		// Random().nextInt(10));
		// item.price = Format.defaultRound(new Random().nextDouble() * new
		// Random().nextInt(100));
		// }
		// try {
		// b.Save();
		// } catch (Exception e) {
		// log.ERROR(e);
		// }
		// }
		//
		// for (int i = 0; i < 5; i++) {
		// DeliveryNote b = new DeliveryNote();
		// b.code = "test";
		// // if(i< 5)
		// b.contractor = new Contractor().GetById(i + 1);
		// b.TablePartProduct.add((DeliveryNoteTablePart.Product) new
		// DeliveryNoteTablePart.Product()
		// .setProduct(new Product().GetById(i * 4 + 2)));
		// b.TablePartProduct.add((DeliveryNoteTablePart.Product) new
		// DeliveryNoteTablePart.Product()
		// .setProduct(new Product().GetById(i * 4 + 3)));
		// b.TablePartProduct.add((DeliveryNoteTablePart.Product) new
		// DeliveryNoteTablePart.Product()
		// .setProduct(new Product().GetById(i * 4 + 4)));
		// b.TablePartProduct.add((DeliveryNoteTablePart.Product) new
		// DeliveryNoteTablePart.Product()
		// .setProduct(new Product().GetById(i * 4 + 5)));
		// for (CommonTablePart item : b.TablePartProduct) {
		// item.code = "test";
		// item.count = Format.defaultRound(new Random().nextDouble() * new
		// Random().nextInt(10));
		// item.price = Format.defaultRound(new Random().nextDouble() * new
		// Random().nextInt(100));
		// }
		// try {
		// b.Save();
		// } catch (Exception e) {
		// log.ERROR(e);
		// }
		// }
	}

	public static boolean Do() {
		boolean retVal = false;
		// TODO: сохранияем время для продолжение итп
		Global.sync_flag = System.currentTimeMillis();
		// log.INFO("Synchronization start");
		Profiler profiler = new Profiler();
		String key = "Synchronization " + Global.sync_flag;
		profiler.Start(key);

		Map<Class, SyncInfo> info = new HashMap<>();
		try {

			// synchronized (locker) {
			// if (isProcess)
			// return info;// TODO:message
			// else
			// isProcess = true;
			// }

			Backup(Global.sync_flag);
			// TODO:
			for (Class t : Global.dbClasses)
				SetAdequacy(t, Global.sync_flag);

			LoadCurrency(info);

			// TODO: первый раз - работает. потом надо синхр. фильтр отключать
			GlobalConstantsFactory.Load();

			if (true) {
				LoadRegistryAccounting(info);
				// LoadStore(info);
				// LoadCoworker(info);
				// LoadPrice(info);
				// LoadReturnOfGoods(info);
			} else {

				LoadStore(info);
				LoadCoworker(info);
				//
				LoadTradeAddition(info);
				LoadUnits(info);
				LoadProduct(info);
				// Cache.I().clearAllCache();

				List<Contract> allContract = LoadContract(info);
				List<Contractor> allContractor = LoadContractor(info);
				BulkUpdateOwner(allContract, allContractor);

				List<ContactInfo> allContactInfo = LoadContactInfo(info);
				BulkUpdateOwner(allContactInfo, allContractor);

				LoadRemainingStock(info);

				LoadInvoice(info);
				// ClearSyncFlag();
				// if (true)
				// throw new Exception("stop");

				LoadCashVoucher(info);
				LoadReturnOfGoods(info);
				LoadDeliveryNote(info);
				LoadBill(info);

				LoadStrictForm(info);

			}

			// Фильтруется по текущему sync_flag
			// TODO: проводим только после удачной синхронизации(загрузки)
			// new Registry().GroupTransactionT(new Invoice().GetAll());
			// new Registry().GroupTransactionT(new DeliveryNote().GetAll());

			ClearSyncFlag();

			EndProcess();
			profiler.Stop(key);
			profiler.PrintElapsed(key);
			retVal = true;

		} catch (Exception e) {
			profiler.Stop(key);
			profiler.PrintElapsed(key);
			log.ERROR("Do", e);
		}
		Global.sync_flag = 0;
		Cache.I().clearAllCache();
		return retVal;
	}

	public static void Backup() throws Exception {
		Backup(new Date().getTime());
	}

	public static void Backup(long time) throws Exception {
		// if (!Settings.isServer())
		// return;
		//
		// Profiler profiler = new Profiler();
		// profiler.Start("Backup");
		// String pathToMysqlBin = "c:/Program Files/MySQL/MySQL Server
		// 5.7/bin/";
		// String pathToBackUpFile = FileUtils.GetBackUpDir() + "c2_" + time +
		// ".backup";
		// String backUpCmd = "\"" + pathToMysqlBin
		// + "mysqldump\" -u root -proot --routines warehouse
		// --single-transaction > " + pathToBackUpFile;
		//
		// String res = CmdUtils.Exec(backUpCmd);
		// profiler.Stop("Backup");
		// profiler.PrintElapsed("Backup");
		//
		// if (res.equals("") || res.contains("Системе не удается найти
		// указанный путь.")
		// || res.contains("не является внутренней или внешней"))
		// throw new Exception(res);
	}

	public static <T> void SetAdequacy(Class<T> type, long sync_flag) throws Exception {
		// BO bo = ((BO) type.newInstance());
		FactoryBO factory = new FactoryBO<>().Create(type);
		log.DEBUG("SetAdequacy", "==== " + factory.getTableName());
		String path = FileUtils.Get1cDir() + factory.getTableName() + ".adequacy";
		String data = FileUtils.readFileAsString(path);
		if (data.equals("")) {
			log.DEBUG("SetAdequacy", "Нет данных");
			return;
		}
		data = data.replace("\u000b", "\t");
		String[] adequacies = data.split("[\\r\\n]+");
		for (String adequacy : adequacies) {

			String[] ids = adequacy.split("\t");
			int id = Integer.parseInt(ids[0]);
			UUID uuid = UUID.fromString(ids[1]);
			log.DEBUG("SetAdequacy", ids[0] + " " + ids[1]);

			if (uuid.equals(BO.zero_uuid))
				continue;

			// bo = ((BO) type.newInstance());
			BO bo = (BO) factory.GetById(id);
			if (bo == null) {
				log.DEBUG("SetAdequacy", "bo == null");
				continue;
			}
			if (bo.uuid.equals(BO.zero_uuid)) {
				factory.UpdateUUID(bo, uuid);
				log.DEBUG("SetAdequacy", "Обновлен uuid");
			} else
				log.DEBUG("SetAdequacy", "Пропускаем обновление uuid");
		}

		Files.move(Paths.get(path), Paths.get(path + "_" + sync_flag));
	}
}