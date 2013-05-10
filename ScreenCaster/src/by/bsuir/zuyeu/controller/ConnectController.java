/**
 * 
 */
package by.bsuir.zuyeu.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import by.bsuir.zuyeu.app.Constants;
import by.bsuir.zuyeu.app.Main;

/**
 * @author Fieryphoenix
 * 
 */
public class ConnectController extends AnchorPane implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(ConnectController.class);

    private Main application;

    @FXML
    private Button shareButton;
    @FXML
    private TextField shareRoomNumber;
    @FXML
    private Button joinButton;
    @FXML
    private TextField joinRoomNumber;
    @FXML
    private AnchorPane waitPane;
    @FXML
    private ProgressIndicator waitIndicator;

    private boolean isSharingFired;
    private boolean isJoiningFired;

    public void setApp(Main application) {
	logger.info("setApp() - start;");
	this.application = application;
	logger.info("setApp() - end;");
    }

    @Override
    public void initialize(URL url, ResourceBundle rBundle) {
	logger.info("initialize() - start: url = {}, rBundle = {}", new Object[] { url, rBundle });
	isSharingFired = false;
	isJoiningFired = false;
	logger.info("initialize() - end;");
    }

    public void processShare(ActionEvent event) throws InterruptedException {
	logger.info("processShare() - start: event = {}", event);
	if (!isJoiningFired) {
	    startWaiting();
	    if (!isSharingFired) {
		shareButton.setText("STOP");

	    } else {
		shareButton.setText("SHARE");

	    }
	    isSharingFired = !isSharingFired;
	    stopWaiting();
	} else {
	    // TODO: show error
	}
	// showPopupError("test");
	logger.info("processShare() - end;");
    }

    public void processJoin(final ActionEvent event) throws InterruptedException {
	logger.info("processJoin() - start: event = {}", event);
	if (!isSharingFired) {
	    startWaiting();
	    Task<Void> task = null;
	    if (!isJoiningFired) {
		joinButton.setText("STOP");
		task = new Task<Void>() {

		    @Override
		    protected Void call() throws Exception {
			logger.trace("call() - start;");
			stopWaiting();
			// TODO: add start player
			final Media media = new Media("d:/output.mp4");
			final MediaPlayer player = new MediaPlayer(media);
			player.play();

			logger.trace("call() - end;");
			return null;
		    }
		};

	    } else {
		joinButton.setText("JOIN");
		task = new Task<Void>() {

		    @Override
		    protected Void call() throws Exception {
			logger.trace("call() - start;");
			try {
			    Thread.sleep(Constants.GLOBAL_DELAY_MILLIS);
			} catch (final InterruptedException e) {
			    logger.error("call()", e);
			}
			stopWaiting();
			logger.trace("call() - end;");
			return null;
		    }
		};

	    }
	    isJoiningFired = !isJoiningFired;
	    startTask(task);
	} else {
	    // TOFO: show error
	}
	logger.info("processJoin() - end;");
    }

    public void closeApp(ActionEvent event) throws Exception {
	logger.info("closeApp() - start: event = {}", event);
	application.exit();
	logger.info("closeApp() - end;");
    }

    private void startTask(final Task<Void> task) {
	logger.trace("startTask() - start: task = {}", task);
	final Thread th = new Thread(task);
	th.setDaemon(true);
	th.start();
	logger.trace("startTask() - end;");
    }

    private void startWaiting() {
	waitPane.setVisible(true);
	waitIndicator.setProgress(-1);
    }

    private void stopWaiting() {
	waitPane.setVisible(false);
	waitIndicator.setProgress(1);
    }
    //
    // private void showPopupError(final String message) {
    // final Popup popup = new Popup();
    // // TODO: define normal position
    // popup.setX(300);
    // popup.setY(200);
    // popup.getContent().addAll(new Circle(25, 25, 50, Color.AQUAMARINE), new
    // Text(message));
    // popup.show(application.getStage());
    // }

}
