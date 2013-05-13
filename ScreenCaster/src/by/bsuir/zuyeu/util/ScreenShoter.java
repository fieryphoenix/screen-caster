/**
 * 
 */
package by.bsuir.zuyeu.util;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Fieryphoenix
 * 
 */
public class ScreenShoter {
    private static final Logger logger = LoggerFactory.getLogger(ScreenShoter.class);

    public static final int VIDEO_FRAME_RATE = 3;
    public static final double SCALE_RATE = 0.5;

    private final Robot robot;
    private final Toolkit toolkit;
    private final Rectangle screenBounds;

    private BlockingQueue<BufferedImage> imageStream;
    private boolean isStreamOpen;
    private Timer timer;

    public ScreenShoter() throws AWTException {
	robot = new Robot();
	toolkit = Toolkit.getDefaultToolkit();
	screenBounds = new Rectangle(toolkit.getScreenSize());
    }

    public BlockingQueue<BufferedImage> startFrameShoting() {
	logger.info("startFrameShoting() - start;");
	imageStream = new ArrayBlockingQueue<>(20);
	isStreamOpen = true;
	final long frameDelay = 1000 / VIDEO_FRAME_RATE;
	timer = new Timer(true);
	timer.schedule(new TimerTask() {

	    @Override
	    public void run() {
		if (isStreamOpen) {
		    try {
			imageStream.put(newShot());
		    } catch (final InterruptedException e) {
			logger.error("run()", e);
		    }
		    if (imageStream.remainingCapacity() == 0) {
			for (int i = 0; i < 10; i++) {
			    imageStream.poll();
			}
		    }
		} else {
		    this.cancel();
		}
	    }
	}, 0, frameDelay);
	logger.info("startFrameShoting() - end;");
	return imageStream;
    }

    public void stopStreaming() {
	isStreamOpen = false;
    }

    public BufferedImage newShot() {
	logger.info("newShoot() - start;");
	// take the screen shot
	final BufferedImage screen = robot.createScreenCapture(screenBounds);
	// final convert to the final right image type
	final BufferedImage bgrScreen = convertToType(screen, BufferedImage.TYPE_INT_RGB);
	// final BufferedImage bgrScreen = Scalr.resize(screen, FINAL_WIDTH,
	// FINAL_HEIGHT);
	logger.info("newShoot() - end: shoot = {}", bgrScreen);
	return bgrScreen;
    }

    /**
     * Convert a {@link BufferedImage} of any type, to {@link BufferedImage} of
     * a specified type. If the source image is the same type as the target
     * type, then original image is returned, otherwise new image of the correct
     * type is created and the content of the source image is copied into the
     * new image.
     * 
     * @param sourceImage
     *            the image to be converted
     * @param targetType
     *            the desired BufferedImage type
     * 
     * @return a BufferedImage of the specifed target type.
     * 
     * @see BufferedImage
     */

    protected static BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
	BufferedImage image;

	// if the source image is already the target type, return the source
	// image

	if (sourceImage.getType() == targetType) {
	    image = sourceImage;
	} else {
	    image = new BufferedImage((int) (SCALE_RATE * sourceImage.getWidth()), (int) (SCALE_RATE * sourceImage.getHeight()), targetType);
	    image.getGraphics().drawImage(sourceImage, 0, 0, null);
	}

	return image;
    }

}
