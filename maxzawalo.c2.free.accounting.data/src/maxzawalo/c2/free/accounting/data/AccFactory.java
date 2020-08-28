package maxzawalo.c2.free.accounting.data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.j256.ormlite.stmt.Where;

import maxzawalo.c2.base.FunctionC2;
import maxzawalo.c2.base.bo.registry.Registry;
import maxzawalo.c2.base.data.factory.RegistryFactory;
import maxzawalo.c2.free.accounting.AccList;
import maxzawalo.c2.free.accounting.AccRecord;
import maxzawalo.c2.free.bo.registry.AccAcc;

public class AccFactory extends RegistryFactory<AccAcc> {
	public static FunctionC2 getDocRecords = new FunctionC2() {
		{
			in_param_names = new String[] { "registrator", "selectedAcc" };
			out_param_names = new String[] { "docRecords" };
			name = "getDocRecords";
		}

		@Override
		protected boolean FuncBody(Map<String, Object> params) throws Exception {
			Registry registrator = (Registry) params.get("registrator");
			AccAcc selectedAcc = (AccAcc) params.get("selectedAcc");

			List<AccRecord> docRecords = new ArrayList<>();

			List<AccAcc> docEntries = new AccFactory().SelectDocEntries(registrator);

			for (int num = 1; num < 100; num++) {
				int n = num;
				List<AccAcc> forRecord = docEntries.stream().filter(e -> e.transaction_num == n)
						.collect(Collectors.toList());
				// TODO: assert?
				if (forRecord.size() != 2)
					break;

				AccRecord newRec = new AccRecord();
				newRec.Dt = (forRecord.get(0).is_debit ? forRecord.get(0) : forRecord.get(1));
				newRec.Kt = (!forRecord.get(0).is_debit ? forRecord.get(0) : forRecord.get(1));

				if (selectedAcc != null)
					if (newRec.Dt.getClass() != selectedAcc.getClass()
							&& newRec.Kt.getClass() != selectedAcc.getClass())
						continue;// Пропускаем
									// добавление
									// если
									// не
									// попадает
									// в
									// фильтр

				docRecords.add(newRec);
			}
			// return out params
			params.put("docRecords", docRecords);

			return true;
		};

	};

	@Override
	public List<AccAcc> SelectDocEntries(Registry r) throws Exception {
		List<AccAcc> docEntries = new ArrayList<>();
		for (Class acc : AccList.classes) {
			docEntries.addAll(new RegistryFactory().Create(acc).SelectDocEntries(r));
		}
		return docEntries;
	}

	/**
	 * В бухг проводках не учитываются остатки на дату. Берем проводки только из
	 * проводимого периода
	 */
	@Override
	protected void FromDateFilter(Date fromDate, Where<AccAcc, Integer> where) throws SQLException {
		where.and();
		where.ge(Registry.fields.REG_DATE, fromDate);
	}

	/**
	 * В бухг проводках не исп-м Баланс(остатки)...пока
	 */
	@Override
	protected void Add2BalanceCache(Date fromDate, List<AccAcc> all) {

	}

	public static Class[] GetAccByCode(String code) {
		return Arrays.asList(AccList.classes).stream()//
				.map(a -> {
					try {
						return (AccAcc) a.newInstance();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				})//
				.filter(a -> a.code.startsWith(code))//
				.map(v -> v.getClass()).toArray(Class[]::new);
	}

	public static FunctionC2 GetByPeriod = new FunctionC2() {
		{
			name = "GetByPeriod";
			in_param_names = new String[] { "account", "startDate", "endDate" };
			out_param_names = new String[] { "acc_by_period" };
		}

		@Override
		protected boolean FuncBody(Map<String, Object> params) throws Exception {
			List<AccAcc> trs = new AccFactory().Create((Class) params.get("account"))//
					.GetByPeriod((Date) params.get("startDate"), (Date) params.get("endDate"));
			// return out params
			params.put("acc_by_period", trs);

			return true;
		};
	};

}