
import controllers.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;
import models.RSS;


/**
 * Created by samschofield on 7/03/17.
 */
public class App extends Application {

    public void start(Stage primaryStage) throws Exception {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1200, 800);

            RSS rss = new RSS();
            rss.setpStage(primaryStage);
            // pass primary stage to master controller
            LoginController loginController = loader.getController();
            loginController.setRSS(rss);
            primaryStage.setTitle("UC RSS");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}