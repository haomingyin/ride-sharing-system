package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class RideController implements Initializable {

	@FXML
	Parent menuView;
	@FXML
	MenuController menuController;
	@FXML
	RadioButton rideViewModeRbtn, rideAddModeRbtn;
	@FXML
	ListView rideListView;
	@FXML
	TextField rideAliasField;
	@FXML
	ComboBox rideTripComboBox, rideSeatComboBox;
	@FXML
	Button rideSubmitBtn, rideDeleteBtn;
	@FXML
	TableView rideSPTable;
	@FXML
	TableColumn rideSPCol, rideSPTimeCol;

	private RSS rss;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	public RSS getRSS() {
		return rss;
	}

	public void setRSS(RSS rss) {
		this.rss = rss;
		menuController.setRSS(rss);
	}
}
