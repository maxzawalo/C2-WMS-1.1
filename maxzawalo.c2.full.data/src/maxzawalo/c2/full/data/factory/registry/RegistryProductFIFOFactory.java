package maxzawalo.c2.full.data.factory.registry;

import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.free.bo.registry.RegistryProduct;
import maxzawalo.c2.free.data.factory.registry.RegistryProductFactory;
import maxzawalo.c2.full.bo.registry.RegistryProductFIFO;

public class RegistryProductFIFOFactory extends RegistryProductFactory {
	public RegistryProductFIFOFactory() {
		transaction_mode = TRANSACTION_MODE.FIFO;
		typeBO = RegistryProductFIFO.class;
	}

	@Override
	public void ReplaceDocUsedReg(DocumentBO doc) {
		doc.usedRegistries.remove(RegistryProduct.class);
		doc.AddUsedRegistry(new RegistryProductFIFO());
	}
}