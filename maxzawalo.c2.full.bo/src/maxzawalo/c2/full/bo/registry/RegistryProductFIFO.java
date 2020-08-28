package maxzawalo.c2.full.bo.registry;

import com.j256.ormlite.table.DatabaseTable;

import maxzawalo.c2.free.bo.registry.RegistryProduct;

@DatabaseTable(tableName = "registry_product_fifo")
public class RegistryProductFIFO extends RegistryProduct {
	public RegistryProductFIFO() {
		typeBO = getClass();
	}
}