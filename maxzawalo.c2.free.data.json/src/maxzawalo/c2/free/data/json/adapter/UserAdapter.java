package maxzawalo.c2.free.data.json.adapter;

import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.free.data.json.CatalogueAdapter;

public class UserAdapter extends CatalogueAdapter<User> {
	public UserAdapter() {
	}

	@Override
	protected boolean IsSkipField(String fieldName) {
		return (super.IsSkipField(fieldName) || fieldName.equals(User.fields.PASSWORD));
	}

	@Override
	public void SetSkipFields() {
		
		super.SetSkipFields();
		skipFields.add(User.fields.HARDWARE);
		if (web_ui) {
			
		} else {
			skipFields.add(User.fields.COWORKER.replace("_id", ""));
		}
	}
}