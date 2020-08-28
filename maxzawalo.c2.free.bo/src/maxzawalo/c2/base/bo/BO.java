//TODO: Отвязать от Cache, Data итп
package maxzawalo.c2.base.bo;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.annotation.JsonField;
import maxzawalo.c2.base.utils.FileUtils;
import maxzawalo.c2.base.utils.Global;
import maxzawalo.c2.base.utils.Logger;

public class BO<TypeBO> implements java.io.Serializable {

	@Expose
	protected Logger log = Logger.getLogger(this.getClass().getName());

	public static class fields {
		public static final String ID = "id";
		public static final String CODE = "code";
		public static final String UUID = "uuid";
		public static final String CREATED = "created";
		public static final String CHANGED = "changed";
		public static final String CREATED_BY = "created_by_id";
		public static final String CHANGED_BY = "changed_by_id";
		public static final String LOCKED_BY = "locked_by_id";
		public static final String DELETED = "deleted";
		public static final String SYNC_FLAG = "sync_flag";

		// Calc fields
		public static final String DOC_STATE = "DocState";
		public static final String CALC_FULL_NAME_WORDS = "calc_full_name_words";

	}

	public Set<Integer> fuzzy_ids = new HashSet<Integer>();

	// @JsonField(columnName = "CalcFields")
	public Map<String, Object> calcFields = new HashMap<>();

	// @DatabaseField(id = true)
	@DatabaseField(generatedId = true, columnName = BO.fields.ID)
	public int id = 0;

	@BoField(caption = "Код", fieldName1C = "Код") // Номер
	@DatabaseField(index = true, width = 15, columnName = BO.fields.CODE)
	public String code = "";

	// TODO: by settings
	public String zeroCode = "00-000000";

	@DatabaseField(index = true, dataType = DataType.DATE_LONG, columnName = BO.fields.CREATED)
	public Date created = new Date();

	@DatabaseField(index = true, dataType = DataType.DATE_LONG, columnName = BO.fields.CHANGED)
	public Date changed = created;

	@BoField(caption = "Удален", fieldName1C = "ПометкаУдаления")
	@DatabaseField(index = true, columnName = BO.fields.DELETED)
	public boolean deleted = false;

	@DatabaseField(index = true, columnName = BO.fields.SYNC_FLAG)
	public long sync_flag = Global.sync_flag;

	@DatabaseField(columnName = BO.fields.UUID)
	public UUID uuid = zero_uuid;// new UUID(0L, 0L);//
									// UUID.fromString("00000000-0000-0000-0000-000000000000");
	public static UUID zero_uuid = new UUID(0L, 0L);

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = false, defaultValue = "0", columnName = BO.fields.CREATED_BY)
	public User created_by = User.current;

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = false, defaultValue = "0", columnName = BO.fields.CHANGED_BY)
	public User changed_by = User.current;

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, defaultValue = "0", columnName = BO.fields.LOCKED_BY)
	public User locked_by = User.zero;

	@DatabaseField(index = true, width = 30, defaultValue = "''")
	public String meta = "";

	@JsonField(columnName = BO.fields.DOC_STATE)
	public String doc_state = "";

	@Expose
	protected Class typeBO;

	// public GenericRowMapper mapper;

	public BO() {

		Class clazz = this.getClass();
		Object sc = clazz.getGenericSuperclass();
		try {

			if (sc != null && sc instanceof ParameterizedType) {
				Type[] gParams = ((ParameterizedType) sc).getActualTypeArguments();
				if (gParams.length != 0)// для dao - иначе не создает BO
					this.typeBO = (Class<TypeBO>) gParams[0];
			}
		} catch (ClassCastException e) {
			// log.ERROR("BO", e);
		}

		// TODO: uncomment !!!!!!!!!!
		// mapper = GenericRowMapper.get(typeBO);

		// При синхронизации все вновь создаваемые объекты создаем с выключенным
		// фильтром. Это особенно актуально для выборок типа GetAll

		// TODO: BO sync_flag
		// if (Global.sync_flag != 0)
		// ForSync();
	}

	// private TypeBO ForSync() {
	// this.enableDeletedFilter = false;
	// this.enableSyncFilter = false;
	// return (TypeBO) this;
	// }
	//
	// public TypeBO DeleteFilterOff() {
	// this.enableDeletedFilter = false;
	// return (TypeBO) this;
	// }
	//
	// public TypeBO DeleteFilterOn() {
	// this.enableDeletedFilter = true;
	// return (TypeBO) this;
	// }

	public String getRusName() {
		BoField boFiled = this.getClass().getAnnotation(BoField.class);
		String tableName = (boFiled == null) ? this.getClass().getSimpleName().toLowerCase() : boFiled.caption();
		return tableName;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String toJSON() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	String GetClassDir() {
		return FileUtils.GetDataDir() + this.getClass().getSimpleName() + "/";
	}

	String GetNewDir() {
		return GetClassDir() + "/new/";
	}

	String GetSendedDir() {
		return GetClassDir() + "/sended/";
	}

	public void CreateDir() {
		new File(GetNewDir()).mkdirs();
		new File(GetSendedDir()).mkdirs();
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " " + this.id;
	}

	protected void Check() {
		// code = code.trim();
	}

	public String Dump() {
		return Dump(null, null);
	}

	public String Dump(String filename, Gson gson) {
		try {
			if (filename == null)
				filename = FileUtils.GetDumpDir() + this.getClass().getSimpleName() + "_" + this.id + "_" + this.code
						+ "_" + System.currentTimeMillis() + ".xml";
			// XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new
			// FileOutputStream(filename)));
			// encoder.writeObject(this);
			// encoder.close();

			// Gson gson = new GsonBuilder()
			// .excludeFieldsWithoutExposeAnnotation()
			//// .excludeFieldsWithModifiers(TRANSIENT) // STATIC|TRANSIENT in
			// the default configuration
			// .setPrettyPrinting().create();
			FileUtils.Text2File(filename, gson.toJson(this), false);

			// FileOutputStream fileOut = new FileOutputStream(filename);
			// ObjectOutputStream out = new ObjectOutputStream(fileOut);
			// out.writeObject(this);
			// out.close();
			// fileOut.close();

			// File file = new File(filename);
			// JAXBContext ctx = JAXBContext.newInstance(typeBO);
			// Marshaller m = ctx.createMarshaller();
			// m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			// m.marshal(this, file);
		} catch (Exception e) {
			log.FATAL("Dump", e);
		}
		return filename;
	}

	public String Dump4Report(String reportName, Gson gson) {
		return Dump(FileUtils.GetReportDir() + this.getClass().getSimpleName() + "_" + reportName + "_" + this.id + "_"
				+ this.code + "_" + System.currentTimeMillis() + ".c2_report", gson);
	}

	public TypeBO FromDump(String filename, Gson gson) {
		TypeBO obj = null;
		try {
			String json = FileUtils.readFileAsString(filename);
			obj = (TypeBO) gson.fromJson(json, typeBO);

			// XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new
			// FileInputStream(filename)));
			// obj = (TypeBO) decoder.readObject();
			// decoder.close();

			// // FileInputStream fileIn = new FileInputStream(filename);
			// // ObjectInputStream in = new ObjectInputStream(fileIn);
			// // obj = (TypeBO) in.readObject();
			// // in.close();
			// // fileIn.close();
			// File file = new File(filename);
			// JAXBContext readCtx = JAXBContext.newInstance(typeBO);
			// Unmarshaller um = readCtx.createUnmarshaller();
			// obj = (TypeBO) um.unmarshal(file);
		} catch (Exception e) {
			log.FATAL("FromDump", e);
		}
		return obj;
	}

	public boolean HasNoCode() {
		// return this instanceof StoreTP || this instanceof ContactInfo || this
		// instanceof StrictForm
		// || this instanceof StoreDaybook;
		return false;
	}

	public TypeBO cloneObject() {
		return cloneObject(new ArrayList<String>());
	}

	public TypeBO cloneObject(List<String> exceptFields) {
		try {
			TypeBO clone = (TypeBO) typeBO.newInstance();
			for (Field field : typeBO.getFields()) {
				int m = field.getModifiers();
				if (Modifier.isStatic(m))
					continue;
				if (exceptFields.contains(field.getName()))
					continue;
				field.setAccessible(true);
				// TODO: ссылочные типы?
				field.set(clone, field.get(this));
				// System.out.println(field.getName());
			}
			return clone;
		} catch (Exception e) {
			log.ERROR("cloneObject", e);
			return null;
		}
	}

	public <TypeBO, ToBo> ToBo copyToObject(ToBo toBo, List<String> exceptFields) {
		try {
			for (Field field : toBo.getClass().getFields()) {
				if (exceptFields.contains(field.getName()))
					continue;
				field.setAccessible(true);
				field.set(toBo, field.get(this));
				// System.out.println(field.getName());
			}
			return toBo;
		} catch (Exception e) {
			log.ERROR("copyToObject", e);
			return null;
		}
	}

	public <TypeBO, ToBo> ToBo copyToObject(ToBo toBo) {
		return copyToObject(toBo, getExceptFields());
	}

	public TypeBO cloneBO() {
		return cloneObject(getExceptFields());
	}

	protected List<String> getExceptFields() {
		List<String> exceptFields = new ArrayList<>();
		for (Field field : new BO().getClass().getFields())
			// if (field.getAnnotation(DatabaseField.class) != null)
			exceptFields.add(field.getName());
		return exceptFields;
	}

	public void CheckData() {
		// TODO: проверка ошибок данных.
		// -удаленный, но проведенный док
	}

	public boolean just_id = false;

	public Object getCalcField(String name) {
		switch (name) {
		case BO.fields.DOC_STATE:
			if (deleted)
				return "x";
		default:
			return "";
		}
	}

	public List<?> getTablePart4Rep() {
		return new ArrayList<>();
	}

	public Object get(String fieldName) {
		fieldName = fieldName.replace("_id", "");
		try {
			Field field = getClass().getField(fieldName);
			field.setAccessible(true);
			return field.get(this);
		} catch (Exception e) {

			e.printStackTrace();
		}

		return null;
	}

	public static boolean instanceOf(Class clazz, Class parent) {
		Class cl = clazz;
		while (cl != null && cl != BO.class) {
			if (cl.equals(parent))
				return true;
			cl = cl.getSuperclass();

		}
		return false;
	}

	public static boolean ChildOfBO(Class clazz) {
		Class cl = clazz;
		while (cl != null) {
			if (cl == BO.class)
				return true;
			cl = cl.getSuperclass();

		}
		return false;
	}
}