package maxzawalo.c2.full.bo;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import maxzawalo.c2.base.annotation.BoField;
import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.bo.Product;

public class StoreDaybook extends BO<StoreDaybook> {

	@DatabaseField(index = true, dataType = DataType.DATE_LONG)
	public Date entry_time = new Date();

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, canBeNull = false)
	public Contractor contractor;

	@DatabaseField(index = true, foreign = true, foreignAutoRefresh = true, canBeNull = false)
	public Product product;

	// Не так важно.
	// Если сканируем ценник или из Подбора- ставится - чтобы знать, что
	// показали клиенту.
	@BoField(caption = "Цена")
	@DatabaseField
	public double price = 0;

	// Можем корректировать на PC или с терминала
	@BoField(caption = "Количество")
	@DatabaseField
	public double count = 0;

	@DatabaseField(index = true)
	public int link_id = 0;

	// linkhash - ссылка на hash изменяемой
	// previd_prevhash_
	// unp_contractorname_timestamp_linkhash_productname_count_unitscode_ценанаценнике_ктовыдал_ктовзял_deleted_comment
	@DatabaseField(width = 1000, canBeNull = false)
	public String data = "";

	/**
	 * MD5 Hash
	 */
	@DatabaseField(width = 64, canBeNull = false)
	public String hash;

	/**
	 * MD5 Hash
	 */
	@DatabaseField(width = 64)
	public String sign;

	@DatabaseField(width = 100)
	public String comment = "";

	@DatabaseField(width = 50)
	public String who_recieve = "";

	@DatabaseField(width = 10, canBeNull = false)
	public String device = "pc";

	// TODO: Некая пометка что перенесли в док-т. Ссылка на него (учесть виды
	// док).
	// Не брать в дальнейшем в док-ты. Показывать в UI иконку.
//
//	public StoreDaybook() {
//		DeleteFilterOff();
//	}
//


	@Override
	public String toString() {
		return "Складской журнал " + contractor.name + " | " + product.name + " | " + count + " | " + price + " | "
				+ who_recieve + " | " + id;
	}

//	
	@Override
	public boolean HasNoCode() {
		return true;
	}
}