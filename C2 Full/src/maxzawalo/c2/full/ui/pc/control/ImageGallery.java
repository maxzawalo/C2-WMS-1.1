package maxzawalo.c2.full.ui.pc.control;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.Timer;
import javax.swing.border.Border;

import maxzawalo.c2.base.os.Run;
import maxzawalo.c2.base.ui.pc.common.UI;
import maxzawalo.c2.base.utils.Logger;
import maxzawalo.c2.free.bo.Settings;

public class ImageGallery extends JFrame {

	protected Logger log = Logger.getLogger(ImageGallery.class);
	public static ImageGallery instance = new ImageGallery();
	JScrollPane scrollPane;
	AsyncImage imgBig;
	JPanel imgPanel;
	private Timer recalculateTimer = new Timer(300, new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			scrollPane.setLocation(scrollPane.getX(), ImageGallery.this.getHeight() - scrollPane.getHeight() - 60);
			scrollPane.setSize(ImageGallery.this.getWidth() - 60, scrollPane.getHeight());
			scrollPane.revalidate();
			imgHeight = scrollPane.getY() - 20 * 2;
			System.out.println("imgHeight=" + imgHeight);
			imgBig.setSize(imgHeight, imgHeight);
			imgBig.Refresh();
		}
	});

	protected ImageGallery() {
		UI.SET(this);
		getContentPane().setLayout(null);
		setBounds(0, 0, 360, 458);

		imgBig = new AsyncImage();
		imgBig.setBounds(20, 20, imgHeight, imgHeight);
		getContentPane().add(imgBig);
		imgBig.onDblClick = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				Run.OpenFile(evt.getActionCommand());
			}
		};

		scrollPane = new JScrollPane();
		// scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(20, 336, 298, 68);
		getContentPane().add(scrollPane);

		imgPanel = new JPanel();
		// imgPanel.setBounds(0, 0, 500, 70);
		scrollPane.setViewportView(imgPanel);
		imgPanel.setLayout(null);

		recalculateTimer.setRepeats(false);

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				if (recalculateTimer.isRunning()) {
					recalculateTimer.restart();
				} else {
					recalculateTimer.start();
				}
			}
		});
	}

	int imgHeight = 300;

	JLabel loader;

	List<ImageIcon> images = new ArrayList<>();

	protected JLabel CreateLabelIcon() {
		images.add(new ImageIcon());
		JLabel lblNewLabel = new JLabel(images.get(images.size() - 1));
		Border border = BorderFactory.createLineBorder(Color.BLACK, 1);
		lblNewLabel.setBorder(border);
		return lblNewLabel;
	}

	public void LoadData(String title, BufferedImage image) {
		setTitle(title);
		// new LoadTask(id).execute();

		imgBig.setImage(image);

		setVisible(true);
		setState(Frame.NORMAL);
	}

	public void LoadData(String title, int id) {
		setTitle(title);
		// new LoadTask(id).execute();

		setVisible(true);
		setState(Frame.NORMAL);

		String prefix = id + "_";
		List<String> results = new ArrayList<String>();
		try {
			String path = Settings.imagesPath();// FileUtils.GetImgDir();

			File[] files = new File(path).listFiles();
			for (File file : files) {
				if (file.isFile() && file.getName().startsWith(prefix)) {
					results.add(file.getPath());
				}
			}
		} catch (Exception e) {
			log.ERROR("LoadData", e);
		}
		imgPanel.removeAll();
		if (results.size() == 0) {
			imgBig.Clear();
			// for (ImageIcon img : images)
			// img.setImage(UI.getNoImage());
		} else {

			// imgPanel.setBorder(null);
			int pos = 0;
			for (String r : results) {
				AsyncImage img = new AsyncImage();
				img.setBounds(pos * 50, 0, 50, 50);
				img.LoadData(r);
				imgPanel.add(img);
				imgPanel.setPreferredSize(new Dimension((pos + 1) * 50, imgPanel.getHeight()));
				// System.out.println(r);
				img.onClick = new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent evt) {
						imgBig.setSize(imgHeight, imgHeight);
						imgBig.LoadData(evt.getActionCommand());
					}
				};
				pos++;
			}
			scrollPane.revalidate();

			imgBig.setSize(imgHeight, imgHeight);
			imgBig.LoadData(results.get(0));
		}
	}
}