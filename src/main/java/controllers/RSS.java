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
	private MenuController menuController;
	private CarController carController;
	private TripController tripController;
	private RideController rideController;
	private RouteController routeController;
	private SQLConnector sqlConnector;
	private BookRideController bookRideController;

	public void initialize() {
		user = new User();
		// close all other windows when primary stage is closed
		pStage.setOnCloseRequest(e -> Platform.exit());
		pStage.setTitle("UC RSS");
		sqlConnector = new SQLConnector();
		sqlConnector.initializeDatabase();
		showLoginView();
	}

	public void showLoginView() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoginView.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root, 1200, 800);
			LoginController loginController = loader.getController();
			loginController.setRSS(this);
			pStage.setScene(scene);
			pStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showSignUpView() {
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

	public void showProfile() {
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

	public void showCarView() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/CarView.fxml"));
			Parent carView = fxmlLoader.load();
			Scene scene = new Scene(carView);
			CarController carController = fxmlLoader.getController();
			setCarController(carController);
			carController.setRSS(this);
			pStage.setScene(scene);
			pStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showRouteView() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/RouteView.fxml"));
			Parent View = fxmlLoader.load();
			Scene scene = new Scene(View);
			RouteController Controller = fxmlLoader.getController();
			setRouteController(Controller);
			Controller.setRSS(this);
			pStage.setScene(scene);
			pStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showTripView() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/TripView.fxml"));
			Parent View = fxmlLoader.load();
			Scene scene = new Scene(View);
			TripController controller = fxmlLoader.getController();
			setTripController(controller);
			controller.setRSS(this);
			pStage.setScene(scene);
			pStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showRideView() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/RideView.fxml"));
			Parent View = fxmlLoader.load();
			Scene scene = new Scene(View);
			RideController controller = fxmlLoader.getController();
			setRideController(controller);
			controller.setRSS(this);
			pStage.setScene(scene);
			pStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showBookRideView() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/BookRideView.fxml"));
			Parent View = fxmlLoader.load();
			Scene scene = new Scene(View);
			BookRideController controller = fxmlLoader.getController();
			setBookRideController(controller);
			controller.setRSS(this);
			pStage.setScene(scene);
			pStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showErrorDialog(String headMsg, String errorMsg) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("RSS Error Dialog");
		alert.setHeaderText(headMsg);
		alert.setContentText(errorMsg);
		alert.getDialogPane().setPrefWidth(600);
		alert.showAndWait();
	}

	public void showInformationDialog(String headMsg, String infoMsg) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("RSS Information Dialog");
		alert.setHeaderText(headMsg);
		alert.setContentText(infoMsg);
		alert.getDialogPane().setPrefWidth(500);
		alert.showAndWait();
	}

	public void showWarningDialog(String headMsg, String warnMsg) {
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

	public SQLConnector getSqlConnector() {
		return sqlConnector;
	}

	public void setSqlConnector(SQLConnector sqlConnector) {
		this.sqlConnector = sqlConnector;
	}

	public TripController getTripController() {
		return tripController;
	}

	public void setTripController(TripController tripController) {
		this.tripController = tripController;
	}

	public RideController getRideController() {
		return rideController;
	}

	public void setRideController(RideController rideController) {
		this.rideController = rideController;
	}

	public RouteController getRouteController() {
		return routeController;
	}

	public void setRouteController(RouteController routeController) {
		this.routeController = routeController;
	}

	public BookRideController getBookRideController() {
		return bookRideController;
	}

	public void setBookRideController(BookRideController bookRideController) {
		this.bookRideController = bookRideController;
	}
}
