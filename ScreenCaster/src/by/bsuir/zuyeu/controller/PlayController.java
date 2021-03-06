/**
 * 
 */
package by.bsuir.zuyeu.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import by.bsuir.zuyeu.admin.api.CommandType;
import by.bsuir.zuyeu.admin.api.SocketCasterPacket;
import by.bsuir.zuyeu.app.ScreenCaster;
import by.bsuir.zuyeu.model.image.ImageChunk;
import by.bsuir.zuyeu.service.ConnectManagerClient;
import by.bsuir.zuyeu.service.WebStreamer;
import by.bsuir.zuyeu.util.ImageUtils;
import by.bsuir.zuyeu.view.util.HideButtonUtil;

/**
 * @author Fieryphoenix
 * 
 */
public class PlayController extends AnchorController {
    private static final Logger logger = LoggerFactory.getLogger(PlayController.class);

    class TimerUpdater {
	private final BlockingQueue<ImageChunk> imageSource;
	private boolean timerGo;

	public TimerUpdater(final BlockingQueue<ImageChunk> imageSource) {
	    this.imageSource = imageSource;
	}

	public void startTimer() {
	    timerGo = true;
	    final Timer timer = new Timer(true);
	    timer.schedule(new TimerTask() {

		@Override
		public void run() {
		    if (!timerGo) {
			this.cancel();
		    }
		    if (imageSource.size() > 0) {
			final ImageChunk screenImage = imageSource.poll();
			updateImage(screenImage);
		    }

		}
	    }, 0, 100);

	}

	public void stopTimer() {
	    timerGo = false;
	}
    }

    private ScreenCaster application;
    @FXML
    private Button stopButton;
    @FXML
    private ImageView backgroudImageView;
    // private MediaPlayer mediaPlayer;
    private TimerUpdater updater;
    private HideButtonUtil hideButtonUtil;
    private BufferedImage bgrImage;

    @Override
    public void initialize(URL url, ResourceBundle rBundle) {
	logger.info("initialize() - start: url = {}, rBundle = {}", new Object[] { url, rBundle });
	logger.debug("button = {}", stopButton);
	hideButtonUtil = new HideButtonUtil(stopButton);
	final long hideDelay = 5000;
	Platform.runLater(hideButtonUtil.startHiding(hideDelay));
	logger.info("initialize() - end;");

    }

    public void setApp(ScreenCaster application) {
	logger.info("setApp() - start;");
	this.application = application;
	logger.info("setApp() - end;");
    }

    public void onStopPlay(ActionEvent event) {
	logger.info("onStopPlay() - start: event = {}", event);
	hideButtonUtil.stopHidding();
	updater.stopTimer();
	application.gotoConnection();
	logger.info("onStopPlay() - end;");
    }

    public void onMouseAboveButton(MouseEvent event) {
	logger.info("onMouseAboveButton() - start: event = {}", event);
	hideButtonUtil.stopHidding();
	logger.info("onMouseAboveButton() - end;");
    }

    public void onMouseOutOfButton(MouseEvent event) {
	logger.info("onMouseOutOfButton() - start: event = {}", event);
	final long hideDelay = 1000;
	Platform.runLater(hideButtonUtil.startHiding(hideDelay));
	logger.info("onMouseOutOfButton() - end;");
    }

    protected void updateImage(final ImageChunk chunk) {
	logger.trace("updateImage() - start: chunk = {}", chunk);
	final DoubleProperty width = backgroudImageView.fitWidthProperty();
	final DoubleProperty height = backgroudImageView.fitHeightProperty();
	bgrImage = ImageUtils.drawChunk(bgrImage, chunk);
	final Image image = SwingFXUtils.toFXImage(bgrImage, null);
	backgroudImageView.setImage(image);

	width.bind(Bindings.selectDouble(backgroudImageView.sceneProperty(), "width"));
	height.bind(Bindings.selectDouble(backgroudImageView.sceneProperty(), "height"));
	logger.trace("updateImage() - end;");
    }

    public void startImageShow(final String roomNumber) {
	logger.info("startImageShow() - start: roomNumber = {}", roomNumber);
	final ConnectManagerClient client = ConnectManagerClient.getInstance();
	SocketCasterPacket packet = new SocketCasterPacket();
	packet.setCommandType(CommandType.GET_ROOM_ADDRESS);
	packet.setData(roomNumber);
	try {
	    packet = client.dialogToServer(packet);
	    final Object[] data = (Object[]) packet.getData();
	    final WebStreamer ws = new WebStreamer();
	    final BlockingQueue<ImageChunk> imageSource = ws.down((String) data[0], (int) data[1]);
	    updater = new TimerUpdater(imageSource);
	    updater.startTimer();
	} catch (ClassNotFoundException | IOException e) {
	    logger.error("startImageShow()", e);
	} finally {
	    client.close();
	}
	logger.info("startImageShow() - end;");
    }

}
