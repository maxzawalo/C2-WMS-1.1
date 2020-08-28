package maxzawalo.c2.full.data.factory.document;

import maxzawalo.c2.free.data.factory.catalogue.LotOfProductFactoryFree;
import maxzawalo.c2.free.data.factory.document.InvoiceFactoryFree;
import maxzawalo.c2.full.data.factory.catalogue.LotOfProductFactoryFull;

public class InvoiceFactoryFull extends InvoiceFactoryFree {
	protected LotOfProductFactoryFree CreateLotFactory() {
		return new LotOfProductFactoryFull();
	}
}