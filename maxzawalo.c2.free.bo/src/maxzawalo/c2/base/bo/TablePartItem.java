package maxzawalo.c2.base.bo;

import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.interfaces.ActionC2;
import maxzawalo.c2.base.utils.Format;

public class TablePartItem<Item> extends BO<Item> {
	public static class fields {
		public static final String DOC = "doc_id";
		public static final String POS = "pos";
	}

	@BoField(caption = "Документ", fieldName1C = "Документ")
	@DatabaseField(index = true, columnName = fields.DOC)
	public int doc = 0;

	@XmlTransient
	public ActionC2 onChanged;

	public boolean bad = false;

	public TablePartItem() {
	}

	public void Calc(String fieldName) {
		System.out.println("Calc " + fieldName);
	}

	@Override
	public boolean HasNoCode() {
		return true;
	}

	@Override
	public Object getCalcField(String name) {
		switch (name) {
		case fields.POS:
			return calcFields.get(fields.POS);
		default:
			return super.getCalcField(name);
		}
	}
}