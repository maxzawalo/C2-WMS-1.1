package maxzawalo.c2.free.data.json.adapter;

import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.stream.JsonReader;

import maxzawalo.c2.free.bo.ContactInfo;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.data.json.CatalogueAdapter;

/**
 * Created by Max on 20.03.2017.
 */

public class ContractorAdapter extends CatalogueAdapter<Contractor> {

	public ContractorAdapter() {
		replaces.add(new ReplacedField("РезидентРБ", "is_resident"));
		replaces.add(new ReplacedField("ОсновнойДоговорКонтрагентаУИ", "main_contract"));
		replaces.add(new ReplacedField("ОсновнойБанковскийСчетНомерСчета", "main_bank_acccount"));
		replaces.add(new ReplacedField("БанкНаименование", "main_bank_name"));
	}

	@Override
	protected void DeserializeListItem(JsonReader reader, String fieldname) throws IOException {
		if (fieldname.equals(Contractor.fields.CONTACT_INFO)) {
			if (obj.contactInfo == null)
				obj.contactInfo = new ArrayList<>();
			ContactInfo info = ReadBONullable(reader, ContactInfo.class, fieldname);
			obj.contactInfo.add(info);
		}
	}

	// @Override
	// protected void Serialize(JsonWriter writer, BO bo) {
	//
	// super.Serialize(writer, bo);
	// Contractor cat = (Contractor) bo;
	// try {
	// writer.name(Contractor.fields.UNP);
	// writer.value(cat.unp);
	//
	// writer.name(Contractor.fields.LEGAL_ADDRESS);
	// writer.value(cat.legal_address);
	//
	// } catch (Exception e) {
	// log.ERROR("Serialize", e);
	// }
	// }

	// @Override
	// protected void Deserialize(JsonReader reader, String fieldname) throws
	// IOException {
	// if (fieldname.equals(Contractor.fields.UNP))
	// obj.unp = ReadStringNullable(reader);
	// else if (fieldname.equals(Contractor.fields.LEGAL_ADDRESS))
	// obj.legal_address = ReadStringNullable(reader);
	// else
	// super.Deserialize(reader, fieldname);
	// }
}