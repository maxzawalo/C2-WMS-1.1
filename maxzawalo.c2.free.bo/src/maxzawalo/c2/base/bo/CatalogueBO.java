package maxzawalo.c2.base.bo;

import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.free.bo.Product;

/**
 * Created by Max on 16.03.2017.
 */

public class CatalogueBO<TypeBO> extends BO<TypeBO> {
	public static class fields {
		// TODO: привинтить caption для бизконтрола
		public static final String IS_GROUP = "is_group";
		public static final String PARENT = "parent_id";
		public static final String NAME = "name";
		public static final String FULL_NAME = "full_name";
		public static final String ROOT = "root_id";
	}

	@BoField(caption = "Наименование", fieldName1C = "Наименование")
	@DatabaseField(width = 100, columnName = CatalogueBO.fields.NAME)
	public String name = "";

	@BoField(caption = "Полное наименование", fieldName1C = "НаименованиеПолное")
	@DatabaseField(width = 1000, columnName = CatalogueBO.fields.FULL_NAME)
	public String full_name = "";

	// @DatabaseField(index = true, width = 15)
	public String parent_code = "";

	@BoField(caption = "Группа", fieldName1C = "Родитель")
	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = false, columnName = CatalogueBO.fields.PARENT)
	public BO parent;

	@BoField(caption = "Это группа")
	@DatabaseField(columnName = CatalogueBO.fields.IS_GROUP)
	public boolean is_group = false;

	@BoField(caption = "Корневой элемент") // В смысле главный элемент - остальные с разными названиями, но одно и то
											// же.
	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = false, columnName = CatalogueBO.fields.ROOT)
	public BO root;

	public List<TypeBO> childs = new ArrayList<>();

	public boolean fuzzy = false;

	// public CatalogueBO() {
	// DeleteFilterOff();
	// }

	@Override
	protected void Check() {
		super.Check();
		// parent_code = parent_code.trim();
	}

	public Class<?> ReplaceType() {
		Class<?> type = getClass();
		return type;
	}

	@Override
	public String toString() {
		return "(" + this.id + ")" + this.code + " " + this.name;
	}

	public boolean ChildOf(List<CatalogueBO> list) {
		if (parent == null) {
			if (list.size() == 1)
				return true;
			else
				return false;
		}

		for (Object group_ch : list)
			if (parent.id == ((CatalogueBO) group_ch).id)
				return true;

		return false;
	}

}
