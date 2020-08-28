package maxzawalo.c2.free.data.factory.catalogue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.base.data.factory.CatalogueFactory;
import maxzawalo.c2.free.bo.StrictForm;
import maxzawalo.c2.free.bo.StrictFormType;
import maxzawalo.c2.free.cache.Cache;

public class StrictFormFactory extends CatalogueFactory<StrictForm> {

	public StrictFormFactory Create() {
		return (StrictFormFactory) super.Create(StrictForm.class);
	}

	public List<StrictForm> SelectDocEntries(DocumentBO doc) {
		// TODO: from cache - +deleted
		StrictForm form = new StrictForm();
		form.reg_id = doc.id;
		form.reg_type = doc.reg_type;

		List<StrictForm> all = new ArrayList<>();
		try {
			QueryBuilder<StrictForm, Integer> builder = getQueryBuilder();
			Where<StrictForm, Integer> where = builder.where();
			RegistratorFilter(where, form);
			builder.orderBy(BO.fields.ID, true);
			all = builder.query();
		} catch (Exception e) {
			log.ERROR("SelectDocEntries", e);
		}
		return all;
	}

	public boolean Exists(StrictForm form) {
		try {
			QueryBuilder<StrictForm, Integer> builder = getQueryBuilder();
			Where<StrictForm, Integer> where = builder.where();
			builder.setCountOf(true);
			RegistratorFilter(where, form);
			where.and();
			where.eq(StrictForm.fields.FORM_TYPE_NAME, form.form_type_name);
			where.and();
			where.eq(StrictForm.fields.FORM_BATCH, form.form_batch);
			where.and();
			where.eq(StrictForm.fields.FORM_NUMBER, form.form_number);
			if (builder.countOf() != 0)
				return true;
		} catch (Exception e) {
			log.ERROR("Exists", e);
		}
		return false;
	}

	public StrictForm GetByNumber(String form_batch, String form_number) {
		try {
			QueryBuilder<StrictForm, Integer> builder = getQueryBuilder();
			Where<StrictForm, Integer> where = builder.where();
			where.eq(StrictForm.fields.FORM_BATCH, form_batch);
			where.and();
			where.eq(StrictForm.fields.FORM_NUMBER, form_number);
			return builder.queryForFirst();
		} catch (Exception e) {
			log.ERROR("GetByNumber", e);
		}
		return null;
	}

	public void UpdateWriteOff(StrictForm form) {
		try {
			UpdateBuilder<StrictForm, Integer> builder = getUpdateBuilder();
			Where<StrictForm, Integer> where = builder.where();
			where.eq(StrictForm.fields.FORM_TYPE_NAME, form.form_type_name);
			where.and();
			where.eq(StrictForm.fields.FORM_BATCH, form.form_batch);
			where.and();
			where.eq(StrictForm.fields.FORM_NUMBER, form.form_number);

			builder.updateColumnValue(StrictForm.fields.WRITE_OFF_TYPE, form.write_off_type);
			builder.updateColumnValue(BO.fields.CHANGED, new Date());
			builder.updateColumnValue(BO.fields.CHANGED_BY, User.current);
			builder.update();

			// TODO: Cache.I().putInList(typeBO, this); ?
		} catch (Exception e) {
			log.ERROR("UpdateWriteOff", e);
		}
	}

	protected void RegistratorFilter(Where<StrictForm, Integer> where, StrictForm form) throws SQLException {
		NonDeletedFilter(where);
		where.and();
		where.eq(StrictForm.fields.REG_TYPE, form.reg_type);
		where.and();
		where.eq(StrictForm.fields.REG_ID, form.reg_id);
	}

	public List<StrictForm> SelectGroped4Types() {
		List<StrictForm> all = new ArrayList<>();
		try {
			QueryBuilder<StrictForm, Integer> builder = getQueryBuilder();
			Where<StrictForm, Integer> where = builder.where();
			NonDeletedFilter(where);
			builder.groupBy(StrictForm.fields.FORM_TYPE_CODE);
			builder.orderBy(StrictForm.fields.FORM_TYPE_NAME, true);
			all = builder.query();
		} catch (Exception e) {
			log.ERROR("SelectGroped4Types", e);
		}
		return all;
	}

	public List<StrictFormType> getTypes() {
		List<StrictFormType> types = Cache.I().getList("StrictFormType.getTypes");

		if (types == null) {
			types = new ArrayList<>();
			List<StrictForm> strictForms = SelectGroped4Types();
			for (StrictForm form : strictForms) {
				// if (form.form_type_name.equals(""))
				// continue;
				StrictFormType t = new StrictFormType(form.form_type_code, form.form_type_name);
				types.add(t);
			}
			Cache.I().putList("StrictFormType.getTypes", types, 600);
		}
		return types;
	}

	@Override
	protected StrictForm GenerateCode(StrictForm bo) throws Exception {
		return bo;
	}

	@Override
	protected StrictForm BeforeSave(StrictForm bo) throws Exception {
		// Налоговая стала "драть" за строчные
		bo.form_batch = bo.form_batch.toUpperCase().trim();
		return super.BeforeSave(bo);
	}
}