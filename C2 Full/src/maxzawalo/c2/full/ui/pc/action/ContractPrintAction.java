package maxzawalo.c2.full.ui.pc.action;

import maxzawalo.c2.base.interfaces.ActionC2;
import maxzawalo.c2.base.os.Run;
import maxzawalo.c2.base.utils.FileUtils;
import maxzawalo.c2.free.bo.Contract;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.free.data.factory.catalogue.ContractorFactory;
import maxzawalo.c2.full.report.code.ContractReporter;

public class ContractPrintAction implements ActionC2 {
	@Override
	public Object Do(Object[] params) {

		Contract contract = (Contract) params[0];
		ContractReporter reporter = new ContractReporter();
		String reportPath = FileUtils.GetReportDir() + "Contract_" + System.currentTimeMillis() + ".docx";

		contract.owner = new ContractorFactory().LoadContactInfo((Contractor) contract.owner);
		reporter.Print(reportPath, contract);
		Run.OpenFile(reportPath);
		return true;
	}
}