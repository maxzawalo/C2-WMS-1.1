package maxzawalo.c2.free.data.json.adapter;

import java.io.IOException;

import com.google.gson.stream.JsonReader;

import maxzawalo.c2.base.bo.Coworker;
import maxzawalo.c2.free.data.json.CatalogueAdapter;

public class CoworkerAdapter extends CatalogueAdapter<Coworker> {
	public CoworkerAdapter() {
		replaces.add(new ReplacedField("Должность", "position"));
	}
	@Override
	protected void Deserialize(JsonReader reader, String fieldname) throws IOException {
		super.Deserialize(reader, fieldname);
	}
}