package maxzawalo.c2.base.data.factory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.SlaveCatalogueBO;
import maxzawalo.c2.base.data.DbHelper;
import maxzawalo.c2.base.utils.Profiler;

public class SlaveCatalogueFactory<TypeBO, OwnerType> extends CatalogueFactory<TypeBO> {

	public Class<OwnerType> ownerType;

	public SlaveCatalogueFactory() {
		Class clazz = this.getClass();
		Type[] gParams = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments();
		if (gParams.length == 2)
			this.ownerType = (Class<OwnerType>) gParams[1];
	}
	
	public List<TypeBO> GetByOwner( BO owner, String searchData) {
		return GetByOwner(owner, false, searchData);
	}

	public List<TypeBO> GetByOwner(BO owner, boolean loadOwner, String searchData) {
		try {
			Profiler profiler = new Profiler();
			profiler.Start("GetByOwner");
			Dao<TypeBO, Integer> lotDao = DbHelper.geDaos(typeBO);
			QueryBuilder<TypeBO, Integer> builder = getQueryBuilder();
			Where<TypeBO, Integer> where = builder.where();
			SynchronizationFilter(where);
			if (owner != null) {
				where.and();
				where.eq("owner_id", owner.id);
			}

			builder.orderBy(BO.fields.CODE, true);

			// GenericRowMapper mapper = new GenericRowMapper<>(typeBO);
			// // Добавляем загрузку foreignAutoRefresh = false
			mapper.nonSkipForeign.put("owner", ownerType);
			mapper.setEntityClass(typeBO);

			mapper.level = 0;
			List<TypeBO> data = lotDao.queryRaw(builder.prepareStatementString(), mapper).getResults();
			profiler.Stop("GetByOwner");
			profiler.PrintElapsed("GetByOwner");
			return data;
		} catch (Exception e) {
			log.ERROR("GetByOwner", e);
		}

		return new ArrayList<>();
	}

	public void UpdateOwner(SlaveCatalogueBO cat) throws Exception {
		if (cat.owner == null)
			return;
		UpdateBuilder<TypeBO, Integer> builder = getUpdateBuilder();
		builder.where().eq(BO.fields.ID, cat.id);
		// TODO: chaeck cat.owner.id
		builder.updateColumnValue("owner_id", cat.owner);
		builder.update();
	}
}