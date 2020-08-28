package maxzawalo.c2.free.data.json.adapter;

import maxzawalo.c2.base.bo.SlaveCatalogueBO;
import maxzawalo.c2.free.bo.ContactInfo;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.data.json.SlaveCatalogueAdapter;

public class ContactInfoAdapter extends SlaveCatalogueAdapter<ContactInfo, Contractor> {
	public ContactInfoAdapter() {
		// replaces.add(new ReplacedField("НаименованиеПолное", "full_name"));
	}

	@Override
	protected boolean IsSkipField(String fieldName) {
		return (super.IsSkipField(fieldName) || (fieldName.equals(SlaveCatalogueBO.fields.OWNER.replace("_id", ""))));
	}
}