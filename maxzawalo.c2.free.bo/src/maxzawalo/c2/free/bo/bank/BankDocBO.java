package maxzawalo.c2.free.bo.bank;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.annotation.JsonField;
import maxzawalo.c2.base.bo.Coworker;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.bo.TablePartItem;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.free.bo.BankAccount;

public class BankDocBO<Doc> extends DocumentBO<Doc> {
	public static class fields {
		public static final String BANK_ACCOUNT = "bank_account_id";
		
		// calc
		public static final String ShowTotalSum = "ShowTotalSum";
		public static final String ShowTotalVat = "ShowTotalVat";

		public static final String TablePartPayment = "TablePartPayment";

		public static final String IN_NUMBER = "in_number";
		public static final String IN_DATE = "in_date";
	}

	// @BoField(caption = "Контрагент")
	// @DatabaseField(index = true, foreign = true, foreignAutoRefresh = true,
	// columnName = BankDocBO.fields.CONTRACTOR)
	// public Contractor contractor;

	@JsonField(columnName = BankDocBO.fields.TablePartPayment)
	public List TablePartPayment = new ArrayList<>();

	@Expose
	public Class itemPaymentT;

	@BoField(caption = "Вх. номер", fieldName1C = "НомерВходящегоДокумента")
	@DatabaseField(index = true, width = 30, columnName = fields.IN_NUMBER)
	public String in_number = "";

	@BoField(caption = "Вх. дата", fieldName1C = "ДатаВходящегоДокумента")
	@DatabaseField(index = true, dataType = DataType.DATE_LONG, columnName = fields.IN_DATE)
	public Date in_date = new Date();

	@BoField(caption = "Сумма", fieldName1C = "СуммаДокумента")
	@DatabaseField(columnName = DocumentBO.fields.TOTAL)
	public double total = 0;

	@BoField(caption = "Сумма")
	@DatabaseField(columnName = DocumentBO.fields.TOTAL_VAT)
	public double totalVat = 0;

	// @DatabaseField
	// public double total_1c = 0;// для тестирования округления

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = false)
	public Coworker chief;

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = false)
	public Coworker chief_accountant;
	
	@BoField(caption = "Счет организации", fieldName1C = "СчетОрганизации")
	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = false, columnName = fields.BANK_ACCOUNT)
	public BankAccount bank_account;

	public BankDocBO() {
	}

	@Override
	public List<TablePartItem> GetTPByName(String name) {
		switch (name) {
		case fields.TablePartPayment:
			return TablePartPayment;
		}
		return new ArrayList<>();
	}

	@Override
	public void SetTPByName(String name, List<TablePartItem> tp) {
		switch (name) {
		case fields.TablePartPayment:
			TablePartPayment = tp;
		}
	}

	@Override
	public Class GetTypeTPByName(String name) {
		switch (name) {
		case fields.TablePartPayment:
			return itemPaymentT;
		}

		return super.GetTypeTPByName(name);
	}

	@Override
	public String[] GetTPNames() {
		return new String[] { fields.TablePartPayment };
	}

	@Override
	public Object getCalcField(String name) {
		switch (name) {
		case BankDocBO.fields.ShowTotalSum:
			return Format.Show(total);
		case BankDocBO.fields.ShowTotalVat:
			return Format.Show(totalVat);
		default:
			return super.getCalcField(name);
		}
	}

	@Override
	protected List<String> getExceptFields() {
		List<String> exceptFields = super.getExceptFields();
		for (Field field : new BankDocBO().getClass().getFields()) {
			String fieldName = field.getName();
			if (fieldName.contains(DocumentBO.fields.TOTAL))
				exceptFields.add(fieldName);
		}
		// exceptFields.add("totalVat");
		// exceptFields.add("total_1c");

		return exceptFields;
	}

	@Override
	public List getTablePart4Rep() {
		List tps = new ArrayList<>();
		// TODO: GetTPByName(name)
		tps.addAll(TablePartPayment);
		return tps;
	}

	public void CalcTotal() {
		total = CalcSumTotal();
		totalVat = CalcSumVat();
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

	public double CalcSumTotal() {
		double sum = 0;
		for (String name : GetTPNames()) {
			sum += CalcTPTotal(GetTPByName(name), 0, GetTPByName(name).size());
			sum = Format.defaultRound(sum);
		}
		return sum;
	}

	// public double CalcSumTotal(int fromRow, int toRow) {
	// return Format.defaultRound(CalcTPTotal(TablePartPayment, fromRow,
	// toRow));
	// }

	@Override
	protected <Item> double GetTpTotal(Item item) {
		return ((BankTP) item).sum;
	}

	@Override
	protected <Item> double GetTpSumVat(Item item) {
		return ((BankTP) item).sumVat;
	}

}