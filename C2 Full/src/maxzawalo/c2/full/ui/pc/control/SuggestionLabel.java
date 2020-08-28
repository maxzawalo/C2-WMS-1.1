package maxzawalo.c2.full.ui.pc.control;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import maxzawalo.c2.free.search.SearchContext;

public class SuggestionLabel extends JLabel {

	private boolean focused = false;
	private final JWindow autoSuggestionsPopUpWindow;
	private final JTextField textField;
	private final AutoSuggestor autoSuggestor;
	private Color suggestionsTextColor, suggestionBorderColor;

	// Border selectedItemBorder = new CompoundBorder(new
	// LineBorder(suggestionBorderColor), new EmptyBorder(0, 6, 0, 0));
	Border selectedItemBorder = new EmptyBorder(0, 6, 0, 0);
	Border normalItemBorder = new EmptyBorder(0, 5, 0, 0);

	public SuggestionLabel(String string, final Color borderColor, Color suggestionsTextColor,
			AutoSuggestor autoSuggestor) {
		super(string);

		this.suggestionsTextColor = suggestionsTextColor;
		this.autoSuggestor = autoSuggestor;
		this.textField = autoSuggestor.getTextField();
		this.suggestionBorderColor = borderColor;
		this.autoSuggestionsPopUpWindow = autoSuggestor.getAutoSuggestionPopUpWindow();
		setBorder(normalItemBorder);
		setBackground(new Color(230, 230, 230));
		initComponent();
	}

	private void initComponent() {
		setFocusable(true);
		setForeground(suggestionsTextColor);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				super.mouseClicked(me);
				replaceWithSuggestedText();
				autoSuggestionsPopUpWindow.setVisible(false);
				autoSuggestor.setFullFocusToTextField();
			}
		});

		getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "Enter released");
		getActionMap().put("Enter released", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				replaceWithSuggestedText();
				autoSuggestionsPopUpWindow.setVisible(false);
				autoSuggestor.setFullFocusToTextField();
				// System.out.println("Enter");
			}
		});
	}

	public void setFocused(boolean focused) {
		if (focused) {
			setBorder(selectedItemBorder);
			setOpaque(true);
		} else {
			setBorder(normalItemBorder);
			setOpaque(false);
		}
		repaint();
		this.focused = focused;
	}

	public boolean isFocused() {
		return focused;
	}

	private void replaceWithSuggestedText() {
		String suggestedWord = getText();
		String text = textField.getText();
		// String typedWord = autoSuggestor.getCurrentlyTypedWord();
		String[] words = text.trim().split(" ");
		String t = "";// text.substring(0, text.lastIndexOf(typedWord));
		int i = 0;
		for (; i < words.length - 1; i++)
			t += words[i].trim() + " ";
		String last = (words.length != 0) ? words[words.length - 1] : "";
		if (!suggestedWord.startsWith(last))
			t += words[i].trim();

		final String tmp = (t.trim() + " " + suggestedWord).trim();
		// t + text.substring(text.lastIndexOf(typedWord)).replace(typedWord,
		// suggestedWord);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				textField.setText(tmp);// + " ");
			}
		});

		// Добавили слово в поиск и филльтруем контекст
		autoSuggestor.setDictionary(SearchContext.FromPhrases(autoSuggestor.context, tmp));
	}
}