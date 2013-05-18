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
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import by.bsuir.zuyeu.model.image.ImageChunk;
import by.bsuir.zuyeu.model.image.ImagePacket;
import by.bsuir.zuyeu.util.ImageUtils;
import by.bsuir.zuyeu.util.PropertiesUtil;

/**
 * @author Fieryphoenix
 * 
 */
public final class WebStreamer {
    private static final Logger logger = LoggerFactory.getLogger(WebStreamer.class);
    public static final long STREAMING_DELAY = 50;
    public static final int UDP_DATAGRAM_SIZE = ImagePacket.PACKET_SIZE;

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
		    final InetAddress addr = InetAddress.getByName(PropertiesUtil.getProperty("multicast_host"));
		    final int port = Integer.valueOf(PropertiesUtil.getProperty("multicast_port"));

		    while (uploadEnable) {

			if (!images.isEmpty()) {

			    final BufferedImage nextPooledImage = images.poll();
			    logger.debug("pool space = {}", images.remainingCapacity());

			    final ImageChunk[] chunks = ImageUtils.splitToChunks(nextPooledImage);

			    for (int i = 0; i < chunks.length; i++) {
				final byte[] originalBuffer = ImageUtils.bufferedImageToByteArray(chunks[i].getImageChunk());
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
				    final ImagePacket packet = ImageUtils.convertToImagePacket(chunks[i]);
				    packet.setPartIndex((offset / UDP_DATAGRAM_SIZE) + 1);
				    packet.setData(imagePart);
				    objectOutStream.writeObject(packet);
				    objectOutStream.flush();

				    final byte[] writePack = byteOutStream.toByteArray();
				    logger.debug("out size = {}", writePack.length);
				    final DatagramPacket pack = new DatagramPacket(writePack, writePack.length, addr, port);
				    ds.send(pack);

				    objectOutStream.close();
				    byteOutStream.close();
				}
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

    public BlockingQueue<ImageChunk> down(final String host, final int port) throws IOException {
	logger.info("down() - start();");
	final BlockingQueue<ImageChunk> oos = new ArrayBlockingQueue<>(20);

	downloadEnable = true;
	final Thread t = new Thread() {

	    @Override
	    public void run() {
		logger.trace("run() - start;");
		MulticastSocket ds = null;
		try {
		    ds = new MulticastSocket(port);
		    // ds.bind(new InetSocketAddress(host, port));
		    ds.joinGroup(InetAddress.getByName(host));
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
				final ImageChunk chunk = ImageUtils.restoreImage(imageParts);
				if (chunk != null) {
				    oos.put(chunk);
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
