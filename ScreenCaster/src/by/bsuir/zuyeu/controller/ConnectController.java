/**
 * 
 */
package by.bsuir.zuyeu.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import by.bsuir.zuyeu.app.Main;

/**
 * @author Fieryphoenix
 * 
 */
public class ConnectController extends AnchorPane implements Initializable {

    private Main application;

    public void setApp(Main application) {
	this.application = application;
    }

    @Override
    public void initialize(URL url, ResourceBundle rBundle) {

    }

}
