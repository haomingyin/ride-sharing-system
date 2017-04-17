package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import models.Trip;
import models.database.SQLExecutor;
import models.ride.Ride;
import models.ride.RideFilter;
import models.ride.RideInstance;
import org.sqlite.SQLiteException;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class SearchRideController extends Controller implements Initializable {

	@FXML
	private TextField spField;
	@FXML
	private DatePicker beginDatePicker, endDatePicker;
	@FXML
	private CheckBox toUCCheckBox, fromUCCheckBox;
	@FXML
	private Button searchBtn;
	@FXML
	private WebView mapWebView;
	@FXML
	private TableView<RideInstance> table;
	@FXML
	private TableColumn<RideInstance, String> addressCol, directionCol, dateCol, timeCol, actionCol;
	@FXML
	private TableColumn<RideInstance, Integer> seatCol;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		searchBtn.setOnAction(event -> clickSearchBtn());
	}

	@Override
	protected void afterSetRSS() {}

	private void clickSearchBtn() {
		RideFilter rd = getRideFilter();
	}

	private RideFilter getRideFilter() {
		RideFilter rideFilter = new RideFilter();
		rideFilter.setSpRequest(spField.getText());
		rideFilter.setBeginDate(beginDatePicker.getValue());
		rideFilter.setEndDate(endDatePicker.getValue());
		rideFilter.setToUC(toUCCheckBox.isSelected());
		rideFilter.setFromUC(fromUCCheckBox.isSelected());
		return rideFilter;
	}
}
