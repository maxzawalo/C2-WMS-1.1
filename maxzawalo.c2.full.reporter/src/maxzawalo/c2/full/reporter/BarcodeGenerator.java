package maxzawalo.c2.full.reporter;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class BarcodeGenerator {
	public static String generateEAN(String barcode) {
		// int first = 0;
		// int second = 0;
		//
		// if(barcode.length() == 7 || barcode.length() == 12) {
		//
		// for (int counter = 0; counter < barcode.length() - 1; counter++) {
		// first = (first + Integer.valueOf(barcode.substring(counter, counter +
		// 1)));
		// counter++;
		// second = (second + Integer.valueOf(barcode.substring(counter, counter
		// + 1)));
		// }
		// second = second * 3;
		// int total = second + first;
		// int roundedNum = Math.round((total + 9) / 10 * 10);
		//
		// barcode = barcode + String.valueOf(roundedNum - total);
		// }
		return barcode + checkSum(barcode);
	}

	// public static int checkSum(String code){
	// int val=0;
	// for(int i=0;i<code.length();i++){
	// val+=((int)Integer.parseInt(code.charAt(i)+""))*((i%2==0)?1:3);
	// }
	//
	// int checksum_digit = 10 - (val % 10);
	// if (checksum_digit == 10) checksum_digit = 0;
	//
	// return checksum_digit;
	// }

	public static String CreatePriceBarcode(String barcode_data) {
		String path = "D:\\Barcodes\\" + barcode_data + ".png";
		try {
			// 268, 30);
			BufferedImage bitmap = encodeAsBitmap(generateEAN(barcode_data), BarcodeFormat.EAN_8, 1000, 50);
			// double crop = 0.06;
			// bitmap = cropImage(bitmap, new Rectangle((int) (bitmap.getWidth()
			// * crop / 2), 0,
			// (int) (bitmap.getWidth() * (1 - crop)), bitmap.getHeight()));
			// bitmap = createResizedCopy(bitmap, 268, 30, false);
			// // bitmap = cropImage(bitmap, new Rectangle(7, 0, width, height))
			File outputfile = new File(path);
			ImageIO.write(bitmap, "png", outputfile);
		} catch (Exception e) {
			e.printStackTrace();
			path = "";
		}
		return path;
	}

	public static String CreatePriceQR(String barcode_data) {
		String path = "D:\\Barcodes\\" + barcode_data + ".png";
		try {
			BufferedImage bitmap = encodeAsBitmap(barcode_data, BarcodeFormat.QR_CODE, 200, 200);
			File outputfile = new File(path);
			ImageIO.write(bitmap, "png", outputfile);
		} catch (Exception e) {
			e.printStackTrace();
			path = "";
		}
		return path;
	}

	public static void SavePlace(String barcode_data) {
		try {
			String path = "D:\\Barcodes\\" + barcode_data + ".png";
			// 268, 30);
			BufferedImage bitmap = encodeAsBitmap(barcode_data, BarcodeFormat.CODE_128, 1000, 50);
			double crop = 0.06;
			bitmap = cropImage(bitmap, new Rectangle((int) (bitmap.getWidth() * crop / 2), 0,
					(int) (bitmap.getWidth() * (1 - crop)), bitmap.getHeight()));
			bitmap = createResizedCopy(bitmap, 268, 30, false);
			// bitmap = cropImage(bitmap, new Rectangle(7, 0, width, height))
			File outputfile = new File(path);
			ImageIO.write(bitmap, "png", outputfile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void SaveLoginQR(String barcode_data) {
		try {
			// TODO: settings
			String path = "D:\\Barcodes\\" + barcode_data + ".png";
			// 268, 30);
			BufferedImage bitmap = encodeAsBitmap((barcode_data), BarcodeFormat.QR_CODE, 300, 300);
			File outputfile = new File(path);
			ImageIO.write(bitmap, "png", outputfile);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	static BufferedImage cropImage(BufferedImage src, Rectangle rect) {
		return src.getSubimage(rect.x, rect.y, rect.width, rect.height);
	}

	static BufferedImage createResizedCopy(Image originalImage, int scaledWidth, int scaledHeight,
			boolean preserveAlpha) {
		System.out.println("resizing...");
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

	public static int checkSum(String barcode) {
		// int val=0;
		// for(int i=0; i<code.length()-1; i++){
		// val+=((int)Integer.parseInt(code.charAt(i)+""))*((i%2==0)?1:3);
		// }
		int[] mul = { 3, 1 };
		if (barcode.length() == 12)
			mul = new int[] { 1, 3 };

		int[] code = new int[48];// Максимум 48
		int length = Math.min(code.length, barcode.length());
		int checksum_value = 0;
		for (int i = 0; i < length; i++) {
			code[i] = Integer.parseInt(barcode.charAt(i) + "");
		}

		for (int i = 0; i < length; i++) {
			code[i] *= mul[i % 2];
		}

		for (int i = 0; i < length; i++)
			checksum_value += code[i];
		// int checksum_digit = (10 - (val % 10)) % 10;

		// int sum1 = code[1] + code[3] + code[5];
		// int sum2 = 3 * (code[0] + code[2] + code[4] + code[6]);
		// int checksum_value = sum1 + sum2;

		int checksum_digit = 10 - (checksum_value % 10);
		if (checksum_digit == 10)
			checksum_digit = 0;

		return checksum_digit;
	}

	private static final int WHITE = 0xFFFFFFFF;
	private static final int BLACK = 0xFF000000;

	public static BufferedImage encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) {
		String contentsToEncode = contents;
		if (contentsToEncode == null) {
			return null;
		}
		// Map<EncodeHintType, Object> hints = null;
		// String encoding = guessAppropriateEncoding(contentsToEncode);
		// if (encoding != null) {
		// hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
		// hints.put(EncodeHintType.CHARACTER_SET, encoding);
		// }
		HashMap hintMap = new HashMap();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);
		hintMap.put(EncodeHintType.MARGIN, 0);

		MultiFormatWriter writer = new MultiFormatWriter();
		BitMatrix result;
		try {
			result = writer.encode(contentsToEncode, format, img_width, img_height, hintMap);
		} catch (Exception iae) {
			// Unsupported format
			return null;
		}
		int width = result.getWidth();
		int height = result.getHeight();

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			int offset = y * width;
			for (int x = 0; x < width; x++) {
				image.setRGB(x, y, result.get(x, y) ? BLACK : WHITE);

				// pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
			}
		}

		// image.setRGB(5, 20, Color.BLUE.getRGB())

		// Bitmap bitmap = Bitmap.createBitmap(width, height,
		// Bitmap.Config.ARGB_8888);
		// bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return image;
	}

	private static String guessAppropriateEncoding(CharSequence contents) {
		// Very crude at the moment
		for (int i = 0; i < contents.length(); i++) {
			if (contents.charAt(i) > 0xFF) {
				return "UTF-8";
			}
		}
		return null;
	}

}