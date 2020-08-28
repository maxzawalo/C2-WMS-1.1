package maxzawalo.c2.free.bo.document.deliverynote;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.annotation.JsonField;
import maxzawalo.c2.base.bo.Coworker;
import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.base.bo.registry.RegType;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.bo.store.StoreDocBO;

@BoField(caption = "Расходная накладная")
public class DeliveryNote extends StoreDocBO<DeliveryNote> {

	public static class fields {
		public static final String PROCURATION_NUMBER = "procuration_number";
		public static final String PROCURATION_DATE = "procuration_date";
		public static final String PROCURATION_NAME = "procuration_name";
		public static final String SHIPMENT_PERMITED = "shipment_permited_id";
		public static final String SHIPMENT_PRODUCED = "shipment_produced_id";
		public static final String CLIENT = "client_id";
		public static final String WAYBILL = "waybill";
		public static final String DRIVER = "driver";
		public static final String SHIPMENT_MOTIVE = "shipment_motive";
		public static final String CAR = "car";
		public static final String SHIPPER_HAND_IN = "shipper_hand_in_id";
		public static final String LADING_PLACE = "lading_place";
		public static final String DISCHARGE_PLACE = "discharge_place";
	}

	@DatabaseField(index = true, width = 15, columnName = DeliveryNote.fields.PROCURATION_NUMBER)
	public String procuration_number = "";

	@DatabaseField(index = true, dataType = DataType.DATE_LONG, columnName = DeliveryNote.fields.PROCURATION_DATE)
	public Date procuration_date = new Date();

	@DatabaseField(index = true, width = 150, columnName = DeliveryNote.fields.PROCURATION_NAME)
	public String procuration_name = "";

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, columnName = DeliveryNote.fields.SHIPMENT_PERMITED)
	public Coworker shipment_permited = (Coworker) User.current.coworker;

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, columnName = DeliveryNote.fields.SHIPMENT_PRODUCED)
	public Coworker shipment_produced = (Coworker) User.current.coworker;

	@BoField(caption = "Заказчик")
	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, columnName = DeliveryNote.fields.CLIENT)
	public Contractor client;

	@BoField(caption = "Путевой лист")
	@DatabaseField(index = true, width = 30, columnName = DeliveryNote.fields.WAYBILL)
	public String waybill = "";

	@BoField(caption = "Водитель")
	@DatabaseField(index = true, width = 30, columnName = DeliveryNote.fields.DRIVER)
	public String driver = "";

	@BoField(caption = "Основание отпуска")
	@DatabaseField(index = true, width = 50, columnName = DeliveryNote.fields.SHIPMENT_MOTIVE)
	public String shipment_motive = "";

	@BoField(caption = "Автомобиль")
	@DatabaseField(index = true, width = 100, columnName = DeliveryNote.fields.CAR)
	public String car = "";

	@BoField(caption = "Сдал грузоотправитель")
	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, columnName = DeliveryNote.fields.SHIPPER_HAND_IN)
	public Coworker shipper_hand_in = (Coworker) User.current.coworker;

	@BoField(caption = "Пункт погрузки")
	@DatabaseField(index = true, width = 100, columnName = DeliveryNote.fields.LADING_PLACE)
	public String lading_place = "";

	@BoField(caption = "Пункт разгрузки")
	@DatabaseField(index = true, width = 100, columnName = DeliveryNote.fields.DISCHARGE_PLACE)
	public String discharge_place = "";

	@JsonField(columnName = "RowsPerPage")
	public int rowsPerPage = 1;

	public DeliveryNote() {
		reg_type = RegType.DeliveryNote;
	}

	@Override
	protected void setTpTypes() {
		itemProductT = DeliveryNoteTablePart.Product.class;
		itemServiceT = DeliveryNoteTablePart.Service.class;
		itemEquipmentT = DeliveryNoteTablePart.Equipment.class;
	}

	@Override
	protected List<String> getExceptFields() {
		List<String> exceptFields = super.getExceptFields();
		for (Field field : this.getClass().getFields()) {
			String fieldName = field.getName();
			if (fieldName.contains("procuration"))
				exceptFields.add(fieldName);
		}
		// exceptFields.add("totalVat");
		// exceptFields.add("total_1c");

		return exceptFields;
	}
}