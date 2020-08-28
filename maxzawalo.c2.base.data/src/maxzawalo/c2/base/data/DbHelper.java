package maxzawalo.c2.base.data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.data.factory.FactoryBO;
import maxzawalo.c2.base.utils.AnnotationInvocationHandler;
import maxzawalo.c2.base.utils.FileUtils;
import maxzawalo.c2.base.utils.Global;
import maxzawalo.c2.base.utils.Logger;
import maxzawalo.c2.base.utils.Profiler;
import maxzawalo.c2.free.bo.enums.EnumC2;

public class DbHelper {
	static Logger log = Logger.getLogger(DbHelper.class);

	static Map<Class, Dao> daos = new HashMap<Class, Dao>();
	public static String connectionString = "";
	static ConnectionSource connectionSource;

	public static void setConnection() {
		try {
			// connectionString = connStr;
			connectionSource = new JdbcConnectionSource(connectionString);
		} catch (Exception e) {
			log.ERROR("setConnection", e);
		}
	}

	public static void setConnectionSource(ConnectionSource source) {
		connectionSource = source;
	}

	// public static Class[] classes;
	//
	// public static Class[] enums;

	public static Annotation setAttrValue(Annotation anno, Class<? extends Annotation> type, String attrName, Object newValue) throws Exception {
		InvocationHandler handler = new AnnotationInvocationHandler(anno, attrName, newValue);
		Annotation proxy = (Annotation) Proxy.newProxyInstance(anno.getClass().getClassLoader(), new Class[] { type }, handler);
		return proxy;
	}

	public static void Create() throws Exception {
		Profiler profiler = new Profiler();
		profiler.Start("Create");
		try {
			for (Class t : Global.dbClasses) {

				// if (t.equals(Barcode.class)) {
				// try {
				// Field field = t.getField(BO.fields.CODE);
				// DatabaseField anno =
				// field.getAnnotation(DatabaseField.class);
				// setAttrValue(anno, DatabaseField.class, "width", 48);
				// } catch (Exception e) {
				// log.ERROR(e);
				// }
				// }

				TableUtils.createTable(connectionSource, t);
			}
			for (Class t : Global.enums) {
				TableUtils.createTable(connectionSource, t);
			}
		} catch (Exception e) {
			log.FATAL("Create", e);
			profiler.Stop("Create");
			profiler.PrintElapsed("Create");
			throw new Exception(e);
		}

		profiler.Stop("Create");
		profiler.PrintElapsed("Create");
	}

	public static void Drop() throws Exception {
		Profiler profiler = new Profiler();
		profiler.Start("Drop");
		try {
			Connection connection = DriverManager.getConnection(connectionString);
			Statement s = connection.createStatement();
			String sql = "";
			for (Class t : Global.dbClasses) {
				s.addBatch("DROP TABLE if exists `" + FactoryBO.getTableName(t) + "`;");

			}
			s.executeBatch();
			// TableUtils.dropTable(connectionSource, t, true);

			for (Class t : Global.enums) {
				s.addBatch("DROP TABLE if exists `" + FactoryBO.getTableName(t) + "`;");
			}
			s.executeBatch();

		} catch (Exception e) {
			log.FATAL("Drop", e);
			throw new Exception(e);
		}
		profiler.Stop("Drop");
		profiler.PrintElapsed("Drop");
	}

	public static ConnectionSource getConnectionSource() {
		return connectionSource;
	}

	public static <T> Dao<T, Integer> geDaos(Class<T> param) {
		if (!daos.containsKey(param)) {
			try {
				daos.put(param, DaoManager.createDao(connectionSource, param));
			} catch (Exception e) {
				log.ERROR("geDaos", e);
			}
		}
		return daos.get(param);
	}

	public static boolean Alter(Class[] alter_classes) throws Exception {
		// if (!Settings.isServer())
		// return false;

		// Synchronization.Backup(System.currentTimeMillis());

		Connection connection = DriverManager.getConnection(connectionString);
		DatabaseMetaData md = connection.getMetaData();

		String[] types = { "TABLE" };
		ResultSet rs = md.getTables(null, null, "%", types);
		List<String> tables = new ArrayList<>();
		while (rs.next())
			tables.add(rs.getString("TABLE_NAME"));

		for (Class t : alter_classes) {
			String tableName = FactoryBO.getTableName(t);
			if (!tables.contains(tableName)) {
				log.INFO("Alter", "create table " + tableName);
				TableUtils.createTable(connectionSource, t);
			}
		}

		for (Class t : Global.enums) {
			String tableName = FactoryBO.getTableName(t);
			if (!tables.contains(tableName)) {
				log.INFO("Alter", "create table " + tableName);
				TableUtils.createTable(connectionSource, t);
			}
		}

		for (Class cl : alter_classes)
			getDbFields(cl);

		return true;
	}

	public static void AlerScript() throws Exception {
		Connection connection = DriverManager.getConnection(connectionString);
		log.INFO("Alter", "Запуск скрипта");
		String sql = FileUtils.GetStringFromResource(DbHelper.class, "alter/alter.sql");
		System.out.println(sql);
		Statement s = connection.createStatement();
		s.execute(sql);
	}

	public static <T> void getDbFields(Class<T> type) throws Exception {
		String tableName = FactoryBO.getTableName(type);
		log.INFO("getDbFields", "===== " + tableName);

		Connection connection = DriverManager.getConnection(connectionString);
		DatabaseMetaData md = connection.getMetaData();
		// TODO: foreach there
		ResultSet rsColumns = md.getColumns(null, null, tableName, "%");

		Map<String, Integer> dbFields = new HashMap<>();
		while (rsColumns.next()) {
			String columnName = rsColumns.getString("COLUMN_NAME").toLowerCase();
			String columnType = rsColumns.getString("TYPE_NAME");
			int size = rsColumns.getInt("COLUMN_SIZE");

			log.INFO("getDbFields", "-" + columnName + " " + columnType + " " + size);
			dbFields.put(columnName, size);
		}

		// CHANGE COLUMN `code` `code` VARCHAR(15) NULL DEFAULT NULL ;
		for (Field field : type.getFields()) {
			Object[] params = new Object[1];
			String fieldSql = CreateColumnSql(type, field, dbFields, params);
			if (fieldSql.equals(""))
				continue;

			// found
			if ((boolean) params[0])
				fieldSql = "ALTER TABLE `" + tableName + "` MODIFY COLUMN " + fieldSql + ";";
			else
				fieldSql = "ALTER TABLE `" + tableName + "` ADD COLUMN " + fieldSql + ";";

			log.INFO("getDbFields", fieldSql);
			Statement s = connection.createStatement();
			s.execute(fieldSql);
		}
	}

	protected static String CreateColumnSql(Class type, Field field, Map<String, Integer> dbFields, Object[] params) {
		params[0] = true;
		String sql = "";
		DatabaseField fa = field.getAnnotation(DatabaseField.class);
		if (fa == null)
			return sql;
		// TODO:.toLowerCase();

		String defaultValue = fa.defaultValue();
		if (defaultValue.equals("") || defaultValue.contains("__ormlite__"))
			defaultValue = "NULL";

		String fieldName = GetFieldDbName(field);

		if (!dbFields.containsKey(fieldName.toLowerCase())) {
			if (fa.foreign()) {
				sql = "`" + fieldName + "` int(11) DEFAULT " + defaultValue;
			} else if (field.getType() == Integer.class || int.class == field.getType()) {
				sql = "`" + fieldName + "` int(11) DEFAULT " + defaultValue; //
			} else if (field.getType() == Double.class || double.class == field.getType()) {
				// System.out.println(fieldName);
				sql = "`" + fieldName + "` DOUBLE NOT NULL DEFAULT 0";
			} else if (field.getType() == Boolean.class || boolean.class == field.getType()) {
				// System.out.println(fieldName);
				sql = "`" + fieldName + "` tinyint(1) DEFAULT 0";
			} else if (field.getType() == Date.class) {
				// System.out.println(fieldName);
				sql = "`" + fieldName + "` bigint(20) DEFAULT 0";
			}
			params[0] = false;
		}

		if (field.getType() == String.class) {
			int width = fa.width();

			if (type.getName().contains("ScannedBarcode") && fieldName.equals(BO.fields.CODE))
				width = 50;

			sql = "`" + fieldName + "` varchar(" + width + ") DEFAULT NULL";
			if (dbFields.containsKey(fieldName))
				if (dbFields.get(fieldName) >= width)
					// Длина совпадает - пропускаем. Не допускаем изменений.
					sql = "";
		}

		return sql;
	}

	public static String GetFieldDbName(Field field) {
		DatabaseField fa = field.getAnnotation(DatabaseField.class);
		if (fa == null)
			return field.getName().toLowerCase();

		String fieldName = fa.columnName();
		if (fieldName.isEmpty())
			fieldName = field.getName();

		// Принудительно ставим _id для ссылочных типов
		if (fa.foreign() && !fieldName.contains("_id"))
			fieldName += "_id";
		return fieldName;
	}

	public static String GetFieldDbType(Field field) {
		DatabaseField fa = field.getAnnotation(DatabaseField.class);
		if (fa == null)
			return "";

		try {
			Class type = field.getType();
			if (type == String.class)
				return "varchar(" + fa.width() + ")";
			else if (type == Boolean.class || type == boolean.class)
				return "tinyint(1)";
			else if (type == Integer.class || type == int.class)
				return "int(11)";
			else if (type == Double.class || double.class == type)
				return "double";
			else if (type == Date.class || type == Long.class || type == long.class)
				return "bigint(20)";
			else if (type == UUID.class)
				return "varchar(48)";
			else if (type.newInstance() instanceof BO)
				return "int(11) FK";
		} catch (Exception e) {
			log.ERROR("GetFieldDbType", e);
		}

		return "";
	}

	public static void CreateUpdateStoreSql() {
		// TODO: uncomment?
		// for (Class cl : classes) {
		// try {
		// DocumentBO doc = (DocumentBO) cl.newInstance();
		// if (doc != null) {
		// UpdateBuilder<DocumentBO, Integer> builder = doc.getUpdateBuilder();
		// builder.updateColumnValue("store_id", Settings.mainStore.id);
		// log.DEBUG("CreateUpdateStoreSql", builder.prepareStatementString() +
		// ";");
		// }
		//
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// // e.printStackTrace();
		// }
		// }
		// try {
		// UpdateBuilder<RegistryProduct, Integer> builder = new
		// RegistryProduct().getUpdateBuilder();
		// builder.updateColumnValue("store_id", Settings.mainStore.id);
		// log.DEBUG("CreateUpdateStoreSql", builder.prepareStatementString() +
		// ";");
		// } catch (Exception e) {
		// }
	}

	// public static void CreateUpdateLockedBySql() {
	// for (Class cl : Classes.dbClasses) {
	// try {
	// BO bo = (BO) cl.newInstance();
	//
	// UpdateBuilder<?, Integer> builder = bo.getUpdateBuilder();
	// builder.updateColumnValue(BO.fields.LOCKED_BY, User.zero);
	// // log.DEBUG("CreateUpdateLockedBySql",
	// // builder.prepareStatementString() + ";");
	// System.out.println(builder.prepareStatementString() + ";");
	//
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// // e.printStackTrace();
	// }
	// }
	// }

	public static boolean CheckEnums() {
		boolean retVal = true;
		log.INFO("CheckEnums", "==== Тестируем перечисления ====");
		for (Class t : Global.enums) {
			log.INFO("CheckEnums", t.getName());
			try {
				EnumC2 bo = ((EnumC2) t.newInstance());
				// TODO: тестрирование при старте системы
				List<Integer> ids = new ArrayList<>();
				List<String> names = new ArrayList<>();
				List<EnumC2> all = bo.getEnum();
				for (EnumC2 enumBo : all) {
					ids.add(enumBo.id);
					names.add(enumBo.name);
				}

				if (all.size() > new ArrayList(new HashSet(ids)).size()) {
					log.FATAL("CheckEnums", "id не уникальны");
					retVal = false;
				}
				if (all.size() > new ArrayList(new HashSet(names)).size()) {
					log.FATAL("CheckEnums", "name не уникальны");
					retVal = false;
				}
			} catch (Exception e) {
				log.FATAL("CheckEnums", e);
				retVal = false;
			}
		}

		if (retVal)
			log.INFO("CheckEnums", "==== Успешно ====");
		return retVal;
	}
}