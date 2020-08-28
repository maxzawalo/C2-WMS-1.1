package maxzawalo.c2.free.data.json;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import maxzawalo.c2.base.bo.TablePartItem;
import maxzawalo.c2.free.bo.store.StoreTP;

/**
 * Created by Max on 20.03.2017.
 */

public class StoreTPAdapter<TypeBO, Doc> extends BoAdapter<TypeBO> {
	protected Class<Doc> typeDoc;

	public StoreTPAdapter() {

		Class clazz = this.getClass();
		Type[] gParams = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments();
		if (gParams.length == 2) {
			this.typeDoc = (Class<Doc>) gParams[1];
		}

		types.put(TablePartItem.fields.DOC.replace("_id", ""), typeDoc);

		// super(new InvoiceTablePart());
		// ДокУИ doc_id
		replaces.add(new ReplacedField("Количество", StoreTP.fields.COUNT));
		replaces.add(new ReplacedField("Цена", StoreTP.fields.PRICE));
		replaces.add(new ReplacedField("СтавкаНДС", StoreTP.fields.RATE_VAT));
		replaces.add(new ReplacedField("ДокУИ", TablePartItem.fields.DOC));
		// replaces.add(new ReplacedField("ДатаДок",
		// DocumentBO.fields.DOC_DATE));
		replaces.add(new ReplacedField("НоменклатураУИ", StoreTP.fields.PRODUCT));
		replaces.add(new ReplacedField("СуммаНДС", StoreTP.fields.SUM_VAT));
		replaces.add(new ReplacedField("Сумма", StoreTP.fields.SUM));
		replaces.add(new ReplacedField("Всего", StoreTP.fields.TOTAL));

		// "": "СТ00-000100",
		// "": "00-0008839 ",
		// "": 1,
		// "": 112.94,
		// "": "20%"
	}
	// TODO: 1c
	// @Override
	// protected void Deserialize(JsonReader reader, String fieldname) throws
	// IOException {
	// if (StoreTP.fields.RATE_VAT.equals(fieldname)) {
	// JsonToken token = reader.peek();
	// String rateVat = reader.nextString();
	// rateVat = rateVat.replace("%", "");
	//
	// // НДС 20% / 120% - это что?
	// // Это когда цена уже включает НДС в колодке 4.
	// // Для расчёта 7, берётся не 20/100, а 20/120
	// if (rateVat.contains("/"))
	// ((StoreTP) obj).sum_contains_vat = true;
	//
	// rateVat = rateVat.split("/")[0].trim();
	// if (rateVat.equals("Без НДС"))
	// rateVat = "0";
	// ((StoreTP) obj).rateVat = Format.extractDouble(rateVat);
	// } else if ("doc_id".equals(fieldname)) {
	// JsonToken token = reader.peek();
	// String uuid = reader.nextString();
	// // uuid = uuid.trim();
	//
	// try {
	// StoreDocBO doc = (StoreDocBO) new
	// DocumentFactory<>().Create(typeDoc).GetByUUID(uuid);
	// if (doc != null)
	// ((StoreTP) obj).doc_id = doc.id;
	// else
	// log.WARN("Deserialize", "Не найден документ для таб. части " + uuid);
	// } catch (Exception e) {
	// log.ERROR("Deserialize", e);
	// }
	//
	// }
	//// else if (StoreTP.fields.PRODUCT.replace("_id", "");;.equals(fieldname))
	// {
	//// JsonToken token = reader.peek();
	//// String uuid = reader.nextString();
	//// // uuid = uuid.trim();
	//// ((CommonTablePart) obj).product = new Product();
	//// ((CommonTablePart) obj).product = ((CommonTablePart)
	// obj).product.GetByUUID(UUID.fromString(uuid));
	//// }
	//
	// else {
	// super.Deserialize(reader, fieldname);
	// }
	// }

	// @Override
	// protected void Serialize(JsonWriter writer, BO bo) {
	// super.Serialize(writer, bo);
	//
	// try {
	// writer.name(StoreTP.fields.PRODUCT.replace("_id", ""));
	// gson.toJson(gson.toJsonTree(((StoreTP) bo).product), writer);
	//
	// writer.name(StoreTP.fields.COUNT);
	// writer.value(((StoreTP) bo).count);
	//
	// writer.name(StoreTP.fields.PRICE);
	// writer.value(((StoreTP) bo).price );
	//
	// writer.name(StoreTP.fields.SUM);
	// writer.value(((StoreTP) bo).sum);
	//
	// writer.name(StoreTP.fields.RATE_VAT);
	// writer.value(((StoreTP) bo).rateVat);
	//
	// writer.name(StoreTP.fields.SUM_VAT);
	// writer.value(((StoreTP) bo).sumVat);
	//
	// writer.name(StoreTP.fields.TOTAL);
	// writer.value(((StoreTP) bo).total);
	//
	// } catch (IOException e) {
	// 
	// e.printStackTrace();
	// }
	// }

	// @Override
	// protected void Deserialize(JsonReader reader, String fieldname) throws
	// IOException {
	// if (fieldname.equals(StoreTP.fields.PRODUCT.replace("_id", "")))
	// ((StoreTP) obj).product = ReadBONullable(reader, Product.class);
	// else if (fieldname.equals(StoreTP.fields.COUNT))
	// ((StoreTP) obj).count = Double.parseDouble(reader.nextString());
	// else if (fieldname.equals(StoreTP.fields.PRICE))
	// ((StoreTP) obj).price = Double.parseDouble(reader.nextString());
	// else if (fieldname.equals(StoreTP.fields.SUM))
	// ((StoreTP) obj).sum = Double.parseDouble(reader.nextString());
	// else if (fieldname.equals(StoreTP.fields.RATE_VAT))
	// ((StoreTP) obj).rateVat = Double.parseDouble(reader.nextString());
	// else if (fieldname.equals(StoreTP.fields.SUM_VAT))
	// ((StoreTP) obj).sumVat = Double.parseDouble(reader.nextString());
	// else if (fieldname.equals(StoreTP.fields.TOTAL))
	// ((StoreTP) obj).total = Double.parseDouble(reader.nextString());
	// else
	// super.Deserialize(reader, fieldname);
	// }
}