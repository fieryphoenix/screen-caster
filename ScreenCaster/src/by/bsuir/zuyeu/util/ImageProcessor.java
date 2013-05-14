/**
 * 
 */
package by.bsuir.zuyeu.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fieryphoenix
 * 
 */
public class ImageProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ImageProcessor.class);
    private static final int ROWS = 4;
    private static final int COLS = 4;

    public static byte[] bufferedImageToByteArray(final BufferedImage image) throws IOException {
	logger.info("bufferedImageToByteArray() - start;");
	final ByteArrayOutputStream baos = new ByteArrayOutputStream();
	ImageIO.write(image, "jpg", baos);
	baos.flush();
	final byte[] imageInByte = baos.toByteArray();
	baos.close();
	logger.info("bufferedImageToByteArray() - end;");
	return imageInByte;
    }

    public BufferedImage[] splitToChunks(final BufferedImage originalImage) throws IOException {
	logger.info("splitToChunks() - start: origImg = {}", originalImage);
	final int chunks = ROWS * COLS;

	// determines the chunk width and height
	final int chunkWidth = originalImage.getWidth() / COLS;
	final int chunkHeight = originalImage.getHeight() / ROWS;
	int count = 0;
	// Image array to hold image chunks
	final BufferedImage imgs[] = new BufferedImage[chunks];
	for (int x = 0; x < ROWS; x++) {
	    for (int y = 0; y < COLS; y++) {
		// Initialize the image array with image chunks
		imgs[count] = new BufferedImage(chunkWidth, chunkHeight, originalImage.getType());

		// draws the image chunk
		final Graphics2D gr = imgs[count++].createGraphics();
		gr.drawImage(originalImage, 0, 0, chunkWidth, chunkHeight, chunkWidth * y, chunkHeight * x, (chunkWidth * y) + chunkWidth, (chunkHeight * x)
			+ chunkHeight, null);
		gr.dispose();
	    }
	}
	logger.info("splitToChunks() - end;");
	return imgs;
    }

}
