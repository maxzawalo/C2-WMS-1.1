package maxzawalo.c2.full.bo;

import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.bo.CatalogueBO;

/**
 * Created by Max on 10.03.2017.
 */
@BoField(caption = "Наценка")
public class TradeAddition extends CatalogueBO<TradeAddition> {

	public static class fields {
		public static final String FROM_SUM = "fromSum";
		public static final String TO_SUM = "toSum";
		public static final String PERCENT = "percent";
		public static final String PERCENT_DELIVERY = "percentDelivery";
	}

	@DatabaseField(columnName = TradeAddition.fields.FROM_SUM)
	public Double fromSum = 0.0;

	@DatabaseField(columnName = TradeAddition.fields.TO_SUM)
	public Double toSum = 0.0;

	@DatabaseField(columnName = TradeAddition.fields.PERCENT)
	public Double percent = 0.0;

	@DatabaseField(columnName = TradeAddition.fields.PERCENT_DELIVERY)
	public double percentDelivery = 0;

	public TradeAddition() {
	}

	@Override
	public String toString() {
		return "Наценка " + fromSum + " " + toSum + " " + percent + " " + percentDelivery;
	}
}