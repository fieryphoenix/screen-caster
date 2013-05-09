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

/**
 * @author Fieryphoenix
 * 
 */
public class Main extends Application {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String CONNECT_VIEW_LOCATION = "../view/ConnectView.fxml";
    private static final String APP_TITLE = "ScreenCaster v0.1";

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
	try {
	    this.stage = stage;
	    this.stage.setTitle(APP_TITLE);
	    final ConnectController connect = (ConnectController) replaceSceneContent(CONNECT_VIEW_LOCATION);
	    connect.setApp(this);

	    this.stage.show();
	} catch (final Exception ex) {
	    logger.error("start", ex);
	}

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
	double stageHeight = stage.getHeight();
	if (!Double.isNaN(stageHeight)) {
	    stageHeight -= (stage.getHeight() - stage.getScene().getHeight());
	}
	// Scene scene = stage.getScene();
	// if (scene == null) {
	final Scene scene = new Scene(page);
	if (!Double.isNaN(stageWidth)) {
	    page.setPrefWidth(stageWidth);
	}
	if (!Double.isNaN(stageHeight)) {
	    page.setPrefHeight(stageHeight);
	}
	stage.setScene(scene);
	// } else {
	// stage.getScene().setRoot(page);
	// }
	stage.sizeToScene();
	return (Initializable) loader.getController();
    }
}
