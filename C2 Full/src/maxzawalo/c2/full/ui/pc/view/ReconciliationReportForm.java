package maxzawalo.c2.full.ui.pc.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;

import maxzawalo.c2.base.os.Run;
import maxzawalo.c2.base.ui.pc.controls.BizControlBase;
import maxzawalo.c2.base.ui.pc.controls.DateBizControl;
import maxzawalo.c2.base.utils.FileUtils;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.free.bo.Contractor;
import maxzawalo.c2.full.bo.view.ReconciliationReport;
import maxzawalo.c2.full.data.factory.view.ReconciliationReportFactory;
import maxzawalo.c2.full.report.code.ReconciliationReporter;
import maxzawalo.c2.full.reporter.Xlsx;

public class ReconciliationReportForm extends JFrame {
	public ReconciliationReportForm() {
		setTitle("Акт сверки");
		getContentPane().setLayout(null);
		setBounds(0, 0, 438, 228);

		final BizControlBase fromDateControl = new DateBizControl();
		fromDateControl.setCaption("Дата с");
		fromDateControl.onBOSelected(Format.AddDay(new Date(), -30));
		fromDateControl.setBounds(12, 12, 121, 56);
		getContentPane().add(fromDateControl);

		final DateBizControl reportDateControl = new DateBizControl();
		reportDateControl.setCaption("Дата по");
		reportDateControl.onBOSelected(new Date());
		reportDateControl.setBounds(169, 13, 121, 56);
		getContentPane().add(reportDateControl);

		final BizControlBase contractorControl = new  BizControlBase();
		contractorControl.fieldType = Contractor.class;
		// parentControl.setFieldName("contractor");
		contractorControl.setCaption("Контрагент");
		contractorControl.setBounds(12, 81, 409, 56);
		contractorControl.setBo(new Contractor());
		getContentPane().add(contractorControl);

		JButton button = new JButton("Сформировать");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Contractor contractor = (Contractor) contractorControl.getBO();
				ReconciliationReport report = ReconciliationReportFactory.Create(contractor, fromDateControl.getDate(),
						reportDateControl.getDate());

				String reportPath = FileUtils.GetReportDir() + "АктСверки_" + System.currentTimeMillis() + ".xlsx";
				Xlsx.PrintMatrix(reportPath, Arrays.asList(report), new ReconciliationReporter(), 1, 1);
				Run.OpenFile(reportPath);

			}
		});
		button.setBounds(124, 148, 143, 34);
		getContentPane().add(button);

	}
}