package maxzawalo.c2.free.data.factory.catalogue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import maxzawalo.c2.base.Actions;
import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.CatalogueBO;
import maxzawalo.c2.base.bo.registry.RegType;
import maxzawalo.c2.base.data.DbHelper;
import maxzawalo.c2.base.data.factory.CatalogueFactory;
import maxzawalo.c2.base.data.factory.DocumentFactory;
import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.base.utils.Global;
import maxzawalo.c2.base.utils.Profiler;
import maxzawalo.c2.free.bo.LotOfProduct;
import maxzawalo.c2.free.bo.Price;
import maxzawalo.c2.free.bo.PriceState;
import maxzawalo.c2.free.bo.Product;
import maxzawalo.c2.free.bo.Settings;
import maxzawalo.c2.free.bo.Store;
import maxzawalo.c2.free.bo.document.invoice.Invoice;
import maxzawalo.c2.free.bo.store.StoreDocBO;
import maxzawalo.c2.free.bo.store.StoreTP;
import maxzawalo.c2.free.cache.Cache;

public class LotOfProductFactoryFree extends CatalogueFactory<LotOfProduct> {
	// Полчаем объект, а не его наличие - возможно далее будут обновлятся поля
	public LotOfProduct getExists(StoreDocBO doc, Product product, double cost_price) {
		if (Global.InMemoryGroupTransaction) {
			List<LotOfProduct> lots = Cache.I().getList(LotOfProduct.InMemoryGroupTransactionKey);
			for (LotOfProduct l : lots)
				if (l.doc.id == doc.id && l.doc_type == doc.reg_type && l.product.id == product.id
						&& l.cost_price == cost_price)
					return l;
		} else {
			// TODO: all loong cache - практически не меняется
			try {
				QueryBuilder<LotOfProduct, Integer> builder = getQueryBuilder();
				Where<LotOfProduct, Integer> where = builder.where();
				NonDeletedFilter(where);
				where.and();
				where.eq(LotOfProduct.fields.DOC, doc.id);
				where.and();
				where.eq(LotOfProduct.fields.DOC_TYPE, doc.reg_type);
				where.and();
				if (product == null)
					log.ERROR("getExists", "product == null");
				where.eq(LotOfProduct.fields.PRODUCT, product.id);
				where.and();
				where.eq(LotOfProduct.fields.COST_PRICE, cost_price);
				return builder.queryForFirst();
			} catch (Exception e) {
				log.ERROR("getExists", e);
			}
		}
		return null;
	}

	public List<LotOfProduct> getByDoc(StoreDocBO doc, Product product) {
		List<LotOfProduct> list = new ArrayList<>();
		if (Global.InMemoryGroupTransaction) {
			List<LotOfProduct> lots = Cache.I().getList(LotOfProduct.InMemoryGroupTransactionKey);
			for (LotOfProduct l : lots)
				if (l.doc.id == doc.id && l.doc_type == doc.reg_type && l.product.id == product.id)
					list.add(l);
		} else {
			// TODO: all loong cache - практически не меняется
			try {
				QueryBuilder<LotOfProduct, Integer> builder = getQueryBuilder();
				Where<LotOfProduct, Integer> where = builder.where();
				NonDeletedFilter(where);
				where.and();
				where.eq(LotOfProduct.fields.DOC, doc.id);
				where.and();
				where.eq(LotOfProduct.fields.DOC_TYPE, doc.reg_type);
				where.and();
				if (product == null)
					log.ERROR("getByDoc", "product == null");
				where.eq(LotOfProduct.fields.PRODUCT, product.id);
				// where.and();
				// where.eq(LotOfProduct.fields.COST_PRICE, cost_price);
				// builder.orderBy(BO.fields.ID, true);
				list = builder.query();
			} catch (Exception e) {
				log.ERROR("getByDoc", e);
			}
		}
		// Сортируем - далее может быть обрезание партий по кол-ву из ТЧ
		list.sort((p1, p2) -> ((Integer) p1.id).compareTo((Integer) p2.id));

		return list;
	}

	// public int getCount(StoreDocBO doc, Product product) {
	// int count = 0;
	// if (Global.InMemoryGroupTransaction) {
	// List<LotOfProduct> lots =
	// Cache.I().getList(LotOfProduct.InMemoryGroupTransactionKey);
	// for (LotOfProduct l : lots)
	// if (l.doc.id == doc.id && l.doc_type == doc.reg_type && l.product.id ==
	// product.id)
	// count++;
	// } else {
	// // TODO: all loong cache - практически не меняется
	// try {
	// QueryBuilder<LotOfProduct, Integer> builder = getQueryBuilder();
	// Where<LotOfProduct, Integer> where = builder.where();
	// NonDeletedFilter(where);
	// where.and();
	// where.eq(LotOfProduct.fields.DOC, doc.id);
	// where.and();
	// where.eq(LotOfProduct.fields.DOC_TYPE, doc.reg_type);
	// where.and();
	// if (product == null)
	// log.ERROR("getCount", "product == null");
	// where.eq(LotOfProduct.fields.PRODUCT, product.id);
	// builder.setCountOf(true);
	// count = (int) builder.countOf();
	// } catch (Exception e) {
	// log.ERROR("getCount", e);
	// }
	// }
	// return count;
	// }

	// @Override
	public List<LotOfProduct> GetPageFiltered(CatalogueBO catalogue, Date docDate, long currentPage, long pageSize,
			CatalogueBO parent, String searchData, boolean showZeroBalance, boolean showZeroLot, Store store,
			boolean selectChildGroups) {
		// TODO:currentPage, long pageSize in key
		String cacheKey = "LotOfProduct.GetPageFiltered." + docDate.getTime() + "_" + currentPage +
		// "_" + pageSize +
				"_" + ((parent == null) ? 0 : parent.id) + "_" + searchData + "_" + showZeroBalance + "_" + showZeroLot
				+ "_" + ((store == null) ? 0 : store.id) + "_" + selectChildGroups;

		Profiler profiler = new Profiler();
		profiler.Start(cacheKey);

		Dao<LotOfProduct, Integer> lotDao = DbHelper.geDaos(typeBO);
		List<LotOfProduct> list = new ArrayList<>();

		String sql = "(select";
		sql += " IFNULL(reg.lotOfProduct_id, 0) as `id`,";
		sql += " reg.reserve,";
		sql += " IFNULL(reg.product_id, p.id) as `product_id`,";
		// TODO: reg_type switch(invoice etc)
		// TODO: reg_id test
		sql += " IFNULL(lot.doc_id, 0) as `doc_id`,";
		sql += " IFNULL(lot.doc_type, 0) as `doc_type`,";
		sql += " IFNULL(lot.cost_price, 0) as `cost_price`,";
		sql += " IFNULL(lot.price_bo_id, 0) as `price_bo_id`,";
		sql += " IFNULL(reg.price, 0) as `price`,";
		sql += " IFNULL(reg.count, 0) as `count`,";
		sql += " IFNULL(reg.reg_date, 0) as `reg_date`,";
		sql += " IFNULL(p.`" + CatalogueBO.fields.NAME + "`, '') as `product_name`";
		// sql += " IFNULL(reg.code, '') as `code`";
		sql += " from `registry_product` reg";
		// sql += ((showZeroBalance) ? " right " : "");
		sql += " join (select * from product where `sync_flag` = 0 and is_group = false AND deleted=false "
				+ ItemFilter(catalogue, parent, selectChildGroups, searchData) + " LIMIT " + currentPage * pageSize
				+ "," + pageSize + ") p ON reg.`product_id` = p.`id` ";
		sql += " join lotofproduct lot ON reg.lotOfProduct_id = lot.id";
		sql += " WHERE reg.deleted=false AND lot.deleted=false ";

		// берем все проводки <= конца дня док-та
		sql += " AND reg.reg_date<=" + Format.endOfDay(docDate).getTime();
		if (store != null)
			sql += " AND reg.store_id=" + store.id;
		sql += ")";
		if (showZeroLot) {
			sql += " union all ";
			sql += " (select 0 as `id`, 0 as reserve, id as `product_id`,0 as `doc_id`, 1 as `doc_type`,0 as `cost_price`,";
			sql += " 0 as `price_bo_id`,0 as `price`,0 as `count`,0 as `reg_date`,`" + CatalogueBO.fields.NAME
					+ "` as `product_name`";
			sql += " from product ";
			sql += " where `sync_flag` = 0 and is_group = false AND deleted=false "
					+ ItemFilter(catalogue, parent, selectChildGroups, searchData);
			sql += " and id not in (select product_id from registry_product) group by `" + CatalogueBO.fields.NAME
					+ "` " + " LIMIT " + currentPage * pageSize + "," + pageSize + ")";
		}
		sql += " ORDER BY product_name, id, reg_date";// product_id

		// TODO: filter
		try {
			list = Cache.I().getList(cacheKey);
			System.out.println("LotOfProduct.GetPageFiltered |" + sql);
			if (list == null) {
				list = new ArrayList<>();
				mapper.level = 0;// GenericRowMapper.get(LotOfProduct.class)
				list = lotDao.queryRaw(sql, mapper).getResults();

				// Только что введенные позиции в Номенклатуру
				// int lot_id = -10;
				for (LotOfProduct lot : list)
					if (lot.id == 0)
						// если из разных групп/запросов - дублируются. поэтому
						// не lot.id = lot_id--, а hash ;
						lot.id = -Math.abs((lot.product.id + lot.product.name).hashCode());

				// Подгружаем документы и ед.изм.
				ProductFactory pf = new ProductFactory();
				for (LotOfProduct lot : list) {
					String value = (String) lot.calcFields.get(LotOfProduct.fields.GROUP);
					if (value == null) {
						value = "";
						Product parent1 = pf.getParent(lot.product);
						if (parent1 != null) {
							Product parent2 = pf.getParent(parent1);
							value = parent1.name;
							if (parent2 != null)
								value = parent2.name + "|" + value;
						}
						lot.calcFields.put(LotOfProduct.fields.GROUP, value);
					}

					lot.doc = getDoc(lot);// FactoryByRegTypeFree.Create(lot.doc_type).GetById(lot.doc.id);
					if (lot.product.units != null)
						lot.product.units = new UnitsFactory().GetById(lot.product.units.id);
					lot.fuzzy = (catalogue.fuzzy_ids.size() != 0 && catalogue.fuzzy_ids.contains(lot.product.id));
				}
				// Агрегация
				list = SumGroupBalace(list);
				Cache.I().putList(cacheKey, list, 20);

				for (LotOfProduct lot : list)
					if (lot.product == null)
						log.ERROR("GetPageFiltered", "lot.product == null");
					else if (lot.product.id == 0)
						log.ERROR("GetPageFiltered", "lot.product.id == 0");
			}

			// for (LotOfProduct lot : list)
			// log.DEBUG("" + lot);

			profiler.Stop(cacheKey);
		} catch (Exception e) {
			profiler.Stop(cacheKey);
			log.ERROR("GetPageFiltered", e);
		}

		profiler.PrintElapsed(cacheKey);

		// for (LotOfProduct lot : list)
		// lot.fuzzy = (this.fuzzy_ids.size() != 0 &&
		// this.fuzzy_ids.contains(lot.product.id));

		return list;
	}

	public static List<LotOfProduct> SumGroupBalace(List<LotOfProduct> list) {

		// TODO: сортировка тут по id - reg_date, а не на Mysql
		// Collections.sort(list, new RegistryProductComparator());

		List<LotOfProduct> sumList = new ArrayList<LotOfProduct>();

		// Измерения для группировки
		int lot = -1;
		boolean reserve = false;
		//
		double sumCount = 0;
		LotOfProduct current = null;
		for (LotOfProduct l : list) {
			// System.out.println(l.product.id + "|" + l.lotOfProduct.id);
			if ((lot != l.id) || reserve != l.reserve) {
				reserve = l.reserve;
				lot = l.id;
				current = l;
				sumList.add(current);
				sumCount = 0;
			} else {
				current.count += l.count;
				current.count = Format.countRound(current.count);
			}
		}

		// Если тут очищаем список - обнуляется в кэше
		// list.clear();

		return sumList;
	}

	// public List<LotOfProduct> CreateLotOfProduct(StoreDocBO doc) throws
	// Exception {
	// return CreateLotOfProduct(doc, false);
	// }

	public List<LotOfProduct> CreateLotOfProduct(StoreDocBO doc, boolean updateLot) throws Exception {
		// в созданом на основании док-те партии не обновляем
		// if (doc.meta.contains(DocumentBO.fields.FROM_SOURCE_DOC))
		// if (doc.source_doc_type != 0 && doc.source_doc_id != 0)
		// updateLot = false;

		// double k = 1;
		// if (!doc_currency.equals(Settings.mainCurrency))
		// k = 30.5445;
		boolean hasNewLot = false;

		// TODO: check exists - full chain
		List<LotOfProduct> allLot = new ArrayList<>();
		List<StoreTP> grouped = new ArrayList<>();
		for (StoreTP tp : (List<StoreTP>) doc.TablePartProduct) {
			// Клонируем чтобы не затереть кол-во в реальной ТЧ
			StoreTP tpl = (StoreTP) tp.cloneObject();
			tpl.count = 0;
			((List<StoreTP>) doc.TablePartProduct).stream()
					.filter(t -> t.product.id == tpl.product.id && t.price_discount_off == tpl.price_discount_off)
					.forEach(item -> {
						tpl.count += item.count;
					});

			tpl.count = Format.countRound(tpl.count);
			grouped.add(tpl);
		}

		// Map<String, List<RegistryProduct>> groupBalance =
		// newList.parallelStream()
		// .collect(Collectors.groupingBy(p -> p.dimensionGroupingByKey(),
		// Collectors.mapping((RegistryProduct p) -> p, Collectors.toList())));
		//
		// for (String key : groupBalance.keySet()) {
		// List<RegistryProduct> group = groupBalance.get(key);
		// RegistryProduct rp = group.get(0);
		// rp.count =
		// Format.countRound(group.parallelStream().collect(Collectors.summingDouble(RegistryProduct::getCount)));
		// sumList.add(rp);
		// }

		// Группируем ТЧ по product,cost_price sum(count)
		for (StoreTP tp : grouped) {
			double priceNormByCurrency = GetPriceTP(tp);
			if (!doc.doc_currency.code.equals(Settings.mainCurrency.code))
				priceNormByCurrency = new CurrencyFactory().Convert(doc.doc_currency.name, doc.DocDate,
						priceNormByCurrency);
			hasNewLot |= CheckAndCreateLot(doc, updateLot, allLot, tp, priceNormByCurrency);
		}

		// При групповом проведении партии не обновляются, поэтому updateLot не
		// учитываем
		assert !(Global.InMemoryGroupTransaction && hasNewLot);
		if (!Global.InMemoryGroupTransaction && hasNewLot)// было ||
			BulkSave(allLot);
		return allLot;
	}

	protected boolean CheckAndCreateLot(StoreDocBO doc, boolean updateLot, List<LotOfProduct> allLot, StoreTP tp,
			double priceNormByCurrency) throws Exception {
		LotOfProduct lot = new LotOfProductFactoryFree().getExists(doc, tp.product, priceNormByCurrency);

		if (lot == null) {
			// Партии сохраняются пачкой и если есть одинаковые Товары в разных
			// строках - ищем в List<LotOfProduct> allLot
			for (LotOfProduct l : allLot)
				if (l.product.id == tp.product.id && l.cost_price == priceNormByCurrency) {
					lot = l;
					break;
				}
		}

		boolean hasNewLot = false;
		if (lot == null || updateLot) {
			if (lot == null)// || !updateLot)
			{
				lot = new LotOfProduct();
				Console.I().INFO(getClass(), "CheckAndCreateLot",
						"Создана новая партия|" + tp.product.name + "|" + tp.count);
				hasNewLot = true;
			}
			lot.doc = doc;
			lot.doc_type = doc.reg_type;
			lot.product = tp.product;

			if (lot.doc instanceof Invoice) {
				// TODO: RemainingStock RegType.isInDoc(doc.reg_type)
				Price priceBo = new Price();
				if (Settings.c2_price_gen) {
					// TODO: Складское перемещение - лепим старый ценник
					// чтобы не плодить newTp.meta =
					// LotOfProduct.fields.FROM_LOT_ID + "=" +
					// newTp.lotOfProduct.id;
					if (updateLot) {
						// Ищем уже сформированный ценник
						priceBo = new PriceFactory().Get(((Invoice) lot.doc), lot.product, priceNormByCurrency);
						// Не нашли - создаем новый
						if (priceBo == null)
							priceBo = new Price();
						else
							// Нашли - меняем статус - чтобы напечатать
							priceBo.price_state = PriceState.Новый;
					}

					// TODO: if (doc instanceof RemainingStock)
					// Как быть с ценниками при разбивке комплекта на
					// штуки
					priceBo.invoice = (Invoice) lot.doc;
					priceBo.product = lot.product;

					// TODO: Не менять цену?
					// if
					// (!tp.meta.contains(LotOfProduct.fields.FROM_LOT_ID))
					{
						priceBo.price = CalcAddition(lot, priceNormByCurrency);
						priceBo.total = Format.roundTo10Kop(priceBo.price * (100 + tp.rateVat) / 100);
					}

					if (tp.meta != null && tp.meta.contains(LotOfProduct.fields.FROM_LOT_ID) && !updateLot) {
						// Чтобы не печатались ценники после складского
						// перемещения
						priceBo.price_state = PriceState.Напечатан;
					}
					new PriceFactory().Save(priceBo);//TODO: bulk
				} else
					priceBo = new PriceFactory().Get(((Invoice) lot.doc), lot.product, priceNormByCurrency);

				lot.price_bo = priceBo;
			}
			lot.cost_price = priceNormByCurrency;
			// TODO: перебиваем цену, если наценка сменится - как быть?
			// - в
			// новой нет, а если при перепроведении? - удаляются только
			// вхождения регистров - Партии остануться, если не
			// трогать!!!
			// TODO: ? total/count
			if (lot.price_bo == null) {
				if (WithoutAddition(doc))
					lot.price = priceNormByCurrency;
				else
					lot.price = CalcAddition(lot, priceNormByCurrency);
			} else {
				// log.DEBUG("CreateLotOfProduct", "Ценник найден");
				lot.price = lot.price_bo.price;
			}
			lot.count = tp.count;
		} else {
			log.DEBUG("CreateLotOfProduct", "Партия существет. " + lot);
			// TODO: lot.count = tp.count;
			// lot.Save();
		}
		allLot.add(lot);
		return hasNewLot;
	}

	protected double GetPriceTP(StoreTP tp) {
		return tp.price;
	}

	protected boolean WithoutAddition(StoreDocBO doc) {
		if (doc.reg_type == RegType.RemainingStock)// || doc.reg_type ==
													// RegType.ReturnFromCustomer)
			return true;
		return false;
	}

	protected double CalcAddition(LotOfProduct lot, double priceNormByCurrency) {
		return (double) Actions.CalcAdditionAction.Do(lot.product, priceNormByCurrency, lot.getDelivery());
		// return new TradeAdditionFactory().CalcAddition(lot.product,
		// priceNormByCurrency, lot.getDelivery())
		// return priceNormByCurrency;
	}

	@Override
	protected LotOfProduct GenerateCode(LotOfProduct lot) throws Exception {
		return lot;
	}

	public StoreDocBO getDoc(LotOfProduct lot) {
		// Кэширование надо для Группового проведения - загрузки партий
		// TODO: сделать вообще кэш Документов (heating)
		String key = "StoreDocBO_" + lot.doc_type + "_" + lot.doc.id;
		StoreDocBO doc = (StoreDocBO) Cache.I().get(key);
		if (doc == null) {
			doc = (StoreDocBO) ((DocumentFactory) Actions.FactoryByRegTypeAction.Do(lot.doc_type)).GetById(lot.doc.id);
			Cache.I().put(key, doc, 60);
		}
		return doc;
	}

	protected static <T> void ParentFilter(Where<T, Integer> where, BO parent) throws SQLException {
		// Фильтруем только элементы.Группы
		where.eq(CatalogueBO.fields.IS_GROUP, false);
		// выдаются в дерево сразу все.
		where.and();
		if (parent != null)
			where.eq(CatalogueBO.fields.PARENT, parent.id);
		else
			where.isNull(CatalogueBO.fields.PARENT);
	}

	// Группируем по партиям
	public static boolean addGrouped(List<LotOfProduct> list, LotOfProduct addLot, int mul) {
		boolean reserve = false;
		for (LotOfProduct l : list) {
			int invoice_id = 0;
			if (l.doc != null)
				invoice_id = l.doc.id;
			// TODO: lot.id
			if (l.id == addLot.id && l.reserve == addLot.reserve)
			// if (lot.product.id == addLot.product.id && invoice_id ==
			// addLot.invoice.id)
			// && l.price == returnLot.price - Цену сравнивать не надо - Партия
			// формируется)
			{
				l.count += mul * addLot.count;
				l.count = Format.countRound(l.count);
				return true;
			}
		}
		return false;
	}

}