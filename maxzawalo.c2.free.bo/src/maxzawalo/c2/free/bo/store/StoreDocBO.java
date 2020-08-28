package maxzawalo.c2.free.bo.store;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.annotation.JsonField;
import maxzawalo.c2.base.bo.Coworker;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.bo.TablePartItem;
import maxzawalo.c2.base.bo.registry.Registry;
import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.free.bo.Contract;
import maxzawalo.c2.free.bo.Currency;
import maxzawalo.c2.free.bo.Settings;
import maxzawalo.c2.free.bo.Store;
import maxzawalo.c2.free.bo.StrictForm;
import maxzawalo.c2.free.bo.bank.BankTP;
import maxzawalo.c2.free.bo.registry.RegistryProduct;

public class StoreDocBO<Doc> extends DocumentBO<Doc> {
	public static class fields {
		public static final String DOC_CURRENCY = "doc_currency_id";
		public static final String DOC_CONTRACT = "doc_contract_id";
		public static final String STORE = "store_id";

		// calc
		public static final String ShowTotalSum = "ShowTotalSum";
		public static final String ShowTotalVat = "ShowTotalVat";

		public static final String TablePartProduct = "TablePartProduct";
		public static final String TablePartService = "TablePartService";
		public static final String TablePartEquipment = "TablePartEquipment";
		public static final String StrictForms = "StrictForms";
		public static final String OFFER = "Коммерческое предложение";
	}

	@BoField(caption = "Договор")
	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, columnName = StoreDocBO.fields.DOC_CONTRACT)
	public Contract doc_contract;// TODO:

	@BoField(caption = "Валюта документа")
	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, columnName = StoreDocBO.fields.DOC_CURRENCY)
	public Currency doc_currency = Settings.mainCurrency;

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, columnName = StoreDocBO.fields.STORE)
	public Store store = Settings.mainStore;

	// @XmlElementWrapper(name = "roster")
	// @XmlElement(name = "rosterItem")
	// @XmlPath("roster/rosterItem")
	@JsonField(columnName = StoreDocBO.fields.TablePartProduct)
	public List TablePartProduct = new ArrayList<>();
	@JsonField(columnName = StoreDocBO.fields.TablePartService)
	public List TablePartService = new ArrayList<>();
	@JsonField(columnName = StoreDocBO.fields.TablePartEquipment)
	public List TablePartEquipment = new ArrayList<>();

	@JsonField(columnName = StoreDocBO.fields.StrictForms)
	public List<StrictForm> strictForms = new ArrayList<>();

	@JsonField(columnName = StoreDocBO.fields.ShowTotalSum)
	public String ShowTotalSum;

	@JsonField(columnName = StoreDocBO.fields.ShowTotalVat)
	public String ShowTotalVat;

	// @Expose
	// Class<Doc> DocT;
	@Expose
	public Class itemProductT;

	@Expose
	public Class itemServiceT;

	@Expose
	public Class itemEquipmentT;

	@DatabaseField(columnName = DocumentBO.fields.TOTAL)
	public double total = 0;

	@DatabaseField(columnName = DocumentBO.fields.TOTAL_VAT)
	public double totalVat = 0;

	@DatabaseField
	public double total_1c = 0;// для тестирования округления

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = false)
	public Coworker chief;

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = false)
	public Coworker chief_accountant;

	public boolean updateLot = false;

	public StoreDocBO() {
	}

	@Override
	public List<TablePartItem> GetTPByName(String name) {
		switch (name) {
		case fields.TablePartProduct:
			return TablePartProduct;
		case fields.TablePartService:
			return TablePartService;
		case fields.TablePartEquipment:
			return TablePartEquipment;
		}
		return new ArrayList<>();
	}

	@Override
	public void SetTPByName(String name, List<TablePartItem> tp) {
		switch (name) {
		case fields.TablePartProduct:
			TablePartProduct = tp;
		case fields.TablePartService:
			TablePartService = tp;
		case fields.TablePartEquipment:
			TablePartEquipment = tp;
		}
	}

	@Override
	public Class GetTypeTPByName(String name) {
		switch (name) {
		case fields.TablePartProduct:
			return itemProductT;
		case fields.TablePartService:
			return itemServiceT;
		case fields.TablePartEquipment:
			return itemEquipmentT;
		}

		return super.GetTypeTPByName(name);
	}

	@Override
	public String[] GetTPNames() {
		return new String[] { fields.TablePartProduct, fields.TablePartService, fields.TablePartEquipment };
	}

	public void CalcTotal() {
		total = CalcSumTotal();
		totalVat = CalcSumVat();
	}

	public double CalcCount() {
		// double sum = 0;
		// for (String name : GetTPNames()) {
		// sum += CalcTPCount(GetTPByName(name), 0, GetTPByName(name).size());
		// sum = Format.defaultRound(sum);
		// }
		// return sum;
		return Format.countRound(CalcTPCount(TablePartProduct, 0, TablePartProduct.size()));
		// TODO: + CalcTPCount(TablePartService) +
		// CalcTPCount(TablePartEquipment));
	}

	public double CalcCount(int fromRow, int toRow) {
		// double sum = 0;
		// for (String name : GetTPNames()) {
		// sum += CalcTPCount(GetTPByName(name), fromRow, toRow);
		// sum = Format.defaultRound(sum);
		// }
		// return sum;
		return Format.countRound(CalcTPCount(TablePartProduct, fromRow, toRow));
		// TODO: + CalcTPCount(TablePartService) +
		// CalcTPCount(TablePartEquipment));
	}

	public <Item> double CalcTPCount(List<Item> TablePart, int fromRow, int toRow) {
		double value = 0;
		if (TablePart.size() != 0) {
			for (int row = fromRow; row < toRow; row++) {
				Item item = TablePart.get(row);
				value += ((StoreTP) item).count;
				// TODO: if count
				value = Format.countRound(value);
			}
		}
		// value = Format.defaultRound(value);
		return value;
	}

	public double CalcSum() {
		double sum = 0;
		for (String name : GetTPNames()) {
			sum += CalcTPSum(GetTPByName(name), 0, GetTPByName(name).size());
			sum = Format.defaultRound(sum);
		}
		return sum;
		// return Format.defaultRound(CalcTPSum(TablePartProduct, 0,
		// TablePartProduct.size())
		// + CalcTPSum(TablePartService, 0, TablePartService.size())
		// + CalcTPSum(TablePartEquipment, 0, TablePartEquipment.size()));
	}

	public double CalcSum(int fromRow, int toRow) {
		// // TODO: check
		// double sum = 0;
		// for (String name : GetTPNames()) {
		// sum += CalcTPSum(GetTPByName(name), fromRow, toRow);
		// sum = Format.defaultRound(sum);
		// }
		// return sum;
		return Format.defaultRound(CalcTPSum(TablePartProduct, fromRow, toRow));
		// + CalcTPSum(TablePartService, fromRow, toRow) +
		// CalcTPSum(TablePartEquipment, fromRow, toRow));
	}

	public <Item> double CalcTPSum(List<Item> TablePart, int fromRow, int toRow) {
		double value = 0;
		if (TablePart.size() != 0) {
			for (int row = fromRow; row < toRow; row++) {
				Item item = TablePart.get(row);
				value += ((StoreTP) item).sum;
				value = Format.defaultRound(value);
			}
		}
		// value = Format.defaultRound(value);
		return value;
	}

	public double CalcSumVat() {
		// TODO: check
		double sum = 0;
		for (String name : GetTPNames()) {
			sum += CalcTPSumVat(GetTPByName(name), 0, GetTPByName(name).size());
			sum = Format.defaultRound(sum);
		}
		return sum;
		// return Format.defaultRound(CalcTPSumVat(TablePartProduct, 0,
		// TablePartProduct.size())
		// + CalcTPSumVat(TablePartService, 0, TablePartService.size())
		// + CalcTPSumVat(TablePartEquipment, 0, TablePartEquipment.size()));
	}

	public double CalcSumVat(int fromRow, int toRow) {
		// double sum = 0;
		// for (String name : GetTPNames()) {
		// sum += CalcTPSumVat(GetTPByName(name), fromRow, toRow);
		// sum = Format.defaultRound(sum);
		// }
		// return sum;
		return Format.defaultRound(CalcTPSumVat(TablePartProduct, fromRow, toRow));
		// + CalcTPSumVat(TablePartService, fromRow, toRow) +
		// CalcTPSumVat(TablePartEquipment, fromRow, toRow));
	}

	public double CalcSumTotal() {
		double sum = 0;
		for (String name : GetTPNames()) {
			sum += CalcTPTotal(GetTPByName(name), 0, GetTPByName(name).size());
			sum = Format.defaultRound(sum);
		}
		return sum;
	}

	public double CalcSumTotal(int fromRow, int toRow) {
		// double sum = 0;
		// for (String name : GetTPNames()) {
		// sum += CalcTPTotal(GetTPByName(name), fromRow, toRow);
		// sum = Format.defaultRound(sum);
		// }
		// return sum;
		return Format.defaultRound(CalcTPTotal(TablePartProduct, fromRow, toRow));
		// + CalcTPTotal(TablePartService, fromRow, toRow) +
		// CalcTPTotal(TablePartEquipment, fromRow, toRow));
	}

	@Override
	public Object getCalcField(String name) {
		switch (name) {
		case StoreDocBO.fields.ShowTotalSum:
			return Format.Show(total);
		case StoreDocBO.fields.ShowTotalVat:
			return Format.Show(totalVat);
		default: {
			// TODO: убрать!!!
			ShowTotalSum = (String) getCalcField(StoreDocBO.fields.ShowTotalSum);
			ShowTotalVat = (String) getCalcField(StoreDocBO.fields.ShowTotalVat);
			return super.getCalcField(name);
		}
		}
	}

	public String asLot() {
		// TODO: Доработать
		return Format.Show("dd.MM.yy", this.DocDate) + " " + this.code;
	}

	@Override
	protected List<String> getExceptFields() {
		List<String> exceptFields = super.getExceptFields();
		for (Field field : new StoreDocBO().getClass().getFields()) {
			String fieldName = field.getName();
			if (fieldName.contains(DocumentBO.fields.TOTAL))
				exceptFields.add(fieldName);
		}
		// exceptFields.add("totalVat");
		// exceptFields.add("total_1c");

		return exceptFields;
	}

	@Override
	public boolean CheckDoc() {
		boolean retVal = (doc_contract != null);

		if (!retVal) {
			String message = "Не заполнено поле Договор";
			log.ERROR("TransactionBody", message);
			Console.I().ERROR(getClass(), "TransactionBody", message);
		}
		return retVal;
	}

	@Override
	public List getTablePart4Rep() {
		List tps = new ArrayList<>();
		// TODO: GetTPByName(name)
		tps.addAll(TablePartProduct);
		tps.addAll(TablePartService);
		tps.addAll(TablePartEquipment);
		return tps;
	}

	public StrictForm getCurrentStrictForm() {
		// List<StrictForm> strictForms = (List<StrictForm>)
		// calcFields.get("strictForms");
		if (strictForms.size() != 0)
			for (StrictForm form : strictForms)
				if (!form.deleted && form.write_off_type.equals("Списание"))
					return form;
		return null;
	}

	@Override
	public void setUsedRegistries() {
		super.setUsedRegistries();
		AddUsedRegistry(new RegistryProduct());
	}

	@Override
	protected <Item> double GetTpTotal(Item item) {
		return ((StoreTP) item).total;
	}

	@Override
	protected <Item> double GetTpSumVat(Item item) {
		return ((StoreTP) item).sumVat;
	}

	public RegistryProduct getRegistryProductFromUsed() {
		for (Object r : usedRegistries.values())
			// RegistryProduct, RegistryProductFIFO etc.
			if (r instanceof RegistryProduct)
				return (RegistryProduct) r;
		return null;
	}
}