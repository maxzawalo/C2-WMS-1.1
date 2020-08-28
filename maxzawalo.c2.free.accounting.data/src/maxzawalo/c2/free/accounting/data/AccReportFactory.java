package maxzawalo.c2.free.accounting.data;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import maxzawalo.c2.base.Actions;
import maxzawalo.c2.base.FunctionC2;
import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.data.factory.DocumentFactory;
import maxzawalo.c2.base.data.factory.RegistryFactory;
import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.base.utils.Profiler;
import maxzawalo.c2.free.accounting.AccList;
import maxzawalo.c2.free.accounting.AccRecord;
import maxzawalo.c2.free.bo.registry.AccAcc;

public class AccReportFactory extends RegistryFactory<AccAcc> {
	public Map<String, Object> SelectTurnover(Date startDate, Date endDate) throws Exception {
		return SelectTurnover(AccList.classes, startDate, endDate);
	}

	public Map<String, Object> SelectTurnover(String code, Date startDate, Date endDate) throws Exception {
		return SelectTurnover(AccFactory.GetAccByCode(code), startDate, endDate);
	}

	public Map<String, Object> SelectTurnover(Class[] accounts, Date startDate, Date endDate) throws Exception {
		Map<String, Object> retVal = new HashMap<>();
		Map<String, Object> params = new HashMap<>();

		params.put("startDate", startDate);
		params.put("endDate", endDate);
		
		Profiler profiler = new Profiler();
		profiler.Start("SelectTurnover");

		double turnoverDtFull = 0;
		double turnoverKtFull = 0;

		for (Class accT : accounts)
		// Class accT = Acc_18_3.class;
		{
			AccAcc currAcc = ((AccAcc) accT.newInstance());
			System.out.println(currAcc.code);
			profiler.Start("SelectTurnover_" + currAcc.code);

			boolean active = currAcc.isActive();
			// protected boolean passive = false;

			profiler.Start("SelectTurnover.Load");
			params.put("account", accT);
			AccFactory.GetByPeriod.call(params);
			profiler.Stop("SelectTurnover.Load");

			// profiler.Start("SelectTurnover.Sort");
			params.put("registries", params.get("acc_by_period"));
			RegistryFactory.SortRegistriesByDate.call(params);
			// profiler.Stop("SelectTurnover.Sort");

			double startSaldo = 0;
			double saldo = startSaldo;
			double turnoverDt = 0;
			double turnoverKt = 0;

			for (AccAcc t : (List<AccAcc>) params.get("registries_sorted_by_date")) {
				assert !t.deleted : "Удаленная проводка";
				// if (t.deleted)
				// System.out.println("Удаленная проводка");

				if (t.is_debit)
					turnoverDt = Format.defaultRound(turnoverDt + t.sum);
				else
					turnoverKt = Format.defaultRound(turnoverKt + t.sum);

				if (active) {
					if (t.is_debit)
						saldo = Format.defaultRound(saldo + t.sum);
					else
						saldo = Format.defaultRound(saldo - t.sum);
				} else {
					if (!t.is_debit)
						saldo = Format.defaultRound(saldo + t.sum);
					else
						saldo = Format.defaultRound(saldo - t.sum);
				}
			}

			turnoverDtFull = Format.defaultRound(turnoverDtFull + turnoverDt);
			turnoverKtFull = Format.defaultRound(turnoverKtFull + turnoverKt);

			System.out.println(startSaldo + "\t" + saldo);
			System.out.println(turnoverDt + "\t" + turnoverKt);
			profiler.Stop("SelectTurnover_" + currAcc.code);

			profiler.PrintElapsed("SelectTurnover.Load");
			profiler.PrintElapsed("SelectTurnover.Sort");
			profiler.PrintElapsed("SelectTurnover_" + currAcc.code);
			System.out.println("----------");
		}

		// assert turnoverDtFull == turnoverKtFull;
		if (turnoverDtFull != turnoverKtFull) {
			log.WARN("SelectTurnover", "Обороты Дт/Кт не равны");
			Console.I().WARN(getClass(), "SelectTurnover", "Обороты Дт/Кт не равны");
		}
		retVal.put("turnoverDtFull", turnoverDtFull);
		retVal.put("turnoverKtFull", turnoverKtFull);

		System.out.println("Обороты итог: " + turnoverDtFull + "\t" + turnoverKtFull);
		System.out.println("----------------------------");
		// QueryBuilder<AccAcc, Integer> builder = getQueryBuilder();
		// List<AccAcc> all = builder.query();

		profiler.Stop("SelectTurnover");
		profiler.PrintElapsed("SelectTurnover");
		return retVal;
	}

	public void SelectAccCard(Class accT, Date startDate, Date endDate) throws Exception {
		System.out.println("=========== Карточка счета " + ((BO) accT.newInstance()).code);

		Map<String, Object> params = new HashMap();

		double turnoverDt = 0;
		double turnoverKt = 0;

		List<AccAcc> trs = new AccFactory().Create(accT).GetByPeriod(startDate, endDate);

		params.put("registries", trs);
		RegistryFactory.DistinctByRegistrator.call(params);
		// адаптер
		params.put("registries", params.get("registries_distinct_by_registrator"));
		//TODO: call by name - или вообще сама пусть вызывается по in_params
		RegistryFactory.SortRegistriesByDate.call(params);

		for (AccAcc a : (List<AccAcc>) params.get("registries_sorted_by_date")) {
			DocumentFactory documentFactory = ((DocumentFactory) Actions.FactoryByRegTypeAction.Do(a.reg_type));
			DocumentBO doc = (DocumentBO) documentFactory.GetById(a.reg_id);

			System.out.println(doc);

			params.put("registrator", a);
			params.put("selectedAcc", accT.newInstance());
			if (!AccFactory.getDocRecords.call(params))
				System.out.println("Ошибка getDocRecords");

			// Перекладываем с линии на линию чтобы были вх.переменные.
			// TODO: как такую спайку искать в автомате? (можно искать по типу+похожему
			// названию var - предлагать)
			//
			// адаптер
			params.put("records", params.get("docRecords"));
			GroupAccRecords.call(params);

			System.out.println("--");
			for (AccRecord r : (List<AccRecord>) params.get("recordsGroup")) {
				System.out.println(r.ForAccCard());

				assert (r.Dt.getClass() == accT || r.Kt.getClass() == accT) : "Эта проводка тут не нужна";
				assert (r.Dt.sum != 0 && r.Kt.sum != 0) : "Сумма = 0";

				if (r.Dt.getClass() == accT)
					turnoverDt = Format.defaultRound(turnoverDt + r.Dt.sum);
				else if (r.Kt.getClass() == accT)
					turnoverKt = Format.defaultRound(turnoverKt + r.Kt.sum);
			}

			System.out.println("--------------------------------");
		}

		System.out.println("Обороты: " + turnoverDt + "\t" + turnoverKt);
	}

	// Группируем по счетам Д/К
	public static FunctionC2 GroupAccRecords = new FunctionC2() {
		{
			name = "GroupAccRecords";
			in_param_names = new String[] { "records" };
			out_param_names = new String[] { "recordsGroup" };
		}

		@Override
		protected boolean FuncBody(Map<String, Object> params) throws Exception {
			List<AccRecord> records = (List<AccRecord>) params.get("records");

			records = records.stream()//
					.collect(Collectors.groupingBy(r -> r.getGroupByCodeKey()))//
					.entrySet().stream()//
					.map(r -> r.getValue().stream().reduce((r1, r2) -> {
						r1.Dt.sum = Format.defaultRound(r1.Dt.sum + r2.Dt.sum);
						r1.Dt.count = Format.defaultRound(r1.Dt.count + r2.Dt.count);

						r1.Kt.sum = Format.defaultRound(r1.Kt.sum + r2.Kt.sum);
						r1.Kt.count = Format.defaultRound(r1.Kt.count + r2.Kt.count);
						return r1;
					}))//
					.map(r -> r.get())//
					.sorted((r1, r2) -> r1.getGroupByCodeKey().compareTo(r2.getGroupByCodeKey()))//
					.collect(Collectors.toList());
			// return out params
			params.put("recordsGroup", records);

			return true;
		};
	};
}