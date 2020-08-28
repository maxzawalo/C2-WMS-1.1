package maxzawalo.c2.free.data.factory;

import maxzawalo.c2.base.data.factory.FactoryBO;
import maxzawalo.c2.free.bo.Settings;
import maxzawalo.c2.free.bo.Store;
import maxzawalo.c2.free.data.factory.catalogue.ContractorFactory;
import maxzawalo.c2.base.data.factory.CoworkerFactory;
import maxzawalo.c2.free.data.factory.catalogue.CurrencyFactory;
import maxzawalo.c2.free.data.factory.catalogue.UnitsFactory;

public class GlobalConstantsFactory {
	// TODO: Save
	public static void Load() {

		Settings.myFirm = new ContractorFactory().GetByCode(Settings.get("МояКомпанияКод"));
		Settings.myFirm = new ContractorFactory().LoadContactInfo(Settings.myFirm);
		
		Settings.ChiefAccounting = new CoworkerFactory().GetByCode(Settings.get("ГлавБухКод"));// ГлавБухКод=СТ00-00002
		Settings.Head = new CoworkerFactory().GetByCode(Settings.get("ДиректорКод"));// ДиректорКод=00БС-00002

		// TODO: загрузка в UI - action
		Settings.mainCurrency = new CurrencyFactory().GetByCode(Settings.get("ОсновнаяВалютаКод"));// ОсновнаяВалютаКод=933
		Settings.mainUnits = new UnitsFactory().GetByCode(Settings.get("ОсновнаяЕдИзмКод"));// ОсновнаяЕдИзмКод=796
		Settings.mainStore = (Store) new FactoryBO<>().Create(Store.class).GetByCode(Settings.get("ОсновнойСкладКод"));// ОсновнойСкладКод=00-000007

		Settings.rentStore = (Store) new FactoryBO<>().Create(Store.class)
				.GetByCode(Settings.get("ПрокатИнструментаСкладКод"));
		Settings.rentContractorStore = (Store) new FactoryBO<>().Create(Store.class)
				.GetByCode(Settings.get("ИнструментУКонтрагентаСкладКод"));

	}
}