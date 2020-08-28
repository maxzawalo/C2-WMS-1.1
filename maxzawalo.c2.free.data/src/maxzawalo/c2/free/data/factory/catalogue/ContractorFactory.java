package maxzawalo.c2.free.data.factory.catalogue;

import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import maxzawalo.c2.base.bo.CatalogueBO;
import maxzawalo.c2.base.data.factory.CatalogueFactory;
import maxzawalo.c2.free.bo.ContactInfo;
import maxzawalo.c2.free.bo.Contractor;

public class ContractorFactory extends CatalogueFactory<Contractor> {

	public List<Contractor> GetPageByFiltered(Contractor contractor, long currentPage, long pageSize,
			CatalogueBO parent, String searchData) {
		try {
			QueryBuilder<Contractor, Integer> builder = getQueryBuilder();
			Where<Contractor, Integer> where = builder.where();
			SynchronizationFilter(where);
			String sql = where.getStatement();
			sql += ItemFilter(contractor, parent, true, "", searchData);
			where = builder.where();
			where.raw(sql);
			builder.offset(currentPage * pageSize).limit(pageSize);
			List<Contractor> list = builder.query();

			// TODO: fuzzy_ids filter
			System.out.println("this.fuzzy_ids " + contractor.fuzzy_ids);
			for (Contractor c : list) {
				c.fuzzy = (contractor.fuzzy_ids.size() != 0 && contractor.fuzzy_ids.contains(c.id));
			}
			return list;
		} catch (Exception e) {
			log.ERROR("GetPageFiltered", e);
		}

		return new ArrayList<>();
	}

	public static String ReportPaymentData(Contractor contractor) {
		// TODO: код?
		if (contractor != null)
			return contractor.main_bank_acccount + " в " + contractor.main_bank_name + " код " + ", УНП:"
					+ contractor.unp;
		return "";
	}

	public static boolean CheckUnp(Contractor contractor) {
		return !contractor.unp.trim().equals("");
	}

	public Contractor getContactInfo(Contractor contractor) {
		if (contractor != null && contractor.contactInfo == null)
			contractor.contactInfo = new ContactInfoFactory().GetByOwner(contractor, "");
		// else
		// contractor.contactInfo = new ArrayList<>();
		return contractor;
	}

	public Contractor LoadContactInfo(Contractor contractor) {
		contractor = getContactInfo(contractor);
		contractor = setContactInfoFields(contractor);
		return contractor;
	}

	public Contractor setContactInfoFields(Contractor contractor) {
		if (contractor != null)
			for (ContactInfo info : getContactInfo(contractor).contactInfo)
				if (info.contact_type.equals("Юридический адрес")) {
					// TODO: calc fields
					contractor.legal_address = info.name;
				} else if (info.contact_type.equals("Телефоны")) {
					contractor.phone = info.name;
				} else if (info.contact_type.equals("Факс")) {
					contractor.fax = info.name;
				}
		// Фактический адрес
		return contractor;
	}

	@Override
	public Contractor Save(Contractor contractor) throws Exception {
		contractor = super.Save(contractor);

		if (contractor.contactInfo == null)
			contractor.contactInfo = new ArrayList<>();

		Create(contractor, "Юридический адрес");
		Create(contractor, "Почтовый адрес");
		Create(contractor, "Телефоны");
		Create(contractor, "Факс");
		//

		for (ContactInfo info : contractor.contactInfo)
			if (info.contact_type.equals("Юридический адрес")) {
				info.name = contractor.legal_address;
			} else if (info.contact_type.equals("Телефоны")) {
				info.name = contractor.phone;
			} else if (info.contact_type.equals("Факс")) {
				info.name = contractor.fax;
			}
		ContactInfoFactory factory = new ContactInfoFactory();
		for (ContactInfo info : contractor.contactInfo)
			factory.Save(info);

		return contractor;
	}

	protected void Create(Contractor contractor, String contact_type) {
		boolean found = false;
		for (ContactInfo info : contractor.contactInfo)
			if (info.contact_type.equals(contact_type)) {
				found = true;
				break;
			}
		if (!found) {
			ContactInfo info = new ContactInfo();
			info.owner = contractor;
			info.contact_type = contact_type;
			contractor.contactInfo.add(info);
		}
	}

	public Contractor GetByUnp(String unp) {
		return GetByParam(Contractor.fields.UNP, unp);
	}

}