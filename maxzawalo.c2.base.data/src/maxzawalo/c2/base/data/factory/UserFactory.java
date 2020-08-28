package maxzawalo.c2.base.data.factory;

import java.util.Date;

import com.j256.ormlite.stmt.UpdateBuilder;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.Coworker;
import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.base.crypto.Hash;

public class UserFactory extends CatalogueFactory<User> {

	@Override
	public boolean CheckCodeExists(BO bo, String code) {
		// Коды не генерируем
		return false;
	}

	@Override
	protected User GenerateCode(User bo) throws Exception {
		// Коды не генерируем
		bo.code = "";
		return bo;
	}

	// TODO: Зависит от Клиента(Контрагента) - Это же дополнительная защита от
	// передачи софта и подмены БД - не смогут войти
	static String pref = "ism=83*-90(44v!$%^s30338";

	public static boolean CheckPassword(User user, String password) {
		String hash = CreatePasswordHash(password);
		System.out.println(hash);
		return user.password.equals(hash);
	}

	public static String CreatePasswordHash(String password) {
		return Hash.sha256(pref + password);
	}

	public User SetNewPassword(User user, String password) throws Exception {
		String hash = CreatePasswordHash(password);

		UpdateBuilder<User, Integer> builder = getUpdateBuilder();
		builder.where().eq(BO.fields.ID, user.id);
		user.changed = new Date();
		builder.updateColumnValue(BO.fields.CHANGED, user.changed);
		builder.updateColumnValue(User.fields.PASSWORD, hash);
		builder.updateColumnValue(BO.fields.CHANGED_BY, User.current);
		builder.update();

		return user;
	}

	public User GetByHardware(String hdw) {
		return GetByParam(User.fields.HARDWARE, hdw);
	}

	public User CreateNew(String name, String coworkerCode, String coworkerPosition) {
		User user = null;
		try {
			Coworker c = new CoworkerFactory().GetByCode(coworkerCode);
			boolean isNew = false;
			if (c == null) {
				c = new Coworker();
				isNew = true;
			}
			c.name = name;
			c.code = coworkerCode;
			c.position = coworkerPosition;
			// Сначала Сотрудник - если не создался - не сохраняем и
			// пользователя
			new CoworkerFactory().Save(c);

			if (isNew) {
				user = new User();
				// user.name = name;
				user.password = new UserFactory().CreatePasswordHash("");
				user.coworker = c;
				Save(user);
			}
		} catch (Exception e) {
			log.ERROR("CreateNew", e);
		}
		return user;
	}
}