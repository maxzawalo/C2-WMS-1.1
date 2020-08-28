package maxzawalo.c2.full.data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import maxzawalo.c2.base.Actions;
import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.bo.comparator.SortDocByDate;
import maxzawalo.c2.base.bo.registry.Registry;
import maxzawalo.c2.base.data.factory.DocumentFactory;
import maxzawalo.c2.base.data.factory.RegistryFactory;
import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.base.utils.FileUtils;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.base.utils.Global;
import maxzawalo.c2.base.utils.Logger;
import maxzawalo.c2.base.utils.Profiler;
import maxzawalo.c2.free.accounting.data.AccFactory;
import maxzawalo.c2.free.bo.LotOfProduct;
import maxzawalo.c2.free.bo.store.StoreDocBO;
import maxzawalo.c2.free.cache.Cache;
import maxzawalo.c2.free.data.factory.document.StoreDocFactory;
import maxzawalo.c2.free.data.factory.registry.RegistryProductFactory;
import maxzawalo.c2.full.data.factory.catalogue.LotOfProductFactoryFull;
import maxzawalo.c2.full.data.factory.registry.RegistryProductFIFOFactory;

public class GroupTransaction {
	static Logger log = Logger.getLogger(GroupTransaction.class);

	public static void Do(Date fromDate, Date toDate, boolean doSaveTransaction, boolean ProductTransactionOnly,
			boolean AccTransactionOnly) {
		boolean fifo = false;
		// TODO: fifo = AccTransactionOnly;
		Global.GroupTransactionErrorMessages.clear();

		Global.AccTransactionOnlyTest = AccTransactionOnly;
		// Global.AccTransactionOnly = true;

		Profiler profiler = new Profiler();
		Global.groupTransactionKey = "Групповое проведение " + Format.Show(fromDate) + "-" + Format.Show(toDate);
		profiler.Start(Global.groupTransactionKey);

		Global.InMemoryGroupTransaction = true;

		try {
			Console.I().INFO(GroupTransaction.class, "Do", "Групповое проведение");

			List<DocumentBO> docs = new ArrayList<>();
			Console.I().INFO(GroupTransaction.class, "Do", "Загрузка документов");
			profiler.Start("Загрузка документов");
			for (Class type : Global.transactionChains) {
				// TODO: все проводимые док-ты
				Console.I().INFO(GroupTransaction.class, "Do", "Загрузка " + ((BO) type.newInstance()).getRusName());
				docs.addAll((Collection<? extends DocumentBO>) DocFactoryByType(type).GetByPeriod(fromDate, toDate));
				Global.CheckAndPause();
			}
			profiler.Stop("Загрузка документов");
			Console.I().INFO(GroupTransaction.class, "Do", profiler.ElapsedStr("Загрузка документов"));

			if (fromDate == null)
				fromDate = Format.beginOfDay(Collections.min(docs, new SortDocByDate()).DocDate);
			if (toDate == null)
				toDate = Format.endOfDay(Collections.max(docs, new SortDocByDate()).DocDate);

			Cache.I().put("GroupTransaction_fromDate", fromDate, RegistryFactory.IN_MEMORY_CACHE_TIME_SEC);
			Cache.I().put("GroupTransaction_toDate", toDate, RegistryFactory.IN_MEMORY_CACHE_TIME_SEC);

			RegistryFactory registryFactory = null;
			if (!AccTransactionOnly) {
				System.out.println("TODO: учесть что документы меняют Партии - в этот кэш надо чтобы попали");
				List<LotOfProduct> lots = Cache.I().getMapList(LotOfProduct.class);
				if (lots == null) {
					Global.CheckAndPause();

					Console.I().INFO(GroupTransaction.class, "Do", "Загрузка партий в кэш");
					profiler.Start("Загрузка партий в кэш");
					System.out.println("TODO: LotOfProduct.InMemoryGroupTransaction by period");
					// TODO: учесть что период может измениться - а в кэше будет нехватать.
					// Надо загружать по ТЧ док-в а не по регистрам. Потому что может не быть
					// поздних проводок (ручная очистка БД)
					// Сча есть дозагрузка в регистре - это для сортировки FIFO
					lots = new LotOfProductFactoryFull().GetByPeriod(fromDate, toDate);
					for (LotOfProduct lot : lots) {
						lot.doc = (StoreDocBO) docs.stream()//
								.filter(doc -> doc.reg_type == lot.doc_type && doc.id == lot.doc.id)//
								.findFirst()//
								.orElse(lot.doc);

						if (lot.doc == null) {
							System.out.print("lot.doc getDoc");
							lot.doc = new LotOfProductFactoryFull().getDoc(lot);
						}
						// else
						// System.out.println("docs.stream().filter");

						if (lot.doc == null)
							System.out.print("lot.doc == null");

						Global.CheckAndPause();
					}
					profiler.Stop("Загрузка партий в кэш");
					Console.I().INFO(GroupTransaction.class, "Do", profiler.ElapsedStr("Загрузка партий в кэш"));
				} else {
					log.INFO("GroupTransaction", "Партии уже в кэше");
					Console.I().INFO(GroupTransaction.class, "Do", "Партии уже в кэше");
				}

				System.err.println("TODO: load Cache.I().getMapList(LotOfProduct.class);");

				// TODO: берем все/исп-мые в док-х регистры, фабрики,
				// TODO: загружаем себестоимость. Для FIFO
				registryFactory = new RegistryProductFactory();

				if (fifo)
					registryFactory = new RegistryProductFIFOFactory();
				Cache.I().put(RegistryFactory.grouptransactionRegGactory(RegistryProductFactory.class), registryFactory,
						RegistryFactory.IN_MEMORY_CACHE_TIME_SEC);

				Global.CheckAndPause();

				Console.I().INFO(GroupTransaction.class, "Do", "Загрузка проводок");
				profiler.Start("Загрузка проводок");
				// TODO: загружать только измененные только что документы/закидывать
				// прямо в кэш из документы, если система в режиме "Групповое
				// проведение" (форма открыта, глобальный флаг)
				Console.I().INFO(GroupTransaction.class, "Do", "Загрузка " + registryFactory.getTypeBO());
				registryFactory.Load2Cache(fromDate, toDate, (Registry) registryFactory.getTypeBO().newInstance());
			} else {
				Console.I().INFO(GroupTransaction.class, "Do", "Загрузка проводок");
				profiler.Start("Загрузка проводок");
			}
			Global.CheckAndPause();

			if (!ProductTransactionOnly) {
				if (Actions.getAccRegisters != null) {
					for (Registry r : (List<Registry>) Actions.getAccRegisters.Do()) {
						registryFactory = new AccFactory().Create(r.getClass());
						Console.I().INFO(GroupTransaction.class, "Do", "Загрузка " + registryFactory.getTypeBO());
						registryFactory.Load2Cache(fromDate, toDate,
								(Registry) registryFactory.getTypeBO().newInstance());
						Global.CheckAndPause();
					}
				}
			}

			profiler.Stop("Загрузка проводок");
			Console.I().INFO(GroupTransaction.class, "Do", profiler.ElapsedStr("Загрузка проводок"));
			Global.CheckAndPause();

			GroupTransaction(docs, fromDate, toDate, fifo, ProductTransactionOnly, AccTransactionOnly);
			Global.CheckAndPause();

			if (doSaveTransaction) {
				SaveTransactions(ProductTransactionOnly, AccTransactionOnly);
			} else
				Console.I().WARN(GroupTransaction.class, "Do", "ВНИМАНИЕ! Проводки не сохранены!");

			// Global.InMemoryGroupTransaction = false;
			System.out.println("TODO: setCommited после InMemoryGroupTransaction = false");

			// Удаляем ТЧ
			Cache.I().clearBySubstr("TablePart");
			Cache.I().ShowAllKeys();

			profiler.Stop(Global.groupTransactionKey);
		} catch (Exception e) {
			log.ERROR("GroupTransaction", e);
			profiler.Stop(Global.groupTransactionKey);
		}
		Global.InMemoryGroupTransaction = false;

		// При масштабном тестировании - отключаем - оставляем партии в кэше
		ClearGroupTransactionCache();

		Console.I().INFO(GroupTransaction.class, "Do", profiler.ElapsedStr(Global.groupTransactionKey));
		profiler.PrintElapsed(Global.groupTransactionKey);

		Global.groupTransactionKey = null;

		Global.AccTransactionOnlyTest = false;
	}

	public static void SaveTransactions(boolean ProductTransactionOnly, boolean AccTransactionOnly) {
		Profiler profiler = new Profiler();
		Console.I().INFO(GroupTransaction.class, "SaveTransactions", "Сохранение проводок...");
		profiler.Start("Сохранение проводок");
		try {
			Date fromDate = (Date) Cache.I().get("GroupTransaction_fromDate");
			Date toDate = (Date) Cache.I().get("GroupTransaction_toDate");

			// Удаляем проводки не из Периода
			long fd = fromDate.getTime();
			long td = toDate.getTime();
			DocumentBO badDoc = (DocumentBO) Global.GetBadTransactionDoc();
			if (badDoc != null)
				// Спотыкается на док-те. Сохраняются его удаленные проводки и остальные по
				// документам в пределах дня
				toDate = Format.endOfDay(badDoc.DocDate);

			// Чтобы BulkSave показывала прогресс
			Global.InMemoryGroupTransaction = true;
			RegistryFactory registryFactory = null;
			List<Registry> all = new ArrayList<>();
			if (!AccTransactionOnly) {
				registryFactory = (RegistryFactory) Cache.I()
						.get(RegistryFactory.grouptransactionRegGactory(RegistryProductFactory.class));// TODO: fifo
				Console.I().INFO(GroupTransaction.class, "SaveTransactions",
						"Сохранение " + registryFactory.getTypeBO());
				all = Cache.I().getList(RegistryFactory.listGroupTransactionKey(registryFactory.getTypeBO()));
				all.removeIf(rp -> rp.reg_date.getTime() < fd || rp.reg_date.getTime() > td);

				Console.I().INFO(GroupTransaction.class, "SaveTransactions", "Всего: " + all.size());
				registryFactory.BulkSave(all, 1000);
			}

			if (!ProductTransactionOnly) {
				if (Actions.getAccRegisters != null) {
					for (Registry r : (List<Registry>) Actions.getAccRegisters.Do()) {
						registryFactory = new RegistryFactory<>().Create(r.getClass());
						Console.I().INFO(GroupTransaction.class, "SaveTransactions",
								"Сохранение " + registryFactory.getTypeBO());
						all = Cache.I().getList(RegistryFactory.listGroupTransactionKey(registryFactory.getTypeBO()));
						all.removeIf(rp -> rp.reg_date.getTime() < fd || rp.reg_date.getTime() > td);
						registryFactory.BulkSave(all, 1000);
						Global.CheckAndPause();
						Cache.I().clearCache(RegistryFactory.listGroupTransactionKey(registryFactory.getTypeBO()));
					}
				}
			}

			profiler.Stop("Сохранение проводок");

			Cache.I().clearCache(RegistryProductFactory.listGroupTransactionKey(registryFactory.getTypeBO()));
			Cache.I()
					.clearCache(RegistryProductFactory.listGroupTransactionKey(registryFactory.getTypeBO(), "balance"));
		} catch (Exception e) {
			Console.I().ERROR(GroupTransaction.class, "SaveTransactions", "Ошибка. См. лог.");
			profiler.Stop("Сохранение проводок");
			log.ERROR("SaveTransactions", e);
		}
		Global.InMemoryGroupTransaction = false;
		Console.I().INFO(GroupTransaction.class, "SaveTransactions", profiler.ElapsedStr("Сохранение проводок"));
		Global.setPriority(0);

		// Global.AccTransactionOnly = false;
	}

	public static void ClearGroupTransactionCache() {
		Console.I().INFO(GroupTransaction.class, "ClearGroupTransactionCache", "Очистка кэша Группового проведения");
		// Cache.I().clearCache(LotOfProduct.InMemoryGroupTransactionKey);
		Console.I().INFO(GroupTransaction.class, "GroupTransaction", "Очистка кэша ТЧ");
		for (Class t : Global.transactionChains) {
			try {
				Console.I().INFO(GroupTransaction.class, "GroupTransaction",
						"Очистка кэша ТЧ:" + ((BO) t.newInstance()).getRusName());
				System.out.println(t);
				DocumentFactory factory = DocFactoryByType(t);
				factory.ClearAllTPFromCache();
			} catch (Exception e) {
				log.ERROR("ClearGroupTransactionCache", e);
			}
		}
		// Cache.I().clearCache(RegistryProductFactory.listGroupTransactionKey(registryProductFactory.getTypeBO()));
	}

	static List<DocumentBO> getEntriesByDate(List<DocumentBO> entries, Date date) {
		Date startDate = Format.beginOfDay(date);
		Date endDate = Format.endOfDay(date);

		return entries.stream()//
				.filter(doc -> doc.DocDate.getTime() >= startDate.getTime()
						&& doc.DocDate.getTime() <= endDate.getTime())//
				// .sorted((e1, e2) -> ((Long)
				// e1.DocDate.getTime()).compareTo(e2.DocDate.getTime()))//
				.collect(Collectors.toList());
	}

	static boolean GroupTransaction(List<DocumentBO> allEntries, Date startDate, Date endDate, boolean fifo,
			boolean ProductTransactionOnly, boolean AccTransactionOnly) throws Exception {
		boolean retVal = false;
		Global.SetBadTransactionDoc(null);
		Global.groupTransaction = true;
		final Profiler profiler = new Profiler();

		// distinct
		Set<Class> docTypes = new HashSet<>();
		for (DocumentBO doc : allEntries)
			docTypes.add(doc.getClass());

		// String key = ".GroupTransaction";
		if (allEntries != null && allEntries.size() != 0) {
			allEntries = allEntries.stream()//
					.sorted((e1, e2) -> ((Long) e1.DocDate.getTime()).compareTo(e2.DocDate.getTime()))//
					.collect(Collectors.toList());
		} else {
			log.WARN("GroupTransaction", "GroupTransaction entries zero");
			// Не забываем перед всеми return
			Global.groupTransaction = false;
			return retVal;
		}

		Global.CheckAndPause();

		final Date lStartDate = startDate;
		final Date lEndDate = endDate;
		// final String lKey = key;

		// profiler.Start(key);
		try {
			log.DEBUG("GroupTransaction", "GroupTransaction size = " + allEntries.size());

			Console.I().INFO(GroupTransaction.class, "GroupTransaction", "Загрузка табличной части...");
			profiler.Start("Загрузка табличной части");
			for (Class t : docTypes) {
				DocumentFactory factory = DocFactoryByType(t);
				factory.LoadAllTablePart2Cache((DocumentBO) t.newInstance(), startDate, endDate,
						RegistryFactory.IN_MEMORY_CACHE_TIME_SEC);
				Global.CheckAndPause();
			}
			profiler.Stop("Загрузка табличной части");
			Console.I().INFO(GroupTransaction.class, "GroupTransaction",
					profiler.ElapsedStr("Загрузка табличной части"));

			// TODO: блокирование АРМов
			{
				Date currentDate = lStartDate;
				while (Format.endOfDay(currentDate).getTime() <= Format.endOfDay(lEndDate).getTime()) {
					Global.CheckAndPause();

					String dateLogMess = "======= Проведение на дату " + Format.Show("dd.MM.yy", currentDate) + "("
							+ profiler.PrintCurrentElapsed(Global.groupTransactionKey) + ")";
					log.DEBUG("GroupTransaction", dateLogMess);
					Console.I().INFO(GroupTransaction.class, "GroupTransaction", dateLogMess);
					// profiler.PrintCurrentElapsed(lKey);

					List<DocumentBO> docsByDate = getEntriesByDate(allEntries, currentDate);
					for (Class docType : Global.transactionChains) {
						for (DocumentBO doc : docsByDate) {
							Global.CheckAndPause();

							if (doc.getClass() != docType)
								continue;
							// if (doc.reg_type == RegType.ReturnOfGoods && doc.code.equals("Пож687630"))
							// System.out.println();
							log.DEBUG("GroupTransaction", "Проведение документа " + doc.getRusName() + " "
									+ Format.Show("dd.MM.yy", doc.DocDate) + " code=" + doc.code + " id=" + doc.id);
							Console.I().INFO(GroupTransaction.class, "GroupTransaction",
									"Проведение документа " + doc.getRusName() + " "
											+ Format.Show("dd.MM.yy", doc.DocDate) + " code=" + doc.code + " id="
											+ doc.id);
							// ((DocumentBO) item).LoadTablePart();
							// Не проводим док-т без остатков и с другими
							// проблемами
							// - пропускаем

							// TODO: проработать каждую ошибку отдельно
							// TODO: ошибку в консоль
							DocumentFactory factory = DocFactoryByType(doc.getClass());
							SetRegisters(doc, factory, fifo);

							if (!factory.DoTransaction(doc, ProductTransactionOnly, AccTransactionOnly)
									&& !Global.canBalanceBeMinus) {
								Global.SetBadTransactionDoc(doc);
								String mess = CurrentDocMessage(doc);
								Console.I().ERROR(GroupTransaction.class, "GroupTransaction", mess);

								if (Global.SkipTransactionErrors) {
									Global.GroupTransactionErrorMessages.add(mess);
									Global.GroupTransactionErrorMessages.add("--------------");
								} else
									throw new SQLException(mess);
							}
						}
					}

					currentDate = Format.AddDay(currentDate, 1);
				}
				// return null;
			}
			// });
			retVal = true;
		} catch (Exception e) {
			// profiler.Stop(key);
			// profiler.PrintElapsed(key);
			log.ERROR("GroupTransaction", e);
			// Global.groupTransaction = false;
			// return false;
		}

		Global.CheckAndPause();

		// profiler.Stop(key);
		// profiler.PrintElapsed(key);

		Global.groupTransaction = false;

		if (Global.SkipTransactionErrors) {
			if (Global.GroupTransactionErrorMessages.size() != 0) {
				String path = "Ошибки проведения.txt";
				FileUtils.Text2File(path, "----- Ошибки проведения\n", false);
				System.err.println("----- Ошибки проведения");
				Global.GroupTransactionErrorMessages.stream().forEach(m -> {
					FileUtils.Text2File(path, m + "\n", true);
					Console.I().ERROR(GroupTransaction.class, "GroupTransaction", m);
				});
			}
		}

		return retVal;
	}

	protected static void SetRegisters(DocumentBO doc, DocumentFactory factory, boolean fifo) {

		RegistryFactory registryFactory = new RegistryProductFactory();
		if (fifo)
			registryFactory = new RegistryProductFIFOFactory();
		// .getTypeBO().newInstance()

		// Устанавливает Factory в StoreDocFactory если FIFO. transaction_mode итп
		if (registryFactory instanceof RegistryProductFactory && factory instanceof StoreDocFactory)
			((RegistryProductFactory) registryFactory).setRPFactory((StoreDocFactory) factory);

		// тут меняем в doc Registry если FIFO
		registryFactory.ReplaceDocUsedReg(doc);
	}

	static DocumentFactory DocFactoryByType(Class type) {
		try {
			// TODO: global cache
			return (DocumentFactory) Actions.FactoryByRegTypeAction.Do(((DocumentBO) type.newInstance()).reg_type);
		} catch (Exception e) {
			log.ERROR("DocFactoryByType", e);
		}
		return null;
	}

	static String CurrentDocMessage(DocumentBO doc) {
		return doc.getRusName() + " " + Format.Show("dd.MM.yy", doc.DocDate) + " code=" + doc.code + " id=" + doc.id
				+ "|" + (doc.contractor == null ? "" : doc.contractor.name);
	}
}