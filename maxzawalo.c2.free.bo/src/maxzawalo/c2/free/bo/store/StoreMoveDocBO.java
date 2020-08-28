package maxzawalo.c2.free.bo.store;

import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.free.bo.Settings;
import maxzawalo.c2.free.bo.Store;

public class StoreMoveDocBO<Doc> extends StoreDocBO<Doc> {
	public static class fields {
		public static final String TO_STORE = "to_store_id";
	}

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, columnName = fields.TO_STORE)
	public Store to_store = Settings.mainStore;
}