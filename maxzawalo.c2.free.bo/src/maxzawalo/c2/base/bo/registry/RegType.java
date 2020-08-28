package maxzawalo.c2.base.bo.registry;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.utils.Logger;

public class RegType {

	// TODO: вынести ? русифицировать
	@BoField(caption = "Приходная")
	public static final int Invoice = 1;
	public static final int DeliveryNote = 2;
	public static final int Bill = 3;
	public static final int Order = 4;
	public static final int RemainingStock = 5;
	public static final int CashVoucher = 6;
	public static final int ReturnOfGoods = 7;
	public static final int WriteOffProduct = 8;
	public static final int ReturnFromCustomer = 10;
	public static final int Warrant4Receipt = 11;

	public static final int FromMarketToRent = 12;
	public static final int FromRentToContractor = 13;
	public static final int FromContractorToRent = 14;

	public static final int ReceiptMoney = 100;
	public static final int WriteOffMoney = 101;

	public static final int CashReceiptVoucher = 102;
	public static final int CashPaymentVoucher = 103;

	/**
	 * Приходные документы
	 * 
	 * @param reg_type
	 * @return
	 */
	public static boolean isInDoc(int reg_type) {
		return (reg_type == RegType.RemainingStock || reg_type == RegType.Invoice || reg_type == RegType.ReturnFromCustomer || reg_type == RegType.ReceiptMoney
				|| reg_type == RegType.FromMarketToRent);
	}

	/**
	 * Документы с учетными БСО
	 * 
	 * @param reg_type
	 * @return
	 */
	public static boolean isStrictFromDoc(int reg_type) {
		return (reg_type == RegType.DeliveryNote || reg_type == RegType.ReturnOfGoods);
	}

	public static boolean isOutDoc(int reg_type) {
		return (reg_type == RegType.DeliveryNote || reg_type == RegType.ReturnOfGoods || reg_type == RegType.Bill || reg_type == RegType.CashVoucher);
	}

	public static String ToText(int type) {
		switch (type) {
		case Invoice:
			return "Приходная";
		case DeliveryNote:
			return "Расходная";
		case Bill:
			return "Счет";
		case Order:
			return "Заказ";
		case RemainingStock:
			return "Оприходование";
		case CashVoucher:
			return "Чеки";
		case ReturnOfGoods:
			return "Возврат поставщику";
		case WriteOffProduct:
			return "Списание товаров";
		case ReturnFromCustomer:
			return "Возврат от покупателя";
		case Warrant4Receipt:
			return "Доверенность";
		case ReceiptMoney:
			return "Поступление на расчетный счет";
		case WriteOffMoney:
			return "Списание с расчетного счета";
		case CashReceiptVoucher:
			return "Приходно кассовый ордер";
		case CashPaymentVoucher:
			return "Расходно кассовый ордер";
		default:
			return "";
		}
	}

	public static String GetDocClass(int type) {
		switch (type) {
		case Invoice:
			return "Invoice";
		case DeliveryNote:
			return "DeliveryNote";
		case Bill:
			return "Bill";
		case Order:
			return "Order";
		case RemainingStock:
			return "RemainingStock";
		case CashVoucher:
			return "CashVoucher";
		case ReturnOfGoods:
			return "ReturnOfGoods";
		case WriteOffProduct:
			return "WriteOffProduct";
		case ReturnFromCustomer:
			return "ReturnFromCustomer";
		case Warrant4Receipt:
			return "Warrant4Receipt";
		case ReceiptMoney:
			return "ReceiptMoney";
		case WriteOffMoney:
			return "WriteOffMoney";
		case CashReceiptVoucher:
			return "CashReceiptVoucher";
		case CashPaymentVoucher:
			return "CashPaymentVoucher";
		default:
			return "";
		}
	}

	public static List<Field> getStatics(Class<?> clazz) {
		return Arrays.stream(clazz.getDeclaredFields()).filter(f -> Modifier.isStatic(f.getModifiers())).collect(Collectors.toList());
	}

	public static boolean Check() {
		boolean retVal = true;
		List<Integer> ids = new ArrayList<>();
		for (Field f : getStatics(RegType.class)) {
			try {
				ids.add((Integer) f.get(null));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (ids.size() > new ArrayList(new HashSet(ids)).size())
			retVal = false;

		return retVal;
	}
}