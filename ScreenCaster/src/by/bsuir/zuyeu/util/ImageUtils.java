/**
 * 
 */
package by.bsuir.zuyeu.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import by.bsuir.zuyeu.model.image.ImageChunk;
import by.bsuir.zuyeu.model.image.ImagePacket;
import by.bsuir.zuyeu.model.image.ImagePacketIndexComparator;

/**
 * @author Fieryphoenix
 * 
 */
public class ImageUtils {
    private static final Logger logger = LoggerFactory.getLogger(ImageUtils.class);
    private static final int ROWS = 5;
    private static final int COLS = 5;

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

    public static ImageChunk[] splitToChunks(final BufferedImage originalImage) throws IOException {
	logger.info("splitToChunks() - start: origImg = {}", originalImage);
	final int chunks = ROWS * COLS;

	// determines the chunk width and height
	final int chunkWidth = originalImage.getWidth() / COLS;
	final int chunkHeight = originalImage.getHeight() / ROWS;
	int count = 0;
	// Image array to hold image chunks
	final ImageChunk imgs[] = new ImageChunk[chunks];
	for (int x = 0; x < ROWS; x++) {
	    for (int y = 0; y < COLS; y++) {
		// Initialize the image array with image chunks
		final BufferedImage imagePart = new BufferedImage(chunkWidth, chunkHeight, originalImage.getType());

		// draws the image chunk
		final Graphics2D gr = imagePart.createGraphics();
		gr.drawImage(originalImage, 0, 0, chunkWidth, chunkHeight, chunkWidth * y, chunkHeight * x, (chunkWidth * y) + chunkWidth, (chunkHeight * x)
			+ chunkHeight, null);
		gr.dispose();

		final ImageChunk chunk = new ImageChunk();
		chunk.setImageChunk(imagePart);
		chunk.setChunkWidth(chunkWidth);
		chunk.setChunkHeight(chunkHeight);
		chunk.setStartHeight(chunkHeight * x);
		chunk.setStartWidth(chunkWidth * y);
		chunk.setParentHeight(originalImage.getHeight());
		chunk.setParentWidth(originalImage.getWidth());

		imgs[count] = chunk;
		count++;
	    }
	}
	logger.info("splitToChunks() - end;");
	return imgs;
    }

    public static BufferedImage drawChunk(BufferedImage outImage, final ImageChunk chunk) {
	logger.info("drawChunk() - start;");
	if (outImage == null) {
	    outImage = new BufferedImage(chunk.getParentWidth(), chunk.getParentHeight(), chunk.getImageChunk().getType());
	}
	final Graphics2D gr = outImage.createGraphics();
	gr.drawImage(chunk.getImageChunk(), chunk.getStartWidth(), chunk.getStartHeight(), chunk.getStartWidth() + chunk.getChunkWidth(),
		chunk.getStartHeight() + chunk.getChunkHeight(), 0, 0, chunk.getChunkWidth(), chunk.getChunkHeight(), null);
	gr.dispose();
	logger.info("drawChunk() - end;");
	return outImage;
    }

    public static ImageChunk restoreImage(final List<ImagePacket> imageParts) {
	logger.trace("restoreImage() - start: imageParts size = {}", imageParts.size());
	ImageChunk imageChunk = null;
	Collections.sort(imageParts, new ImagePacketIndexComparator());
	final int lastPacketIndex = imageParts.size() - 1;

	// restore only images that has all its part in array
	if (imageParts.get(lastPacketIndex).getPartIndex() == (lastPacketIndex + 1)) {
	    final byte[] resultBuffer = constructResultBuffer(imageParts);
	    logger.debug("result buffer length = {}", resultBuffer.length);
	    final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(resultBuffer);
	    BufferedImage bufferedImage = null;
	    try {
		bufferedImage = ImageIO.read(byteArrayInputStream);
	    } catch (final IOException e) {
		logger.error("restoreImage", e);
	    }
	    imageChunk = convertToImageChunk(imageParts.get(0));
	    imageChunk.setImageChunk(bufferedImage);
	}
	// ImageIO.createImageInputStream(input);
	logger.trace("restoreImage() - end: imageChunk = {}", imageChunk);
	return imageChunk;
    }

    public static BufferedImage restoreImage(final byte[] buffer) {
	logger.trace("restoreImage() - start: buffer = {}", buffer);
	BufferedImage bufferedImage = null;
	final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
	try {
	    bufferedImage = ImageIO.read(byteArrayInputStream);
	} catch (final IOException e) {
	    logger.error("restoreImage", e);
	}
	logger.trace("restoreImage() - end: bufferedImage = {}", bufferedImage);
	return bufferedImage;
    }

    protected static byte[] constructResultBuffer(final List<ImagePacket> imageParts) {
	logger.trace("constractResultBuffer() - start;");
	byte[] resultBuffer = new byte[0];
	for (final ImagePacket packet : imageParts) {
	    resultBuffer = ArrayUtils.addAll(resultBuffer, packet.getData());
	}
	logger.trace("constractResultBuffer() - end;");
	return resultBuffer;
    }

    public static ImageChunk convertToImageChunk(final ImagePacket imagePacket) {
	logger.info("convertToImageChunk() - start;");
	final ImageChunk chunk = new ImageChunk();
	chunk.setChunkHeight(imagePacket.getChunkHeight());
	chunk.setChunkWidth(imagePacket.getChunkWidth());
	chunk.setImageChunk(restoreImage(imagePacket.getData()));
	chunk.setParentHeight(imagePacket.getParentHeight());
	chunk.setParentWidth(imagePacket.getParentWidth());
	chunk.setStartHeight(imagePacket.getStartHeight());
	chunk.setStartWidth(imagePacket.getStartWidth());
	logger.info("convertToImageChunk() - end;");
	return chunk;
    }

    public static ImagePacket convertToImagePacket(final ImageChunk chunk) throws IOException {
	logger.info("convertToImagePacket() - start;");
	final ImagePacket imagePacket = new ImagePacket(null, 0);
	imagePacket.setChunkHeight(chunk.getChunkHeight());
	imagePacket.setChunkWidth(chunk.getChunkWidth());
	imagePacket.setData(bufferedImageToByteArray(chunk.getImageChunk()));
	imagePacket.setParentHeight(chunk.getParentHeight());
	imagePacket.setParentWidth(chunk.getParentWidth());
	imagePacket.setStartHeight(chunk.getStartHeight());
	imagePacket.setStartWidth(chunk.getStartWidth());
	logger.info("convertToImagePacket() - end;");
	return imagePacket;
    }
}
