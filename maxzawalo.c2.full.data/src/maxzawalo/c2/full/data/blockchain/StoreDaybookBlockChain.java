package maxzawalo.c2.full.data.blockchain;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.full.bo.StoreDaybook;
import maxzawalo.c2.full.data.factory.StoreDaybookFactory;

public class StoreDaybookBlockChain extends BlockChain {

	public StoreDaybookBlockChain(BO bo) {
		super(bo);
		StoreDaybook sd = (StoreDaybook) bo;

		AddVariable("устройство_источник", sd.device);
		AddVariable("timestamp", sd.created.getTime());
		AddVariable("entry_time", sd.entry_time.getTime());
		AddVariable("contractor.unp", sd.contractor.unp);
		AddVariable("contractor.name", sd.contractor.name);
		AddVariable("product.name", sd.product.name);
		AddVariable("count", sd.count);
		AddVariable("units.code", sd.product.units.code);

		// Надо ли клиенту видеть? ДА. Так как нужная история изменений.
		// хэш исходной записи, если не новая
		String linkhash = ((sd.link_id == 0) ? "-"
				: new StoreDaybookFactory().Create().GetById(sd.link_id, 0, false).hash);
		AddVariable("хэш_исходной_записи", linkhash);
		AddVariable("кто_получил", sd.who_recieve);// Сотрудник Контрагента
		AddVariable(BO.fields.DELETED, sd.deleted);

		// // Некое доп. хэш для себя.
		// // Надо ли клиенту видеть? НЕТ. Оно есть в StoreDaybook. Более не
		// // храним.
		// AddVariable("comment", sd.comment);
		// // Надо ли клиенту видеть? НЕТ.
		// // Ценник скорее нужен для определения номенклатуры.
		// AddVariable("ценанаценнике", sd.price);
		// // Надо ли клиенту видеть этот код? НЕТ
		// AddVariable("product.code", sd.product.code);
		// // Надо ли клиенту видеть? ДА. Это параметр важен? для хэша.
		// // Контрагенту всеравно кто выдал. Скорее когда и кому.
		// // Этот параметр важен для внутренних "разборок"
		// AddVariable("ктовыдал", sd.created_by);

		// От этой строки формируем второй хэш. Саму строку не храним - есть
		// поля в БД.
		// Этот второй хэш можно показать клиенту.
		// Так же его включаем в подпись строки. (слияние двух строк хэшей)

		// TODO: продумать защиту от внутренних подделок. на персепктиву.
		// Что если подделают данные в БД? - Хэш не совпадет.
		// Подделают и хэш - вся цепочка поплывет. Перед клиентом будет стыдно.
		// Возможно просто вести внутреннюю подпись. Причем можно не в разрезе
		// контрагента.
		// Вести подпись по всей таблице.
		// С таким успехом можно подделать строку хранимых данных для
		// контрагента.
		// Поэтому не стоит так заморачиваться.
		// TODO: продумать про непечатаемые сиволы. очистка.
		// "product.code#ценанаценнике#comment"

	}
}