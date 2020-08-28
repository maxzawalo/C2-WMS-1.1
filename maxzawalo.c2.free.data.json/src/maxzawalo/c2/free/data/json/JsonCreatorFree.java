package maxzawalo.c2.free.data.json;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.Coworker;
import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.base.interfaces.ActionC2;
import maxzawalo.c2.free.bo.ContactInfo;
import maxzawalo.c2.free.bo.Contract;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.bo.Currency;
import maxzawalo.c2.free.bo.LotOfProduct;
import maxzawalo.c2.free.bo.Product;
import maxzawalo.c2.free.bo.Store;
import maxzawalo.c2.free.bo.StrictForm;
import maxzawalo.c2.free.bo.Units;
import maxzawalo.c2.free.bo.document.bill.Bill;
import maxzawalo.c2.free.bo.document.bill.BillTablePart;
import maxzawalo.c2.free.bo.document.deliverynote.DeliveryNote;
import maxzawalo.c2.free.bo.document.deliverynote.DeliveryNoteTablePart;
import maxzawalo.c2.free.bo.enums.ContractType;
import maxzawalo.c2.free.data.json.adapter.BillAdapter;
import maxzawalo.c2.free.data.json.adapter.BillTablePartAdapter;
import maxzawalo.c2.free.data.json.adapter.ContactInfoAdapter;
import maxzawalo.c2.free.data.json.adapter.ContractAdapter;
import maxzawalo.c2.free.data.json.adapter.ContractTypeAdapter;
import maxzawalo.c2.free.data.json.adapter.ContractorAdapter;
import maxzawalo.c2.free.data.json.adapter.CoworkerAdapter;
import maxzawalo.c2.free.data.json.adapter.CurrencyAdapter;
import maxzawalo.c2.free.data.json.adapter.DeliveryNoteAdapter;
import maxzawalo.c2.free.data.json.adapter.DeliveryNoteTablePartAdapter;
import maxzawalo.c2.free.data.json.adapter.LotOfProductAdapter;
import maxzawalo.c2.free.data.json.adapter.ProductAdapter;
import maxzawalo.c2.free.data.json.adapter.StoreAdapter;
import maxzawalo.c2.free.data.json.adapter.StrictFormAdapter;
import maxzawalo.c2.free.data.json.adapter.UnitsAdapter;
import maxzawalo.c2.free.data.json.adapter.UserAdapter;

public class JsonCreatorFree {
	Map<Class, BoAdapter> adapters;
	boolean web_ui = false;

	public JsonCreatorFree(boolean web_ui) {
		this.web_ui = web_ui;
	}

	public Gson CreateGson() {
		return CreateGson(null);
	}

	Gson gson;

	public Gson CreateGson(BO bo) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		if (adapters == null) {
			adapters = new HashMap<>();
			AddAdapters(adapters);
			for (Class cl : adapters.keySet())
				gsonBuilder.registerTypeAdapter(cl, adapters.get(cl));

			gsonBuilder.excludeFieldsWithoutExposeAnnotation();
			gsonBuilder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
			gsonBuilder.serializeNulls();
			// TODO: IF?
			gsonBuilder.setPrettyPrinting();

			gson = gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();

			for (Class cl : adapters.keySet()) {
				// adapters.get(cl).web_ui = true;
				adapters.get(cl).SetSkipFields();
				adapters.get(cl).gson = gson;
				// adapters.get(cl).obj = bo;
			}

		} else {
			// for (Class cl : adapters.keySet()) {
			// adapters.get(cl).web_ui = true;
			//// adapters.get(cl).SetSkipFields();
			//// adapters.get(cl).gson = gson;
			// adapters.get(cl).obj = bo;
			// System.out.println("");
			// }
		}

		for (Class cl : adapters.keySet()) {
			adapters.get(cl).web_ui = web_ui;
			// adapters.get(cl).SetSkipFields();
			// adapters.get(cl).gson = gson;
			adapters.get(cl).obj = bo;
		}

		return gson;
	}

	protected void AddAdapters(Map<Class, BoAdapter> adapters) {
		adapters.put(User.class, new UserAdapter());
		adapters.put(Contractor.class, new ContractorAdapter());
		adapters.put(Coworker.class, new CoworkerAdapter());
		adapters.put(Units.class, new UnitsAdapter());
		adapters.put(Product.class, new ProductAdapter());

		adapters.put(ContractType.class, new ContractTypeAdapter());
		adapters.put(Contract.class, new ContractAdapter());
		adapters.put(Currency.class, new CurrencyAdapter());
		adapters.put(Store.class, new StoreAdapter());
		adapters.put(ContactInfo.class, new ContactInfoAdapter());

		adapters.put(StrictForm.class, new StrictFormAdapter());

		adapters.put(LotOfProduct.class, new LotOfProductAdapter());
		adapters.put(DeliveryNote.class, new DeliveryNoteAdapter());
		adapters.put(DeliveryNoteTablePart.Product.class, new DeliveryNoteTablePartAdapter.Product());
		adapters.put(DeliveryNoteTablePart.Service.class, new DeliveryNoteTablePartAdapter.Service());

		adapters.put(Bill.class, new BillAdapter());
		adapters.put(BillTablePart.Product.class, new BillTablePartAdapter.Product());

		if (AddAdaptersExt != null)
			AddAdaptersExt.Do(adapters);

	}

	/**
	 * Для расширения в WWW
	 */
	public ActionC2 AddAdaptersExt;
}