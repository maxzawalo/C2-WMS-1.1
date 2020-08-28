package maxzawalo.c2.free.ui.pc.form;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.free.bo.Product;
import maxzawalo.c2.free.ui.pc.catalogue.LotOfProductListFormFree;

public class EnterValueForm extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField countText;
	public double maxValue = 1;
	public double cost_price = 0;
	public double count = 0;
	public Product product;
	JTextArea textArea;
	JLabel label;
	public boolean forInvoice = false;
	private JTextField cost_priceText;

	/**
	 * Create the dialog.
	 */
	public EnterValueForm(LotOfProductListFormFree parent) {
		super(parent);

		setBounds(100, 100, 485, 197);
		setLocationRelativeTo(parent);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			countText = new JTextField();
			countText.setBounds(41, 78, 63, 26);
			countText.setText("0.00");
			contentPanel.add(countText);
			countText.setColumns(10);

			InputMap im = countText.getInputMap();
			ActionMap am = countText.getActionMap();

			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
			am.put("Enter", new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					OK();
				}
			});
		}

		label = new JLabel("<=");
		label.setBounds(119, 81, 118, 20);
		contentPanel.add(label);

		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setBackground(EnterValueForm.this.getContentPane().getBackground());
		textArea.setBounds(15, 16, 439, 46);
		contentPanel.add(textArea);

		cost_priceText = new JTextField();
		cost_priceText.setText("0.00");
		cost_priceText.setColumns(10);
		cost_priceText.setBounds(291, 78, 63, 26);
		contentPanel.add(cost_priceText);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						OK();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}

		setModalityType(ModalityType.DOCUMENT_MODAL);
		// setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

	}

	public double getCountValue() {
		return Format.extractDouble(countText.getText());

	}

	public double getCostPriceValue() {
		return Format.extractDouble(cost_priceText.getText());
	}

	public void ShowDialog() {

		label.setText("<= " + maxValue);
		countText.setText(Format.Show(count));
		cost_priceText.setText(Format.Show(cost_price));
		cost_priceText.setVisible(forInvoice);

		textArea.setText(product + "");
		setVisible(true);
		// setModal(true);
	}

	protected void OK() {
		count = getCountValue();
		if (!forInvoice)
			count = Math.min(count, maxValue);// TODO: message

		cost_price = getCostPriceValue();

		((LotOfProductListFormFree) getParent()).valueSelected(count, cost_price);
		setVisible(false);
		// dispose();
	}
}
