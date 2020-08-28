package maxzawalo.c2.base.ui.pc.common;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import maxzawalo.c2.base.utils.Logger;
import maxzawalo.c2.free.ui.pc.resource.Resource;

public class UI {
	static Logger log = Logger.getLogger(UI.class);

	public static void SET(Component component) {
		try {
			if (component instanceof JFrame)
				((JFrame) component).setIconImage(getAppIcon());

			// Toolkit.getDefaultToolkit().setDynamicLayout(true);
			// System.setProperty("sun.awt.noerasebackground", "true");
			// JFrame.setDefaultLookAndFeelDecorated(true);
			// JDialog.setDefaultLookAndFeelDecorated(true);

			UIManager.put("OptionPane.yesButtonText", "Да");
			UIManager.put("OptionPane.noButtonText", "Нет");

			Font font = new Font("Tahoma", Font.PLAIN, 16);

			Hashtable defaults = UIManager.getDefaults();
			Enumeration keys = defaults.keys();
			while (keys.hasMoreElements()) {
				Object key = keys.nextElement();
				if ((key instanceof String) && (((String) key).endsWith(".font"))) {
					// System.out.println(key);
					UIManager.put("" + key, font);
				}
			}
			UIManager.put("Table.rowHeight", 22);

			UIManager.setLookAndFeel("net.sf.tinylaf.TinyLookAndFeel");
			
			// UIManager.getLookAndFeelDefaults()
			// .put("defaultFont", font);

			// UIManager.put("defaultFont", font);

			// UIManager.put("Button.font", font);
			// UIManager.put("Label.font", font);
			// UIManager.put("Table.font", font);
			// UIManager.put("TableHeader.font", font);

			SwingUtilities.updateComponentTreeUI(component);

		} catch (Exception e) {
			log.ERROR("SET", e);
		}
	}

	public static Image getAppIcon() {
		Image img = Toolkit.getDefaultToolkit().getImage(Resource.class.getResource("img/app_48_48.png"));
		return img;
	}

	public static ImageIcon getCommitedIcon(int new_width, int new_height) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(Resource.class.getResource("img/commited.png"))
				.getScaledInstance(new_width, new_height, Image.SCALE_DEFAULT));
	}

	public static ImageIcon getDeletedIcon(int new_width, int new_height) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(Resource.class.getResource("img/deleted.png"))
				.getScaledInstance(new_width, new_height, Image.SCALE_DEFAULT));
	}

	public static ImageIcon getRefreshIcon(int new_width, int new_height) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(Resource.class.getResource("img/refresh.png"))
				.getScaledInstance(new_width, new_height, Image.SCALE_DEFAULT));
	}

	public static ImageIcon getLockIcon(int new_width, int new_height) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(Resource.class.getResource("img/lock.gif"))
				.getScaledInstance(new_width, new_height, Image.SCALE_DEFAULT));
	}

	public static ImageIcon getLockOffIcon(int new_width, int new_height) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(Resource.class.getResource("img/lock_off.gif"))
				.getScaledInstance(new_width, new_height, Image.SCALE_DEFAULT));
	}

	public static ImageIcon getMobileIcon() {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(Resource.class.getResource("img/mobile.png")));
	}

	public static ImageIcon getMobileIcon(int new_width, int new_height) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(Resource.class.getResource("img/mobile.png"))
				.getScaledInstance(new_width, new_height, Image.SCALE_DEFAULT));
	}

	public static ImageIcon getBarcodeScannerIcon(int new_width, int new_height) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(Resource.class.getResource("img/barcode_scanner.png"))
				.getScaledInstance(new_width, new_height, Image.SCALE_DEFAULT));
	}

	public static ImageIcon getLoaderIcon() {
		return new ImageIcon(getLoaderImage());
	}

	public static Image getLoaderImage() {
		return Toolkit.getDefaultToolkit().getImage(Resource.class.getResource("img/loader.gif"));
	}

	public static ImageIcon getImageIcon(int new_width, int new_height) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(Resource.class.getResource("img/image.png"))
				.getScaledInstance(new_width, new_height, Image.SCALE_DEFAULT));
	}

	public static ImageIcon getUpdaterIcon(int new_width, int new_height) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(Resource.class.getResource("img/c2_updater.png"))
				.getScaledInstance(new_width, new_height, Image.SCALE_DEFAULT));
	}

	public static ImageIcon getSearchIcon(int new_width, int new_height) {
		return new ImageIcon(Toolkit.getDefaultToolkit().getImage(Resource.class.getResource("img/search.png"))
				.getScaledInstance(new_width, new_height, Image.SCALE_DEFAULT));
	}

	public static Image getNoImage() {
		return Toolkit.getDefaultToolkit().getImage(Resource.class.getResource("img/no_image.jpg"));
	}

	public static ImageIcon getSignatureIcon() {
		return new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(Resource.class.getResource("img/signature-mini.png")));
	}

	public static Image createResizedCopy(Image originalImage, int scaledWidth, int scaledHeight,
			boolean preserveAlpha) {
		// System.out.println("resizing...");
		int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
		BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
		Graphics2D g = scaledBI.createGraphics();
		if (preserveAlpha) {
			g.setComposite(AlphaComposite.Src);
		}
		g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
		g.dispose();
		return scaledBI;
	}

	public static void Start(Component component) {
		component.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	public static void Stop(Component component) {
		component.setCursor(Cursor.getDefaultCursor());
	}

	// public static BufferedImage ResizeImage(Image originalImage, int
	// new_width, int new_height) {
	// BufferedImage resizedImage = new BufferedImage(new_width, new_height,
	// BufferedImage.TYPE_INT_ARGB);
	// Graphics2D g = resizedImage.createGraphics();
	// g.drawImage(originalImage, 0, 0, new_width, new_height, null);
	// g.dispose();
	// g.setComposite(AlphaComposite.Src);
	// g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	// RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	// g.setRenderingHint(RenderingHints.KEY_RENDERING,
	// RenderingHints.VALUE_RENDER_QUALITY);
	// g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	// RenderingHints.VALUE_ANTIALIAS_ON);
	// return resizedImage;
	//
	// }

}
// RadioButtonMenuItem.font
// Panel.font
// TitledBorder.font
// RadioButton.font
// MenuItem.font
// TabbedPane.font
// FormattedTextField.font
// Button.font
// CheckBox.font
// TableHeader.font
// ScrollPane.font
// PasswordField.font
// Slider.font
// OptionPane.font
// Viewport.font
// TextPane.font
// ToolTip.font
// Tree.font
// InternalFrame.font
// MenuBar.font
// ProgressBar.font
// ColorChooser.font
// ToggleButton.font
// ToolBar.font
// PopupMenu.font
// TextField.font
// Menu.font
// CheckBoxMenuItem.font
// Spinner.font
// ComboBox.font
// List.font
// Label.font
// TextArea.font
// Table.font
// DesktopIcon.font
// EditorPane.font