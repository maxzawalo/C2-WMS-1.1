package maxzawalo.c2.free.data.json;

import maxzawalo.c2.base.bo.DocumentBO;

public class DocAdapter<T> extends BoAdapter<T> {

	public DocAdapter() {
		replaces.add(new ReplacedField("ОрганизацияУИ", DocumentBO.fields.ORGANIZATION));
		replaces.add(new ReplacedField("Проведен", DocumentBO.fields.COMMITED));
	}

	@Override
	public void SetSkipFields() {
		super.SetSkipFields();
		if (web_ui) {
			// skipFields.add(BO.fields.DOC_STATE);
		}
	}
}