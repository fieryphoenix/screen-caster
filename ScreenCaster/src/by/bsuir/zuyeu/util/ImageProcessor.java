/**
 * 
 */
package by.bsuir.zuyeu.util;

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
    private static final int BUFFER_SIZE = 65535;

    // public static ByteArrayInputStream serializeImages(List<BufferedImage>
    // images) {
    // byte[] buf = new byte[BUFFER_SIZE];
    // ByteArrayInputStream inputStream = new ByteArrayInputStream(buf);
    // ByteArrayOutputStream outputStream = new
    // ByteArrayOutputStream(BUFFER_SIZE);
    // ImageIO.
    // return inputStream;
    // }

    public static byte[] bufferedImageToByteArray(final BufferedImage image) throws IOException {
	final ByteArrayOutputStream baos = new ByteArrayOutputStream();
	ImageIO.write(image, "jpg", baos);
	baos.flush();
	final byte[] imageInByte = baos.toByteArray();
	baos.close();
	return imageInByte;
    }
}
