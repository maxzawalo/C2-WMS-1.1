package maxzawalo.c2.base.ui.pc.controls;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class JTextFieldRegularPopupMenu extends JTextField {

	public JTextFieldRegularPopupMenu() {
		addTo(this);
	}

	public static void addTo(final JTextField txtField) {
		JPopupMenu popup = new JPopupMenu();
		final UndoManager undoManager = new UndoManager();
		txtField.getDocument().addUndoableEditListener(undoManager);

		Action undoAction = new AbstractAction("Undo") {
			@Override
			public void actionPerformed(ActionEvent ae) {
				if (undoManager.canUndo()) {
					undoManager.undo();
				} else {
					System.out.println("No Undo Buffer.");
				}
			}
		};

		Action copyAction = new AbstractAction("Копировать") {
			@Override
			public void actionPerformed(ActionEvent ae) {
				txtField.copy();
			}
		};

		Action cutAction = new AbstractAction("Вырезать") {
			@Override
			public void actionPerformed(ActionEvent ae) {
				txtField.cut();
			}
		};

		Action pasteAction = new AbstractAction("Вставить") {
			@Override
			public void actionPerformed(ActionEvent ae) {
				txtField.paste();
			}
		};

		Action selectAllAction = new AbstractAction("Выделить все") {
			@Override
			public void actionPerformed(ActionEvent ae) {
				txtField.selectAll();
			}
		};

		KeyStroke undoKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z,
				Event.CTRL_MASK);
		KeyStroke redoKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_Y,
				Event.CTRL_MASK);

		// Map undo action
		txtField.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				undoKeyStroke, "undoKeyStroke");
		txtField.getActionMap().put("undoKeyStroke", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					undoManager.undo();
				} catch (CannotUndoException cue) {
				}
			}
		});
		// Map redo action
		txtField.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				redoKeyStroke, "redoKeyStroke");
		txtField.getActionMap().put("redoKeyStroke", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					undoManager.redo();
				} catch (CannotRedoException cre) {
				}
			}
		});
		cutAction.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke("control X"));
		copyAction.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke("control C"));
		pasteAction.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke("control V"));
		selectAllAction.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke("control A"));

//		popup.add(undoAction);
//		popup.addSeparator();
		popup.add(cutAction);
		popup.add(copyAction);
		popup.add(pasteAction);
		popup.addSeparator();
		popup.add(selectAllAction);

		txtField.setComponentPopupMenu(popup);
	}
}