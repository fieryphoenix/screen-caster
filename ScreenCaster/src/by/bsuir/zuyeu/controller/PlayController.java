/**
 * 
 */
package by.bsuir.zuyeu.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import by.bsuir.zuyeu.app.Main;
import by.bsuir.zuyeu.view.util.HideButtonUtil;

/**
 * @author Fieryphoenix
 * 
 */
public class PlayController extends AnchorPane implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(PlayController.class);

    private Main application;
    @FXML
    private Button stopButton;
    private MediaPlayer mediaPlayer;
    private HideButtonUtil hideButtonUtil;

    @Override
    public void initialize(URL url, ResourceBundle rBundle) {
	logger.info("initialize() - start: url = {}, rBundle = {}", new Object[] { url, rBundle });
	logger.debug("button = {}", stopButton);
	hideButtonUtil = new HideButtonUtil(stopButton);
	Platform.runLater(hideButtonUtil.startHiding(5000));
	logger.info("initialize() - end;");

    }

    public void setApp(Main application) {
	logger.info("setApp() - start;");
	this.application = application;
	addPlayer();
	logger.info("setApp() - end;");
    }

    public void onStopPlay(ActionEvent event) {
	logger.info("onStopPlay() - start: event = {}", event);
	hideButtonUtil.stopHidding();
	mediaPlayer.stop();
	application.replaceToConnection();
	logger.info("onStopPlay() - end;");
    }

    public void onMouseAboveButton(MouseEvent event) {
	logger.info("onMouseAboveButton() - start: event = {}", event);
	hideButtonUtil.stopHidding();
	logger.info("onMouseAboveButton() - end;");
    }

    public void onMouseOutOfButton(MouseEvent event) {
	logger.info("onMouseOutOfButton() - start: event = {}", event);
	Platform.runLater(hideButtonUtil.startHiding(1000));
	logger.info("onMouseOutOfButton() - end;");
    }

    private void addPlayer() {

	// Create the media source.
	// TODO: setup stream here
	final File file = new File("D:/output.mp4");
	final String path = file.toURI().toASCIIString();
	final Media media = new Media(path);

	// Create the player and set to play automatically.
	mediaPlayer = new MediaPlayer(media);
	mediaPlayer.setAutoPlay(true);

	// Create the view and add it to the Scene.
	final MediaView mediaView = new MediaView(mediaPlayer);
	// mediaView.autosize();

	final DoubleProperty width = mediaView.fitWidthProperty();
	final DoubleProperty height = mediaView.fitHeightProperty();

	width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
	height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

	mediaView.setPreserveRatio(false);
	logger.warn("test stage = {}", ((AnchorPane) application.getStage().getScene().getRoot()).getChildren());
	((AnchorPane) application.getStage().getScene().getRoot()).getChildren().add(mediaView);
	mediaView.toBack();
    }

}
