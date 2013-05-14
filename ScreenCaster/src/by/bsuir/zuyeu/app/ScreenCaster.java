/**
 * 
 */
package by.bsuir.zuyeu.app;

import java.io.InputStream;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import by.bsuir.zuyeu.controller.ConnectController;
import by.bsuir.zuyeu.controller.PlayController;

/**
 * @author Fieryphoenix
 * 
 */
public class ScreenCaster extends Application {
    private static final Logger logger = LoggerFactory.getLogger(ScreenCaster.class);

    private Stage stage;

    // private MainController mainController;

    /**
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
	Application.launch(ScreenCaster.class, (java.lang.String[]) null);
    }

    @Override
    public void start(Stage stage) throws Exception {
	logger.info("start() - start: stage = {}", stage);
	try {
	    this.stage = stage;
	    this.stage.setTitle(Constants.APP_TITLE);
	    gotoConnection();
	    this.stage.show();
	} catch (final Exception ex) {
	    logger.error("start", ex);
	}
	logger.info("start() - end;");
    }

    public void gotoPlayVideo(final String roomNumber) {
	logger.info("replaceToPlayVideo() - start;");
	try {
	    final PlayController playController = (PlayController) replaceSceneContent(Constants.PLAY_VIEW_LOCATION);
	    playController.setApp(this);
	    playController.startImageShow(roomNumber);
	    this.getStage().setResizable(true);

	} catch (final Exception ex) {
	    logger.error("start", ex);
	}
	logger.info("replaceToPlayVideo() - end;");
    }

    public void gotoConnection() {
	logger.info("replaceToConnection() - start;");
	try {

	    final ConnectController connectController = (ConnectController) replaceSceneContent(Constants.CONNECT_VIEW_LOCATION);
	    connectController.setApp(this);
	    this.getStage().setResizable(false);
	} catch (final Exception ex) {
	    logger.error("start", ex);
	}
	logger.info("replaceToConnection() - end;");
    }

    public void exit() throws Exception {
	logger.warn("exit() - start;");
	this.getStage().close();
    }

    protected Initializable replaceSceneContent(final String fxml) throws Exception {
	final FXMLLoader loader = new FXMLLoader();
	final InputStream in = ScreenCaster.class.getResourceAsStream(fxml);
	loader.setBuilderFactory(new JavaFXBuilderFactory());
	loader.setLocation(ScreenCaster.class.getResource(fxml));
	AnchorPane page;
	try {
	    page = (AnchorPane) loader.load(in);
	} finally {
	    in.close();
	}
	// store the stage height in case the user has resized the window
	double stageWidth = stage.getWidth();
	if (!Double.isNaN(stageWidth)) {
	    stageWidth -= (stage.getWidth() - stage.getScene().getWidth());
	}
	logger.debug("stageWidth = {}", stageWidth);
	double stageHeight = stage.getHeight();
	if (!Double.isNaN(stageHeight)) {
	    stageHeight -= (stage.getHeight() - stage.getScene().getHeight());
	}
	logger.debug("stageHeight = {}", stageHeight);
	final Scene scene = new Scene(page);
	if (!Double.isNaN(stageWidth)) {
	    stage.setWidth(page.getPrefWidth());
	}
	if (!Double.isNaN(stageHeight)) {
	    stage.setHeight(page.getPrefHeight());
	}
	stage.setScene(scene);
	stage.sizeToScene();
	return (Initializable) loader.getController();
    }

    public Stage getStage() {
	return stage;
    }

}
