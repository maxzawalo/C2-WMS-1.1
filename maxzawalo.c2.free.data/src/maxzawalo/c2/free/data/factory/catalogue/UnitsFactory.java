package maxzawalo.c2.free.data.factory.catalogue;

import maxzawalo.c2.base.data.factory.CatalogueFactory;
import maxzawalo.c2.free.bo.Units;

public class UnitsFactory extends CatalogueFactory<Units> {
	public UnitsFactory Create() {
		return (UnitsFactory) super.Create(Units.class);
	}
}