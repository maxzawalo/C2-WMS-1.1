package maxzawalo.c2.free.data.json;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.annotation.JsonField;
import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.data.factory.FactoryBO;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.base.utils.Global;
import maxzawalo.c2.base.utils.Logger;
import maxzawalo.c2.free.bo.enums.EnumC2;

/**
 * Created by Max on 20.03.2017.
 */

public class BoAdapter<TypeBO> extends TypeAdapter<BO> {

	public Gson gson;

	protected Logger log = Logger.getLogger(this.getClass().getSimpleName());

	public TypeBO obj;
	Class<TypeBO> typeBO;
	protected List<ReplacedField> replaces = new ArrayList<>();

	Map<String, Field> jsonFields = new HashMap<>();
	protected Set<String> skipFields = new HashSet<>();

	// Для тех кто BO надо норм. В новой ORM не актуально
	protected Map<String, Class> types = new HashMap<>();

	public boolean mode1c = false;
	public boolean web_ui = false;

	public BoAdapter() {
		Class clazz = this.getClass();
		Type[] gParams = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments();
		if (gParams.length != 0)// для dao - иначе не создает BO
			this.typeBO = (Class<TypeBO>) gParams[0];

		replaces.add(new ReplacedField("Код", BO.fields.CODE));
		replaces.add(new ReplacedField("ПометкаУдаления", BO.fields.DELETED));
		replaces.add(new ReplacedField("УИ", BO.fields.UUID));

		for (Field field : typeBO.getFields()) {
			DatabaseField df = field.getAnnotation(DatabaseField.class);
			if (df != null) {
				String fieldName = df.columnName();

				fieldName = (fieldName.equals("") ? field.getName().toLowerCase() : fieldName);
				fieldName = fieldName.replace("_id", "");
				jsonFields.put(fieldName, field);
			} else {
				JsonField jf = field.getAnnotation(JsonField.class);
				if (jf != null)
					jsonFields.put(jf.columnName(), field);
			}
		}
	}

	public void SetSkipFields() {
		// Если не приходит BO - значит надо включить сюда. Т.к. где то данный
		// класс есть ниже по иерархии и ломает структуру.
		// TODO: разобраться с Level

		skipFields.add("created_by");
		skipFields.add("changed_by");

		if (web_ui) {
			skipFields.add("sync_flag");
			skipFields.add("uuid");
			// skipFields.add("deleted");

			skipFields.add("created");
			skipFields.add("changed");

			skipFields.add("meta");
		} else
			skipFields.add("locked_by");
	}

	public class ReplacedField {
		public String external = "";
		public String local = "";

		public ReplacedField(String external, String local) {
			this.external = external;
			this.local = local.replace("_id", "");
		}
	}

	protected String getReplace(String external) {
		for (ReplacedField r : replaces)
			if (r.external.equals(external))
				return r.local;
		return external;
	}

	protected void Serialize(JsonWriter writer, BO bo) {
		try {
			for (Field field : bo.getClass().getFields()) {
				DatabaseField df = field.getAnnotation(DatabaseField.class);
				if (df != null) {
					String fieldName = df.columnName();

					fieldName = (fieldName.equals("") ? field.getName().toLowerCase() : fieldName);
					fieldName = fieldName.replace("_id", "");
					if (IsSkipField(fieldName))
						continue;
					writer.name(fieldName);

					if (fieldName.equals(""))
						System.out.println("fieldName=" + fieldName);
					Object value = field.get(bo);

					WriteValue(writer, field.getType(), value);

					// if (value == null)
					// writer.nullValue();
					// else {
					// Class type = field.getType();
					// if (type == Date.class) {
					// writer.value(((Date) value).getTime());
					// // } else if (type == Double.class || double.class
					// // == type) {
					// // writer.value((Double) value);
					// // } else if (type == Boolean.class || type ==
					// // boolean.class) {
					// // writer.value((Boolean) value);
					// // } else if (type == String.class) {
					// // writer.value("" + value);
					// // } else if (type == UUID.class) {
					// // writer.value("" + value);
					// } else {
					// // writer.value("");
					// gson.toJson(gson.toJsonTree(value), writer);
					// }
					// }
				} else {
					JsonField jf = field.getAnnotation(JsonField.class);
					if (jf != null) {
						String fieldName = jf.columnName();
						if (IsSkipField(fieldName))
							continue;
						writer.name(fieldName);
						Object value = field.get(bo);
						WriteValue(writer, field.getType(), value);
					}
				}
			}
			// writer.name(BO.fields.ID);
			// writer.value(bo.id);
			//
			// writer.name(BO.fields.CODE);
			// writer.value(bo.code);

			// TODO: BO.fields.CREATED BO.fields.CHANGED
			// writer.name(BO.fields.CREATED);
			// writer.value(bo.created);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void WriteValue(JsonWriter writer, Type type, Object value) throws IOException {
		if (value == null)
			writer.nullValue();
		else if (type == List.class) {
			writer.beginArray();
			for (Object item : (List) value) {
				gson.toJson(gson.toJsonTree(item), writer);
			}
			writer.endArray();
		} else if (type == Date.class) {
			writer.value(((Date) value).getTime());
		} else {
			gson.toJson(gson.toJsonTree(value), writer);
		}
	}

	protected boolean IsSkipField(String fieldName) {
		return skipFields.contains(fieldName);
	}

	@Override
	public BO read(JsonReader reader) throws IOException {
		// BO bo = null;
		try {
			if (!web_ui || (web_ui && obj == null))
				obj = typeBO.newInstance();

			if (mode1c) {
				// При приеме из 1С и др. систем, считаем что объект не
				// измененен
				((BO) obj).changed = ((BO) obj).created;
			}
		} catch (Exception e) {
			log.FATAL("read", e);
		}
		reader.beginObject();
		String fieldname = null;

		while (reader.hasNext()) {
			JsonToken token = reader.peek();
			if (token.equals(JsonToken.NAME)) {
				fieldname = reader.nextName();
				// Ищем по таблице сопоставления полей
				fieldname = getReplace(fieldname);
			}
			// else if (BO.fields.CODE.equals(fieldname)) {
			// token = reader.peek();
			// bo.code = reader.nextString();
			// }
			else {
				Deserialize(reader, fieldname);
				// if (obj instanceof Bill && ((Bill) obj).contractor == null) {
				// System.out.println(fieldname);
				// }
			}
		}
		reader.endObject();

		// if (obj instanceof Bill) {
		// System.out.println("");
		// }

		return (BO) obj;
	}

	protected void Deserialize(JsonReader reader, String fieldname) throws IOException {
		if (!mode1c) {
			try {
				Field field = jsonFields.get(fieldname);
				// Если версия выше, то скорее всего будет меньше полей
				if (field != null) {
					Class type = field.getType();

					if (type == List.class) {
						reader.beginArray();
						while (reader.hasNext()) {
							DeserializeListItem(reader, fieldname);
						}
						reader.endArray();

					} else if (type == Date.class) {
						field.set(obj, new Date(Long.parseLong(reader.nextString())));
					} else if (type == Double.class || double.class == type) {
						field.set(obj, Double.parseDouble(reader.nextString()));
					} else if (type == Boolean.class || type == boolean.class) {
						field.set(obj, reader.nextBoolean());
					} else if (type == String.class) {
						// TODO: обрезка?
						field.set(obj, ReadStringNullable(reader));
					} else if (type == UUID.class) {
						field.set(obj, UUID.fromString(reader.nextString()));
					} else {
						field.set(obj, ReadBONullable(reader, type, fieldname));
					}
				}
			} catch (Exception e) {
				log.ERROR("Deserialize", e);
			}

		} else {
			// Синхронизация 1C
			// System.out.println(fieldname);
			// if ("store".equals(fieldname))
			// System.out.println();

			try {
				JsonToken token = reader.peek();

				Field field = null;
				try {
					field = obj.getClass().getField(fieldname);
				} catch (NoSuchFieldException fe) {
					((BO) obj).calcFields.put(getReplace(fieldname), reader.nextString().trim());
					return;
				}
				DatabaseField df = field.getAnnotation(DatabaseField.class);
				Class type = field.getType();
				if (type == Integer.class || type == int.class) {
					// TODO: многие к одному
					if ("doc".equals(fieldname)) {
						String data = reader.nextString().trim();
						BO bo = GetByUUID(fieldname, type, data);
						if (bo != null)
							field.set(obj, bo.id);
					} else
						field.set(obj, reader.nextInt());
				} else if (type == Date.class) {
					field.set(obj, Format.extractJsonDate(reader.nextString()));
					// } else if (type == Integer.class || int.class == type) {
					// field.set(obj, Integer.parseInt(reader.nextString()));
				} else if (type == Double.class || double.class == type) {
					field.set(obj, Format.extractDouble(reader.nextString()));
				} else if (type == Boolean.class || type == boolean.class) {
					field.set(obj, reader.nextBoolean());
					// try {
					// field.set(obj, Integer.valueOf(fieldData) > 0);
					// } catch (NumberFormatException e) {
					// field.set(obj, Boolean.parseBoolean(fieldData));
					// }
				} else if (type == String.class) {
					String data = reader.nextString().trim();
					int fieldWidth = ((df != null) ? df.width() : 0);
					if (fieldWidth != 0 && data.length() > fieldWidth) {
						log.WARN("Deserialize",
								"Строка [" + data + "] обрезана " + this.getClass().getSimpleName() + "." + fieldname);
						data = data.substring(0, fieldWidth);
						// log.WARN("Строка обрезана " +
						// this.clone().getClass().getSimpleName() + "." +
						// fieldname);
					}
					field.set(obj, data);// считаем что поле текстовое
				} else if (type == UUID.class) {
					String data = reader.nextString().trim();
					field.set(obj, UUID.fromString(data));
				} else {
					// try {
					String data = reader.nextString().trim();
					// if (uuid.equals("00000000-0000-0000-0000-000000000000"))
					// {
					// log.DEBUG("", "");
					// }

					BO bo = null;
					// TODO: cache
					if (Arrays.asList(Global.enums).contains(type)) {
						bo = (BO) type.newInstance();
						bo = (BO) ((EnumC2) bo).getEnumByName(data);
					} else if (BO.ChildOfBO(type)) {
						// В смысле наследники BO
						// if ("doc".equals(fieldname))
						// System.out.println("-ChildOfBO");

						bo = GetByUUID(fieldname, type, data);

						// if (fieldname.equals(CatalogueBO.fields.PARENT)) {
						// bo = (BO) (this.typeBO).newInstance();
						// bo.uuid = UUID.fromString(data);

						// if (fieldname.equals(SlaveCatalogueBO.fields.OWNER)) {
						// bo = (BO) ((SlaveCatalogueAdapter) this).ownerType.newInstance();
						// bo.uuid = UUID.fromString(data);
						// }
						// }
					}
					// else if (fieldname.equals(SlaveCatalogueBO.fields.OWNER)) {
					// bo = (BO) ((SlaveCatalogueAdapter) this).ownerType.newInstance();
					// bo.uuid = UUID.fromString(data);
					// }
					// else if (fieldname.equals(CatalogueBO.fields.PARENT)) {
					// bo = (BO) (this.typeBO).newInstance();
					// bo.uuid = UUID.fromString(data);
					// }
					if (data.equals("00000000-0000-0000-0000-000000000000"))
						bo = null;

					if (bo == null) {
						// Временно сохраняем для дальнейшей загрузки
						((BO) obj).calcFields.put(fieldname, data);
					} else {
						field.set(obj, bo);
					}
				}
			} catch (IllegalStateException e) {
				// log.DEBUG("Deserialize", "Настроить сопоставление полей |" +
				// fieldname);
				if (e.getMessage().contains("BOOLEAN"))
					reader.nextBoolean();
				// log.DEBUG(e.getMessage());
			} catch (Exception e) {
				log.ERROR("Deserialize", "Настроить сопоставление полей |" + fieldname);
				log.ERROR("Deserialize", e);
			}
		}

	}

	public BO GetByUUID(String fieldname, Class type, String data) {
		Class t = types.get(fieldname);
		if (t != null)
			type = t;
		// bo = (BO) type.newInstance();
		BO bo = (BO) new FactoryBO<>().Create(type).GetByUUID(data);
		return bo;
	}

	protected void DeserializeListItem(JsonReader reader, String fieldname) throws IOException {
	}

	@Override
	public void write(JsonWriter writer, BO bo) throws IOException {
		writer.beginObject();
		Serialize(writer, bo);

		// for (Field field : bill.getClass().getFields())//
		// .getDeclaredField(fieldName);
		// {
		// //field.setAccessible(true);
		// //String value = (String)field.get(object);
		// try {
		// writer.name(field.getName());
		// writer.value("" + field.get(bill));
		// } catch (IllegalAccessException e) {
		// e.printStackTrace();
		// }
		//// writer.name("contractor");
		//// writer.value(bill.id);
		// }
		//// writer.name("rollNo");
		//// writer.value(student.getRollNo());
		writer.endObject();
	}

	protected String ReadStringNullable(JsonReader reader) throws IOException {
		String value = null;
		try {
			value = reader.nextString();
		} catch (IllegalStateException e) {
			if (e.getMessage().contains("but was NULL")) {
				reader.nextNull();
			}
		}
		return value;
	}

	protected <T> T ReadBONullable(JsonReader reader, Class<T> type, String fieldname) throws IOException {
		T value = null;
		if (IsSkipField(fieldname)) {
			reader.skipValue();
		} else {
			try {
				value = gson.fromJson(reader, type);
			} catch (Exception e) {
				// e.printStackTrace();
				if (e.getMessage().contains("but was NULL")) {
					reader.nextNull();
				}
			}
		}
		return value;
	}
}