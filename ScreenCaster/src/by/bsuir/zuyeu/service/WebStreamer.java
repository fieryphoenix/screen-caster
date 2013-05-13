/**
 * 
 */
package by.bsuir.zuyeu.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import by.bsuir.zuyeu.model.image.ImagePacket;
import by.bsuir.zuyeu.model.image.ImagePacketIndexComparator;
import by.bsuir.zuyeu.util.ImageProcessor;

/**
 * @author Fieryphoenix
 * 
 */
public final class WebStreamer {
    private static final Logger logger = LoggerFactory.getLogger(WebStreamer.class);
    public static final long STREAMING_DELAY = 50;
    public static final int UDP_DATAGRAM_SIZE = ImagePacket.PACKET_SIZE;

    private static final String HOST = "235.1.1.2";
    private static final int PORT = 8888;

    private boolean uploadEnable;
    private boolean downloadEnable;

    public void up(final BlockingQueue<BufferedImage> images) throws IOException, InterruptedException {
	logger.info("up() - start(): images = {}", images);

	uploadEnable = true;
	final Thread t = new Thread() {

	    @Override
	    public void run() {
		logger.trace("run() - start;");
		DatagramSocket ds = null;
		try {
		    ds = new DatagramSocket();
		    final InetAddress addr = InetAddress.getByName(HOST);

		    while (uploadEnable) {

			if (!images.isEmpty()) {

			    final BufferedImage nextPooledImage = images.poll();
			    logger.debug("pool space = {}", images.remainingCapacity());

			    final byte[] originalBuffer = ImageProcessor.bufferedImageToByteArray(nextPooledImage);
			    logger.debug("write new image, image size = {}", originalBuffer.length);

			    for (int offset = 0; offset < originalBuffer.length; offset += UDP_DATAGRAM_SIZE) {
				final ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
				final ObjectOutputStream objectOutStream = new ObjectOutputStream(byteOutStream);

				int length = UDP_DATAGRAM_SIZE;
				if ((offset + length) > originalBuffer.length) {
				    length = originalBuffer.length - offset;
				}
				logger.debug("offset = {}, length = {}, original length = {}", new Object[] { offset, length, originalBuffer.length });
				final byte[] imagePart = Arrays.copyOfRange(originalBuffer, offset, offset + length);
				logger.debug("img part size = {}", imagePart.length);
				final ImagePacket packet = new ImagePacket(imagePart, (offset / UDP_DATAGRAM_SIZE) + 1);

				objectOutStream.writeObject(packet);
				objectOutStream.flush();

				final byte[] writePack = byteOutStream.toByteArray();
				logger.debug("out size = {}", writePack.length);
				final DatagramPacket pack = new DatagramPacket(writePack, writePack.length, addr, PORT);
				ds.send(pack);

				objectOutStream.close();
				byteOutStream.close();
			    }

			} else {
			    Thread.sleep(STREAMING_DELAY);
			}

		    }

		} catch (final Exception e) {
		    logger.error("run()", e);
		    // throw e;
		} finally {
		    if (!ds.isClosed()) {
			ds.close();
		    }
		}
		logger.trace("run() - end;");
	    }

	};
	t.setDaemon(true);
	t.start();

	// TODO: add port validation
	// SecurityManager sm = new SecurityManager();
	// sm.checkListen(port);
	logger.info("up() - end;");
    }

    // @Test
    // public void testImageSplitting() throws Exception {
    // final ScreenShoter screenShoter = new ScreenShoter();
    //
    // final List<ImagePacket> imagesParts = new ArrayList<>();
    // final BufferedImage bufferedImage = screenShoter.newShot();
    // final byte[] originalBuffer =
    // ImageProcessor.bufferedImageToByteArray(bufferedImage);
    // for (int offset = 0; offset < originalBuffer.length; offset +=
    // UDP_DATAGRAM_SIZE) {
    // final ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
    // final ObjectOutputStream objectOutStream = new
    // ObjectOutputStream(byteOutStream);
    //
    // int length = UDP_DATAGRAM_SIZE;
    // if ((offset + length) > originalBuffer.length) {
    // length = originalBuffer.length - offset;
    // }
    // logger.debug("offset = {}, length = {}, original length = {}", new
    // Object[] { offset, length, originalBuffer.length });
    // final byte[] imagePart = Arrays.copyOfRange(originalBuffer, offset,
    // offset + length);
    // logger.debug("img part size = {}", imagePart.length);
    // final ImagePacket packet = new ImagePacket(imagePart, offset /
    // UDP_DATAGRAM_SIZE);
    //
    // objectOutStream.writeObject(packet);
    // objectOutStream.flush();
    //
    // final byte[] writePack = byteOutStream.toByteArray();
    // logger.debug("out size = {}", writePack.length);
    //
    // final ByteArrayInputStream byteInputStream = new
    // ByteArrayInputStream(writePack);
    // final ObjectInputStream objectInStream = new
    // ObjectInputStream(byteInputStream);
    // final ImagePacket inPacket = (ImagePacket) objectInStream.readObject();
    //
    // imagesParts.add(inPacket);
    // objectOutStream.close();
    // byteOutStream.close();
    // }
    // final BufferedImage bufferedImage2 = restoreImage(imagesParts);
    // ImageIO.write(bufferedImage2, "jpg", new File("D:/t.jpg"));
    // }
    //
    // protected ImagePacket getTestImagePacket() throws IOException {
    // final ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
    // final ObjectOutputStream objectOutStream = new
    // ObjectOutputStream(byteOutStream);
    // final byte[] imagePart = new byte[ImagePacket.PACKET_SIZE];
    //
    // Arrays.fill(imagePart, (byte) 5);
    // final ImagePacket packet = new ImagePacket(imagePart, 1);
    // return packet;
    // }

    public BlockingQueue<BufferedImage> down() throws IOException {
	logger.info("down() - start();");
	final BlockingQueue<BufferedImage> oos = new ArrayBlockingQueue<>(20);

	downloadEnable = true;
	final Thread t = new Thread() {

	    @Override
	    public void run() {
		logger.trace("run() - start;");
		MulticastSocket ds = null;
		try {
		    ds = new MulticastSocket(PORT);
		    ds.joinGroup(InetAddress.getByName(HOST));
		    final List<ImagePacket> imageParts = new ArrayList<>();
		    final byte[] datagramBuffer = new byte[ImagePacket.OBJECT_SIZE];
		    logger.debug("connect to channel");
		    while (downloadEnable) {

			final DatagramPacket pack = new DatagramPacket(datagramBuffer, datagramBuffer.length);

			ds.receive(pack);
			final int length = pack.getLength();
			if (length > 0) {
			    final byte[] packetData = pack.getData();
			    logger.debug("read packet size = {}", length);
			    final ByteArrayInputStream byteInputStream = new ByteArrayInputStream(packetData);
			    final ObjectInputStream objectOutStream = new ObjectInputStream(byteInputStream);

			    final ImagePacket imagePacket = (ImagePacket) objectOutStream.readObject();

			    if ((imageParts.size() > 0) && (imagePacket.getPartIndex() == 1)) {
				final BufferedImage image = restoreImage(imageParts);
				if (image != null) {
				    oos.put(image);
				}
				imageParts.clear();
			    }
			    imageParts.add(imagePacket);

			    objectOutStream.close();
			    byteInputStream.close();
			} else {
			    Thread.sleep(STREAMING_DELAY);
			}
		    }
		} catch (final Exception e) {
		    logger.error("run()", e);
		} finally {
		    if (!ds.isClosed()) {
			ds.close();
		    }
		}
		logger.trace("run() - end;");
	    }

	};
	t.setDaemon(true);
	t.start();
	logger.info("down() - end();");
	return oos;
    }

    private BufferedImage restoreImage(final List<ImagePacket> imageParts) {
	logger.trace("restoreImage() - start: imageParts size = {}", imageParts.size());
	BufferedImage bufferedImage = null;
	Collections.sort(imageParts, new ImagePacketIndexComparator());
	final int lastPacketIndex = imageParts.size() - 1;

	// restore only images that has all its part in array
	if (imageParts.get(lastPacketIndex).getPartIndex() == (lastPacketIndex + 1)) {
	    final byte[] resultBuffer = constructResultBuffer(imageParts);
	    logger.debug("result buffer length = {}", resultBuffer.length);
	    final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(resultBuffer);
	    try {
		bufferedImage = ImageIO.read(byteArrayInputStream);
	    } catch (final IOException e) {
		logger.error("restoreImage", e);
	    }
	}
	// ImageIO.createImageInputStream(input);
	logger.trace("restoreImage() - end: bufferedImage = {}", bufferedImage);
	return bufferedImage;
    }

    private byte[] constructResultBuffer(final List<ImagePacket> imageParts) {
	logger.trace("constractResultBuffer() - start;");
	byte[] resultBuffer = new byte[0];
	for (final ImagePacket packet : imageParts) {
	    resultBuffer = ArrayUtils.addAll(resultBuffer, packet.getData());
	}
	logger.trace("constractResultBuffer() - end;");
	return resultBuffer;
    }

    public boolean isUploadEnable() {
	return uploadEnable;
    }

    public void setUploadEnable(boolean uploadEnable) {
	this.uploadEnable = uploadEnable;
    }

    public boolean isDownloadEnable() {
	return downloadEnable;
    }

    public void setDownloadEnable(boolean downloadEnable) {
	this.downloadEnable = downloadEnable;
    }
}
