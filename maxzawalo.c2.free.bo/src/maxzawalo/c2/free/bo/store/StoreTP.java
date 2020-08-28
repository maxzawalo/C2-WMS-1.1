package maxzawalo.c2.free.bo.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.TablePartItem;
import maxzawalo.c2.base.interfaces.CompareT;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.base.utils.ListUtils;
import maxzawalo.c2.free.bo.LotOfProduct;
import maxzawalo.c2.free.bo.Product;

@XmlRootElement(name = "rosterItem", namespace = "")
@XmlType(name = "rosterItem")
@XmlAccessorType(XmlAccessType.FIELD)
public class StoreTP<Item> extends TablePartItem<Item> {

	public static class fields {
		public static final String PRODUCT = "product_id";
		public static final String PRICE_DISCOUNT_OFF = "price_discount_off";
		public static final String PRICE = "price";
		public static final String COUNT = "count";
		public static final String SUM = "sum";
		public static final String RATE_VAT = "rateVat";
		public static final String SUM_VAT = "sumVat";
		public static final String DISCOUNT = "discount";
		public static final String TOTAL = "total";
		public static final String SUM_CONTAINS_VAT = "sum_contains_vat";

		// calc
		public static final String UNITS = "units";
	}

	@XmlTransient
	@BoField(caption = "Номенклатура")
	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, columnName = StoreTP.fields.PRODUCT)
	public Product product;

	@XmlTransient
	@BoField(caption = "Себестоимость")
	@DatabaseField(columnName = StoreTP.fields.PRICE_DISCOUNT_OFF)
	public double price_discount_off = 0;

	@BoField(caption = "Цена")
	@DatabaseField(columnName = StoreTP.fields.PRICE)
	public double price = 0;

	// @XmlElement(name = "count")
	@BoField(caption = "Количество")
	@DatabaseField(columnName = StoreTP.fields.COUNT)
	public double count = 0;

	@XmlElement(name = "cost")
	@BoField(caption = "Сумма")
	@DatabaseField(columnName = StoreTP.fields.SUM)
	public double sum = 0;

	// @XmlPath("vat/rate/text()")
	@BoField(caption = "Ставка НДС")
	@DatabaseField(columnName = StoreTP.fields.RATE_VAT)
	// TODO:Settings.defaultVat;
	public double rateVat = 20;

	// @XmlPath("vat/summaVat/text()")
	@BoField(caption = "Сумма НДС")
	@DatabaseField(columnName = StoreTP.fields.SUM_VAT)
	public double sumVat = 0;

	@XmlTransient
	@BoField(caption = "Наценка, %")
	@DatabaseField(columnName = StoreTP.fields.DISCOUNT)
	// TODO: rename
	public double discount = 0;

	@XmlElement(name = "costVat")
	@BoField(caption = "Всего")
	@DatabaseField(columnName = StoreTP.fields.TOTAL)
	public double total = 0;

	@XmlTransient
	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true)
	public LotOfProduct lotOfProduct;

	@XmlTransient
	@DatabaseField(columnName = StoreTP.fields.SUM_CONTAINS_VAT)
	public boolean sum_contains_vat = false;

	public boolean nalichka = false;

	public StoreTP() {
	}

	public StoreTP(Product product) {
		this.product = product;
	}

	public StoreTP setProduct(Product p) {
		this.product = p;
		return this;
	}

	@Override
	public void Calc(String fieldName) {
		System.out.println("Calc " + fieldName);

		// if ("discount".equals(fieldName)) {
		//
		// }
		if (StoreTP.fields.TOTAL.equals(fieldName)) {
			boolean roznichnNDS = isRetailVat();

			if (roznichnNDS)
				sumVat = Format.defaultRound(Format.roundDouble(total * rateVat / 100, 3));
			else
				// Сначала до 3 потом до 2х - хак
				sumVat = Format.defaultRound(Format.roundDouble(total * rateVat / (100 + rateVat), 3));

			if (sum_contains_vat)
				sum = total;
			else
				sum = total - sumVat;

			price = Format.defaultRound(sum / Count_());
			// if (nalichka)
			// price = Format.roundTo10Kop(price);

			if (price_discount_off == 0 && discount == 0) {
				// TODO: актуально для приходной. сделать при discount!= 0
				price_discount_off = price;
			} else {
				// discount = Format.roundDouble(((s / price_discount_off /
				// count -
				// 1) * 100), 10);
				// discount = Format.defaultRound((s / price_discount_off /
				// count -
				// 1) * 100);
				CalcDiscount(fieldName);
			}
		} else if (StoreTP.fields.PRICE.equals(fieldName)) {
			// TODO: вынести из базового класса
			if (IsZeroDiscount(this))
				price_discount_off = price;
			Calc("");
		} else if (StoreTP.fields.SUM_VAT.equals(fieldName)) {
			if (IsService(this) && IsInvoice(this)) {
				CalcTotal();
			}
		} else {
			double cost_price = price_discount_off;
			System.out.println("price_discount_off=" + price_discount_off);
			if (sum_contains_vat)
				cost_price = Format.roundDouble(price_discount_off * (rateVat + 100) / 100, 4);
			System.out.println("cost_price=" + cost_price);
			price = Format.defaultRound(cost_price * (1 + discount / 100));
			System.out.println("price=" + price);
			if (nalichka)
				price = Format.roundTo10Kop(price);
			CalcSum();
			CalcSumVat();
			CalcTotal();
		}
		// super.Calc();
	}

	private boolean IsZeroDiscount(StoreTP tp) {
		return IsInvoice(tp) || IsReturnFromCustomer(tp);
	}

	private boolean IsReturnFromCustomer(StoreTP tp) {
		return tp.getClass().getName().contains("ReturnFromCustomerTablePart");
	}

	public double Count_() {
		return IsService(this) && count == 0 ? 1 : count;
	}

	public static boolean IsInvoice(StoreTP tp) {
		return tp.getClass().getName().contains("InvoiceTablePart");
	}

	public static boolean IsService(StoreTP tp) {
		return tp.getClass().getName().contains("Service");
	}

	protected boolean isRetailVat() {
		double truncRateVat = Format.truncDouble(rateVat, 0);
		boolean retailVat = false;
		for (double r : new double[] { 9, 15, 16, 25 })
			if (r == truncRateVat) {
				retailVat = true;
				break;
			}
		return retailVat;
	}

	public void CalcDiscount(String fieldName) {
		if (IsInvoice(this))
			discount = 0;
		else {
			if (StoreTP.fields.TOTAL.equals(fieldName)) {
				discount = Format.roundDouble(((total / (Count_() * price_discount_off * (1 + rateVat / 100)) - 1) * 100), 2);
			} else {
				double p = (rateVat + 100) / 100 * price;
				// System.out.println(price_discount_off);
				// System.out.println(p);
				if (nalichka)
					p = Format.roundTo10Kop(p);
				System.out.println(p);
				discount = Format.roundDouble(((p / (price_discount_off * (rateVat + 100) / 100) - 1) * 100), 2);
			}
		}
		System.out.println("discount=" + discount);
	}

	public void CalcSum() {
		sum = Format.defaultRound(Count_() * price);
	}

	public void CalcSumVat() {
		if (sum_contains_vat)
			sumVat = Format.defaultRound(sum * rateVat / (100 + rateVat));
		else
			sumVat = Format.defaultRound(sum * rateVat / 100);
	}

	public double CalcTotal() {
		if (sum_contains_vat)
			total = Format.defaultRound(sum);
		else
			total = Format.defaultRound(sum + sumVat);
		return total;
	}

	// @Override
	// public void Save() throws Exception {
	// // CalcTotal();
	// super.Save();
	// }

	// public static void CopyTP(StoreDocBO fromDoc, StoreDocBO toDoc, StoreTP
	// toTp) {
	// CopyTP(fromDoc, toDoc, toTp, false);
	// }

	public static List[] AppendTP(StoreDocBO fromDoc, StoreDocBO toDoc, StoreTP toTpType, boolean add) {
		if (!add)
			toDoc.TablePartProduct.clear();
		// TODO: all TP types
		// for (Object fromTP : fromDoc.TablePartProduct)
		// {
		List intersection = new ArrayList<>();
		// except
		List newList = ListUtils.Except(fromDoc.TablePartProduct, toDoc.TablePartProduct, intersection, TPCompare);
		List intersection2 = new ArrayList<>();
		// То чего нет в источнике
		List deletedList = ListUtils.Except(toDoc.TablePartProduct, intersection, intersection2, TPCompare);

		Copy(toDoc, toTpType, newList);
		return new List[] { newList, deletedList, intersection };
	}

	/**
	 * Используется для ввода на основании (тупо добавляет)
	 * 
	 * @param fromDoc
	 * @param toDoc
	 * @param toTpType
	 */
	public static void AddTP(StoreDocBO fromDoc, StoreDocBO toDoc, StoreTP toTpType) {
		List newList = fromDoc.TablePartProduct;
		Copy(toDoc, toTpType, newList);
	}

	protected static void Copy(StoreDocBO toDoc, StoreTP toTpType, List newList) {
		for (Object fromTP : newList) {
			StoreTP newTp = (StoreTP) toTpType.cloneObject();
			((BO) fromTP).copyToObject(newTp);

			if (IsInvoice(newTp)) {
				newTp.meta = LotOfProduct.fields.FROM_LOT_ID + "=" + newTp.lotOfProduct.id;
				newTp.lotOfProduct = null;
			}
			// TODO: Временное решение. Чтобы нумеровалась ТЧ при вводе на
			// основании. Если надо будет копировать calcFields - доработать
			newTp.calcFields = new HashMap<>();

			toDoc.TablePartProduct.add(newTp);
		}
	}

	public static CompareT TPCompare = new CompareT<StoreTP>() {
		public boolean Do(StoreTP item1, StoreTP item2) {
			return (item1.product.id == item2.product.id && item1.count == item2.count && item1.price_discount_off == item2.price_discount_off);
		}
	};

	@Override
	public Object getCalcField(String name) {
		switch (name) {
		case StoreTP.fields.UNITS:
			if (product == null || product.units == null)
				return "";
			return "" + product.units;
		default:
			return super.getCalcField(name);
		}

	}

	public String toConsole() {
		return (product == null ? "" : product.name) + "|" + count + "|" + price_discount_off;
	}

	public double getCount() {
		return count;
	}

	public String group4LotKey() {
		return product.id + "-" + price_discount_off;
	}
}