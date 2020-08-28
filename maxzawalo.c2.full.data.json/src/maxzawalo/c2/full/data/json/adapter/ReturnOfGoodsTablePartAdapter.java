package maxzawalo.c2.full.data.json.adapter;

import maxzawalo.c2.free.data.json.StoreTPAdapter;
import maxzawalo.c2.full.bo.document.return_of_goods.ReturnOfGoods;
import maxzawalo.c2.full.bo.document.return_of_goods.ReturnOfGoodsTablePart;

public class ReturnOfGoodsTablePartAdapter {
	public static class Product extends StoreTPAdapter<ReturnOfGoodsTablePart.Product, ReturnOfGoods> {
	}

	public static class Service extends StoreTPAdapter<ReturnOfGoodsTablePart.Service, ReturnOfGoods> {
	}

	public static class Equipment extends StoreTPAdapter<ReturnOfGoodsTablePart.Equipment, ReturnOfGoods> {
	}
}