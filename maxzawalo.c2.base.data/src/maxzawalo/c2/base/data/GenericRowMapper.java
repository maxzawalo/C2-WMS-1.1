package maxzawalo.c2.base.data;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.data.factory.FactoryBO;
import maxzawalo.c2.base.utils.Global;
import maxzawalo.c2.base.utils.Logger;
import maxzawalo.c2.free.bo.enums.EnumC2;

public class GenericRowMapper<T> implements RawRowMapper<T> {

	static Map<Class, GenericRowMapper> all = new HashMap<>();
	static Map<Class, GenericRowMapper> allNewCore = new HashMap<>();

	// static {
	// for (Class cl : DbHelper.classes) {
	// all.put(cl, new GenericRowMapper<>(cl));
	// allNewCore.put(cl, new GenericRowMapper<>(cl, true));
	// }
	//
	// }

	public static GenericRowMapper get(Class cl) {
		return get(cl, false);
	}

	public static GenericRowMapper get(Class cl, boolean newCore) {
		// lazzy
		if (!all.containsKey(cl)) {
			all.put(cl, new GenericRowMapper<>(cl));
			allNewCore.put(cl, new GenericRowMapper<>(cl, true));
		}
		GenericRowMapper mapper = null;
		if (newCore)
			mapper = allNewCore.get(cl);
		else
			mapper = all.get(cl);
		// Иначе пропадает Номенклатура из Подбора итп (когда к одному мапперу
		// несколько раз обращаемся)
		// mapper.level = 0;
		return mapper;
	}

	protected Logger log = Logger.getLogger(this.getClass().getSimpleName());

	private Class<T> entityClass;
	private Set<Field> fields = new HashSet<>();
	private Map<String, Field> colNameFieldMap = new HashMap<>();

	boolean newCore = false;

	public int level = 0;

	public GenericRowMapper(Class<T> entityClass) {
		this(entityClass, false);
	}

	protected GenericRowMapper(Class<T> entityClass, boolean newCore) {
		this.newCore = newCore;
		setEntityClass(entityClass);
	}

	public void setEntityClass(Class<T> entityClass) {
		if (entityClass == null)
			return;

		if (this.entityClass != null)
			return;

		this.entityClass = entityClass;
		Class cl = entityClass;
		do {
			for (Field field : cl.getDeclaredFields()) {
				if (field.isAnnotationPresent(DatabaseField.class)) {
					DatabaseField an = field.getAnnotation(DatabaseField.class);
					fields.add(field);
					String colName = an.columnName();
					if (colName.equals("")) {
						colName = field.getName();// .toLowerCase();

					}
					// System.out.println(colName);
					colNameFieldMap.put(colName, field);
				}
			}
			cl = cl.getSuperclass();
		} while (cl != Object.class);
	}

	@Override
	public T mapRow(String[] columnNames, String[] resultColumns) {
		try {
			T entity = entityClass.newInstance();
			for (int i = 0; i < columnNames.length; i++) {
				Field f = colNameFieldMap.get(columnNames[i].replace("_id", ""));
				if (f == null)
					// TODO: переделать (для reg_id,doc_id)
					f = colNameFieldMap.get(columnNames[i]);
				// в БД есть но в классе нет
				// TODO: сигнал для удаления
				if (f == null)
					continue;

				// System.out.println(columnNames[i]);
				boolean accessible = f.isAccessible();
				f.setAccessible(true);
				Object value = stringToJavaObject(f, resultColumns[i]);
				f.set(entity, value);
				f.setAccessible(accessible);
			}
			return entity;
		} catch (Exception e) {
			log.ERROR("mapRow", e);
		}
		return null;
	}

	public Object stringToJavaObject(Field f, String result) {
		Class cl = f.getType();
		DatabaseField df = f.getAnnotation(DatabaseField.class);
		if (result == null) {
			return null;
		}

		else if (cl == Integer.class || int.class == cl) {
			return Integer.parseInt(result);
		} else if (cl == Long.class || long.class == cl) {
			return Long.parseLong(result);
		} else if (cl == Float.class || float.class == cl) {
			return Float.parseFloat(result);
		} else if (cl == Double.class || double.class == cl) {
			return Double.parseDouble(result);
		} else if (cl == Boolean.class || cl == boolean.class) {
			try {
				return Integer.valueOf(result) > 0;
			} catch (NumberFormatException e) {
				return Boolean.parseBoolean(result);
			}
		} else if (cl == Date.class) {
			return new Date(Long.parseLong(result));
			// TODO:
			// DateLongType lType = DateLongType.getSingleton();
			// DateStringType sType = DateStringType.getSingleton();
			// try {
			// return lType.resultStringToJava(null, result, -1);
			// } catch (NumberFormatException e) {
			// try {
			// return sType.resultStringToJava(null, result, -1);
			// } catch (SQLException e2) {
			// throw new RuntimeException(e);
			// }
			// }

		} else if (cl == String.class) {
			return result;

		} else if (cl == UUID.class) {
			return UUID.fromString(result);
		}

		try {
			if (f.getType().getSimpleName().contains("Bill")) {
				System.out.println("");
			}
			if (newCore)
				return CreateBONew(result, f);
			else
				return CreateBO(result, f);
		} catch (Exception e) {
			log.ERROR("stringToJavaObject", e);
			return null;
		}

	}

	public Map<String, Class> nonSkipForeign = new HashMap<>();

	protected Object CreateBO(String result, Field f) throws Exception {
		// System.out.println("CreateBO " + f.getType().getName() + "." +
		// f.getName() + " " + level);
		DatabaseField df = f.getAnnotation(DatabaseField.class);

		BO bo = (BO) f.getType().newInstance();
		if ((level < Global.max_cache_level && df != null && df.foreignAutoRefresh())
				|| nonSkipForeign.containsKey(f.getName())) {
			Class t = null;
			if (nonSkipForeign.containsKey(f.getName())) {
				// Меняем тип объекта (для Owner например)
				t = nonSkipForeign.get(f.getName());
				bo = (BO) t.newInstance();
				// System.out.println(
				// "CreateBO.nonSkipForeign " + bo.getClass().getName() + "." +
				// f.getName() + " " + level);
			}
			// TODO: check --- if (Arrays.asList(Global.enums).contains(t))
			if (bo instanceof EnumC2)
				bo = (BO) ((EnumC2) bo).getEnumById(Integer.parseInt(result));
			else {
				// log.DEBUG("CreateBO", "mapper GetById " +
				// f.getType().getName());
				// System.out.println("CreateBO.GetById");
				// bo = (BO) bo.GetById(Integer.parseInt(result), level + 1,
				// true);
				bo = (BO) new FactoryBO<>().Create(bo.getClass()).GetById(Integer.parseInt(result), level + 1, true);
			}

		} else {
			bo.id = Integer.parseInt(result);
			bo.just_id = true;
		}
		return bo;
	}

	protected Object CreateBONew(String result, Field f) throws Exception {
		DatabaseField df = f.getAnnotation(DatabaseField.class);
		BO bo = (BO) f.getType().newInstance();
		bo.id = Integer.parseInt(result);
		bo.just_id = true;
		return bo;
	}
}