package maxzawalo.c2.base.bo;

import com.j256.ormlite.field.DatabaseField;

public class User extends CatalogueBO<User> {

	public static class fields {
		public static final String COWORKER = "coworker_id";
		public static final String PASSWORD = "password";
		public static final String HARDWARE = "hdwr";
	}

	public static User current = new User();
	public static User zero = new User();

	// TODO: meta
	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, columnName = User.fields.COWORKER)
	public Coworker coworker;

	/**
	 * SHA256 Hash
	 */
	@DatabaseField(width = 64, columnName = User.fields.PASSWORD)
	public String password = "";

	/**
	 * Привязка к железу
	 */
	@DatabaseField(width = 64, columnName = User.fields.HARDWARE)
	public String hdw = "";

	public User() {
		// coworker = new Coworker().GetById(id)
	}

	@Override
	public String toString() {
		// TODO:
		if (coworker != null)
			return coworker.name;

		return name;
	}

	// TODO: factory
	public boolean isAdmin() {
		// TODO
		return (("" + this).contains("Линник") || ("" + this).contains("Завало"));
	}

	public boolean isSimple() {
		// TODO
		return ("" + this).contains("Белоусов");
	}

	public boolean isDirector() {
		return (("" + this).contains("Шимак"));
	}
}