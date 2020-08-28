package maxzawalo.c2.free.data.json.adapter;

import maxzawalo.c2.free.bo.Units;
import maxzawalo.c2.free.data.json.CatalogueAdapter;

public class UnitsAdapter extends CatalogueAdapter<Units> {

	public UnitsAdapter() {		
	}

//	@Override
//	protected void Serialize(JsonWriter writer, BO bo) {
//
//		super.Serialize(writer, bo);
//		Units cat = (Units) bo;
//		try {
//			writer.name("full_name");
//			writer.value(cat.full_name);
//
//		} catch (Exception e) {
//			log.ERROR("Serialize", e);
//		}
//	}
}