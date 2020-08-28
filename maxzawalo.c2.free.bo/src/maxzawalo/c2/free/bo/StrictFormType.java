package maxzawalo.c2.free.bo;

public class StrictFormType {
	public String code = "";
	public String name = "";

	public StrictFormType(String code, String name) {
		this.code = code;
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}