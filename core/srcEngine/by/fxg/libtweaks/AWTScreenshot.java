package by.fxg.libtweaks;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import com.badlogic.gdx.utils.ScreenUtils;

public class AWTScreenshot {
	public static void captureToClipboardAsync(final int posX, final int posY, final int sizeX, final int sizeY) {
		new Thread() {
			{ this.setDaemon(true); }
			public void run() { captureToClipboard(posX, posY, sizeX, sizeY); }
		}.run();
	}
	
	public static void captureToClipboard(int posX, int posY, int sizeX, int sizeY) {
		sendToClipboard(captureImageFromScreen(posX, posY, sizeX, sizeY));
	}
	
	public static BufferedImage captureImageFromScreen(int posX, int posY, int sizeX, int sizeY) {
		BufferedImage bImage = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_INT_ARGB);
		WritableRaster wr = bImage.getRaster();
		byte[] bytes = ScreenUtils.getFrameBufferPixels(posX, posY, sizeX, sizeY, false);
		int[] pixel = new int[4];
		for (int iy = sizeY - 1, b = 0; iy >= 0; iy--) {
		    for (int ix = 0; ix < sizeX; ix++) {
		        pixel[0] = bytes[b++];
		        pixel[1] = bytes[b++];
		        pixel[2] = bytes[b++];
		        pixel[3] = bytes[b++];
		        wr.setPixel(ix, iy, pixel);
		    }
		}
		return bImage;
	}
	
	public static void sendToClipboard(Image image) {
		TransferableImage transferableImage = new TransferableImage(image);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(transferableImage, null);
	}
}
