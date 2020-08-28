package maxzawalo.c2.free.data.factory.catalogue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.base.data.factory.CatalogueFactory;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.free.bo.Price;
import maxzawalo.c2.free.bo.Product;
import maxzawalo.c2.free.bo.document.invoice.Invoice;
import maxzawalo.c2.free.cache.Cache;

public class PriceFactory extends CatalogueFactory<Price> {

	public Price Get(Invoice invoice, Product product, double price) {
		try {
			QueryBuilder<Price, Integer> builder = getQueryBuilder();
			Where<Price, Integer> where = builder.where();
			NonDeletedFilter(where);
			where.and();
			where.eq("invoice_id", invoice.id);
			where.and();
			where.eq("product_id", product.id);
			// TODO: близкую цену (пару % разницы) - учесть что забыли доставку
			// и перепроводим
			// where.and();
			// where.eq("price", price);
			// log.DEBUG("Get", builder.prepareStatementString());

			return builder.queryForFirst();
		} catch (Exception e) {
			log.ERROR("Get", e);
		}

		return null;
	}

	public List<Price> GetAllContains(String param, Object value) {
		try {
			QueryBuilder<Price, Integer> builder = getQueryBuilder();
			Where<Price, Integer> where = builder.where();
			SynchronizationFilter(where);
			where.and();
			SelectArg selectArg = new SelectArg();
			where.like(param, selectArg);
			selectArg.setValue("%" + value + "%");
			builder.orderBy(BO.fields.CODE, true);
			return builder.query(); // returns list of ten items
		} catch (Exception e) {
			log.ERROR("GetAllContains", e);
		}
		return new ArrayList<>();
	}

	// TODO:
	// public Barcode AddBarcode() {
	// barcode = new Barcode(EAN.generateEAN(code.split("-")[1]));
	// return barcode;
	// }

	// TODO: full
	// public List<Price> GetByBarcode(String code) {
	// Dao<Price, Integer> priceDao = DbHelper.geDaos(Price.class);
	// QueryBuilder<Price, Integer> priceBuilder = priceDao.queryBuilder();
	//
	// Dao<Barcode, Integer> barcodeDao = DbHelper.geDaos(Barcode.class);
	// QueryBuilder<Barcode, Integer> barcodeBuilder =
	// barcodeDao.queryBuilder();
	//
	// try {
	// barcodeBuilder.where().eq(BO.fields.CODE, code);
	// priceBuilder.join(barcodeBuilder);
	// return priceDao.query(priceBuilder.prepare());
	// } catch (Exception e) {
	// log.ERROR("GetByBarcode", e);
	// }
	//
	// return new ArrayList<>();
//	}

	public List<Price> GetByInvoiceDate(Date date, int price_state) {
		try {

			QueryBuilder<Invoice, Integer> invoiceBuilder = getQueryBuilderT(Invoice.class);
			Where<Invoice, Integer> whereInvoice = invoiceBuilder.where();
			QueryBuilder<Price, Integer> priceBuilder = getQueryBuilder();
			Where<Price, Integer> wherePrice = priceBuilder.where();
			priceBuilder.join("invoice_id", BO.fields.ID, invoiceBuilder);
			NonDeletedFilter(wherePrice);
			wherePrice.and();
			wherePrice.eq("price_state", price_state);

			whereInvoice.ge(DocumentBO.fields.DOC_DATE, Format.beginOfDay(date));
			whereInvoice.and();
			whereInvoice.le(DocumentBO.fields.DOC_DATE, Format.endOfDay(date));
			System.out.println(priceBuilder.prepareStatementString());
			return priceBuilder.query();
		} catch (Exception e) {
			log.ERROR("GetByInvoiceDate", e);
		}
		return new ArrayList<>();
	}

	public List<Price> GetByState(int price_state) {
		try {
			QueryBuilder<Price, Integer> priceBuilder = getQueryBuilder();
			Where<Price, Integer> wherePrice = priceBuilder.where();
			NonDeletedFilter(wherePrice);
			wherePrice.and();
			wherePrice.eq("price_state", price_state);
			System.out.println(priceBuilder.prepareStatementString());
			return priceBuilder.query();
		} catch (Exception e) {
			log.ERROR("GetByState", e);
		}
		return new ArrayList<>();
	}

	public boolean UpdateState(Price price, int price_state) {
		try {
			UpdateBuilder<Price, Integer> builder = getUpdateBuilder();
			Where<Price, Integer> where = builder.where();
			where.eq(BO.fields.ID, price.id);
			builder.updateColumnValue("price_state", price_state);
			// Ставим для синхронизации с 1С
			price.changed = new Date();
			builder.updateColumnValue(BO.fields.CHANGED, price.changed);
			builder.updateColumnValue(BO.fields.CHANGED_BY, User.current);
			builder.update();

			price.changed_by = User.current;
			price.price_state = price_state;
			Cache.I().putInMap(typeBO, price);
			return true;
		} catch (Exception e) {
			log.ERROR("UpdateState", e);
			return false;
		}
	}

}