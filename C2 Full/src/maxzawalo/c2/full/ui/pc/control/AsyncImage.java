package maxzawalo.c2.full.ui.pc.control;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.Border;

import maxzawalo.c2.base.ui.pc.common.UI;

public class AsyncImage extends JPanel {

	double currentProgress = 0;
	Image img;

	public Action onClick;
	public Action onDblClick;

	public AsyncImage() {
		Border border = BorderFactory.createLineBorder(Color.BLACK, 1);
		setBorder(border);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (onDblClick != null)
						onDblClick.actionPerformed(new ActionEvent(AsyncImage.this, 0, path));
				}
				if (e.getClickCount() == 1) {
					if (onClick != null)
						onClick.actionPerformed(new ActionEvent(AsyncImage.this, 0, path));
				}
			}
		});
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				onFormResized();
			}

		});
	}

	private void onFormResized() {

	}

	@Override
	public void paintComponent(Graphics g) {
		int width = getWidth();
		int height = getHeight();

		if (currentProgress < 1.0) {
			g.setColor(getBackground());
			g.fillRect(0, 0, width, height);

			g.setColor(Color.WHITE);
			g.fillRect(0, (int) (height * (1d - currentProgress)), width, (int) (height * currentProgress));
		} else {
			g.setColor(getBackground());
			g.fillRect(0, 0, width, height);
			g.drawImage(img, 0, 0, null);
		}
	}

	class LoadTask extends SwingWorker<Void, Void> {
		String path;

		public LoadTask(String path) {
			this.path = path;
			img = null;
		}

		@Override
		protected Void doInBackground() throws Exception {
			int bufSize = 8192;
			FileInputStream in = new FileInputStream(path);

			double completeFileSize = in.getChannel().size();
			// System.out.println(completeFileSize);
			ByteArrayOutputStream fos = new ByteArrayOutputStream();

			// ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte[] data = new byte[bufSize];
			double downloadedFileSize = 0;
			int x = 0;
			while ((x = in.read(data, 0, bufSize)) >= 0) {
				downloadedFileSize += x;
				// System.out.println(downloadedFileSize);

				// calculate progress
				currentProgress = downloadedFileSize / completeFileSize;
				repaint();
//				Thread.sleep(10);

				// System.out.println(currentProgress);
				fos.write(data, 0, x);

			}

			// bout
			img = ImageIO.read(new ByteArrayInputStream(fos.toByteArray()));
			// bout.close();
			in.close();

			return null;
		}

		@Override
		protected void done() {
			int size = getHeight();

			int maxSize = Math.max(img.getWidth(null), img.getHeight(null));
			System.out.println("size=" + size + " maxSize=" + maxSize);
			double k = (double) size / (double) maxSize;

			int width = (int) (k * img.getWidth(null));
			int height = (int) (k * img.getHeight(null));
			setSize(width, height);

			img = UI.createResizedCopy(img, width, height, false);
			revalidate();
			repaint();
			
			imgLoaded = true;
			System.out.println("--done");
		}
	}

	LoadTask task;
	String path;

	public void LoadData(String path) {
		this.path = path;
		task = new LoadTask(path);
		task.execute();
	}

	public void Clear() {
		if (task != null)
			task.cancel(true);
		currentProgress = 1.0;
		img = UI.getNoImage();
		setSize(300, 300);
		revalidate();
		repaint();

	}

	boolean imgLoaded = true;

	public void setImage(Image image) {
		imgLoaded = false;
		currentProgress = 1.0;
		this.img = image;
		revalidate();
		repaint();
	}

	public void Refresh() {
		if (imgLoaded) {
			task = new LoadTask(path);
			task.execute();
		}
	}
}