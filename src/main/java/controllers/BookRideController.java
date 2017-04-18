package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class BookRideController extends Controller implements Initializable {

	@FXML
	private Controller menuController, searchRideController;
	@FXML
	private BookedRideController bookedRideController;
	@FXML
	private Text titleText;
	@FXML
	private Tab bookedTab, searchTab;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		searchTab.setOnSelectionChanged(event -> titleText.setText("Search Rides"));
		bookedTab.setOnSelectionChanged(event -> {
			titleText.setText("My Rides");
			bookedRideController.afterSetRSS();
		});
	}

	@Override
	protected void afterSetRSS() {
		menuController.setRSS(rss);
		searchRideController.setRSS(rss);
		bookedRideController.setRSS(rss);
	}

}

