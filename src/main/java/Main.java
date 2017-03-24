
import controllers.SQLiteController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;

import java.sql.Connection;


/**
 * Created by samschofield on 7/03/17.
 */
public class Main extends Application {

    public void start(Stage stage) throws Exception {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 500, 400);
            stage.setTitle("UC RSS");
            stage.setScene(scene);
            SQLiteController controller = new SQLiteController();
            controller.connect();
            //stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
