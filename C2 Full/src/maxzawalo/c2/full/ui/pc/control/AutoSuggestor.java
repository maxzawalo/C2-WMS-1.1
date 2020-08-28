package maxzawalo.c2.full.ui.pc.control;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import maxzawalo.c2.base.interfaces.AutoSuggestorI;
import maxzawalo.c2.free.search.SearchContext;

public class AutoSuggestor implements AutoSuggestorI {

	private final JTextField textField;
	private final Window container;
	private JPanel suggestionsPanel;
	private JWindow autoSuggestionPopUpWindow;
	private String typedWord;
	private final ArrayList<String> dictionary = new ArrayList<>();
	private int currentIndexOfSpace, tW, tH;
	private DocumentListener documentListener = new DocumentListener() {
		@Override
		public void insertUpdate(DocumentEvent de) {
			checkForAndShowSuggestions();
		}

		@Override
		public void removeUpdate(DocumentEvent de) {
			checkForAndShowSuggestions();
		}

		@Override
		public void changedUpdate(DocumentEvent de) {
			checkForAndShowSuggestions();
		}
	};
	private final Color suggestionsTextColor;
	private final Color suggestionFocusedColor;
	public Class context;

	public AutoSuggestor(JTextField textField, Window mainWindow, ArrayList<String> words, Color popUpBackground,
			Color textColor, Color suggestionFocusedColor, float opacity) {
		this.textField = textField;
		this.suggestionsTextColor = textColor;
		this.container = mainWindow;
		this.suggestionFocusedColor = suggestionFocusedColor;
		this.textField.getDocument().addDocumentListener(documentListener);

		// textField.addComponentListener(new ComponentAdapter() {
		// public void componentMoved(ComponentEvent e) {
		// Move();
		// }
		// });

		setDictionary(words);

		typedWord = "";
		currentIndexOfSpace = 0;
		tW = 0;
		tH = 0;

		autoSuggestionPopUpWindow = new JWindow(mainWindow);
		autoSuggestionPopUpWindow.setOpacity(opacity);

		suggestionsPanel = new JPanel();
		suggestionsPanel.setLayout(new GridLayout(0, 1));
		suggestionsPanel.setBackground(popUpBackground);
		suggestionsPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

		addKeyBindingToRequestFocusInPopUpWindow();
	}

	private void addKeyBindingToRequestFocusInPopUpWindow() {
		textField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true),
				"Down released");
		textField.getActionMap().put("Down released", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				// focuses the first label on popwindow
				for (int i = 0; i < suggestionsPanel.getComponentCount(); i++) {
					if (suggestionsPanel.getComponent(i) instanceof SuggestionLabel) {
						((SuggestionLabel) suggestionsPanel.getComponent(i)).setFocused(true);
						autoSuggestionPopUpWindow.toFront();
						autoSuggestionPopUpWindow.requestFocusInWindow();
						suggestionsPanel.requestFocusInWindow();
						suggestionsPanel.getComponent(i).requestFocusInWindow();
						// System.out.println("suggestionsPanel.requestFocusInWindow");
						break;
					}
				}

			}
		});

		textField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true),
				"Space released");
		textField.getActionMap().put("Space released", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				if (!AutoSuggestor.this.isEnabled())
					return;
				// Нажали пробел - смена контекста
				// textField.setFocusable(false);
				WaitNextWord();
				// TODO: show list
				// System.out.println("SPACE");

			}
		});

		textField.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0, true),
				"BACK_SPACE released");
		textField.getActionMap().put("BACK_SPACE released", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				if (!AutoSuggestor.this.isEnabled())
					return;
				// // Нажали удаление - смена контекста
				// //Удаляем последнее слово из контекста, так как оно
				// учавствует в поиске, если фраза не заканчивается пробелом
				String text = textField.getText();
				// if (!text.endsWith(" "))
				{
					String[] words = text.trim().split(" ");
					String t = "";
					for (int i = 0; i < words.length - 1; i++)
						t += words[i].trim() + " ";
					setDictionary(SearchContext.FromPhrases(context, t.trim()));
				}
				// setDictionary(SearchContext.FromPhrases(context,
				// textField.getText()));
				// TODO: show list
				System.out.println("BACK_SPACE");
			}
		});

		suggestionsPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "Down released");
		suggestionsPanel.getActionMap().put("Down released", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent ae) {

				ArrayList<SuggestionLabel> sls = getAddedSuggestionLabels();
				int max = sls.size();
				int i = ClearSuggestionSelection(sls, max);
				if (i >= max - 1)
					i = -1;
				i++;
				SelectSuggestion(sls, i);

			}
		});

		suggestionsPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true), "UP released");
		suggestionsPanel.getActionMap().put("UP released", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent ae) {

				ArrayList<SuggestionLabel> sls = getAddedSuggestionLabels();
				int max = sls.size();

				int i = ClearSuggestionSelection(sls, max);
				if (i == 0)
					i = max;
				i--;
				SelectSuggestion(sls, i);

			}
		});
	}

	public void setFocusToTextField() {
		container.toFront();
		// textField.setCaretPosition(textField.getText().length() - 1);
		container.requestFocusInWindow();
		textField.requestFocusInWindow();

		System.out.println("setFocusToTextField");
	}

	public void setFullFocusToTextField() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				container.toFront();
				// textField.grabFocus();
				container.requestFocusInWindow();
				textField.requestFocusInWindow();
				// textField.setCaretPosition(textField.getText().length());
				System.out.println("setFullFocusToTextField");
			}
		});
	}

	public ArrayList<SuggestionLabel> getAddedSuggestionLabels() {
		ArrayList<SuggestionLabel> sls = new ArrayList<>();
		for (int i = 0; i < suggestionsPanel.getComponentCount(); i++) {
			if (suggestionsPanel.getComponent(i) instanceof SuggestionLabel) {
				SuggestionLabel sl = (SuggestionLabel) suggestionsPanel.getComponent(i);
				sls.add(sl);
			}
		}
		return sls;
	}

	boolean enabled = true;

	private void checkForAndShowSuggestions() {
		if (!isEnabled())
			return;
		typedWord = getCurrentlyTypedWord();
		System.out.println(typedWord);
		// remove previos words/jlabels that were added
		suggestionsPanel.removeAll();

		// used to calcualte size of JWindow as new Jlabels are added
		tW = 0;
		tH = 0;

		boolean added = wordTyped(typedWord);
		// if (typedWord.endsWith(" "))
		// added = true;

		if (!added) {
			if (autoSuggestionPopUpWindow.isVisible()) {
				autoSuggestionPopUpWindow.setVisible(false);
			}
		} else {
			showPopUpWindow();
			setFocusToTextField();
		}
	}

	int maxListSize = 20;

	protected void addWordToSuggestions(String word) {
		if (suggestionsPanel.getComponentCount() >= maxListSize)
			return;
		SuggestionLabel suggestionLabel = new SuggestionLabel(word, suggestionFocusedColor, suggestionsTextColor, this);
		calculatePopUpWindowSize(suggestionLabel);
		suggestionsPanel.add(suggestionLabel);
	}

	// get newest word after last white spaceif any or the first word if no
	// white spaces
	public String getCurrentlyTypedWord() {
		String text = textField.getText();
		String wordBeingTyped = "";

		String[] words = text.toLowerCase().split(" ");
		if (words.length != 0)
			wordBeingTyped = words[words.length - 1];
		else
			wordBeingTyped = text;
		// if (text.contains(" ")) {
		// int tmp = text.lastIndexOf(" ");
		// if (tmp >= currentIndexOfSpace) {
		// currentIndexOfSpace = tmp;
		// wordBeingTyped = text.substring(text.lastIndexOf(" "));
		// }
		// } else {
		// wordBeingTyped = text;
		// }

		if (text.endsWith(" "))
			wordBeingTyped = wordBeingTyped.trim() + " ";
		return wordBeingTyped;
	}

	private void calculatePopUpWindowSize(JLabel label) {
		// so we can size the JWindow correctly
		if (tW < label.getPreferredSize().width) {
			tW = label.getPreferredSize().width;
		}
		tH += label.getPreferredSize().height;
	}

	private void showPopUpWindow() {
		autoSuggestionPopUpWindow.getContentPane().add(suggestionsPanel);
		autoSuggestionPopUpWindow.setMinimumSize(new Dimension(textField.getWidth(), 30));
		autoSuggestionPopUpWindow.setSize(tW, tH);
		autoSuggestionPopUpWindow.setVisible(true);

		SetPosition();
		autoSuggestionPopUpWindow.setMinimumSize(new Dimension(textField.getWidth(), 30));
		autoSuggestionPopUpWindow.revalidate();
		autoSuggestionPopUpWindow.repaint();

	}

	protected void SetPosition() {
		int windowX = 0;
		int windowY = 0;

		// windowX = textField.getX() + 5 + container.getX();
		// для биз контрола
		windowX += container.getX() + textField.getParent().getX() + textField.getX() + 5;
		// System.out.println("windowX=" + windowX);

		if (suggestionsPanel.getHeight() > autoSuggestionPopUpWindow.getMinimumSize().height) {
			windowY = container.getY() + textField.getY() + textField.getHeight()
					+ autoSuggestionPopUpWindow.getMinimumSize().height;
		} else {
			windowY = container.getY() + textField.getY() + textField.getHeight()
					+ autoSuggestionPopUpWindow.getHeight();
		}

		autoSuggestionPopUpWindow.setLocation(windowX, windowY);
	}

	public void setDictionary(List<String> words) {
		dictionary.clear();
		if (words == null) {
			return;// so we can call constructor with null value for dictionary
					// without exception thrown
		}
		for (String word : words) {
			dictionary.add(word);
		}
	}

	public JWindow getAutoSuggestionPopUpWindow() {
		return autoSuggestionPopUpWindow;
	}

	public Window getContainer() {
		return container;
	}

	public JTextField getTextField() {
		return textField;
	}

	public void addToDictionary(String word) {
		dictionary.add(word);
	}

	protected boolean wordTyped(String typedWord) {

		if (typedWord.isEmpty()) {
			return false;
		}

		// Заканчивается пробелом - добавляем все - введенные слова исключает
		// SearchContext
		if (typedWord.endsWith(" ")) {
			for (String word : dictionary)
				addWordToSuggestions(word);
			return true;
		}
		// System.out.println("Typed word: " + typedWord);

		boolean suggestionAdded = false;
		// get words in the dictionary which we added
		for (String word : dictionary) {

			String[] words = typedWord.toLowerCase().split(" ");
			String last = "";
			if (words.length != 0)
				last = words[words.length - 1];

			boolean fullymatches = true;
			for (int i = 0; i < last.length(); i++) {// each string in the
				// word check for match
				if (!word.startsWith(last)) {
					fullymatches = false;
					break;
				}
			}
			// for (int i = 0; i < last.length(); i++) {// each string in the
			// // word check for match
			// if
			// (!last.toLowerCase().startsWith(String.valueOf(word.charAt(i)),
			// i)) {
			// fullymatches = false;
			// break;
			// }
			// }
			if (fullymatches) {
				addWordToSuggestions(word);
				suggestionAdded = true;
			}
		}
		return suggestionAdded;
	}

	public void TextFieldPressEnter() {
		autoSuggestionPopUpWindow.setVisible(false);
		WaitNextWord();
	}

	public void Move() {
		SetPosition();
	}

	protected int ClearSuggestionSelection(ArrayList<SuggestionLabel> sls, int max) {
		int i = 0;
		for (; i < max; i++) {
			SuggestionLabel sl = sls.get(i);
			if (sl.isFocused()) {
				sl.setFocused(false);
				break;
			}
		}
		return i;
	}

	protected void SelectSuggestion(ArrayList<SuggestionLabel> sls, int i) {
		sls.get(i).setFocused(true);
		autoSuggestionPopUpWindow.toFront();
		autoSuggestionPopUpWindow.requestFocusInWindow();
		suggestionsPanel.requestFocusInWindow();
		suggestionsPanel.getComponent(i).requestFocusInWindow();
	}

	protected void WaitNextWord() {
		setDictionary(SearchContext.FromPhrases(context, textField.getText()));
	}

	public void setEnabled(boolean value) {
		enabled = value;

	}

	boolean isEnabled() {
		return enabled;
	}
}