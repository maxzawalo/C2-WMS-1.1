package maxzawalo.c2.free.data.json;

import maxzawalo.c2.base.bo.CatalogueBO;

public class CatalogueAdapter<T> extends BoAdapter<T> {
	public CatalogueAdapter() {
		types.put(CatalogueBO.fields.PARENT.replace("_id", ""), typeBO);
		replaces.add(new ReplacedField("Наименование", CatalogueBO.fields.NAME));
		replaces.add(new ReplacedField("НаименованиеПолное", CatalogueBO.fields.FULL_NAME));
		replaces.add(new ReplacedField("РодительУИ", CatalogueBO.fields.PARENT));
		replaces.add(new ReplacedField("ЭтоГруппа", CatalogueBO.fields.IS_GROUP));
	}

//	@Override
//	protected void Serialize(JsonWriter writer, BO bo) {
//		super.Serialize(writer, bo);
//		CatalogueBO cat = (CatalogueBO) bo;
//		try {
//			writer.name(CatalogueBO.fields.NAME);
//			writer.value(cat.name);
//
//			writer.name(CatalogueBO.fields.FULL_NAME);
//			writer.value(cat.full_name);
//
//		} catch (Exception e) {
//			log.ERROR("Serialize", e);
//		}
//	}

//	@Override
//	protected void Deserialize(JsonReader reader, String fieldname) throws IOException {
//		if (fieldname.equals(CatalogueBO.fields.NAME)) {
//			((CatalogueBO) obj).name = reader.nextString().trim();
//		} else if (fieldname.equals(CatalogueBO.fields.FULL_NAME)) {
//			((CatalogueBO) obj).full_name = reader.nextString().trim();
//		} else
//			super.Deserialize(reader, fieldname);
//	}
}