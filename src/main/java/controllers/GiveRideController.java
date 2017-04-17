package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class GiveRideController extends Controller implements Initializable {

	@FXML
	private Parent menuView, createRideView, viewRideView;
	@FXML
	private Controller menuController, createRideController;
	@FXML
	private ViewRideController viewRideController;
	@FXML
	private Text titleText;
	@FXML
	private Tab createTab, viewTab;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		createTab.setOnSelectionChanged(event -> titleText.setText("Create Rides"));
		viewTab.setOnSelectionChanged(event -> {
			titleText.setText("Manage Rides");
			viewRideController.loadTrips();
		});
	}

	@Override
	protected void afterSetRSS() {
		menuController.setRSS(rss);
		createRideController.setRSS(rss);
		viewRideController.setRSS(rss);
	}

}

