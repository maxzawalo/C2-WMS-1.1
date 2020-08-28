package maxzawalo.c2.base.data;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import maxzawalo.c2.base.data.factory.FactoryBO;
import maxzawalo.c2.base.utils.Global;

//TODO: по кэшу и бинам
public class Core {

	int id = 0;
	Map<String, Class> classes = new HashMap<>();

	public Core(int id) {
		this.id = id;

		for (Class type : Global.dbClasses) {
			try {
				classes.put(FactoryBO.getTableName(type), type);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Map<String, Object> GetBo(String sql) {
		System.out.println(sql);
		Map<String, Object> bo = new HashMap<>();

		try {
			Connection connection = DriverManager.getConnection(DbHelper.connectionString);

			Statement stmt = connection.createStatement();

			ResultSet rs = stmt.executeQuery(sql);
			ResultSetMetaData meta = rs.getMetaData();
			while (rs.next()) {
				for (int col = 1; col <= meta.getColumnCount(); col++)
					bo.put(meta.getColumnName(col), rs.getObject(col));

				// //Retrieve by column name
				// int id = rs.getInt(BO.fields.ID);
				// int age = rs.getInt("age");
				// String first = rs.getString("first");
				// String last = rs.getString("last");
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bo;
	}

	String[] functions = { "date_format" };

	Map<String, Object> root;

	public String get(String path) {
		// if (root == null)
		// root = new HashMap<>();
		String retVal = "";
		// context = deliverynote[id=25]
		// String path = "deliverynote.store.address";
		String[] pathItems = path.split("\\.");
		// String path = "deliverynote[@id=25]/store/address";
		try {
			for (int i = 0; i < pathItems.length - 1; i++) {
				String fieldName = pathItems[i];
				String sql = "select * from " + fieldName + " where id=" + id;
				if (root == null && i == 0) {
					root = GetBo(sql);
				} else if (i > 0) {
					if (!root.containsKey(fieldName)) {
						Class parentType = classes.get(pathItems[i - 1]);
						Field field = parentType.getField(fieldName);
						String tabName = FactoryBO.getTableName(field.getType());
						sql = "select * from " + tabName + " where id=" + root.get(fieldName + "_id");
						root.put(fieldName, GetBo(sql));
					}
				}
			}

			// Проходим по дереву от корневого объекта
			Map<String, Object> obj = root;
			// i = 0 root
			for (int i = 1; i < pathItems.length; i++) {
				if (obj.get(pathItems[i]) instanceof Map)
					obj = (Map<String, Object>) obj.get(pathItems[i]);
				else {
					// Последний лепесток
					Object o = obj.get(pathItems[i]);
					if (o == null)
						System.out.println("Не заполнено поле");// TODO:
					else
						retVal = "" + o;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println(retVal);

		return retVal;
	}
}