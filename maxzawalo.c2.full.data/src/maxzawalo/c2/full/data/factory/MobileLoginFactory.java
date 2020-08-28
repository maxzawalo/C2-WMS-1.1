package maxzawalo.c2.full.data.factory;

import java.util.Date;

import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.base.crypto.Hash;
import maxzawalo.c2.base.data.factory.FactoryBO;
import maxzawalo.c2.full.bo.MobileLogin;

public class MobileLoginFactory extends FactoryBO<MobileLogin> {

	public String Create() throws Exception {
		String loginHash = Hash.sha256(User.current.id + "" + new Date().getTime());
		MobileLogin ml = new MobileLogin();
		ml.login_hash = loginHash;
		Save(ml);
		return loginHash;
	}

	@Override
	protected MobileLogin GenerateCode(MobileLogin l) throws Exception {
		return l;
	}
}