package maxzawalo.c2.full.data;

import java.lang.reflect.Field;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.CatalogueBO;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.bo.SlaveCatalogueBO;
import maxzawalo.c2.base.bo.TablePartItem;
import maxzawalo.c2.base.data.DbHelper;
import maxzawalo.c2.base.data.factory.FactoryBO;
import maxzawalo.c2.base.utils.FileUtils;
import maxzawalo.c2.base.utils.Logger;
import maxzawalo.c2.free.bo.ContactInfo;
import maxzawalo.c2.free.bo.Contract;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.bo.Product;
import maxzawalo.c2.free.bo.Store;
import maxzawalo.c2.free.bo.document.receiptmoney.ReceiptMoney;
import maxzawalo.c2.free.bo.document.receiptmoney.ReceiptMoneyTablePart;
import maxzawalo.c2.free.bo.document.writeoffmoney.WriteOffMoney;
import maxzawalo.c2.free.bo.document.writeoffmoney.WriteOffMoneyTablePart;

public class Adapter1C {

	static Logger log = Logger.getLogger(Adapter1C.class);

	public static void Create() {
		// Документ = Новый Файл("G:\модуль.txt");
		// Если Документ.Существует() Тогда
		// ФайлСТекстом = "G:\модуль.txt";
		// Док = Новый ЧтениеТекста(ФайлСТекстом);
		// ВыбФайл = Док.Прочитать();
		// //Выполним считанный код из файла модуль.txt
		// Выполнить(ВыбФайл);
		// Иначе
		// Предупреждение("Файла с модулем НЕТ !");
		// КонецЕсли;
		// Class cl = Product.class;// Contract.class; // ReceiptMoney.class;
		// for (Class cl : ClassesFull.dbClasses)
		for (Class cl : new Class[] { Contractor.class, ContactInfo.class, Product.class, Contract.class, Store.class,
				ReceiptMoney.class, ReceiptMoneyTablePart.Payment.class, WriteOffMoney.class,
				WriteOffMoneyTablePart.Payment.class }) {
			String adapter = "";
			BoField bofClass = (BoField) cl.getAnnotation(BoField.class);
			System.out.println("============= " + FactoryBO.getTableName(cl) + "|" + bofClass.type1C());
			for (Field field : cl.getFields()) {
				String dbFieldName = DbHelper.GetFieldDbName(field);

				BoField bofField = field.getAnnotation(BoField.class);
				if (bofField == null)
					continue;

				Class fieldType = field.getType();

				BoField bofFieldType = field.getType().getAnnotation(BoField.class);
				if (dbFieldName.equals(SlaveCatalogueBO.fields.OWNER)) {
					// Создаем объект для получения типа Владельца

					try {
						SlaveCatalogueBO slBo = (SlaveCatalogueBO) cl.newInstance();
						fieldType = slBo.ownerType;
						bofFieldType = (BoField) fieldType.getAnnotation(BoField.class);
						// continue;
					} catch (Exception e) {
						log.ERROR("Create", e);
					}
				}

				if (dbFieldName.equals(TablePartItem.fields.DOC)) {
					// Создаем объект для получения типа Владельца

					try {
						// DocumentBO slBo = (DocumentBO) cl.newInstance();
						// fieldType = slBo.ownerType;

						if (cl == ReceiptMoneyTablePart.Payment.class)
							fieldType = ReceiptMoney.class;

						if (cl == WriteOffMoneyTablePart.Payment.class)
							fieldType = WriteOffMoney.class;

						bofFieldType = (BoField) fieldType.getAnnotation(BoField.class);
						// continue;
					} catch (Exception e) {
						log.ERROR("Create", e);
					}
				}

				if (dbFieldName.equals(CatalogueBO.fields.PARENT))
					bofFieldType = bofClass;

				String fieldName1C = bofField.fieldName1C();
				if (dbFieldName.equals("code") && BO.instanceOf(cl, DocumentBO.class))
					fieldName1C = "Номер";

				if (!fieldName1C.isEmpty()) {
					String table = fieldType.getCanonicalName();
					String type1C = (bofFieldType == null ? "" : bofFieldType.type1C());
					// if (BO.instanceOf(fieldType, EnumC2.class)) {
					// // тут генерируем АДАПТЕР ЗНАЧЕНИЙ
					// // Если Тогда
					// // ИначеЕсли Тогда
					// // КонецЕсли;
					// table = FactoryBO.getTableName(fieldType);
					// } else
					if (BO.instanceOf(fieldType, CatalogueBO.class) || BO.instanceOf(fieldType, DocumentBO.class)) {
						// тут берем док из таблицы по номеру и дате
						table = FactoryBO.getTableName(fieldType);
					}

					if (dbFieldName.equals(CatalogueBO.fields.PARENT))
						table = FactoryBO.getTableName(cl);

					if (cl == ContactInfo.class) {
						if (dbFieldName.equals(CatalogueBO.fields.NAME))
							fieldName1C = "Представление";

						// !!!! .Наименование добавляется к type1C
						if (dbFieldName.equals(ContactInfo.fields.CONTACT_TYPE))
							type1C = "Справочники.ВидыКонтактнойИнформации.Наименование";
					}

					adapter += dbFieldName + "|" + table + "|" + fieldName1C + "|" + type1C + "\n";
					System.out.println(dbFieldName + "|" + table + "|" + fieldName1C + "|" + type1C);
					// Добавляем дополнительные поля/значения 1С типа КодУчета
					// в классы. Если не константы - интеллектуально
				}

				String adapterPath = FileUtils.Get1cDir() + bofClass.type1C() + ".adapter";
				FileUtils.Text2File(adapterPath, adapter, false);

				// if (field.getAnnotation(DatabaseField.class) == null)
				// continue;
			}
		}
	}
}