package maxzawalo.c2.full.data.factory.document;

import java.util.Date;
import java.util.List;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.free.accounting.acc.Acc_41_1;
import maxzawalo.c2.free.accounting.acc.Acc_62_1_1;
import maxzawalo.c2.free.accounting.acc.Acc_68_2_1;
import maxzawalo.c2.free.accounting.acc.Acc_90_1_1;
import maxzawalo.c2.free.accounting.acc.Acc_90_2;
import maxzawalo.c2.free.accounting.acc.Acc_90_4;
import maxzawalo.c2.free.bo.registry.AccAcc;
import maxzawalo.c2.free.bo.store.StoreTP;
import maxzawalo.c2.free.data.factory.document.StoreDocFactory;
import maxzawalo.c2.full.bo.document.return_from_customer.ReturnFromCustomer;

public class ReturnFromCustomerFactory extends StoreDocFactory<ReturnFromCustomer> {

	@Override
	protected boolean ProductTransaction(ReturnFromCustomer doc) {
		return RegistryProductPlus(doc);
	}

	@Override
	protected boolean AccTransaction(ReturnFromCustomer doc) {
		try {
			for (StoreTP tp : (List<StoreTP>) doc.TablePartProduct) {
				// Себестоимость
				AccAcc acc_90_4 = new Acc_90_4();
				acc_90_4.toDebit(-tp.price_discount_off);
				AccAcc acc_41_1 = new Acc_41_1();
				acc_41_1.SubCount1 = tp.product;
				acc_41_1.SubCount2 = tp.lotOfProduct;
				acc_41_1.SubCount3 = doc.store;
				acc_41_1.toCredit(-tp.price_discount_off, -tp.count);
				AccRecord(doc, acc_90_4, acc_41_1);

				// Итого
				AccAcc acc_62_1_1 = new Acc_62_1_1();
				acc_62_1_1.SubCount1 = doc.contractor;
				acc_62_1_1.SubCount2 = doc.doc_contract;
				// typeSubCount3 = Отгрузка и оплата ТМЦ
				acc_62_1_1.toDebit(-tp.total);
				AccAcc acc_90_1_1 = new Acc_90_1_1();
				acc_90_1_1.toCredit(-tp.total);
				AccRecord(doc, acc_62_1_1, acc_90_1_1);

				// НДС
				AccAcc acc_90_2 = new Acc_90_2();
				acc_90_2.toDebit(-tp.sumVat);
				AccAcc acc_68_2_1 = new Acc_68_2_1();
				acc_68_2_1.toCredit(-tp.sumVat);
				AccRecord(doc, acc_90_2, acc_68_2_1);
			}
		} catch (Exception e) {
			log.ERROR("AccTransaction", e);
			Console.I().ERROR(getClass(), "AccTransaction", e.getMessage());
			return false;
		}
		return true;
	}

	public ReturnFromCustomer getByInDoc(Date in_form_date, String in_form_number) {
		try {
			QueryBuilder<ReturnFromCustomer, Integer> builder = getQueryBuilder();
			Where<ReturnFromCustomer, Integer> where = builder.where();
			NonDeletedFilter(where);
			where.and();
			where.eq(ReturnFromCustomer.fields.IN_FORM_DATE, in_form_date);
			where.and();
			where.eq(ReturnFromCustomer.fields.IN_FORM_NUMBER, in_form_number);
			return builder.queryForFirst();
		} catch (Exception e) {
			log.ERROR("getByInDoc", e);
		}
		return null;
	}
}