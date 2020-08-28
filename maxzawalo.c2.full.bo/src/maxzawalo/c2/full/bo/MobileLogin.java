package maxzawalo.c2.full.bo;

import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.bo.BO;

//TODO: в сетевую полную
public class MobileLogin extends BO<MobileLogin> {
	/**
	 * SHA256 Hash
	 */
	@DatabaseField(width = 64)
	public String login_hash = "";
}