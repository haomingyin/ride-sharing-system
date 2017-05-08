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
import models.Car;
import models.User;
import models.database.SQLExecutor;
import models.notification.Notification;
import models.ride.RideInstance;
import models.ride.Status;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
	@FXML
	private TableColumn<RideInstance, Number> priceCol;

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
			timeCol.setCellValueFactory(cell -> cell.getValue().getStopPoint().timeProperty());
			statusCol.setCellValueFactory(cell -> cell.getValue().statusProperty());
			priceCol.setCellValueFactory(cell -> cell.getValue().priceProperty());
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
								if (!Status.BOOKED.equals(rideInstance.getStatus())) {
									cancelBtn.setDisable(true);
								}

								detailBtn.setOnAction(event -> clickDetailBtn(rideInstance));
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

	private void clickDetailBtn(RideInstance ri) {
		StringBuilder sb = new StringBuilder();
		User driver = SQLExecutor.fetchUser(ri.getUsername());
		// driver info
		sb.append("---------- DRIVER INFO ----------\n");
		sb.append(String.format("Driver name: %s\n", driver.getName()));
		sb.append(String.format("Email: %s\n", driver.getEmail()));
		sb.append(String.format("Home phone: %s\n", driver.gethPhone()));
		sb.append(String.format("Mobile phone: %s\n\n", driver.getmPhone()));

		// car detail
		Car car = ri.getTrip().getCar();
		sb.append("----------- CAR DETAIL -----------\n");
		sb.append(String.format("Plate: %s\n", car.getPlate()));
		sb.append(String.format("Model: %s\n", car.getModel()));
		sb.append(String.format("Color: %s\n", car.getColor()));
		sb.append(String.format("Year: %d\n", car.getYear()));
		sb.append(String.format("Performance: %.2f km/litre\n\n", car.getPerformance()));

		// ride status
		sb.append("----------- RIDE STATUS -----------\n");
		sb.append(String.format("Distance: %.2f km\n", ri.getStopPoint().getDistance()));
		sb.append(String.format("Status: %s\n", ri.getStatus()));
		sb.append(String.format("Comment: %s\n\n", ri.getComment()));

		rss.showInformationDialog("Ride Detail", sb.toString());
	}

	private void clickCancelBtn(RideInstance ri, Button btn) {
		String errorMsg;

		boolean confirmed = true;
		// check if now is 2 hours before the ride scheduled time
		if ((LocalDate.now().isEqual(ri.getLocalDate())
				&& LocalTime.now().isAfter(ri.getStopPoint().getLocalTime().minusHours(2)))
				|| LocalDate.now().isAfter(ri.getLocalDate())) {
			confirmed = showConfirmationDialog();
		}

		if (confirmed) {
			String comment = showInputDialog();
			if (comment != null && comment.length() <= 20) {
				rss.showErrorDialog("Cancellation Failed!",
						"Please enter at least 20 characters for your reason.");
			} else if (comment != null){
				ri.setPassengerId(rss.getUser().getUsername());

				if (SQLExecutor.updateRideInstanceStatus(ri, Status.CANCELLED_BY_PASSENGER) == 1) {
					sendNotification(ri);
					rss.showInformationDialog("Cancellation Succeeded!", "You have successfully cancelled this ride.");
					ri.setStatus(Status.CANCELLED_BY_PASSENGER.toString());
					btn.setDisable(true);
				} else {
					errorMsg = "Cancellation failed with unknown reason, please contact administrator.\n";
					rss.showErrorDialog("Cancellation Failed!", errorMsg);
				}
			}
		}
	}

	/**
	 * Filters ride instances according to users' option
	 *
	 * @return a filtered ride instances list
	 */
	private List<RideInstance> getFilteredRideInstances() {
		if ((!toUCCheckBox.isSelected() && !fromUCCheckBox.isSelected()) ||
				(!bookedCheckBox.isSelected() && !cancelledCheckBox.isSelected() && !doneCheckBox.isSelected())) {
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
			if (!(bookedCheckBox.isSelected() && Status.BOOKED.equals(status))
					&& !(cancelledCheckBox.isSelected() && Status.CANCELLED.equals(status))
					&& !(doneCheckBox.isSelected() && Status.DONE.equals(status))) {
				continue;
			}

			filteredRideInstances.add(ri);
		}

		return filteredRideInstances;
	}

	private String showInputDialog() {
		TextInputDialog dialog = new TextInputDialog("");
		dialog.setTitle("Cancel Ride");
		dialog.setHeaderText("Please enter the reason for cancelling this ride.");

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) return result.get();
		return null;
	}

	private boolean showConfirmationDialog() {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Confirmation of Cancelling Ride");
		alert.setHeaderText("Are you sure to cancel this ride?");
		alert.setContentText("Cancelling a ride less than 2 hours before the ride time will lower your evaluation.");

		return alert.showAndWait().get() == ButtonType.OK;
	}

	private void sendNotification(RideInstance ri) {
		Notification no = new Notification();
		no.setRecipient(ri.getUsername());
		no.setMessage(String.format("A passenger has cancelled the ride on %s %s.",
				ri.getDate(), ri.getStopPoint().getTime()));
		SQLExecutor.addNotification(no);
	}
}
