package maxzawalo.c2.free.data.json;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Max on 20.03.2017.
 */

public class SlaveCatalogueAdapter<TypeBO, OwnerType> extends CatalogueAdapter<TypeBO> {

	Class<OwnerType> ownerType;

	public SlaveCatalogueAdapter() {

		Class clazz = this.getClass();
		Type[] gParams = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments();
		if (gParams.length == 2)
			this.ownerType = (Class<OwnerType>) gParams[1];

		// replaces.add(new ReplacedField("Владелец", "owner_code"));
		replaces.add(new ReplacedField("ВладелецУИ", "owner"));

	}
}