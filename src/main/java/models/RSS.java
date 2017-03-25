package models;

import controllers.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RSS {

	private User user;
	private Stage pStage;
	private MenuController menuController;
	private CarController carController;
	private SQLiteConnector sqLiteConnector;

	public void initialize() {
		user = new User();
		pStage.setTitle("UC RSS");
		sqLiteConnector = new SQLiteConnector();
		sqLiteConnector.initializeDatabase();
		showLoginView();
	}

	public void showLoginView() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root, 1200, 800);
			LoginController loginController = loader.getController();
			loginController.setRSS(this);
			pStage.setScene(scene);
			pStage.show();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void showCarView() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/CarView.fxml"));
			Parent carView = fxmlLoader.load();
			Scene scene = new Scene(carView);
			CarController carController = fxmlLoader.getController();
			setCarController(carController);
			carController.setRSS(this);
			carController.loadCars();
			pStage.setScene(scene);
			pStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Stage getpStage() {
		return pStage;
	}

	public void setpStage(Stage pStage) {
		this.pStage = pStage;
	}

	public MenuController getMenuController() {
		return menuController;
	}

	public void setMenuController(MenuController menuController) {
		this.menuController = menuController;
	}

	public CarController getCarController() {
		return carController;
	}

	public void setCarController(CarController carController) {
		this.carController = carController;
	}

	public SQLiteConnector getSqLiteConnector() {
		return sqLiteConnector;
	}

	public void setSqLiteConnector(SQLiteConnector sqLiteConnector) {
		this.sqLiteConnector = sqLiteConnector;
	}
}
