/**
 * 
 */
package by.bsuir.zuyeu.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

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

    protected class JoinTask extends Task<Void> {

	@Override
	protected Void call() throws Exception {
	    logger.trace("call() - start;");
	    final String roomNumber = joinRoomNumber.getText().trim().toLowerCase();
	    application.replaceToPlayVideo(roomNumber);
	    stopWaiting();
	    logger.trace("call() - end;");
	    return null;
	}

    }

    protected class DisconnectTask extends Task<Void> {

	@Override
	protected Void call() throws Exception {
	    logger.trace("call() - start;");
	    application.replaceToConnection();
	    try {
		Thread.sleep(Constants.GLOBAL_DELAY_MILLIS);
	    } catch (final InterruptedException e) {
		logger.error("call()", e);
	    }
	    stopWaiting();
	    logger.trace("call() - end;");
	    return null;
	}

    }

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
		// TODO
	    } else {
		shareButton.setText("SHARE");
		// TODO
	    }
	    isSharingFired = !isSharingFired;
	    stopWaiting();
	} else {
	    // TODO: show error
	}
	logger.info("processShare() - end;");
    }

    public void processJoin(final ActionEvent event) throws InterruptedException {
	logger.info("processJoin() - start: event = {}", event);
	if (!isSharingFired) {
	    startWaiting();
	    Task<Void> task = null;
	    if (!isJoiningFired) {
		joinButton.setText("STOP");
		task = new JoinTask();
	    } else {
		joinButton.setText("JOIN");
		task = new DisconnectTask();
	    }
	    isJoiningFired = !isJoiningFired;
	    Platform.runLater(task);
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

    private void startWaiting() {
	logger.trace("startWaiting() - start;");
	waitPane.setVisible(true);
	waitIndicator.setProgress(-1);
	logger.trace("startWaiting() - end;");
    }

    private void stopWaiting() {
	logger.trace("stopWaiting() - start;");
	waitPane.setVisible(false);
	waitIndicator.setProgress(1);
	logger.trace("stopWaiting() - end;");
    }

}
