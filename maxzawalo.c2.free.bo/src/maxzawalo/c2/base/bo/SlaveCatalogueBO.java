package maxzawalo.c2.base.bo;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.annotation.BoField;

public class SlaveCatalogueBO<TypeBO, OwnerType> extends CatalogueBO<TypeBO> {

	public static class fields {
		public static final String OWNER = "owner_id";
	}

	@BoField(caption = "Владелец ", fieldName1C = "Владелец")
	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = false, columnName = SlaveCatalogueBO.fields.OWNER)
	public BO owner;

	public Class<OwnerType> ownerType;

	public SlaveCatalogueBO() {

		Class clazz = this.getClass();
		Type[] gParams = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments();
		if (gParams.length == 2)
			this.ownerType = (Class<OwnerType>) gParams[1];
	}
}