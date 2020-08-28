package maxzawalo.c2.free.bo;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.bo.CatalogueBO;

/**
 * Created by Max on 10.03.2017.
 */
@BoField(caption = "Единица измерения", type1C = "Справочники.КлассификаторЕдиницИзмерения")
public class Units extends CatalogueBO<Units> {

	public Units() {
	}

	@Override
	public String toString() {
		// return "(" + this.code+")" + this.name + "|"+ this.full_name;
		return this.name;
	}
}