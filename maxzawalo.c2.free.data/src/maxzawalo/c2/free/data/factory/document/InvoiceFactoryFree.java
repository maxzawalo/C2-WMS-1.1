package maxzawalo.c2.free.data.factory.document;

import java.util.Date;
import java.util.List;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.free.accounting.acc.Acc_18_3;
import maxzawalo.c2.free.accounting.acc.Acc_41_1;
import maxzawalo.c2.free.accounting.acc.Acc_44_2;
import maxzawalo.c2.free.accounting.acc.Acc_60_1_1;
import maxzawalo.c2.free.bo.document.invoice.Invoice;
import maxzawalo.c2.free.bo.registry.AccAcc;
import maxzawalo.c2.free.bo.store.StoreTP;

public class InvoiceFactoryFree extends StoreDocFactory<Invoice> {

	public Invoice getByInDoc(Date in_form_date, String in_form_number) {
		try {
			QueryBuilder<Invoice, Integer> builder = getQueryBuilder();
			Where<Invoice, Integer> where = builder.where();
			SynchronizationFilter(where);
			where.and();
			// Не берем удаленные
			where.eq(BO.fields.DELETED, false);
			where.and();
			where.eq(Invoice.fields.IN_FORM_DATE, in_form_date);
			where.and();
			where.eq(Invoice.fields.IN_FORM_NUMBER, in_form_number);
			System.out.println(builder.prepareStatementString());
			return builder.queryForFirst();
		} catch (Exception e) {
			log.ERROR("getByInDoc", e);
		}
		return null;
	}

	@Override
	protected boolean ProductTransaction(Invoice doc) {
		return RegistryProductPlus(doc);
	}

	@Override
	protected boolean AccTransaction(Invoice doc) {
		try {
			for (StoreTP tp : (List<StoreTP>) doc.TablePartProduct) {
				AccAcc acc_41_1 = new Acc_41_1();
				acc_41_1.SubCount1 = tp.product;
				acc_41_1.SubCount2 = tp.lotOfProduct;
				acc_41_1.SubCount3 = doc.store;
				acc_41_1.toDebit(tp.sum, tp.count);

				AccAcc acc_60_1_1 = new Acc_60_1_1();
				acc_60_1_1.SubCount1 = doc.contractor;
				acc_60_1_1.SubCount2 = doc.doc_contract;
				acc_60_1_1.toCredit(tp.sum);

				AccRecord(doc, acc_41_1, acc_60_1_1);

				// ----------------------------------------
				AccAcc acc_18_3 = new Acc_18_3();
				acc_18_3.SubCount1 = doc.contractor;
				// typeSubCount2 = Поступление и оплата ТМЦ
				acc_18_3.SubCount3 = tp.product;
				acc_18_3.toDebit(tp.sumVat);

				acc_60_1_1 = new Acc_60_1_1();
				acc_60_1_1.SubCount1 = doc.contractor;
				acc_60_1_1.SubCount2 = doc.doc_contract;
				// typeSubCount3 = Поступление и оплата ТМЦ
				acc_60_1_1.toCredit(tp.sumVat);

				AccRecord(doc, acc_18_3, acc_60_1_1);
			}

			for (StoreTP tp : (List<StoreTP>) doc.TablePartService) {
				// 44.2 - 60.1
				// 44.2 - 76,1(нужен для учета белгосстраха)
				// поставщики товара на 60.1, а услуг на 76.1 В ИП так проще разделять
				// телефон, письма, БСО, канцелярия, бух услуги
				// НДС 18,3 - 76,1
				AccAcc acc_44_2 = new Acc_44_2();
				// acc_44_2.SubCount1 = tp.product;
				// acc_44_2.SubCount2 = tp.lotOfProduct;
				// acc_44_2.SubCount3 = doc.store;
				acc_44_2.toDebit(tp.sum);

				AccAcc acc_60_1_1 = new Acc_60_1_1();
				acc_60_1_1.SubCount1 = doc.contractor;
				acc_60_1_1.SubCount2 = doc.doc_contract;
				acc_60_1_1.toCredit(tp.sum);

				AccRecord(doc, acc_44_2, acc_60_1_1);
				// ----------------------------------------
				AccAcc acc_18_3 = new Acc_18_3();
				acc_18_3.SubCount1 = doc.contractor;
				// typeSubCount2 = Поступление и оплата ТМЦ
				acc_18_3.SubCount3 = tp.product;
				acc_18_3.toDebit(tp.sumVat);

				acc_60_1_1 = new Acc_60_1_1();
				acc_60_1_1.SubCount1 = doc.contractor;
				acc_60_1_1.SubCount2 = doc.doc_contract;
				// typeSubCount3 = Поступление и оплата ТМЦ
				acc_60_1_1.toCredit(tp.sumVat);

				AccRecord(doc, acc_18_3, acc_60_1_1);
			}
		} catch (Exception e) {
			log.ERROR("AccTransaction", e);
			Console.I().ERROR(getClass(), "AccTransaction", e.getMessage());
			return false;
		}

		return true;
	}

	@Override
	protected Invoice BeforeSave(Invoice bo) throws Exception {
		// Налоговая стала "драть" за строчные
		bo.in_form_number = bo.in_form_number.toUpperCase().trim();
		return super.BeforeSave(bo);
	}
}