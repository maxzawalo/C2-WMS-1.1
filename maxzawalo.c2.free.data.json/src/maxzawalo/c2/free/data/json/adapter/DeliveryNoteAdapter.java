package maxzawalo.c2.free.data.json.adapter;

import maxzawalo.c2.free.bo.document.deliverynote.DeliveryNote;
import maxzawalo.c2.free.data.json.StoreDocAdapter;

public class DeliveryNoteAdapter extends StoreDocAdapter<DeliveryNote> {

	public DeliveryNoteAdapter() {
		replaces.add(new ReplacedField("ДоверенностьНомер", "procuration_number"));
		replaces.add(new ReplacedField("ДоверенностьДата", "procuration_date"));
		replaces.add(new ReplacedField("ДоверенностьФИО", "procuration_name"));
		replaces.add(new ReplacedField("ОтпускРазрешилУИ", "shipment_permited"));
		replaces.add(new ReplacedField("ОтпускПроизвелУИ", "shipment_produced"));
	}

//	@Override
//	protected void Serialize(JsonWriter writer, BO bo) {
//		super.Serialize(writer, bo);
//		DeliveryNote doc = (DeliveryNote) bo;
//		try {
//			writer.name(DeliveryNote.fields.WAYBILL);
//			writer.value(doc.waybill);
//
//			writer.name(DeliveryNote.fields.CAR);
//			writer.value(doc.car);
//
//			writer.name(DeliveryNote.fields.DRIVER);
//			writer.value(doc.driver);
//
//			writer.name(DeliveryNote.fields.CLIENT);
//			gson.toJson(gson.toJsonTree(doc.client), writer);
//
//			writer.name(DeliveryNote.fields.SHIPMENT_MOTIVE);
//			writer.value(doc.shipment_motive);
//
//			writer.name(DeliveryNote.fields.LADING_PLACE);
//			writer.value(doc.lading_place);
//
//			writer.name(DeliveryNote.fields.DISCHARGE_PLACE);
//			writer.value(doc.discharge_place);
//
//			writer.name(DeliveryNote.fields.SHIPMENT_PERMITED);
//			gson.toJson(gson.toJsonTree(doc.shipment_permited), writer);
//
//			writer.name(DeliveryNote.fields.SHIPMENT_PRODUCED);
//			gson.toJson(gson.toJsonTree(doc.shipment_produced), writer);
//
//			writer.name(DeliveryNote.fields.SHIPPER_HAND_IN);
//			gson.toJson(gson.toJsonTree(doc.shipper_hand_in), writer);
//			//
//			// writer.name(DeliveryNote.fields.PROCURATION_DATE);
//			// writer.value(Format.Show("yyyy-MM-dd'T'HH:mm:ss", doc.DocDate));
//			//
//			// writer.name(DeliveryNote.fields.PROCURATION_NUMBER);
//			// writer.value(doc.procuration_number);
//			//
//			// writer.name(DeliveryNote.fields.PROCURATION_NAME);
//			// writer.value(doc.procuration_name);
//		} catch (Exception e) {
//			log.ERROR("Serialize", e);
//		}
//	}

//	@Override
//	protected void Deserialize(JsonReader reader, String fieldname) throws IOException {
//		if (fieldname.equals(DeliveryNote.fields.WAYBILL))
//			obj.waybill = ReadStringNullable(reader);
//		else if (fieldname.equals(DeliveryNote.fields.CAR))
//			obj.car = ReadStringNullable(reader);
//		else if (fieldname.equals(DeliveryNote.fields.DRIVER))
//			obj.driver = ReadStringNullable(reader);
//		else if (fieldname.equals(DeliveryNote.fields.CLIENT))
//			obj.client = ReadBONullable(reader, Contractor.class);
//		else if (fieldname.equals(DeliveryNote.fields.SHIPMENT_MOTIVE))
//			obj.shipment_motive = ReadStringNullable(reader);
//		else if (fieldname.equals(DeliveryNote.fields.LADING_PLACE))
//			obj.lading_place = ReadStringNullable(reader);
//		else if (fieldname.equals(DeliveryNote.fields.DISCHARGE_PLACE))
//			obj.discharge_place = ReadStringNullable(reader);
//
//		else if (fieldname.equals(DeliveryNote.fields.SHIPMENT_PERMITED))
//			obj.shipment_permited = ReadBONullable(reader, Coworker.class);
//		else if (fieldname.equals(DeliveryNote.fields.SHIPMENT_PRODUCED))
//			obj.shipment_produced = ReadBONullable(reader, Coworker.class);
//		else if (fieldname.equals(DeliveryNote.fields.SHIPPER_HAND_IN))
//			obj.shipper_hand_in = ReadBONullable(reader, Coworker.class);
//		else
//			super.Deserialize(reader, fieldname);
//	}
}