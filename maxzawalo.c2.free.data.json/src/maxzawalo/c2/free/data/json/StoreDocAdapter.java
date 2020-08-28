package maxzawalo.c2.free.data.json;

import java.io.IOException;

import com.google.gson.stream.JsonReader;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.free.bo.StrictForm;
import maxzawalo.c2.free.bo.store.StoreDocBO;
import maxzawalo.c2.free.bo.store.StoreTP;

/**
 * Created by Max on 20.03.2017.
 */

public class StoreDocAdapter<Doc> extends DocAdapter<Doc> {

	public StoreDocAdapter() {
		// super(new Invoice());
		replaces.add(new ReplacedField("Номер", BO.fields.CODE));
		replaces.add(new ReplacedField("Дата", DocumentBO.fields.DOC_DATE));
		replaces.add(new ReplacedField("ДатаВходящегоДокумента", "in_form_date"));
		replaces.add(new ReplacedField("НомерВходящегоДокумента", "in_form_number"));
		replaces.add(new ReplacedField("КонтрагентУИ", DocumentBO.fields.CONTRACTOR));
		// replaces.add(new ReplacedField("Всего", StoreDocBO.fields.total"));
		replaces.add(new ReplacedField("СуммаДокумента", "total_1c"));
		replaces.add(new ReplacedField("ВалютаДокументаУИ", "doc_currency"));
		replaces.add(new ReplacedField("ДоговорКонтрагентаУИ", StoreDocBO.fields.DOC_CONTRACT));

		replaces.add(new ReplacedField("РуководительУИ", "chief"));
		replaces.add(new ReplacedField("ГлавныйБухгалтерУИ", "chief_accountant"));
		replaces.add(new ReplacedField("ГлавныйБухгалтерУИ", "chief_accountant"));
		replaces.add(new ReplacedField("СкладУИ", StoreDocBO.fields.STORE));
	}

	// @Override
	// protected void Serialize(JsonWriter writer, BO bo) {
	// super.Serialize(writer, bo);
	// StoreDocBO doc = (StoreDocBO) bo;
	// try {
	// writer.name(DocumentBO.fields.DOC_DATE);
	// writer.value(Format.Show("yyyy-MM-dd'T'HH:mm:ss", doc.DocDate));
	//
	// // TODO: DocumentBO
	// writer.name(StoreDocBO.fields.CONTRACTOR.replace("_id", ""));
	// gson.toJson(gson.toJsonTree(doc.contractor), writer);
	//
	// writer.name(StoreDocBO.fields.TablePartProduct);
	//
	// // String t = doc.TablePartType().toString();
	// // t = t.substring(t.lastIndexOf(".") + 1);
	// // writer.value(t);
	// //
	// // writer.name("TablePart");
	// writer.beginArray();
	// for (Object item : doc.TablePartProduct) {
	// StoreTP tp = (StoreTP) item;
	// // writer.beginObject();
	// // writer.name(StoreTP.fields.PRODUCT.replace("_id", ""));
	// // gson.toJson(gson.toJsonTree(tp.product), writer);
	// gson.toJson(gson.toJsonTree(item), writer);
	//
	// // writer.endObject();
	// }
	// writer.endArray();
	// } catch (Exception e) {
	// log.ERROR("Serialize", e);
	// }
	// }

	@Override
	protected void DeserializeListItem(JsonReader reader, String fieldname) throws IOException {
		if (fieldname.equals(StoreDocBO.fields.TablePartProduct)) {
			StoreTP tp = (StoreTP) ReadBONullable(reader, ((StoreDocBO) obj).itemProductT, fieldname);
			((StoreDocBO) obj).TablePartProduct.add(tp);
		} else if (fieldname.equals(StoreDocBO.fields.TablePartService)) {
			StoreTP tp = (StoreTP) ReadBONullable(reader, ((StoreDocBO) obj).itemServiceT, fieldname);
			((StoreDocBO) obj).TablePartService.add(tp);
		} else if (fieldname.equals(StoreDocBO.fields.TablePartEquipment)) {
			StoreTP tp = (StoreTP) ReadBONullable(reader, ((StoreDocBO) obj).itemEquipmentT, fieldname);
			((StoreDocBO) obj).TablePartEquipment.add(tp);
		} else if (fieldname.equals(StoreDocBO.fields.StrictForms)) {
			StrictForm tp = ReadBONullable(reader, StrictForm.class, fieldname);
			((StoreDocBO) obj).strictForms.add(tp);
		}
	}

	// @Override
	// protected void Deserialize(JsonReader reader, String fieldname) throws
	// IOException {
	// try {
	// // TODO: DocumentBO
	// if (fieldname.equals(DocumentBO.fields.DOC_DATE))
	// ((DocumentBO) obj).DocDate = Format.GetDate(reader.nextString(),
	// "yyyy-MM-dd'T'HH:mm:ss");
	// else if (fieldname.equals(StoreDocBO.fields.CONTRACTOR.replace("_id",
	// "")))
	// ((StoreDocBO) obj).contractor = ReadBONullable(reader, Contractor.class);
	// else if (fieldname.equals(StoreDocBO.fields.TablePartProduct)) {
	// reader.beginArray();
	// while (reader.hasNext()) {
	// // reader.beginObject();
	// StoreTP tp = ReadBONullable(reader, ((StoreDocBO) obj).itemProductT);//
	// (StoreTP)((StoreDocBO)
	// // obj).itemProductT.newInstance();
	//
	// ((StoreDocBO) obj).TablePartProduct.add(tp);
	// // reader.endObject();
	// }
	// reader.endArray();
	// // Type t = new TypeToken<List<StoreTP>>() {
	// // }.getType();
	// // ((StoreDocBO) obj).TablePartProduct = gson.fromJson(reader,
	// // ((StoreDocBO) obj).TablePartProduct.getClass());
	//
	// } else
	// super.Deserialize(reader, fieldname);
	//
	// } catch (
	//
	// Exception e) {
	// e.printStackTrace();
	// }
	// }

}