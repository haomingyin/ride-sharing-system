package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import models.database.SQLConnector;
import models.database.SQLExecutor;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController extends Controller implements Initializable {

	@FXML
	private GridPane driverPane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {}

	@Override
	protected void afterSetRSS() {
		if(this.rss.getUser().getLicenceNo().equals("") || this.rss.getUser().getLicenceNo() == null) {
			driverPane.setVisible(false);
		} else {
			driverPane.setVisible(true);
		}
	}

	public void goToTrips() {
		this.rss.showTripView();
	}

	public void goToCars() {
		this.rss.showCarView();
	}

	public void goToRides() {
		this.rss.showRideView();
	}

	public void goToRoutes() {
		this.rss.showRouteView();
	}

	public void goToBookRides() {
		this.rss.showBookRideView();
	}

	public void goToProfile() {
		this.rss.showProfile();
	}

	public void logout() {

		this.rss.showLoginView();
	}

}
