package maxzawalo.c2.full.data.json.adapter;

import maxzawalo.c2.base.bo.CatalogueBO;
import maxzawalo.c2.free.bo.ContactInfo;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.data.json.SlaveCatalogueAdapter;

/**
 * Created by Max on 20.03.2017.
 */

public class ContactInfoAdapter extends SlaveCatalogueAdapter<ContactInfo, Contractor> {

	public ContactInfoAdapter() {
		replaces.add(new ReplacedField("Представление", CatalogueBO.fields.NAME));
		replaces.add(new ReplacedField("ВидНаименование", "contact_type"));
	}
}