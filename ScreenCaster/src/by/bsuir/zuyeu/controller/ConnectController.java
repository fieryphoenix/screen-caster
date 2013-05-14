/**
 * 
 */
package by.bsuir.zuyeu.controller;

import java.awt.AWTException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import by.bsuir.zuyeu.admin.api.CommandType;
import by.bsuir.zuyeu.admin.api.SocketCasterPacket;
import by.bsuir.zuyeu.app.Constants;
import by.bsuir.zuyeu.app.ScreenCaster;
import by.bsuir.zuyeu.exeption.ErrorMessages;
import by.bsuir.zuyeu.model.UIChainTask;
import by.bsuir.zuyeu.service.ConnectManagerClient;
import by.bsuir.zuyeu.service.WebStreamer;
import by.bsuir.zuyeu.util.ScreenShoter;

/**
 * @author Fieryphoenix
 * 
 */
public class ConnectController extends AnchorController {
    private static final Logger logger = LoggerFactory.getLogger(ConnectController.class);

    protected class JoinTask extends UIChainTask<Void> {

	public JoinTask(Task<Void> task) {
	    super(task);
	}

	@Override
	protected Void call() {
	    logger.trace("call() - start;");
	    final String roomNumber = joinRoomNumber.getText().trim().toLowerCase();
	    if (StringUtils.isNotBlank(roomNumber)) {
		SocketCasterPacket packet = new SocketCasterPacket();
		packet.setCommandType(CommandType.GET_ROOM_ADDRESS);
		packet.setData(roomNumber);
		final ConnectManagerClient client = ConnectManagerClient.getInstance();
		try {
		    packet = client.dialogToServer(packet);
		} catch (ClassNotFoundException | IOException e) {
		    registerError("call", ErrorMessages.NO_SUCH_SESSION, e);
		} finally {
		    isWaitJobDone = true;
		    client.close();
		}
		if (packet.getCommandType().equals(CommandType.REGISTER_SUCCESS)) {
		    logger.debug("join is possible. switch view");
		    isJoiningFired = true;
		    switchToPlayView(roomNumber);
		} else {
		    writeError(ErrorMessages.NO_SUCH_SESSION);
		}
	    } else {
		writeError(ErrorMessages.BLANK_FIELD);
	    }
	    isWaitJobDone = true;
	    logger.trace("call() - end;");
	    return null;
	}

	private void switchToPlayView(final String roomNumber) {
	    Platform.runLater(new Runnable() {
		@Override
		public void run() {
		    joinButton.setText("STOP");
		    application.gotoPlayVideo(roomNumber);
		}
	    });
	}

    }

    protected class OpenSessionTask extends UIChainTask<Void> {

	public OpenSessionTask(Task<Void> task) {
	    super(task);
	}

	@Override
	protected Void call() {
	    logger.trace("call() - start;");
	    try {
		final SocketCasterPacket resultPacket = register();
		if (resultPacket.getCommandType().equals(CommandType.REGISTER_SUCCESS)) {
		    shareRoomNumber.setText((String) resultPacket.getData());
		    startStreaming();
		} else {
		    writeError(ErrorMessages.SESSION_BUSY);
		}
	    } catch (final IOException | ClassNotFoundException e) {
		registerError("call", ErrorMessages.SERVER_UNAVAILABLE, e);
	    } finally {
		isWaitJobDone = true;
	    }
	    logger.trace("call() - end;");
	    return null;
	}

	private SocketCasterPacket register() throws ClassNotFoundException, IOException {
	    logger.trace("register() - start;");
	    final SocketCasterPacket sendPacket = new SocketCasterPacket();
	    sendPacket.setCommandType(CommandType.REGISTER_NEW_ROOM);
	    final Object[] data = new Object[2];
	    data[0] = Constants.BROADCAST_HOST;
	    data[1] = Constants.BROADCAST_PORT;
	    sendPacket.setData(data);
	    final SocketCasterPacket resultPacket = ConnectManagerClient.getInstance().dialogToServer(sendPacket);
	    logger.trace("register() - end;");
	    return resultPacket;
	}

	private void startStreaming() {
	    logger.trace("startStreaming() - start;");
	    try {
		final BlockingQueue<BufferedImage> imageStream = shoter.startFrameShoting();
		streamer.up(imageStream);
		isSharingFired = true;
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
			shareButton.setText("STOP");
		    }
		});
	    } catch (final IOException | InterruptedException e) {
		registerError("call", ErrorMessages.SERVER_UNAVAILABLE, e);
	    }
	    logger.trace("startStreaming() - end;");
	}

    }

    protected class DisconnectTask extends Task<Void> {

	@Override
	protected Void call() {
	    logger.trace("call() - start;");
	    streamer.setDownloadEnable(false);
	    application.gotoConnection();
	    try {
		Thread.sleep(Constants.GLOBAL_DELAY_MILLIS);
	    } catch (final InterruptedException e) {
		logger.error("call()", e);
	    }
	    isWaitJobDone = true;
	    logger.trace("call() - end;");
	    return null;
	}

    }

    protected class CheckWaitTask extends Task<Void> {

	@Override
	protected Void call() throws Exception {
	    logger.trace("call() - start;");
	    if (isWaitJobDone) {
		logger.debug("wait job is done");
		stopWaiting();
	    }
	    logger.trace("call() - end;");
	    return null;
	}

    }

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
    @FXML
    private Text errorMessage;

    private ScreenCaster application;
    private boolean isSharingFired;
    private boolean isJoiningFired;
    private boolean isWaitJobDone;
    // private ScreenRecorder recorder;
    private ScreenShoter shoter;
    private WebStreamer streamer;

    public void setApp(ScreenCaster application) {
	logger.info("setApp() - start;");
	this.application = application;
	logger.info("setApp() - end;");
    }

    @Override
    public void initialize(URL url, ResourceBundle rBundle) {
	logger.info("initialize() - start: url = {}, rBundle = {}", new Object[] { url, rBundle });
	isSharingFired = false;
	isJoiningFired = false;
	clearErrors();
	try {
	    // recorder = new ScreenRecorder();
	    shoter = new ScreenShoter();
	} catch (final AWTException e) {
	    registerError("initialize()", ErrorMessages.APP_INIT_ERROR, e);
	}
	streamer = new WebStreamer();
	logger.info("initialize() - end;");
    }

    public void processShare(ActionEvent event) {
	logger.info("processShare() - start: event = {}", event);
	clearErrors();
	if (!isJoiningFired) {
	    if (!isSharingFired) {
		startWaiting();
		final Task<Void> task = new OpenSessionTask(new CheckWaitTask());
		startDaemonTask(task);
	    } else {
		shareButton.setText("SHARE");
		// recorder.stopRecord();
		shoter.stopStreaming();
		streamer.setUploadEnable(false);
		ConnectManagerClient.getInstance().close();
		isSharingFired = false;
	    }
	} else {
	    // TODO: show error
	}
	logger.info("processShare() - end;");
    }

    public void processJoin(final ActionEvent event) throws InterruptedException {
	logger.info("processJoin() - start: event = {}", event);
	clearErrors();
	if (!isSharingFired) {
	    Task<Void> task = null;
	    if (!isJoiningFired) {
		task = new JoinTask(new CheckWaitTask());
	    } else {
		joinButton.setText("JOIN");
		task = new DisconnectTask();
		isJoiningFired = false;
	    }
	    startWaiting();
	    startDaemonTask(task);
	} else {
	    // TOFO: show error
	}
	logger.info("processJoin() - end;");
    }

    private <T> void startDaemonTask(final Task<T> task) {
	final Thread t = new Thread(task);
	t.setDaemon(true);
	t.start();
    }

    public void closeApp(ActionEvent event) throws Exception {
	logger.info("closeApp() - start: event = {}", event);
	application.exit();
	logger.info("closeApp() - end;");
    }

    private <T> void startWaiting() {
	logger.trace("startWaiting() - start;");
	waitPane.setVisible(true);
	waitIndicator.setProgress(-1);
	isWaitJobDone = false;
	logger.trace("startWaiting() - end;");
    }

    private void stopWaiting() {
	logger.trace("stopWaiting() - start;");
	waitPane.setVisible(false);
	waitIndicator.setProgress(1);
	logger.trace("stopWaiting() - end;");
    }

    protected void clearErrors() {
	errorMessage.setText("");
    }

    protected void registerError(final String method, final ErrorMessages message, final Throwable e) {
	logger.error(method, e);
	errorMessage.setText(message.getMessage());
    }

    protected void writeError(final ErrorMessages message) {
	errorMessage.setText(message.getMessage());
    }

}
