package maxzawalo.c2.base.interfaces;

import java.util.List;

public interface AutoSuggestorI {

	Class context = null;
	void TextFieldPressEnter();
	void setDictionary(List<String> words);
	void setEnabled(boolean selected);
	void Move();

}