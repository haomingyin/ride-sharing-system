package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class TripController implements Initializable {

	@FXML
	Parent menuView;
	@FXML
	MenuController menuController;
	@FXML
	RadioButton tripViewModeRbtn, tripAddModeRbtn;
	@FXML
	ListView tripListView;
	@FXML
	TextField tripAliasField, tripStreetNoField, tripStreetField, tripSuburbField, tripCityField, tripTimeField;
	@FXML
	DatePicker tripStartDatePicker, tripEndDatePicker;
	@FXML
	ComboBox tripCarComboBox;
	@FXML
	Button tripSubmitBtn, tripDeleteBtn, tripAddSPBtn;
	@FXML
	TableView tripSPTable;
	@FXML
	TableColumn tripSPTimeCol, tripSPCol;

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
