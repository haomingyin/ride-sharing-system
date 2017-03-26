package controllers;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

	private RSS rss;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("menu initialized");
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

	public void logout() {
		this.rss.showLoginView();
	}

	public RSS getRSS() {
		return rss;
	}

	public void setRSS(RSS rss) {
		this.rss = rss;
		System.out.println("got rss passed in");
	}

}
