package maxzawalo.c2.free.data.factory.registry;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import maxzawalo.c2.base.bo.registry.RegType;
import maxzawalo.c2.base.bo.registry.Registry;
import maxzawalo.c2.base.data.DbHelper;
import maxzawalo.c2.base.data.GenericRowMapper;
import maxzawalo.c2.base.data.factory.RegistryFactory;
import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.base.utils.Global;
import maxzawalo.c2.base.utils.Profiler;
import maxzawalo.c2.free.bo.LotOfProduct;
import maxzawalo.c2.free.bo.Settings;
import maxzawalo.c2.free.bo.Store;
import maxzawalo.c2.free.bo.comparator.RegistryProductComparatorFIFO;
import maxzawalo.c2.free.bo.comparator.RegistryProductComparatorManual;
import maxzawalo.c2.free.bo.registry.RegistryProduct;
import maxzawalo.c2.free.bo.registry.RegistryProduct.fields;
import maxzawalo.c2.free.bo.store.StoreTP;
import maxzawalo.c2.free.cache.Cache;
import maxzawalo.c2.free.data.factory.catalogue.LotOfProductFactoryFree;
import maxzawalo.c2.free.data.factory.document.StoreDocFactory;

public class RegistryProductFactory extends RegistryFactory<RegistryProduct> {

	public static class TRANSACTION_MODE {
		public static final int MANUAL = 1;
		public static final int FIFO = 2;
	}

	protected int transaction_mode = TRANSACTION_MODE.MANUAL;

	// public static final String balanceKey = "RegistryProduct.Balance";

	@Override
	protected void ItemFilter(RegistryProduct r, Where<RegistryProduct, Integer> where) throws SQLException {
		// При проводке разные Товары и Партии
		// Where<RegistryProduct, Integer> where =
		super.ItemFilter(r, where);
		where.and();
		where.eq(fields.PRODUCT, r.product);
		where.and();
		where.eq(fields.LOT, r.lotOfProduct);
		throw new SQLException("Нафиг он здесь нужен?");
		// TODO: store??
	}

	/**
	 * В этот фильтр надо пихать все Измерения
	 * 
	 * @param where
	 * @param filterItem
	 * @throws SQLException
	 */
	@Override
	protected void BalanceFilter(Where<RegistryProduct, Integer> where, RegistryProduct filterItem)
			throws SQLException {
		super.BalanceFilter(where, filterItem);
		where.and();
		where.eq(fields.STORE, filterItem.store);
		where.and();
		where.eq(fields.PRODUCT, filterItem.product);
		where.and();
		where.eq(fields.LOT, filterItem.lotOfProduct);
	}

	@Override
	protected List<RegistryProduct> BalanceFilter(List<RegistryProduct> all, RegistryProduct filterItem)
			throws Exception {
		List<RegistryProduct> filtered = super.BalanceFilter(all, filterItem);
		// По измерениям
		filtered.removeIf(p -> !(p.store.id == filterItem.store.id && p.product.id == filterItem.product.id
				&& p.lotOfProduct.id == filterItem.lotOfProduct.id));
		return filtered;
	}

	@Override
	protected double CalcBalanceByEntry(List<RegistryProduct> items, RegistryProduct filterItem) {
		double balance = 0;
		for (RegistryProduct rp : items) {
			// System.out.println("PlusEntries count=" + rp.count + " " +
			// rp.product);
			balance += rp.count;// TODO: round .001
			// balance = Format.roundDouble(balance, new
			// RegistryProduct().regRoundPlaces);
			balance = Format.countRound(balance);
		}

		return balance;
	}

	@Override
	protected double CalcBalanceWithNewEntry(List<RegistryProduct> items, RegistryProduct filterItem) {
		// TODO: round .001
		return Format.countRound(CalcBalanceByEntry(items, filterItem) - Math.abs(filterItem.count));
	}

	@Override
	protected void AbsResource(RegistryProduct item) {
		item.count = Math.abs(item.count);
	}

	@Override
	protected void ReverseResource(RegistryProduct item) {
		// abs- чтобы не было недаразумений с отриц. кол-м
		AbsResource(item);
		item.count = -1 * item.count;
	}

	// TODO: super
	public List<RegistryProduct> SelectBalance(Store store, boolean groupTransaction, List<Integer> lotIds,
			Date docDate) throws Exception {
		Profiler profiler = new Profiler();
		profiler.Start("SelectBalance");

		// TODO: если расходные подряд - добавлять тут баланс, т.к. не будет
		// хватать остатков в следующем за первым расходным док-м
		List<RegistryProduct> filtered = new ArrayList<>();
		if (Global.InMemoryGroupTransaction) {
			List<RegistryProduct> balance = Cache.I().getList(listGroupTransactionKey(typeBO, "balance"));
			filtered = SumGroupBalace(balance, transaction_mode);
			// Cache.I().putList(listGroupTransactionKey(typeBO, "balance"), balance,
			// IN_MEMORY_CACHE_TIME_SEC);
			Cache.I().putList(listGroupTransactionKey(typeBO, "balance"), filtered, IN_MEMORY_CACHE_TIME_SEC);
		} else {
			filtered = GetTransactionsFromDb(store, lotIds, new Date(0), docDate);
			filtered = SumGroupBalace(filtered, transaction_mode);
		}
		// TODO:!!!! вернуть кэширование

		profiler.Stop("SelectBalance");
		profiler.PrintElapsed("SelectBalance");

		return filtered;
	}

	public List<RegistryProduct> GetTransactionsFromDb(Store store, List<Integer> lotIds, Date fromDate, Date toDate)
			throws SQLException {
		List<RegistryProduct> filtered;
		Dao<RegistryProduct, Integer> boDao = DbHelper.geDaos(typeBO);
		QueryBuilder<RegistryProduct, Integer> builder = boDao.queryBuilder();
		Where<RegistryProduct, Integer> where = builder.where();
		NonDeletedFilter(where);
		// TODO: фильтрация по Контрагенту (Моя Фирма)
		if (store != null) {
			where.and();
			where.eq(fields.STORE, store);
		}

		where.and();
		// TODO: ?? нужно ли так фильтровать?
		where.eq(fields.RESERVE, false);
		where.and();
		where.ge(Registry.fields.REG_DATE, Format.beginOfDay(fromDate));
		where.and();
		// берем все проводки <= конца дня док-та
		where.le(Registry.fields.REG_DATE, Format.endOfDay(toDate));
		// TODO: не lots а products
		if (lotIds.size() != 0) {
			String sql = "";
			sql += where.getStatement();
			sql += " AND ";
			sql += " lotOfProduct_id IN ";
			sql += "(";
			int pos = 0;
			for (Integer id : lotIds) {
				if (pos != 0)
					sql += ",";
				sql += id;
				pos++;
			}
			sql += ")";
			// Важно здесь получить where = builder.where(); - т.к. стираем
			// старый
			where = builder.where();
			where.raw(sql);
		}

		filtered = boDao.queryRaw(builder.prepareStatementString(), GenericRowMapper.get(typeBO, true)).getResults();
		// for (RegistryProduct rp : filtered) {
		// // Для сортировки SumGroupBalace
		// rp.lotOfProduct.doc_date = rp.reg_date;
		// }
		// profiler.Stop("SelectBalance");
		// profiler.PrintElapsed("SelectBalance");
		return filtered;
	}

	// @Override
	// public boolean PlusEntries(RegistryProduct r, List<RegistryProduct>
	// entries) {
	// // Очищаем кэш баланса
	// // -приходные документы
	// // Не очищаем расходные даже если скопом идут сквозь дату уменьшают
	// // баланс в кэше
	// if (RegType.isInDoc(r.reg_type))
	// Cache.I().clearCache(balanceKey);
	// return super.PlusEntries(r, entries);
	// }

	public static List<RegistryProduct> SumGroupBalace(List<RegistryProduct> balance, int transaction_mode) {
		Profiler profiler = new Profiler();
		profiler.Start("SumGroupBalace");

		profiler.Start("SumGroupBalace.newList");
		List<RegistryProduct> newList = new ArrayList<>();
		// if (Global.InMemoryGroupTransaction) {
		// // Делаем клонирование для группового проведения, чтобы не
		// // обнулялись проводки при минусовке. MinusByLotWithBalance balance.count =
		// 0;
		// list.stream()//
		// // .filter(p -> !p.deleted)// удаленные в балансе не нужны. Хотя их и нет.
		// Это
		// // только баланс, а не проводки.
		// .forEach((rp) -> newList.add(rp.cloneObject()));
		// // for (RegistryProduct rp : list)
		// // if (!rp.deleted)// удаленные в балансе не нужны. Хотя их вроде бы и нет.
		// Это
		// // только баланс, а не
		// // // проводки.
		// // newList.add(rp.cloneObject());
		// } else
		newList.addAll(balance);
		profiler.Stop("SumGroupBalace.newList");

		List<RegistryProduct> sumList = new ArrayList<>();
		profiler.Start("SumGroupBalace.mapping");
		Map<String, List<RegistryProduct>> groupBalance = newList.stream()//
				.collect(Collectors.groupingBy(p -> p.dimensionGroupingByKey(),
						Collectors.mapping((RegistryProduct p) -> p, Collectors.toList())));
		profiler.Stop("SumGroupBalace.mapping");

		profiler.Start("SumGroupBalace.sum");
		for (String key : groupBalance.keySet()) {
			List<RegistryProduct> group = groupBalance.get(key);
			RegistryProduct rp = group.get(0);
			rp.count = Format.countRound(group.stream().collect(Collectors.summingDouble(RegistryProduct::getCount)));
			sumList.add(rp);
		}
		profiler.Stop("SumGroupBalace.sum");

		// Map<String, DoubleSummaryStatistics> peopleBySomeKey =
		// newList.stream()
		// .collect(Collectors.groupingBy(p -> p.dimensionGroupingByKey(),
		// Collectors.summarizingDouble(RegistryProduct::getCount)));

		sumList.removeIf(rp -> rp.count == 0);
		// profiler.Stop("SumGroupBalace.Group");
		// profiler.PrintElapsed("SumGroupBalace.Group");

		// sort
		profiler.Start("SumGroupBalace.Sort");
		if (transaction_mode == TRANSACTION_MODE.MANUAL)
			Collections.sort(sumList, new RegistryProductComparatorManual());
		else if (transaction_mode == TRANSACTION_MODE.FIFO)
			Collections.sort(sumList, new RegistryProductComparatorFIFO());
		System.out.println("transaction_mode=" + transaction_mode);
		profiler.Stop("SumGroupBalace.Sort");

		newList.clear();
		System.out.println("TODO: list.clear(); вернуть?");

		profiler.Stop("SumGroupBalace");
		profiler.PrintElapsed("SumGroupBalace.newList");
		profiler.PrintElapsed("SumGroupBalace.mapping");
		profiler.PrintElapsed("SumGroupBalace.sum");
		profiler.PrintElapsed("SumGroupBalace.Sort");
		profiler.PrintElapsed("SumGroupBalace");

		return sumList;
	}

	public static List<RegistryProduct> SumGroupBalaceByProduct(List<RegistryProduct> list) {
		// Profiler profiler = new Profiler();
		// profiler.Start("SumGroupBalace");

		// sort
		// profiler.Start("SumGroupBalace.Sort");
		Collections.sort(list, new RegistryProductComparatorManual());
		// profiler.Stop("SumGroupBalace.Sort");
		// profiler.PrintElapsed("SumGroupBalace.Sort");

		List<RegistryProduct> newList = new ArrayList<>();
		if (Global.InMemoryGroupTransaction) {
			// Делаем клонирование для группового проведения, чтобы не
			// обнулялись
			// проводки при мнусовке
			for (RegistryProduct rp : list)
				newList.add(rp.cloneObject());
		} else
			newList.addAll(list);

		List<RegistryProduct> sumList = new ArrayList<>();

		// assertEquals(group1.size(), elementCount);

		// profiler.Start("SumGroupBalace.Group");
		int product = -1;
		double sum = 0;
		RegistryProduct current = null;
		for (RegistryProduct rp : newList) {
			// System.out.println(rp.product.id + "|" + rp.lotOfProduct.id);
			// TODO: store (все измерения) - здесь не актуально так как
			// фильтруются проводки по складу GetTransactionsFromDb
			if (product != rp.product.id) {
				product = rp.product.id;
				current = rp;
				sumList.add(current);
				sum = 0;
			} else {
				current.count += rp.count;
				current.count = Format.countRound(current.count);
			}
		}

		sumList.removeIf(rp -> rp.count == 0);
		// profiler.Stop("SumGroupBalace.Group");
		// profiler.PrintElapsed("SumGroupBalace.Group");
		newList.clear();
		System.out.println("TODO: list.clear(); вернуть?");

		// profiler.Stop("SumGroupBalace");
		// profiler.PrintElapsed("SumGroupBalace");

		return sumList;
	}

	public <T> boolean MinusByLotWithBalance(RegistryProduct r, List<T> tablePart, boolean reserve, Date docDate) {
		// Profiler profiler = new Profiler();
		int badCount = 0;

		List<StoreTP> tablePartLoc = new ArrayList<>();
		tablePartLoc.addAll((Collection<? extends StoreTP>) tablePart);

		List<RegistryProduct> sumList;
		try {
			RegistryProduct rp = new RegistryProduct();
			// TODO: "Цены"?
			rp.store = r.store;
			// rp.contractor = r.contractor;
			// profiler.Start("MinusByLotWithBalance_SelectBalance");
			ArrayList<Integer> lots = new ArrayList<Integer>();
			// Пока выключаем
			// for (StoreTP tp : tablePartLoc)
			// lots.add(tp.lotOfProduct.id);
			sumList = SelectBalance(rp.store, Global.groupTransaction, lots, docDate);
			// profiler.Stop("MinusByLotWithBalance_SelectBalance");
			// profiler.PrintElapsed("MinusByLotWithBalance_SelectBalance");
		} catch (Exception e) {
			log.ERROR("MinusByLotWithBalance", e);
			return false;
		}

		int fifo_tp = 0;

		List<RegistryProduct> entries = new ArrayList<>();
		for (StoreTP tp : tablePartLoc) {
			// Сбрасываем флаг для раскраски строки в UI
			tp.bad = false;

			double count = tp.count;
			// if (tp.product.id == 1369) {
			// log.DEBUG("", "");
			// }

			RegistryProduct item = new RegistryProduct();
			// Ставим все Измерения для дальнейшей фильтрации
			item.store = r.store;
			// Уст. Товар
			item.product = tp.product;

			// TODO: добавляем партию в таб. часть и сохраняем док.
			if (transaction_mode == TRANSACTION_MODE.FIFO || tp.lotOfProduct == null) {
				System.out.println("FIFO");
				// TODO: FIFO - надо сортировать по дате партий (reg_date in
				// doc)
				// System.out.println("Поиск партии...");
				// списываем последовательно
				// TODO: порядок списание - алгоритм...возможно с анализом по
				// налогам и наценке
				for (RegistryProduct balance : sumList) {
					// Уже нет остатков - следующий
					if (balance.count == 0)
						continue;

					// TODO: для всех измерение кроме тех что ниже (product,
					// lot)
					if (balance.store.id != item.store.id)
						continue;

					// TODO: для ускорения - Map (key = product.id) - из ф-и
					if (balance.product.id == tp.product.id) {
						System.out.println("tp.product.id=" + tp.product.id);

						item.lotOfProduct = balance.lotOfProduct;
						item.cost_price = item.lotOfProduct.cost_price;
						item.price = tp.price;
						// учитываем кол-во в партиях
						// item.count = -count;
						if (balance.count - count >= 0) {
							// эта партия закрывает весь остаток
							item.count = -count;
							item.count = Format.countRound(item.count);

							// Меняем в кэше баланса для группового проведения
							balance.count -= count;
							balance.count = Format.countRound(balance.count);
							entries.add(item);
							count = 0;

							fifo_tp++;
							System.out.print("Этой партии хватило ");
							System.out.print((balance.lotOfProduct.doc == null ? ""
									: Format.Show(balance.lotOfProduct.doc.DocDate)));
							System.out.println(" (" + balance.lotOfProduct.id + ")");
							break;
						} else {
							// TODO: печать из регистра или добавлять
							// не хватает - следующая партия
							item.count = -balance.count;
							item.count = Format.countRound(item.count);

							count -= balance.count;
							count = Format.countRound(count);
							// Данная партия закончилась. Меняем в кэше баланса
							// для следующего цикла
							balance.count = 0;
							entries.add(item);

							item = new RegistryProduct();
							// Ставим ВСЕ Измерения для фильтра
							// Уст. Товар
							item.product = tp.product;
							// item.lotOfProduct = balance.lotOfProduct;
							// Уст. склад
							item.store = r.store;

							System.out.print("Выбор следующей партии ");
							System.out.print((balance.lotOfProduct.doc == null ? ""
									: Format.Show(balance.lotOfProduct.doc.DocDate)));
							System.out.println(" (" + balance.lotOfProduct.id + ")");
						}
						// break;
					}
				}
				// записываем цену документа, но если отличается -
				// сообщать(лог)
				// нету Товара на складе соооооовсем - нонсенс -
				// в подборе тоже не должно быть -
				// но если надо выписать накладную и прихода еще нет
				// -формиркм партию тут - в приходной нет - используем эту
				if (item.lotOfProduct == null) {
					// TODO: Добавляем партию
					item.count = -count;
					item.count = Format.countRound(item.count);
					item.lotOfProduct = new LotOfProduct();
					item.store = r.store;
					// Оставляем пустые Партии - потом аналит. обработка
					// установит или вручную
					// Но если все 0 - группируются неправильно для ручного
					// подбора.
					// TODO: Надо прятать с пустым invoice
				}

				// item.lotOfProduct = new LotOfProduct();// пустой
			} else if (transaction_mode == TRANSACTION_MODE.MANUAL) {
				System.out.println("MANUAL");
				// TODO: Ищем остатки по данной партии, мало ли изменилось пока
				// вводили

				boolean can = false;
				// Фильтрация по измерениям
				for (RegistryProduct balance : sumList) {
					// Уже нет остатков - следующий
					if (balance.count == 0)
						continue;
					// TODO: все измерения?
					// TODO: для всех измерение кроме тех что ниже (product,
					// lot)
					if (balance.store.id != item.store.id)
						continue;
					// так как ручной выбор партий, то не надо фильтровать по
					// складу, т.к. разные номера партий у разных складов
					// (правда я там немного схимичил и партии установил
					// дубликатами при перемещении. надо исправить)
					if (balance.lotOfProduct.id == tp.lotOfProduct.id && balance.product.id == tp.product.id) {
						if (balance.count - tp.count >= 0) {
							// Отнимаем от баланса, чтобы не было проблем с
							// дубликатами строк с одной проводкой
							balance.count -= tp.count;
							balance.count = Format.countRound(balance.count);
							can = true;
							break;
						} else {
							// Чтобы точно знать количество нехватки
							// TODO: прогонять все строки, а потом error - для
							// быстрого анализа
							count -= balance.count;
							count = Format.countRound(count);
						}
					}
				}

				if (can) {
					// ставим и в документе tp.price = tp.lotOfProduct.price
					// смотрим наценку, налоги, скидку. Вычисляем.
					// записываем в регистр значение табличной части
					item.cost_price = tp.price_discount_off;
					item.price = tp.price;
					// tp.lotOfProduct уже есть в таб части
					item.lotOfProduct = tp.lotOfProduct;
					item.count = tp.count;
					entries.add(item);

					// Чтобы далее не было ошибки нехватки
					count = 0;
				}
			}

			// TODO: списали не все - сообщение о недостатке
			if (count != 0) {
				// Выделяем строку в UI
				tp.bad = true;
				log.DEBUG("MinusByLotWithBalance", tp.getClass().getName() + " " + tp.doc);
				String message = "Нет на балансе " + count + " " + " " + tp.product.code + " " + tp.product.id + " "
						+ tp.product.name;
				log.ERROR("MinusByLotWithBalance", message);
				Console.I().ERROR(getClass(), "MinusByLotWithBalance", message);

				//
				//
				// if (Settings.canBalanceBeMinus) {
				// // TODO: минусовая проводка
				// // делать партии с пометкой
				// } else
				// TODO: что делаем тут??
				// Пропускаем в транзации
				badCount++;
			}
		}

		if (transaction_mode == TRANSACTION_MODE.FIFO && fifo_tp != tablePartLoc.size()) {
			log.ERROR("MinusByLotWithBalance", "Обработана не вся ТЧ");
			Console.I().ERROR(getClass(), "MinusByLotWithBalance", "Обработана не вся ТЧ");
			return false;
		}

		// Сначала минусуем со склада, потом добавляем в резер
		// TODO: transaction all
		if (badCount == 0) {
			// Устанавливаем ВСЕ доп поля. cost_price установлено выше.
			for (RegistryProduct rp : entries)
				rp.contractor = r.contractor;

			if (MinusEntries(r, entries)) {
				if (reserve) {
					List<RegistryProduct> reserveEntries = new ArrayList<>();
					for (RegistryProduct rp : entries) {
						RegistryProduct reserveEntry = rp.cloneObject();
						reserveEntry.reserve = true;
						// Меняем знак, так как MinusEntries делает инверсию
						AbsResource(reserveEntry);
						reserveEntries.add(reserveEntry);
					}

					// entries.addAll(reserveEntries);
					if (PlusEntries(r, reserveEntries))
						return true;
					else {
						// Откатываем транзакцию MinusEntries, если не сработал
						// Резерв (PlusEntries)
						try {
							// Товарное проведение. AccTransactionOnly зависит от галочки при Групповом
							// проведении. Но продуктового регистра тут не должно быть.
							// TODO:
							if (Global.AccTransactionOnlyTest)
								throw new Exception("");
							RemoveEntries(r, false);
						} catch (Exception e) {
							log.ERROR("MinusByLotWithBalance", e);
						}
					}
				} else
					return true;
			}
		}
		return false;
	}

	public List<RegistryProduct> TurnoverByCount(Store store, Date docDate) throws Exception {
		List<RegistryProduct> list = GetTransactionsFromDb(store, new ArrayList<>(), new Date(0), docDate);
		// Берем расход по налу и безналу
		// TODO: исключить возвраты
		list.removeIf(rp -> !(rp.reg_type == RegType.DeliveryNote || rp.reg_type == RegType.CashVoucher));
		list.removeIf(rp -> rp.contractor != null && rp.contractor.id == Settings.myFirm.id);
		list = SumGroupBalaceByProduct(list);
		for (RegistryProduct rp : list)
			rp.count = Math.abs(rp.count);

		Collections.sort(list, (rp1, rp2) -> ((Double) rp2.count).compareTo(rp1.count));
		return list;
	}

	public Map<Integer, Double> TurnoverMap(Store store, Date docDate) throws Exception {
		return TurnoverMap(TurnoverByCount(store, docDate));
	}

	public Map<Integer, Double> TurnoverMap(List<RegistryProduct> list) {
		Map<Integer, Double> turnoverMap = new HashMap<>();
		for (RegistryProduct rp : list) {
			turnoverMap.put(rp.product.id, rp.count);
		}
		return turnoverMap;
	}

	public List<RegistryProduct> TurnoverBySum(Store store, Date fromDate, Date toDate) throws Exception {
		List<RegistryProduct> list = GetTransactionsFromDb(store, new ArrayList<>(), fromDate, toDate);
		// Берем расход по налу и безналу
		// TODO: исключить возвраты
		list.removeIf(rp -> !(rp.reg_type == RegType.DeliveryNote || rp.reg_type == RegType.CashVoucher));
		list.removeIf(rp -> rp.contractor != null && rp.contractor.id == Settings.myFirm.id);
		// TODO: sum price * count
		list = SumGroupBalaceByProduct(list);

		for (RegistryProduct rp : list) {
			rp.count = Math.abs(rp.count);
			// price as sum
			// rp.price = Format.defaultRound(rp.count * rp.price);
		}

		Collections.sort(list, (rp1, rp2) -> ((Double) rp2.price).compareTo(rp1.price));
		return list;
	}

	public void setRPFactory(StoreDocFactory factory) {
		if (this instanceof RegistryProductFactory)
			factory.registryProductFactory = this;
	}

	@Override
	protected void AfterLoad(List<RegistryProduct> all) {
		List<LotOfProduct> lots = Cache.I().getList(LotOfProduct.InMemoryGroupTransactionKey);
		// Связываем с RegistryProduct с lotOfProduct чтобы doc!=null
		for (RegistryProduct rp : all) {
			LotOfProduct filtered = lots.stream().filter(l -> l.id == rp.lotOfProduct.id).findFirst().orElse(null);
			if (filtered == null) {
				System.out.println("Партия не найдена id=" + rp.lotOfProduct.id);
				LotOfProduct lot = new LotOfProductFactoryFree().GetById(rp.lotOfProduct.id);
				// делаем ленивую дозагрузку
				rp.lotOfProduct = lot;
				lots = Cache.I().getList(LotOfProduct.InMemoryGroupTransactionKey);
				lots.add(lot);
				Cache.I().putList(LotOfProduct.InMemoryGroupTransactionKey, lots,
						RegistryFactory.IN_MEMORY_CACHE_TIME_SEC);
			} else
				rp.lotOfProduct = filtered;
		}
	}

	@Override
	public String NotEnoughItemMessage(Registry r) {
		RegistryProduct rp = (RegistryProduct) r;
		return "Не хватает на складе " + rp.count + " " + rp.product.code + " " + rp.product.id + " " + rp.product.name;
	}

	@Override
	protected void AddEntry2Balance(Registry r, RegistryProduct item, boolean isMinus) {
		// MinusByLotWithBalance уже отнял нужное кол-во.
		if (isMinus)
			return;
		super.AddEntry2Balance(r, item, isMinus);
	}

}