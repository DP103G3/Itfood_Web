package tw.dp103g3.main;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageUtil {
	public static byte[] shink(byte[] srcImageData, int newSize) {
		ByteArrayInputStream bais = new ByteArrayInputStream(srcImageData);
		double sampleSize = 1;
		int imageWidth = 0;
		int imageHeight = 0;

		if (newSize <= 50) {
			newSize = 128;
		}

		try {
			BufferedImage srcBufferedImage = ImageIO.read(bais);
			int type = srcBufferedImage.getType();
			String format = "";
			switch (type) {
			case BufferedImage.TYPE_4BYTE_ABGR:
			case BufferedImage.TYPE_4BYTE_ABGR_PRE:
			case BufferedImage.TYPE_INT_ARGB:
			case BufferedImage.TYPE_INT_ARGB_PRE:
				format = "png";
				break;
			default:
				format = "jpg";
				break;
			}
			imageWidth = srcBufferedImage.getWidth();
			imageHeight = srcBufferedImage.getHeight();
			if (imageHeight == 0 || imageWidth == 0) {
				return srcImageData;
			}
			int longer = Math.max(imageHeight, imageWidth);
			if (longer > newSize) {
				sampleSize = (long) newSize / longer;
				imageWidth *= sampleSize;
				imageHeight *= sampleSize;
			}

			BufferedImage scaledBufferedImage = new BufferedImage(imageWidth, imageHeight, type);
			Graphics graphics = scaledBufferedImage.createGraphics();
			graphics.drawImage(srcBufferedImage, 0, 0, imageWidth, imageHeight, null);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(scaledBufferedImage, format, baos);
			return baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return srcImageData;
		}
	}
}
