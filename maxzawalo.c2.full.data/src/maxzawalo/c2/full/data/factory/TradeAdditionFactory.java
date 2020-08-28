package maxzawalo.c2.full.data.factory;

import java.util.Collections;
import java.util.List;

import maxzawalo.c2.base.data.factory.CatalogueFactory;
import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.free.bo.Product;
import maxzawalo.c2.free.bo.Settings;
import maxzawalo.c2.free.cache.Cache;
import maxzawalo.c2.free.data.factory.catalogue.ProductFactory;
import maxzawalo.c2.full.bo.TradeAddition;

public class TradeAdditionFactory extends CatalogueFactory<TradeAddition> {

	public TradeAdditionFactory() {
		// CatalogueFactory - DeleteFilterOff();
		// Нам удаленные не нужны
		DeleteFilterOn();
	}

	public double getAddition(Product product, double cost_price, boolean delivery) {
		double percent = 0;
		Product parent = new ProductFactory().getParent(product);
		while (parent != null) {
			if (parent.addition != 0) {
				percent = parent.addition + ((delivery) ? Settings.defaultDeliveryAddition : 0);
				break;
			} else
				parent = new ProductFactory().Create().getParent(parent);
		}

		// Наценка по группе на нашли - берем из справочник Наценка
		if (percent == 0) {
			List<TradeAddition> all = GetAll();
			for (TradeAddition ta : all) {
				if (ta.deleted)
					// При синхронизации выключаются фильтры
					continue;

				percent = ta.percent + (delivery ? ta.percentDelivery : 0);
				if ((cost_price >= ta.fromSum) && ((ta.toSum == 0) || (cost_price < ta.toSum)))
					break;

				// Разрывы в периодах цен - берем со следующего периода (себе в
				// минус)
				if (cost_price < ta.fromSum)
					break;
			}
		}
		if (percent == 0) {
			log.ERROR("getAddition", "НАЦЕНКА = 0. " + product.name + " " + cost_price + " " + delivery);
			Console.I().INFO(getClass(), "getAddition", "НАЦЕНКА = 0. " + product.name + " " + cost_price + " " + delivery);

		}
		return Format.defaultRound(percent);
	}

	public double CalcAddition(Product product, double cost_price, boolean delivery) {
		double calcPrice = Format.defaultRound(cost_price * (1 + getAddition(product, cost_price, delivery) / 100));
		log.DEBUG("CalcAddition",
				cost_price + "|" + delivery + "|" + product.name + " " + product.code + "|" + calcPrice);
		if (calcPrice == cost_price)
			log.ERROR("CalcAddition", "НАЦЕНКА = 0. " + product.name + " " + cost_price + " " + delivery);
		return calcPrice;
	}

	@Override
	public List<TradeAddition> GetAll() {
		List<TradeAddition> all = Cache.I().getMapList(typeBO);
		Collections.sort(all, (ta1, ta2) -> ta1.fromSum.compareTo(ta2.fromSum));
		// builder.orderBy(TradeAddition.fields.FROM_SUM, true);

		for (TradeAddition ta : all)
			System.out.println(ta);

		return all;
	}
}