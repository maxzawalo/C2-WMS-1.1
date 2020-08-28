package maxzawalo.c2.free.data.factory.document;

import java.sql.SQLException;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.CatalogueBO;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.data.factory.DocumentFactory;
import maxzawalo.c2.free.bo.BankAccount;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.bo.bank.BankDocBO;

public class BankDocFactory<Doc> extends DocumentFactory<Doc> {

	public boolean Exists(BankDocBO doc) {
		try {
			QueryBuilder<Doc, Integer> builder = getQueryBuilder();
			Where<Doc, Integer> where = builder.where();
			SynchronizationFilter(where);
			where.and();
			where.eq(BankDocBO.fields.IN_DATE, doc.in_date);
			where.and();
			where.eq(BankDocBO.fields.IN_NUMBER, doc.in_number);
			where.and();
			where.eq(DocumentBO.fields.CONTRACTOR, doc.contractor);

			QueryBuilder<BankAccount, Integer> baBuilder = getQueryBuilderT(BankAccount.class);
			Where<BankAccount, Integer> baWhere = baBuilder.where();			
			builder.join(BankDocBO.fields.BANK_ACCOUNT, BO.fields.ID, baBuilder);
			baWhere.eq(BankAccount.fields.NUMBER, doc.bank_account.number);

			builder.setCountOf(true);
			System.out.println(builder.prepareStatementString());
			return (builder.countOf() != 0);
		} catch (Exception e) {
			log.ERROR("Exists", e);
		}
		return true;
	}

	@Override
	protected QueryBuilder<Doc, Integer> getBuilderWithFilter(String value) throws SQLException {
		QueryBuilder<Contractor, Integer> ccb = getQueryBuilderT(Contractor.class);
		QueryBuilder<Doc, Integer> builder = getQueryBuilder();
		Where<Doc, Integer> where = builder.where();
		if (!value.equals(""))
			builder.join(DocumentBO.fields.CONTRACTOR, BO.fields.ID, ccb);
		SynchronizationFilter(where);
		if (!value.equals(""))
			CreateMultipleLike(CatalogueBO.fields.NAME, value, ccb.where());
		return builder;
	}

	// @Override
	// public List<TablePartItem> LoadTP(DocumentBO doc, Class type) {
	// List<TablePartItem> allTP = super.LoadTP(doc, type);
	// // Подгружаем Level 3
	// // TODO: Core
	// for (TablePartItem tpi : allTP) {
	// BankTP tp = (BankTP) tpi;
	// // if (tp.product != null && tp.product.units != null)
	// // tp.product.units = new
	// // UnitsFactory().GetById(tp.product.units.id);
	// }
	// return allTP;
	// }

	@Override
	protected boolean ProductTransaction(Doc doc) {
		// Только Бух. проводки
		return true;
	}
}