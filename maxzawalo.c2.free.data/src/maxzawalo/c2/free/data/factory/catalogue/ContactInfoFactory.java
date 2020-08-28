package maxzawalo.c2.free.data.factory.catalogue;

import maxzawalo.c2.base.data.factory.SlaveCatalogueFactory;
import maxzawalo.c2.free.bo.ContactInfo;
import maxzawalo.c2.free.bo.Contractor;

public class ContactInfoFactory extends SlaveCatalogueFactory<ContactInfo, Contractor> {
	@Override
	protected ContactInfo GenerateCode(ContactInfo info) throws Exception {
		return info;
	}
}