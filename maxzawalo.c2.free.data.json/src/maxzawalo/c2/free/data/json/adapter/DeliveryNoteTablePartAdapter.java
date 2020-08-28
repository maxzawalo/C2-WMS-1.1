package maxzawalo.c2.free.data.json.adapter;

import maxzawalo.c2.free.bo.document.deliverynote.DeliveryNote;
import maxzawalo.c2.free.bo.document.deliverynote.DeliveryNoteTablePart;
import maxzawalo.c2.free.data.json.StoreTPAdapter;

public class DeliveryNoteTablePartAdapter {
	public static class Product extends StoreTPAdapter<DeliveryNoteTablePart.Product, DeliveryNote> {
	}

	public static class Service extends StoreTPAdapter<DeliveryNoteTablePart.Service, DeliveryNote> {
	}

	public static class Equipment extends StoreTPAdapter<DeliveryNoteTablePart.Equipment, DeliveryNote> {
	}
}