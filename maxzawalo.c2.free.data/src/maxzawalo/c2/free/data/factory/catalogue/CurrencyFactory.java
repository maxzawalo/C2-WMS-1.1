package maxzawalo.c2.free.data.factory.catalogue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import maxzawalo.c2.base.data.factory.CatalogueFactory;
import maxzawalo.c2.base.utils.FileUtils;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.base.utils.Global;
import maxzawalo.c2.free.bo.Currency;
import maxzawalo.c2.free.cache.Cache;

public class CurrencyFactory extends CatalogueFactory<Currency> {

	public double Convert(String name, Date date, double value) {
		List<Currency> all = LoadFromFile(name);
		Currency selected = new Currency();
		for (int i = 0; i < all.size() - 1; i++) {
			if (date.getTime() >= all.get(i).date.getTime() && date.getTime() < all.get(i + 1).date.getTime()) {
				selected = all.get(i);
				break;
			}
		}
		return Format.defaultRound(value / selected.scale * selected.rate);
	}

	public List<Currency> LoadFromFile(String name) {
		if (Global.InMemoryGroupTransaction) {
			System.out.println("TODO: CurrencyFactory.LoadFromFile InMemoryGroupTransaction ");
		}
		// "RUB"
		List<Currency> all = Cache.I().getList("Currency." + name);

		if (all == null) {
			all = new ArrayList<>();
			String data = FileUtils.readFileAsString(FileUtils.GetDataDir() + name);
			for (String line : data.split("[\\r\\n]+")) {
				String parts[] = line.split("\t");

				if (parts[0].equals(""))
					continue;

				Currency currency = new Currency();
				currency.date = Format.GetDate(parts[0]);
				currency.rate = Double.parseDouble(parts[1]);
				currency.scale = Double.parseDouble(parts[2]);
				all.add(currency);
			}

			Cache.I().putList("Currency." + name, all, 3 * 60);
		}

		return all;
	}
}