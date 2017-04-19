package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.util.Callback;
import models.database.SQLExecutor;
import models.ride.RideInstance;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BookedRideController extends Controller implements Initializable {

	@FXML
	private DatePicker beginDatePicker;
	@FXML
	private DatePicker endDatePicker;
	@FXML
	private CheckBox toUCCheckBox;
	@FXML
	private CheckBox fromUCCheckBox;
	@FXML
	private CheckBox bookedCheckBox;
	@FXML
	private CheckBox cancelledCheckBox;
	@FXML
	private CheckBox doneCheckBox;
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
	private TableColumn<RideInstance, String> statusCol;

	private List<RideInstance> rideInstances;

	@Override

	public void initialize(URL location, ResourceBundle resources) {
		rideInstances = new ArrayList<>();
		beginDatePicker.setOnAction(event -> fillTable());
		endDatePicker.setOnAction(event -> fillTable());
		toUCCheckBox.setOnAction(event -> fillTable());
		fromUCCheckBox.setOnAction(event -> fillTable());
		bookedCheckBox.setOnAction(event -> fillTable());
		cancelledCheckBox.setOnAction(event -> fillTable());
		doneCheckBox.setOnAction(event -> fillTable());
	}

	@Override
	protected void afterSetRSS() {
		loadRideInstances();
	}

	private void loadRideInstances() {
		rideInstances = SQLExecutor.fetchBookedRideInstanceByUser(rss.getUser());
		fillTable();
	}

	private void fillTable() {
		table.getItems().clear();
		List<RideInstance> filteredRideInstances = getFilteredRideInstances();
		if (filteredRideInstances != null) {
			ObservableList<RideInstance> rideInstanceObservableList =
					FXCollections.observableList(filteredRideInstances);

			addressCol.setCellValueFactory(cell -> cell.getValue().getStopPoint().fullProperty());
			directionCol.setCellValueFactory(cell -> cell.getValue().getTrip().directionProperty());
			dateCol.setCellValueFactory(cell -> cell.getValue().dateProperty());
			timeCol.setCellValueFactory(cell -> cell.getValue().timeProperty());
			statusCol.setCellValueFactory(cell -> cell.getValue().statusProperty());
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
								Button cancelBtn = new Button("Cancel");

								RideInstance rideInstance = getTableView().getItems().get(getIndex());
								if (!rideInstance.getStatus().equals("Booked")) {
									cancelBtn.setText("Cancelled");
									cancelBtn.setDisable(true);
								}

								cancelBtn.setOnAction(event -> clickCancelBtn(rideInstance, cancelBtn));

								detailBtn.setFont(Font.font(9));
								cancelBtn.setFont(Font.font(9));
								hbox.getChildren().addAll(detailBtn, cancelBtn);
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

	private void clickCancelBtn(RideInstance rideInstance, Button btn) {
		String errorMsg;
		rideInstance.setPassengerId(rss.getUser().getUsername());

		if (SQLExecutor.updateRideByRideInstance(rideInstance, 2) == 1) {
			rss.showInformationDialog("Cancellation Succeeded!", "You have successfully cancelled this ride.");
			rideInstance.setStatus("Passenger Cancelled");
			btn.setText("Cancelled");
			btn.setDisable(true);
		} else {
			errorMsg = "Cancellation failed with unknown reason, please contact administrator.\n";
			rss.showErrorDialog("Cancellation Failed!", errorMsg);
		}
	}

	/**
	 * Filters ride instances according to users' option
	 * @return a filtered ride instances list
	 */
	private List<RideInstance> getFilteredRideInstances() {
		if ((!toUCCheckBox.isSelected() && !fromUCCheckBox.isSelected()) ||
				(!bookedCheckBox.isSelected() && !cancelledCheckBox.isSelected() || !doneCheckBox.isSelected())) {
			return null;
		}

		List<RideInstance> filteredRideInstances = new ArrayList<>();

		for (RideInstance ri : rideInstances) {

			LocalDate date = LocalDate.parse(ri.getDate());
			if (beginDatePicker.getValue() != null && date.isBefore(beginDatePicker.getValue())) {
				continue;
			}

			if (endDatePicker.getValue() != null && date.isAfter(endDatePicker.getValue())) {
				continue;
			}

			String direction = ri.getTrip().getDirection();
			if (!(toUCCheckBox.isSelected() && direction.equals("To UC")) &&
					!(fromUCCheckBox.isSelected() && direction.equals("From UC"))) {
				continue;
			}

			String status = ri.getStatus();
			if (!(bookedCheckBox.isSelected() && status.equals("Booked")) &&
					!(doneCheckBox.isSelected() && status.equals("Done")) &&
					!(cancelledCheckBox.isSelected() && (status.equals("Driver Cancelled") || status.equals("Passenger Cancelled")))) {
				continue;
			}
			filteredRideInstances.add(ri);
		}

		return filteredRideInstances;
	}
}
