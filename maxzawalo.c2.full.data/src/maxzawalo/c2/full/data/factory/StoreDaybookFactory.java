package maxzawalo.c2.full.data.factory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.base.crypto.Hash;
import maxzawalo.c2.base.data.factory.FactoryBO;
import maxzawalo.c2.base.utils.Console;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.data.factory.catalogue.UnitsFactory;
import maxzawalo.c2.full.bo.StoreDaybook;
import maxzawalo.c2.full.bo.daybook.StoreDaybookComparator;
import maxzawalo.c2.full.data.blockchain.StoreDaybookBlockChain;

public class StoreDaybookFactory extends FactoryBO<StoreDaybook> {

	final String RECORD_DELETED_MSG = "Запись удалена. Отмена удаления и редактирование невозможно.";

	public StoreDaybookFactory Create() {
		return (StoreDaybookFactory) super.Create(StoreDaybook.class);
	}

	public StoreDaybookFactory() {
		DeleteFilterOff();
	}

	@Override
	public StoreDaybook Save(StoreDaybook book) throws Exception {
		if (book.product == null || book.product.id == 0)
			throw new Exception("Необходимо выбрать Номенклатуру");
		if (book.count == 0)
			throw new Exception("Количество не может быть 0");
		if (book.price == 0)
			throw new Exception("Цена не может быть 0");

		// Усьанавливаем параметры, чтобы не копировались с предыдущей записи
		book.created = new Date();
		book.changed = new Date();
		// created_by исп-ся для "ктовыдал", поэтому не меняем.
		book.changed_by = User.current;

		if (book.id != 0) {
			// Редактирование существующей записи (цепочки)
			if (book.link_id == 0)
				// Первое редактирование
				book.link_id = book.id;

			// TODO: transaction всей ф-ии Save
			if (CheckDeleted(book))
				throw new Exception(RECORD_DELETED_MSG);

			// Иначе оставляем ссылку на объект - цепочка редакций
			book.id = 0;
		}

		book = CreateExtData(book);
		// Тут делаем хеш
		book.hash = Hash.sha256(book.data);
		return super.Save(book);
	}

	// TODO: проверить все использования
	protected StoreDaybook CreateExtData(StoreDaybook book) throws Exception {
		book.data = new StoreDaybookBlockChain(book)
				.Create("устройство_источник#timestamp#entry_time#contractor.unp#contractor.name#product.name#count#units.code#кто_получил#deleted#хэш_исходной_записи");
		return book;
	}

	@Override
	public StoreDaybook setDeleted(StoreDaybook book, boolean deleted) throws Exception {
		// if (this.deleted)
		// throw new Exception(RECORD_DELETED_MSG);
		book.deleted = deleted;
		return Save(book);
	}

	@Override
	public BO setLockedBy(BO bo, User locked_by) throws Exception {
		// отключаем эту функцию
		return bo;
	}

	public void toDoc(StoreDaybook book, String comment) throws Exception {
		book.comment = comment + "#" + book.comment;
		book.deleted = true;
		Save(book);
	}

	@Override
	public StoreDaybook GetById(int id, int level, boolean fromCache) {
		StoreDaybook sd = super.GetById(id, level, fromCache);
		// level 3
		if (sd.product.units != null)
			sd.product.units = new UnitsFactory().GetById(sd.product.units.id);
		return sd;
	}

	@Override
	public boolean CheckDeleted(BO bo) throws Exception {
		QueryBuilder<StoreDaybook, Integer> builder = getQueryBuilder();
		Where<StoreDaybook, Integer> where = builder.where();
		SynchronizationFilter(where);
		where.and();
		where.eq("link_id", ((StoreDaybook) bo).link_id);
		where.and();
		where.eq(BO.fields.DELETED, true);
		builder.setCountOf(true);
		return builder.countOf() != 0;
	}

	@Override
	protected StoreDaybook GenerateCode(StoreDaybook bo) throws Exception {
		return bo;
	}

	public List<StoreDaybook> GetPageByFiltered(Contractor contractor, Date date) {
		return GetPageByFiltered(contractor, date, true);
	}

	public List<StoreDaybook> GetPageByFiltered(Contractor contractor, Date date, boolean grouped) {
		if (contractor != null) {
			try {
				QueryBuilder<StoreDaybook, Integer> builder = getQueryBuilder();
				Where<StoreDaybook, Integer> where = builder.where();
				SynchronizationFilter(where);
				where.and();
				where.eq(DocumentBO.fields.CONTRACTOR, contractor.id);
				if (date != null)
					EntryTimeFilter(where, date);
				builder.orderBy(BO.fields.ID, true);

				System.out.println(builder.prepareStatementString());
				List<StoreDaybook> all = builder.query();

				if (!grouped)
					return all;

				return GroupRecords(all);

			} catch (Exception e) {
				log.ERROR("GetPageFiltered", e);
			}
		}

		return new ArrayList<>();
	}

	public String GetLastSign(StoreDaybook book) {
		try {
			QueryBuilder<StoreDaybook, Integer> builder = getQueryBuilder();
			Where<StoreDaybook, Integer> where = builder.where();
			SynchronizationFilter(where);
			where.and();
			// TODO:check book.contractor
			where.eq(DocumentBO.fields.CONTRACTOR, book.contractor);
			EntryTimeFilter(where, book.entry_time);
			builder.orderBy(BO.fields.ID, false);
			return builder.queryForFirst().sign;
		} catch (Exception e) {
			log.ERROR("GetLastSign", e);
		}
		return "";
	}

	public StoreDaybook CreateSign() {
		// TODO:????
		StoreDaybook book = new StoreDaybook();
		try {
			QueryBuilder<StoreDaybook, Integer> builder = getQueryBuilder();
			Where<StoreDaybook, Integer> where = builder.where();
			SynchronizationFilter(where);
			// where.and();
			// where.eq(DocumentBO.fields.CONTRACTOR, contractor.id);
			builder.orderBy(DocumentBO.fields.CONTRACTOR, true).orderBy(BO.fields.ID, true);

			List<StoreDaybook> all = builder.query();
			for (StoreDaybook b : all) {
				// Запись пришла с мобильного терминала
				if (b.hash.trim().equals("")) {
					b = CreateExtData(b);
					// Тут делаем хеш
					b.hash = Hash.sha256(b.data);
					b = UpdateDataAndHash(b);
				}
			}

			all = builder.query();
			String prevSign = "";
			for (StoreDaybook b : all) {
				if (book.contractor == null || book.contractor.id != b.contractor.id) {
					book.contractor = b.contractor;
					System.out.println("=== " + book.contractor.name);
					// Самая первая запись по контрагенту
					prevSign = "";
					System.out.println("===== " + b.contractor.name + " первая запись");
				}

				String sign = Hash.sha256(prevSign + b.hash);
				b = Signature(b, sign);
				prevSign = sign;
			}
		} catch (Exception e) {
			log.ERROR("CreateSign", e);
		}

		return book;
	}

	StoreDaybook UpdateDataAndHash(StoreDaybook book) throws Exception {
		UpdateBuilder<StoreDaybook, Integer> builder = getUpdateBuilder();
		Where<StoreDaybook, Integer> where = builder.where();
		// SynchronizationFilter(where);
		// where.and();
		where.eq(BO.fields.ID, book.id);
		// where.and();
		// where.isNull("sign");

		builder.updateColumnValue("data", book.data);
		builder.updateColumnValue("hash", book.hash);

		System.out.println(builder.prepareStatementString());
		builder.update();

		return book;
	}

	// TODO: uses
	StoreDaybook Signature(StoreDaybook book, String sign) throws Exception {
		UpdateBuilder<StoreDaybook, Integer> builder = getUpdateBuilder();
		Where<StoreDaybook, Integer> where = builder.where();
		// SynchronizationFilter(where);
		// where.and();
		where.eq(BO.fields.ID, book.id);
		where.and();
		where.isNull("sign");

		builder.updateColumnValue("sign", sign);

		System.out.println(builder.prepareStatementString());
		builder.update();
		book.sign = sign;

		return book;
	}

	final String keyReportTablePart = "ReportTablePart";

	public List<StoreDaybook> Get4Report(Date date, boolean grouped) {
		try {
			// Dao<StoreDaybook, Integer> boDao = DbHelper.geDaos(typeBO);
			QueryBuilder<StoreDaybook, Integer> builder = getQueryBuilder();
			Where<StoreDaybook, Integer> where = builder.where();
			SynchronizationFilter(where);
			ChangedFilter(where, date);
			builder.orderBy(DocumentBO.fields.CONTRACTOR, true).orderBy(BO.fields.ID, true);
			// System.out.println(builder.prepareStatementString());

			List<StoreDaybook> all = builder.query();

			if (grouped) {
				all = GroupRecords(all);
			}

			List<StoreDaybook> reportTP = new ArrayList<>();

			List<StoreDaybook> TP = new ArrayList<>();
			Contractor contractor = null;
			for (StoreDaybook b : all) {
				if (b.sign == null) {
					Console.I().WARN(getClass(), "Get4Report", "Есть неподписанные записи. (" + b + ")");
					throw new Exception("Есть неподписанные записи. (" + b + ")");
				}
				if (contractor == null || contractor.id != b.contractor.id) {
					contractor = b.contractor;
					TP = new ArrayList<>();
					b.calcFields.put(keyReportTablePart, TP);
					reportTP.add(b);
				}
				TP.add(b);
			}

			return reportTP;

		} catch (Exception e) {
			log.ERROR("Get4Report", e);
		}

		return new ArrayList<>();
	}

	protected Where<StoreDaybook, Integer> EntryTimeFilter(Where<StoreDaybook, Integer> where, Date date) throws SQLException {
		where.and();
		where.ge("entry_time", Format.beginOfDay(date));
		where.and();
		where.le("entry_time", Format.endOfDay(date));
		return where;
	}

	protected Where<StoreDaybook, Integer> ChangedFilter(Where<StoreDaybook, Integer> where, Date date) throws SQLException {
		where.and();
		where.ge("changed", Format.beginOfDay(date));
		where.and();
		where.le("changed", Format.endOfDay(date));
		return where;
	}

	protected List<StoreDaybook> GroupRecords(List<StoreDaybook> all) {
		// Находим все записи со ссылкой
		Map<Integer, StoreDaybook> links = new HashMap<>();
		for (StoreDaybook sd : all) {
			// Пропускаем пустую
			if (sd.link_id != 0)
				links.put(sd.link_id, sd);
		}

		List<StoreDaybook> filtered = new ArrayList<>();
		for (StoreDaybook sd : all) {
			// Убираем записи, на которые есть ссылки и со ссылками
			if (!links.keySet().contains(sd.id) && sd.link_id == 0)
				filtered.add(sd);
		}
		filtered.addAll(links.values());
		Collections.sort(filtered, new StoreDaybookComparator());
		return filtered;
	}

	public List<StoreDaybook> GetHistory(StoreDaybook book) {
		try {
			int l_id = book.link_id;
			if (l_id == 0)
				l_id = book.id;

			QueryBuilder<StoreDaybook, Integer> builder = getQueryBuilder();
			Where<StoreDaybook, Integer> where = builder.where();
			// enableSyncFilter = false;
			where.and(SynchronizationFilter(where), where.or(where.eq(BO.fields.ID, l_id), where.eq("link_id", l_id)));
			builder.orderBy(BO.fields.ID, true);
			System.out.println(builder.prepareStatementString());

			return builder.query();
		} catch (Exception e) {
			log.ERROR("GetHistory", e);
		}

		return new ArrayList<>();
	}

	// @Override
	// public List<?> getTablePart4Rep() {
	// return (List<?>) calcFields.get(keyReportTablePart);
	// }
}