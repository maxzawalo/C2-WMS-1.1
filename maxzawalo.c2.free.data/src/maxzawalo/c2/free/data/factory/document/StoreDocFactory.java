package maxzawalo.c2.free.data.factory.document;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.CatalogueBO;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.bo.TablePartItem;
import maxzawalo.c2.base.bo.registry.RegType;
import maxzawalo.c2.base.data.DbHelper;
import maxzawalo.c2.base.data.GenericRowMapper;
import maxzawalo.c2.base.data.factory.DocumentFactory;
import maxzawalo.c2.base.data.factory.FactoryBO;
import maxzawalo.c2.base.data.factory.RegistryFactory;
import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.free.accounting.acc.Acc_41_1;
import maxzawalo.c2.free.accounting.acc.Acc_50_2;
import maxzawalo.c2.free.accounting.acc.Acc_62_1_1;
import maxzawalo.c2.free.accounting.acc.Acc_68_2_1;
import maxzawalo.c2.free.accounting.acc.Acc_90_1_1;
import maxzawalo.c2.free.accounting.acc.Acc_90_2;
import maxzawalo.c2.free.accounting.acc.Acc_90_4;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.bo.LotOfProduct;
import maxzawalo.c2.free.bo.registry.AccAcc;
import maxzawalo.c2.free.bo.registry.RegistryProduct;
import maxzawalo.c2.free.bo.store.StoreDocBO;
import maxzawalo.c2.free.bo.store.StoreMoveDocBO;
import maxzawalo.c2.free.bo.store.StoreTP;
import maxzawalo.c2.free.data.factory.catalogue.LotOfProductFactoryFree;
import maxzawalo.c2.free.data.factory.catalogue.StrictFormFactory;
import maxzawalo.c2.free.data.factory.catalogue.UnitsFactory;
import maxzawalo.c2.free.data.factory.registry.RegistryProductFactory;

public class StoreDocFactory<Doc> extends DocumentFactory<Doc> {

	public RegistryFactory registryProductFactory = new RegistryProductFactory();

	@Override
	public Doc Save(Doc bo) throws Exception {
		// CalcRowSum();
		((StoreDocBO) bo).CalcTotal();
		super.Save(bo);

		if (RegType.isOutDoc(((DocumentBO) bo).reg_type)) {
			// TODO: all TP
			for (Object tp : ((StoreDocBO) bo).TablePartProduct)
				if (((StoreTP) tp).discount <= 0) {
					Console.I().WARN(getClass(), "ProcessScanData", "НАЦЕНКА 0 ИЛИ МИНУС");
					break;
				}
		}
		return bo;
	}

	public void UpdateTotal(StoreDocBO doc) throws Exception {
		Dao<Doc, Integer> boDao = DbHelper.geDaos(typeBO);
		UpdateBuilder<Doc, Integer> builder = boDao.updateBuilder();
		builder.where().eq(BO.fields.ID, doc.id);
		builder.updateColumnValue(DocumentBO.fields.TOTAL, doc.total);
		builder.updateColumnValue(DocumentBO.fields.TOTAL_VAT, doc.totalVat);
		builder.update();
	}

	public List getTpDuplicates() {
		return getTpDuplicates(null);
	}

	public List getTpDuplicates(StoreDocBO doc) {
		try {
			if (doc == null)
				doc = (StoreDocBO) typeBO.newInstance();
			String tableName = new FactoryBO<>().Create(doc.itemProductT).getTableName();

			String sql = "SELECT tp.*\r\n" + " FROM " + tableName + " tp\r\n" + " where deleted = false\r\n"
					+ " group by tp.doc_id, tp.product_id, tp.price, tp.sum\r\n" + " having count(*) > 1;";
			Dao<?, Integer> lotDao = DbHelper.geDaos(doc.itemProductT);

			return lotDao.queryRaw(sql, GenericRowMapper.get(doc.itemProductT)).getResults();
		} catch (Exception e) {
			log.ERROR("getTpDuplicates", e);
		}

		return null;
	}

	@Override
	protected QueryBuilder<Doc, Integer> getBuilderWithFilter(String value) throws SQLException {
		QueryBuilder<Contractor, Integer> ccb = getQueryBuilderT(Contractor.class);
		QueryBuilder<Doc, Integer> builder = getQueryBuilder();
		Where<Doc, Integer> where = builder.where();
		if (!value.equals(""))
			builder.join(DocumentBO.fields.CONTRACTOR, BO.fields.ID, ccb);
		SynchronizationFilter(where);
		if (!value.equals(""))
			CreateMultipleLike(CatalogueBO.fields.NAME, value, ccb.where());
		return builder;
	}

	@Override
	public List<TablePartItem> LoadTP(DocumentBO doc, Class itemT) {
		// Не все типы ТЧ есть у док-в
		if (itemT == null)
			return new ArrayList<>();

		List<TablePartItem> allTP = super.LoadTP(doc, itemT);
		// Подгружаем Level 3
		// TODO: Core
		for (TablePartItem tpi : allTP) {
			StoreTP tp = (StoreTP) tpi;
			if (tp.product != null && tp.product.units != null)
				tp.product.units = new UnitsFactory().GetById(tp.product.units.id);
		}
		return allTP;
	}

	// === StrictForm
	public void LoadStrictForm(StoreDocBO doc) {
		// StrictForm form = new StrictForm();
		// form.enableDeletedFilter = false;
		StrictFormFactory factory = new StrictFormFactory();
		factory.enableDeletedFilter = false;
		doc.strictForms = factory.SelectDocEntries(doc);
		// doc.calcFields.put("strictForms", strictForms)
	}

	@Override
	public Doc GetById(int id, int level, boolean fromCache) {
		Doc doc = super.GetById(id, level, fromCache);
		LoadStrictForm((StoreDocBO) doc);
		return doc;
	}

	@Override
	public Doc GetById(int id) {
		Doc doc = super.GetById(id);
		LoadStrictForm((StoreDocBO) doc);
		return doc;
	}

	protected LotOfProductFactoryFree CreateLotFactory() {
		return new LotOfProductFactoryFree();
	}

	protected boolean RegistryProductPlus(StoreDocBO doc) {
		try {
			List<RegistryProduct> entries = new ArrayList<>();
			RegistryProduct r = doc.getRegistryProductFromUsed();
			SetStore(doc, r);
			List<LotOfProduct> lots = CreateLotFactory().CreateLotOfProduct(doc, DoUpdateLot(doc));
			int pos = 0;
			for (Object tpl : doc.TablePartProduct) {
				StoreTP tp = (StoreTP) tpl;
				LotOfProduct lot = lots.get(pos);
				RegistryProduct item = r.cloneBO();
				item.store = r.store;
				item.contractor = doc.contractor;

				item.product = tp.product;
				item.lotOfProduct = lot;
				// // Для сортировки SumGroupBalace
				// item.lotOfProduct.doc_date = doc;
				item.count = tp.count;
				// if(tp.product.toString().contains("Палец рулевой в сб.МАЗ-4370
				// -3003065,РФ"))
				// {
				// System.out.println("");
				// }
				// item.cost_price = lot.cost_price;
				item.price = lot.price;
				entries.add(item);
				pos++;
			}
			return registryProductFactory.PlusEntries(r, entries);
		} catch (Exception e) {
			log.ERROR("RegistryProductPlus", e);
		}

		return false;
	}

	protected void SetStore(StoreDocBO doc, RegistryProduct r) {
		// В складском перемещении плюсуем на склад to_store
		if (doc instanceof StoreMoveDocBO)
			r.store = ((StoreMoveDocBO) doc).to_store;
		else
			r.store = doc.store;
	}

	protected boolean RegistryProductMinus(StoreDocBO doc) {
		RegistryProduct r = doc.getRegistryProductFromUsed();
		r.store = doc.store;
		r.contractor = doc.contractor;

		return ((RegistryProductFactory) registryProductFactory).MinusByLotWithBalance(r, doc.TablePartProduct, false,
				doc.DocDate);
	}

	protected boolean DoUpdateLot(StoreDocBO doc) {
		if (RegType.isInDoc(doc.reg_type))
			return doc.updateLot;
		return false;
	}

	/**
	 * Расход. Для Расходной, Чеков
	 * 
	 * @param doc
	 * @param tp
	 * @param cash
	 * @param serviceTP
	 * @throws Exception
	 */
	protected void AccProductExpenseTransaction(StoreDocBO doc, StoreTP tp, boolean cash, boolean serviceTP)
			throws Exception {
		// Для Товарной ТЧ
		if (!serviceTP) {
			// Себестоимость
			AccAcc acc_90_4 = new Acc_90_4();
			acc_90_4.toDebit(tp.price_discount_off);

			AccAcc acc_41_1 = new Acc_41_1();
			acc_41_1.SubCount1 = tp.product;
			acc_41_1.SubCount2 = tp.lotOfProduct;
			acc_41_1.SubCount3 = doc.store;
			acc_41_1.toCredit(tp.price_discount_off, tp.count);

			AccRecord(doc, acc_90_4, acc_41_1);
		}
		// ----------------------------------------
		// Всего
		AccAcc totalDt = null;
		if (cash) {
			totalDt = new Acc_50_2();
			// typeSubCount1 = Кассы организации
			totalDt.toDebit(tp.total);
		} else {
			totalDt = new Acc_62_1_1();
			totalDt.SubCount1 = doc.contractor;
			totalDt.SubCount2 = doc.doc_contract;
			// typeSubCount3 = Отгрузка и оплата ТМЦ
			totalDt.toDebit(tp.total);
		}

		AccAcc totalKt = new Acc_90_1_1();
		// TODO: надо куда то вставить tp.product
		// Виды деятельности
		// Виды вариантов реализации
		// Ставки НДС
		totalKt.toCredit(tp.total);

		AccRecord(doc, totalDt, totalKt);
		// ----------------------------------------
		// НДС
		AccAcc acc_90_2 = new Acc_90_2();
		acc_90_2.toDebit(tp.sumVat);

		AccAcc acc_68_2_1 = new Acc_68_2_1();
		acc_68_2_1.toCredit(tp.sumVat);

		AccRecord(doc, acc_90_2, acc_68_2_1);
	}
}