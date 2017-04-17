package controllers;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import models.database.SQLConnector;
import models.User;

public class RSS {

	private User user;
	private Stage pStage;
	private Controller menuController, carController, tripController,
			giveRideController, routeController, bookRideController;

	private SQLConnector sqlConnector;

	public void initialize() {
		user = new User();
		// close all other windows when primary stage is closed
		pStage.setOnCloseRequest(e -> Platform.exit());
		pStage.setTitle("UC RSS");
		sqlConnector = new SQLConnector();
		sqlConnector.initializeDatabase();
		showLoginView();
	}

	 void showLoginView() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoginView.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root, 1200, 800);
			Controller loginController = loader.getController();
			loginController.setRSS(this);
			pStage.setScene(scene);
			pStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	 void showSignUpView() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/SignupView.fxml"));
			Parent root = fxmlLoader.load();
			Controller controller = fxmlLoader.getController();
			controller.setRSS(this);
			Stage stage = new Stage();
			controller.setStage(stage);
			stage.setScene(new Scene(root));
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	 void showProfile() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/SignupView.fxml"));
			Parent root = fxmlLoader.load();
			SignupController controller = fxmlLoader.getController();
			controller.setRSS(this);
			Stage stage = new Stage();
			controller.setStage(stage);
			controller.myProfileMode();
			stage.setScene(new Scene(root));
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	 void showCarView() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/CarView.fxml"));
			Parent carView = fxmlLoader.load();
			Scene scene = new Scene(carView);
			Controller carController = fxmlLoader.getController();
			setCarController(carController);
			carController.setRSS(this);
			pStage.setScene(scene);
			pStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	 void showRouteView() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/RouteView.fxml"));
			Parent View = fxmlLoader.load();
			Scene scene = new Scene(View);
			Controller Controller = fxmlLoader.getController();
			setRouteController(Controller);
			Controller.setRSS(this);
			pStage.setScene(scene);
			pStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	 void showTripView() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/TripView.fxml"));
			Parent View = fxmlLoader.load();
			Scene scene = new Scene(View);
			Controller controller = fxmlLoader.getController();
			setTripController(controller);
			controller.setRSS(this);
			pStage.setScene(scene);
			pStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	 void showRideView() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/GiveRideView.fxml"));
			Parent View = fxmlLoader.load();
			Scene scene = new Scene(View);
			Controller controller = fxmlLoader.getController();
			setGiveRideController(controller);
			controller.setRSS(this);
			pStage.setScene(scene);
			pStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	 void showBookRideView() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/BookRideView.fxml"));
			Parent View = fxmlLoader.load();
			Scene scene = new Scene(View);
			Controller controller = fxmlLoader.getController();
			setBookRideController(controller);
			controller.setRSS(this);
			pStage.setScene(scene);
			pStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void showErrorDialog(String headMsg, String errorMsg) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("RSS Error Dialog");
		alert.setHeaderText(headMsg);
		alert.setContentText(errorMsg);
		alert.getDialogPane().setPrefWidth(600);
		alert.showAndWait();
	}

	 void showInformationDialog(String headMsg, String infoMsg) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("RSS Information Dialog");
		alert.setHeaderText(headMsg);
		alert.setContentText(infoMsg);
		alert.getDialogPane().setPrefWidth(500);
		alert.showAndWait();
	}

	protected void showWarningDialog(String headMsg, String warnMsg) {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle("RSS Warning Dialog");
		alert.setHeaderText(headMsg);
		alert.setContentText(warnMsg);
		alert.getDialogPane().setPrefWidth(500);
		alert.showAndWait();
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

	 SQLConnector getSqlConnector() {
		return sqlConnector;
	}

	protected void setSqlConnector(SQLConnector sqlConnector) {
		this.sqlConnector = sqlConnector;
	}

	protected Controller getMenuController() {
		return menuController;
	}

	protected void setMenuController(Controller menuController) {
		this.menuController = menuController;
	}

	protected Controller getCarController() {
		return carController;
	}

	 void setCarController(Controller carController) {
		this.carController = carController;
	}

	protected Controller getTripController() {
		return tripController;
	}

	 void setTripController(Controller tripController) {
		this.tripController = tripController;
	}

	protected Controller getGiveRideController() {
		return giveRideController;
	}

	 void setGiveRideController(Controller giveRideController) {
		this.giveRideController = giveRideController;
	}

	protected Controller getRouteController() {
		return routeController;
	}

	 void setRouteController(Controller routeController) {
		this.routeController = routeController;
	}

	protected Controller getBookRideController() {
		return bookRideController;
	}

	 void setBookRideController(Controller bookRideController) {
		this.bookRideController = bookRideController;
	}
}
