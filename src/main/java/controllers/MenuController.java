package controllers;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController extends Controller implements Initializable {

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@Override
	protected void afterSetRSS() {}

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
