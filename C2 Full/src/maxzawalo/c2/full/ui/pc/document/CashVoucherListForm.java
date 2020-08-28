package maxzawalo.c2.full.ui.pc.document;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

import maxzawalo.c2.base.bo.BO;
import maxzawalo.c2.base.bo.DocumentBO;
import maxzawalo.c2.base.bo.User;
import maxzawalo.c2.base.data.factory.DocumentFactory;
import maxzawalo.c2.base.os.Run;
import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.utils.FileUtils;
import maxzawalo.c2.free.ui.pc.document.store.StoreDocListForm;
import maxzawalo.c2.full.bo.document.cashvoucher.CashVoucher;
import maxzawalo.c2.full.data.factory.document.CashVoucherFactory;
import maxzawalo.c2.full.report.code.CashVoucherReporter;
import maxzawalo.c2.full.reporter.Xlsx;
import maxzawalo.c2.full.ui.pc.form.AnaliticsForm;
import maxzawalo.c2.full.ui.pc.model.document.CashVoucherTableModel;

public class CashVoucherListForm extends StoreDocListForm<CashVoucher, CashVoucherForm> {

	public CashVoucherListForm() {

		JButton btnMultiPtint = new JButton("МП");
		btnMultiPtint.setVisible(User.current.isAdmin());
		btnMultiPtint.setToolTipText("Массовая печать");
		btnMultiPtint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UI.Start(CashVoucherListForm.this);
				List<CashVoucher> list = new ArrayList<>();
				for (int row : table.getSelectedRows()) {
					int id = ((BO) tableModel.getList().get(row)).id;
					CashVoucher doc = factory.GetById(id);
					((DocumentFactory) factory).LoadTablePart(doc);
					list.add(doc);
				}
				String reportPath = FileUtils.GetReportDir() + "CashVoucherMultiple_" + System.currentTimeMillis()
						+ ".xlsx";
				// Xlsx.PrintMultipleDocs(reportPath, list, new
				// CashVoucherReporter(true));
				Xlsx.PrintMatrix(reportPath, list, new CashVoucherReporter(true), 1, 1);
				Run.OpenFile(reportPath);
				UI.Stop(CashVoucherListForm.this);
			}
		});
		btnMultiPtint.setBounds(911, 12, 63, 34);
		getContentPane().add(btnMultiPtint);
		factory = new CashVoucherFactory();
		tableModel = new CashVoucherTableModel();
	}

	@Override
	protected void ShowDocTransaction() {
		AnaliticsForm form = new AnaliticsForm(this);
		form.setVisible(true);
		form.setRegistrator((DocumentBO) GetSelectedItem());
	}
}