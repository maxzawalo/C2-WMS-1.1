package maxzawalo.c2.full.bo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import maxzawalo.c2.base.bo.BO;
@DatabaseTable(tableName="scanned_barcode")
public class ScannedBarcode extends BO<ScannedBarcode> {

	@DatabaseField(index = true, width = 5)
	public String type = "";

	/**
	 * SHA256 Hash
	 */
	@DatabaseField(width = 64)
	public String link = "";

	public ScannedBarcode() {
	}

	public ScannedBarcode(String code) {
		setCode(code);
	}

	public void LogScanned(String code, String format) {
		log.BP("ScannedBarcode.Scan", format + "." + code);
	}

	@Override
	public String toString() {
		return code;
	}
}