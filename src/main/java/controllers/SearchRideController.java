package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.util.Callback;
import models.database.SQLExecutor;
import models.position.StopPoint;
import models.ride.RideFilter;
import models.ride.RideInstance;
import org.controlsfx.control.textfield.TextFields;
import org.sqlite.SQLiteException;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SearchRideController extends Controller implements Initializable {

	@FXML
	private MapController mapController;

	@FXML
	private GridPane filterPane;
	@FXML
	private DatePicker beginDatePicker;
	@FXML
	private DatePicker endDatePicker;
	@FXML
	private CheckBox toUCCheckBox;
	@FXML
	private CheckBox fromUCCheckBox;
	@FXML
	private Button searchBtn;
	@FXML
	private TableView<RideInstance> table;
	@FXML
	private TableColumn<RideInstance, String> addressCol;
	@FXML
	private TableColumn<RideInstance, String> directionCol;
	@FXML
	private TableColumn<RideInstance, String> dateCol;
	@FXML
	private TableColumn<RideInstance, String> timeCol;
	@FXML
	private TableColumn<RideInstance, String> actionCol;
	@FXML
	private TableColumn<RideInstance, Number> seatCol;

	private TextField addressField;
	private List<RideInstance> rideInstances;

	@Override

	public void initialize(URL location, ResourceBundle resources) {
		rideInstances = new ArrayList<>();
		searchBtn.setOnAction(event -> clickSearchBtn());
		beginDatePicker.setValue(LocalDate.now());
		// autocomplete address finder
		addressField = TextFields.createClearableTextField();
		filterPane.add(addressField, 0, 1, 2, 1);

		TextFields.bindAutoCompletion(addressField,
				event -> SQLExecutor.fetchStopPointsByString(addressField.getText(), 6).values()
		).setPrefWidth(350);
	}

	@Override
	protected void afterSetRSS() {
//		clickSearchBtn();
	}

	private void clickSearchBtn() {
		rideInstances = SQLExecutor.fetchRideInstancesByRideFilter(getRideFilter());
		fillTable();
		drawMarkers();
	}

	private void drawMarkers() {
		if (rideInstances != null && rideInstances.size() != 0) {
			List<StopPoint> stopPoints = new ArrayList<>();
			rideInstances.forEach(ri -> stopPoints.add(ri.getStopPoint()));
			mapController.drawMarkers(stopPoints);
		} else {
			mapController.drawMarkers(null);
		}
	}

	private RideFilter getRideFilter() {
		RideFilter rideFilter = new RideFilter();
		rideFilter.setUsername(rss.getUser().getUsername());
		rideFilter.setSpRequest(addressField.getText());
		rideFilter.setBeginDate(beginDatePicker.getValue());
		rideFilter.setEndDate(endDatePicker.getValue());
		rideFilter.setToUC(toUCCheckBox.isSelected());
		rideFilter.setFromUC(fromUCCheckBox.isSelected());
		return rideFilter;
	}

	private void fillTable() {
		table.getItems().clear();
		if (rideInstances != null) {
			ObservableList<RideInstance> rideInstanceObservableList = FXCollections.observableList(rideInstances);

			addressCol.setCellValueFactory(cell -> cell.getValue().getStopPoint().fullProperty());
			directionCol.setCellValueFactory(cell -> cell.getValue().getTrip().directionProperty());
			dateCol.setCellValueFactory(cell -> cell.getValue().dateProperty());
			timeCol.setCellValueFactory(cell -> cell.getValue().timeProperty());
			seatCol.setCellValueFactory(cell -> cell.getValue().seatLeftProperty());
			actionCol.setCellFactory(new Callback<TableColumn<RideInstance, String>, TableCell<RideInstance, String>>() {
				@Override
				public TableCell<RideInstance, String> call(TableColumn<RideInstance, String> param) {
					return new TableCell<RideInstance, String>() {

						@Override
						public void updateItem(String item, boolean empty) {
							super.updateItem(item, empty);

							setText(null);
							setGraphic(null);

							if (!empty) {
								HBox hbox = new HBox();
								Button detailBtn = new Button("Detail");
								Button bookBtn = new Button("Book");

								bookBtn.setOnAction(event ->
										clickBookBtn(getTableView().getItems().get(getIndex()), bookBtn));

								detailBtn.setFont(Font.font(9));
								bookBtn.setFont(Font.font(9));

								hbox.getChildren().addAll(detailBtn, bookBtn);
								hbox.setSpacing(10);
								hbox.setAlignment(Pos.CENTER);

								setGraphic(hbox);
							}
						}
					};
				}
			});

			table.setItems(rideInstanceObservableList);
		}
	}

	private void clickBookBtn(RideInstance rideInstance, Button btn) {
		String errorMsg;
		try {
			rideInstance.setPassengerId(rss.getUser().getUsername());
			if(SQLExecutor.bookRide(rideInstance) == 1) {
				rss.showInformationDialog("Booking Succeeded!", "You have successfully booked this ride.");

				btn.setText("Booked");
				btn.setDisable(true);
			} else {
				errorMsg = "Booking failed with unknown reason, please contact administrator.\n";
				rss.showErrorDialog("Booking Failed!", errorMsg);
			}
		} catch (SQLiteException e) {
			// if error code is 1811, which means sql foreign key constraint is violated.

			/* TODO: if the ride has been cancelled, give user a option to rebook the ride or change stop point. */

			if (e.getResultCode().code == 1555) {
				errorMsg = "Our system indicates that you have already booked this ride.\n" +
						"(Error code: 1555. Database primary key constraint has been violated.)\n";
			} else {
				errorMsg = "Booking failed with unknown reason, please contact administrator.\n";
			}
			rss.showErrorDialog("Booking Failed!", errorMsg);
		}
	}
}
