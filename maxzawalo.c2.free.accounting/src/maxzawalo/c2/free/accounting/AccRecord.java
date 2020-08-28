package maxzawalo.c2.free.accounting;

import maxzawalo.c2.free.bo.registry.AccAcc;

public class AccRecord {
	public AccAcc Dt;
	public AccAcc Kt;

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return Dt + "\t" + Kt;
	}

	public String getGroupByCodeKey() {
		return Dt.code + Kt.code;
	}

	public String ForAccCard() {
		return toString();
	}
}