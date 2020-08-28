package maxzawalo.c2.full.bo.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import maxzawalo.c2.free.bo.store.StoreDocBO;
import maxzawalo.c2.full.bo.registry.RegistryAccounting;

public class ReconciliationReport extends StoreDocBO {
	public Date fromDate;
	public List<RegistryAccounting> convolution = new ArrayList<>();
	public double startSaldo = 0;
	public double endSaldo = 0;
	public double turnoverDebet = 0;
	public double turnoverKredit = 0;

	public ReconciliationReport() {
		code = "1";
	}

	@Override
	public List getTablePart4Rep() {
		return convolution;
	}
}