package maxzawalo.c2.base.bo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.Actions;
import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.bo.registry.Registry;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.bo.Organization;
import maxzawalo.c2.free.bo.registry.RegistryProduct;

/**
 * Created by Max on 16.03.2017.
 */
public class DocumentBO<Doc> extends BO<Doc> {
	public static class fields {
		public static final String DOC_DATE = "DocDate";
		public static final String COMMITED = "commited";
		public static final String SUM_CONTAINS_VAT = "sum_contains_vat";
		public static final String COMMENT = "comment";
		public static final String FROM_SOURCE_DOC = "from_source_doc";

		public static final String CONTRACTOR = "contractor_id";
		public static final String ORGANIZATION = "organization_id";

		public static final String TOTAL = "total";
		public static final String TOTAL_VAT = "totalVat";
	}

	public int reg_type = 0;

	// @BoField(caption = "Дата", fieldName1C = "Дата")
	@DatabaseField(index = true, dataType = DataType.DATE_LONG, columnName = DocumentBO.fields.DOC_DATE)
	public Date DocDate = new Date();

	@BoField(caption = "Контрагент", fieldName1C = "Контрагент")
	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, columnName = DocumentBO.fields.CONTRACTOR)
	public Contractor contractor;

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, columnName = DocumentBO.fields.ORGANIZATION)
	public Organization organization;

	@BoField(caption = "Проведен", fieldName1C = "Проведен")
	@DatabaseField(index = true, columnName = DocumentBO.fields.COMMITED)
	public boolean commited = false;

	@DatabaseField(index = true)
	public int source_doc_id = 0;

	@DatabaseField(index = true)
	public int source_doc_type = 0;

	@BoField(caption = "Комментарий", fieldName1C = "Комментарий")
	@DatabaseField(index = true, width = 100, columnName = DocumentBO.fields.COMMENT)
	public String comment = "";

	@DatabaseField(columnName = DocumentBO.fields.SUM_CONTAINS_VAT)
	public boolean sum_contains_vat = false;

	// @XmlPath("roster/rosterItem")
	// public List<InvoiceTablePart.Product> tp = new
	// ArrayList<InvoiceTablePart.Product>();

	public Map<Class, Registry> usedRegistries = new HashMap();

	public List<TablePartItem> GetTPByName(String name) {
		return new ArrayList<>();
	}

	public void SetTPByName(String name, List<TablePartItem> tp) {
	}

	public Class GetTypeTPByName(String name) {
		return null;
	}

	public String[] GetTPNames() {
		return new String[] {};
	}

	// @Expose
	// //@Expose(serialize = false, deserialize = false)
	// Class<Connect> ConnectT;

	public DocumentBO() {
		// Class clazz = this.getClass();
		// Object sc = clazz.getGenericSuperclass();
		// try {
		// if (sc != null && sc instanceof ParameterizedType) {
		// Type[] gParams = ((ParameterizedType) sc).getActualTypeArguments();
		//
		// this.itemProductT = (Class<?>) gParams[1];
		// this.itemServiceT = (Class<?>) gParams[2];
		// this.itemEquipmentT = (Class<?>) gParams[3];
		// }
		// } catch (ClassCastException e) {
		// // log.ERROR("DocumentBO", e);
		// }
		setTpTypes();
		setUsedRegistries();

		// // TODO: только в формах списка + галочка
		// enableDeletedFilter = false;
	}

	protected void setTpTypes() {

	}

	public void setUsedRegistries() {
		if (Actions.getAccRegisters != null) {
			List<Registry> buchAll = (List<Registry>) Actions.getAccRegisters.Do();
			for (Registry r : buchAll)
				AddUsedRegistry(r);
		}
	}

	public void AddUsedRegistry(Registry transaction) {
		usedRegistries.put(transaction.getClass(), transaction);
	}

	// public Type TablePartType() {
	// return itemProductT;
	// }

	// public FilterT TPCacheFilter = new FilterT<TablePartItem>() {
	// public boolean Check(TablePartItem item) {
	// return (item.doc_id == DocumentBO.this.id);// && item.deleted);
	// }
	// };

	@Override
	public String toString() {
		return getRusName() + " " + Format.Show(this.DocDate) + " " + this.code + " " + this.id;
	}

	// public boolean TransactionBody() {
	// return false;
	// }

	@Override
	public Object getCalcField(String name) {
		switch (name) {
		case BO.fields.DOC_STATE:
			if (commited)
				return "v";
			else
				return super.getCalcField(name);
		default:
			return "";
		}
	}

	public void CreateFromSourceDoc(DocumentBO sourceDoc) {
	}

	@Override
	protected List<String> getExceptFields() {
		List<String> exceptFields = super.getExceptFields();
		exceptFields.add(DocumentBO.fields.DOC_DATE);
		exceptFields.add(DocumentBO.fields.COMMITED);
		exceptFields.add("source_doc_id");
		exceptFields.add("source_doc_type");

		return exceptFields;
	}

	// public void AddToCurrentT(TablePartItem tp) {
	// }

	// TP product,service
	public List GetReportTP() {
		return null;
	}

	public boolean CheckDoc() {
		return true;
	}

	public void EnumTP() {
		for (String name : GetTPNames()) {
			List tp = GetTPByName(name);
			for (int i = 0; i < tp.size(); i++) {
				((BO) tp.get(i)).calcFields.put(TablePartItem.fields.POS, i + 1);
			}
		}
	}

	public <Item> double CalcTPTotal(List<Item> TablePart, int fromRow, int toRow) {
		double value = 0;
		if (TablePart.size() != 0) {
			for (int row = fromRow; row < toRow; row++) {
				Item item = TablePart.get(row);
				value += GetTpTotal(item);
				value = Format.defaultRound(value);
			}
		}
		// value = Format.defaultRound(value);
		return value;
	}

	protected <Item> double GetTpTotal(Item item) {
		return 0;
	}

	public <Item> double CalcTPSumVat(List<Item> TablePart, int fromRow, int toRow) {
		double value = 0;
		if (TablePart.size() != 0) {
			for (int row = fromRow; row < toRow; row++) {
				Item item = TablePart.get(row);
				value += GetTpSumVat(item);
				value = Format.defaultRound(value);
			}
		}
		// value = Format.defaultRound(value);
		return value;
	}

	protected <Item> double GetTpSumVat(Item item) {
		return 0;
	}

	public boolean HasProductRegistry() {
		for (Registry r : usedRegistries.values())
			if (r instanceof RegistryProduct)
				return true;

		return false;
	}
}