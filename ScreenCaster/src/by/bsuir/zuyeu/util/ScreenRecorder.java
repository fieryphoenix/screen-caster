/**
 * 
 */
package by.bsuir.zuyeu.util;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xuggle.ferry.IBuffer;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.ICodec.ID;

/**
 * @author Fieryphoenix
 * 
 */
public final class ScreenRecorder {
    private static final Logger logger = LoggerFactory.getLogger(ScreenRecorder.class);

    // video parameters

    private final static int VIDEO_STREAM_INDEX = 0;
    private final static int VIEDO_STREAM_ID = 0;
    private final static ICodec.ID VIDEO_CODEC = ID.CODEC_ID_H264;
    private final static int VIDEO_FRAME_RATE = 10;

    // audio parameters

    private final static int AUDIO_STREAM_INDEX = 1;
    private final static int AUDIO_STREAM_ID = 0;
    private final static int CHANNELS = 2;
    private final static int SAMPLE_RATE = 44100; // Hz
    private final static int SIZE_IN_BITS = 16;
    private final static int AUDIO_FRAME_SIZE = 4;
    private final static ICodec.ID AUDIO_CODEC = ID.CODEC_ID_AAC;

    private final Robot robot;
    private final Toolkit toolkit = Toolkit.getDefaultToolkit();
    private final Rectangle screenBounds = new Rectangle(toolkit.getScreenSize());

    // First, let's make a IMediaWriter to write the file.
    private final IMediaWriter writer = ToolFactory.makeWriter("d:/output.mp4");

    private boolean isRecorderStarted;

    public ScreenRecorder() throws AWTException {
	robot = new Robot();
    }

    public void run() throws InterruptedException, IOException {
	logger.info("run() - start;");
	// We tell it we're going to add one video stream, with id 0,
	// at position 0, and that it will have a fixed frame rate of
	// FRAME_RATE.
	writer.addVideoStream(VIDEO_STREAM_INDEX, VIEDO_STREAM_ID, VIDEO_CODEC, screenBounds.width, screenBounds.height);
	writer.addAudioStream(AUDIO_STREAM_INDEX, AUDIO_STREAM_ID, AUDIO_CODEC, CHANNELS, SAMPLE_RATE);

	TargetDataLine line = null;
	final AudioFormat audioFormat = getAudioFormat();
	final DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);

	try {
	    line = (TargetDataLine) AudioSystem.getLine(info);
	    line.open(audioFormat);
	} catch (final LineUnavailableException e) {
	    logger.error("unable to get a recording line", e);
	}
	line.start();

	final byte[] audioBuf = new byte[SAMPLE_RATE];

	// Now, we're going to loop
	final long startTime = System.nanoTime();
	final long sleepTimeMillis = (long) (1000.0 / VIDEO_FRAME_RATE);
	isRecorderStarted = true;
	while (isRecorderStarted) {
	    // take the screen shot
	    final BufferedImage screen = robot.createScreenCapture(screenBounds);
	    // convert to the right image type
	    final BufferedImage bgrScreen = convertToType(screen, BufferedImage.TYPE_3BYTE_BGR);
	    final long now = System.nanoTime();
	    final long nTime = now - startTime;

	    // encode the image to stream #0
	    writer.encodeVideo(VIDEO_STREAM_INDEX, bgrScreen, nTime, TimeUnit.NANOSECONDS);

	    // encode audio to stream #1
	    final int nBytesRead = line.read(audioBuf, 0, SAMPLE_RATE);

	    final IBuffer iBuf = IBuffer.make(null, audioBuf, 0, nBytesRead);
	    final IAudioSamples smp = IAudioSamples.make(iBuf, CHANNELS, IAudioSamples.Format.FMT_S16);
	    smp.setComplete(true, SAMPLE_RATE, SAMPLE_RATE, CHANNELS, IAudioSamples.Format.FMT_S16, line.getLongFramePosition());
	    smp.put(audioBuf, 0, 0, SAMPLE_RATE);

	    // encode audio to stream #1
	    writer.encodeAudio(AUDIO_STREAM_INDEX, smp);

	    // sleep for frame rate milliseconds
	    Thread.sleep(sleepTimeMillis);
	}
	// Finally we tell the writer to close and write the trailer if
	// needed
	writer.close();
	logger.info("run() - end;");
    }

    public void stopRecord() {
	isRecorderStarted = false;
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

    public static BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
	BufferedImage image;

	// if the source image is already the target type, return the source
	// image

	if (sourceImage.getType() == targetType) {
	    image = sourceImage;
	} else {
	    image = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), targetType);
	    image.getGraphics().drawImage(sourceImage, 0, 0, null);
	}

	return image;
    }

    // This method creates and returns an
    // AudioFormat object for a given set of format
    // parameters. If these parameters don't work
    // well for you, try some of the other
    // allowable parameter values, which are shown
    // in comments following the declarations.
    private AudioFormat getAudioFormat() {
	final boolean bigEndian = false;
	return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, SAMPLE_RATE, SIZE_IN_BITS, CHANNELS, AUDIO_FRAME_SIZE, SAMPLE_RATE, bigEndian);
    }// end getAudioFormat
}
