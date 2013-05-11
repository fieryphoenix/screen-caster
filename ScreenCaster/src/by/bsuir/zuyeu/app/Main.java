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
public class Main extends Application {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private Stage stage;

    /**
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
	Application.launch(Main.class, (java.lang.String[]) null);
    }

    @Override
    public void start(Stage stage) throws Exception {
	logger.info("start() - start: stage = {}", stage);
	try {
	    this.stage = stage;
	    this.stage.setTitle(Constants.APP_TITLE);
	    replaceToConnection();
	    this.stage.show();
	} catch (final Exception ex) {
	    logger.error("start", ex);
	}
	logger.info("start() - end;");
    }

    // @Override
    // public void start(Stage stage) {
    // // Create and set the Scene.
    // final Scene scene = new Scene(new Group(), 640, 480);
    // stage.setScene(scene);
    //
    // // Name and display the Stage.
    // stage.setTitle("Hello Media");
    // stage.show();
    //
    // // Create the media source.
    // final File file = new File("D:/output.mp4");
    // final String path = file.toURI().toASCIIString();
    // final Media media = new Media(path);
    //
    // // Create the player and set to play automatically.
    // final MediaPlayer mediaPlayer = new MediaPlayer(media);
    // mediaPlayer.setAutoPlay(true);
    //
    // // Create the view and add it to the Scene.
    // final MediaView mediaView = new MediaView(mediaPlayer);
    // // mediaView.autosize();
    //
    // final DoubleProperty width = mediaView.fitWidthProperty();
    // final DoubleProperty height = mediaView.fitHeightProperty();
    //
    // width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
    // height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
    //
    // mediaView.setPreserveRatio(false);
    //
    // ((Group) scene.getRoot()).getChildren().add(mediaView);
    // }

    public void replaceToPlayVideo(final String roomNumber) {
	logger.info("gotoPlayVideo() - start;");
	try {
	    final PlayController playController = (PlayController) replaceSceneContent(Constants.PLAY_VIEW_LOCATION);
	    playController.setApp(this);
	    this.getStage().setResizable(true);
	} catch (final Exception ex) {
	    logger.error("start", ex);
	}
	logger.info("gotoPlayVideo() - end;");
    }

    public void replaceToConnection() {
	logger.info("gotoConnection() - start;");
	try {
	    final ConnectController connectController = (ConnectController) replaceSceneContent(Constants.CONNECT_VIEW_LOCATION);
	    connectController.setApp(this);
	    this.getStage().setResizable(false);
	} catch (final Exception ex) {
	    logger.error("start", ex);
	}
	logger.info("gotoConnection() - end;");
    }

    public void exit() throws Exception {
	logger.warn("exit() - start;");
	this.getStage().close();
    }

    protected Initializable replaceSceneContent(String fxml) throws Exception {
	final FXMLLoader loader = new FXMLLoader();
	final InputStream in = Main.class.getResourceAsStream(fxml);
	loader.setBuilderFactory(new JavaFXBuilderFactory());
	loader.setLocation(Main.class.getResource(fxml));
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
	    // page.setPrefWidth(stageWidth);
	    stage.setWidth(page.getPrefWidth());
	}
	if (!Double.isNaN(stageHeight)) {
	    // page.setPrefHeight(stageHeight);
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
