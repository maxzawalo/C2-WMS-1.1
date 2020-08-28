package maxzawalo.c2.base.ui.pc.controls;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class SwingLink extends JLabel {
	private static final long serialVersionUID = 8273875024682878518L;
	private String text;
	private URI uri;

	public SwingLink() {
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setForeground(Color.decode("#000099"));
	}

	// public SwingLink(String text, URI uri) {
	// super();
	// setup(text, uri);
	// }
	//
	// public SwingLink(String text, String uri) {
	// super();
	// setup(text, URI.create(uri));
	// }

	public Action onClick;

	public void setup(String text) {
		setup(text, null);
	}

	public void setup(String text, URI uri) {
		this.text = text;
		this.uri = uri;
		setText(text);

		if (SwingLink.this.uri != null)
			setToolTipText(uri.toString());
		else
			setToolTipText(text);
		// [0] ToolTipManager (id=49)

		for (MouseListener l : getMouseListeners())
			removeMouseListener(l);

		{
			addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (SwingLink.this.uri != null)
						open(SwingLink.this.uri);
					else if (onClick != null)
						onClick.actionPerformed(null);
				}

				public void mouseEntered(MouseEvent e) {
					Font font = getFont();
					Map attributes = font.getAttributes();
					attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
					setFont(font.deriveFont(attributes));
					// setText(SwingLink.this.text, false);
				}

				public void mouseExited(MouseEvent e) {
					Font font = getFont();
					Map attributes = font.getAttributes();
					attributes.put(TextAttribute.UNDERLINE, null);
					setFont(font.deriveFont(attributes));
					// setText(SwingLink.this.text, true);
				}
			});
		}
	}

	@Override
	public void setText(String text) {
		setText(text, true);
	}

	public void setText(String text, boolean ul) {
		String link = ul ? "<u>" + text + "</u>" : text;
		super.setText(text);// "<html><span style=\"color: #000099;white-space:
							// nowrap;\">" + link + "</span></html>");
		this.text = text;
	}

	public String getRawText() {
		return text;
	}

	private static void open(URI uri) {
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.browse(uri);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Failed to launch the link, your computer is likely misconfigured.",
						"Cannot Launch Link", JOptionPane.WARNING_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(null, "Java is not able to launch links on your computer.",
					"Cannot Launch Link", JOptionPane.WARNING_MESSAGE);
		}
	}
}