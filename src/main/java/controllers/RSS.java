package controllers;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.User;
import models.database.SQLConnector;
import models.database.SQLExecutor;
import models.notification.Notification;
import models.notification.Notifications;
import models.ride.Status;
import org.controlsfx.control.action.Action;

import java.util.*;

public class RSS implements Observer {

	private User user;
	private Stage pStage;
//	private Controller menuController, carController, tripController,
//			giveRideController, routeController, bookRideController;
	private String style;
	private Notifications notificationCenter;
	private Set<Integer> nIds; // record notifications have already been shown but not deleted.

	public void initialize() {
		user = new User();
		style = getClass().getResource("/views/style.css").toExternalForm();
		// close all other windows when primary stage is closed
		pStage.setOnCloseRequest(e -> Platform.exit());
		pStage.setTitle("UC RSS");
		SQLConnector sqlConnector = new SQLConnector();
		sqlConnector.initializeDatabase();
		showLoginView();
	}

	void login() {
		showBookRideView();

		// set up notifications
		notificationCenter = new Notifications(user);
		notificationCenter.addObserver(this);
		nIds = new HashSet<>();
		Thread thread = new Thread(notificationCenter);
		thread.setDaemon(true);
		thread.start();

	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof Notifications) {
			List<Notification> rawNos = (List<Notification>) arg;
			List<Notification> nos = new ArrayList<>();

			// remove notifications that have already been shown but not deleted.
			if (rawNos != null)
				rawNos.forEach(note -> {
					if (!nIds.contains(note.getnId())) {
						nIds.add(note.getnId());
						nos.add(note);
					}
				});

			new Thread(new Runnable() {
				@Override
				public void run() {
					nos.forEach(no -> {

						Action action = new Action("Dismiss", e -> {
							SQLExecutor.deleteNotification(no);
						});

						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								org.controlsfx.control.Notifications.create()
										.title("Notification")
										.text(no.getMessage())
										.hideAfter(Duration.seconds(10))
										.hideCloseButton()
										.action(action)
										.showInformation();
							}
						});
						try {
							Thread.sleep(2000);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					});
				}
			}).start();

//			if (nos.size() > 0) {
//				Platform.runLater(new Runnable() {
//					@Override
//					public void run() {
//						org.controlsfx.control.Notifications.create()
//								.title("Notification")
//								.text(String.format("You have %d new notifications.", nos.size()))
//								.hideAfter(Duration.seconds(40))
//								.showInformation();
//					}
//				});
//			}
		}
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
//			setCarController(carController);
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
			scene.getStylesheets().add(style);
			Controller Controller = fxmlLoader.getController();
//			setRouteController(Controller);
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
			scene.getStylesheets().add(style);
			Controller controller = fxmlLoader.getController();
//			setTripController(controller);
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
			scene.getStylesheets().add(style);
			Controller controller = fxmlLoader.getController();
//			setGiveRideController(controller);
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
			scene.getStylesheets().add(style);
			Controller controller = fxmlLoader.getController();
//			setBookRideController(controller);
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

//	protected void setMenuController(Controller menuController) {
//		this.menuController = menuController;
//	}
//
//	private void setCarController(Controller carController) {
//		this.carController = carController;
//	}
//
//	private void setTripController(Controller tripController) {
//		this.tripController = tripController;
//	}
//
//	private void setGiveRideController(Controller giveRideController) {
//		this.giveRideController = giveRideController;
//	}
//
//	private void setRouteController(Controller routeController) {
//		this.routeController = routeController;
//	}
//
//	private void setBookRideController(Controller bookRideController) {
//		this.bookRideController = bookRideController;
//	}
}
