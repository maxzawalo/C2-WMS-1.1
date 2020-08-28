package maxzawalo.c2.full.data.factory.registry;

import java.sql.SQLException;

import com.j256.ormlite.stmt.Where;

import maxzawalo.c2.base.data.factory.RegistryFactory;
import maxzawalo.c2.full.bo.registry.RegistryInventory;

public class RegistryInventoryFactory extends RegistryFactory<RegistryInventory> {
	@Override
	protected void ItemFilter(RegistryInventory r, Where<RegistryInventory, Integer> where) throws SQLException {
		// При проводке разные Товары и Партии
		// Where<RegistryProduct, Integer> where =
		super.ItemFilter(r, where);
		where.and();
		where.eq("product_id", r.product);
	}
}