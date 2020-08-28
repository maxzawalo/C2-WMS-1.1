package maxzawalo.c2.base.ui.pc.controls;

import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class MyCellEditor extends DefaultCellEditor {

	private boolean keyTriggered;

	public MyCellEditor() {
		super(new JTextField());
		final JTextField textField = (JTextField) getComponent();
		textField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if (!keyTriggered) {
							textField.selectAll();
						}
					}
				});
			}
		});
	}

	public void setKeyTriggered(boolean keyTriggered) {
		this.keyTriggered = keyTriggered;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		final JTextField textField = (JTextField) super.getTableCellEditorComponent(table, value, isSelected, row,
				column);
		textField.selectAll();
		return textField;
	}
}