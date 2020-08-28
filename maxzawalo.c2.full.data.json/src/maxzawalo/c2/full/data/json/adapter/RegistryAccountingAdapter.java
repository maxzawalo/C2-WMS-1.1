package maxzawalo.c2.full.data.json.adapter;

import java.io.IOException;

import com.google.gson.stream.JsonReader;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.data.factory.FactoryBO;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.data.json.BoAdapter;
import maxzawalo.c2.full.bo.registry.RegistryAccounting;

public class RegistryAccountingAdapter extends BoAdapter<RegistryAccounting> {
	public RegistryAccountingAdapter() {
		replaces.add(new ReplacedField("ДокДата", "reg_date"));
		replaces.add(new ReplacedField("ДтСчет", "DtAccount"));
		replaces.add(new ReplacedField("КтСчет", "KtAccount"));

		replaces.add(new ReplacedField("ДтСубконто1УИ", "DtSubcount1"));
		replaces.add(new ReplacedField("КтСубконто1УИ", "KtSubcount1"));

		replaces.add(new ReplacedField("Сумма", "sum"));
		replaces.add(new ReplacedField("Регистратор", "RegMeta"));
	}

	@Override
	protected void Deserialize(JsonReader reader, String fieldname) throws IOException {
		if ("DtSubcount1".equals(fieldname)) {
			String uuid = reader.nextString();
			if (obj.DtAccount.startsWith("62") || obj.DtAccount.startsWith("60")) {
				obj.DtSubcount1 = (BO) new FactoryBO<>().Create(Contractor.class).GetByUUID(uuid);
				if (obj.DtSubcount1 == null)
					obj.DtSubcount1 = new BO();
			}
		} else if ("KtSubcount1".equals(fieldname)) {
			String uuid = reader.nextString();
			if (obj.KtAccount.startsWith("62") || obj.KtAccount.startsWith("60")) {
				obj.KtSubcount1 = (BO) new FactoryBO<>().Create(Contractor.class).GetByUUID(uuid);
				if (obj.KtSubcount1 == null)
					obj.KtSubcount1 = new BO();
			}
		} else
			super.Deserialize(reader, fieldname);
	}
}