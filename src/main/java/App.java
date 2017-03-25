
import controllers.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;
import models.RSS;


/**
 * Created by Haoming Yin on 7/03/17.
 */
public class App extends Application {

    public void start(Stage primaryStage) throws Exception {

        try {
        	// rss is the master controller who control all other controllers.
            RSS rss = new RSS();
            rss.setpStage(primaryStage);
            rss.initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}