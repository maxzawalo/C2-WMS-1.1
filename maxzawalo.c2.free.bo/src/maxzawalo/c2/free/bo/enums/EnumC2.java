package maxzawalo.c2.free.bo.enums;

import java.util.ArrayList;
import java.util.List;

import maxzawalo.c2.base.bo.CatalogueBO;

public class EnumC2<TypeBO> extends CatalogueBO<TypeBO> {
	protected List<EnumC2> allEnum;

	protected CatalogueBO CreateEnum(int id, String name) {
		CatalogueBO newEnum = null;
		try {
			newEnum = (CatalogueBO) typeBO.newInstance();
		} catch (Exception e) {
			log.ERROR("CreateEnum", e);
		}
		newEnum.id = id;
		newEnum.name = name;
		return newEnum;
	}

	public List<TypeBO> getEnum() {
		return new ArrayList<>();
	}

	public TypeBO getEnumById(int id) {
		for (TypeBO ct : getEnum())
			if (((EnumC2) ct).id == id)
				return ct;
		return null;
	}

	public TypeBO getEnumByName(String name) {
		for (TypeBO ct : getEnum())
			if (((EnumC2) ct).name.equals(name))
				return ct;
		return null;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		return this.id == ((EnumC2) obj).id;
	}

	public boolean Check(String name) {
		return equals(getEnumByName(name));
	}

}