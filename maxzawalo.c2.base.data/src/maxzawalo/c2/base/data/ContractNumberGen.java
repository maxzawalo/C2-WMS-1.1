package maxzawalo.c2.base.data;

import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.free.bo.Contract;
import maxzawalo.c2.free.bo.enums.ContractType;

public class ContractNumberGen {

	public static String GenerateNumber(Contract contract) throws Exception {
		if (contract.is_bill)
			return "";
		ContractType ctFilter = new ContractType().getEnumByName("С покупателем");
		if (!ctFilter.equals(contract.contract_type))
			return "";

		Dao<Contract, Integer> boDao = DbHelper.geDaos(Contract.class);
		QueryBuilder<Contract, Integer> builder = boDao.queryBuilder();
		builder.selectColumns(Contract.fields.NUMBER);
		// builder.orderBy(Contract.fields.NUMBER, false);
		Where<Contract, Integer> where = builder.where();
		where.eq(BO.fields.SYNC_FLAG, 0);
		where.and();
		where.eq(Contract.fields.CONTRACT_TYPE, ctFilter);
		where.and();
		where.ge(Contract.fields.DOC_DATE, Format.beginOfDay(Format.FirstDayOfYear(contract.DocDate)));
		where.and();
		where.le(Contract.fields.DOC_DATE, Format.endOfDay(Format.LastDayOfYear(contract.DocDate)));

		System.out.println(builder.prepareStatementString());

		String number = "1";
		List<String[]> results = builder.queryRaw().getResults();
		int intNum = 0;
		for (String[] row : results) {
			String n = Contract.CleanNumber(row[0]);
			if (!n.equals("") && isNumeric(n)) {
				// System.out.println(n);
				intNum = Math.max(intNum, Integer.parseInt(n));
			}
			number = "" + (intNum + 1);
		}
		// System.out.println(number + "|" + intNum);
		return number;
	}

	public static boolean isNumeric(String str) {
		return str.matches("[+-]?\\d*(\\.\\d+)?");
	}
}