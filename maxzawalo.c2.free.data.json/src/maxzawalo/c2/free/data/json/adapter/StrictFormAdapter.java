package maxzawalo.c2.free.data.json.adapter;

import maxzawalo.c2.free.bo.StrictForm;
import maxzawalo.c2.free.data.json.CatalogueAdapter;

public class StrictFormAdapter extends CatalogueAdapter<StrictForm> {
	public StrictFormAdapter() {
		replaces.add(new ReplacedField("НаименованиеПолное", "full_name"));
		replaces.add(new ReplacedField("НомерБланка", StrictForm.fields.FORM_NUMBER));
		replaces.add(new ReplacedField("СерияБСОКод", StrictForm.fields.FORM_BATCH));
		replaces.add(new ReplacedField("ТипБСОКод", StrictForm.fields.FORM_TYPE_CODE));
		replaces.add(new ReplacedField("ТипБСОНаименование", StrictForm.fields.FORM_TYPE_NAME));
		replaces.add(new ReplacedField("ТипСписания", StrictForm.fields.WRITE_OFF_TYPE));

		replaces.add(new ReplacedField("ДокУИ", "doc_uuid"));
	}
}