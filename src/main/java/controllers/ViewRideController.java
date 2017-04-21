package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.util.Callback;
import models.Trip;
import models.database.SQLExecutor;
import models.notification.Notification;
import models.ride.Ride;
import models.ride.RideInstance;
import models.ride.Status;
import org.sqlite.SQLiteException;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class ViewRideController extends Controller implements Initializable {

	@FXML
	private ComboBox<Trip> tripComboBox;
	@FXML
	private ToggleButton allTripsToggle;
	@FXML
	private TableView<RideInstance> passengerTable;
	@FXML
	private TableColumn<RideInstance, String> addressCol, timeCol, passengerCol, statusCol, actionCol;
	@FXML
	private TableView<Ride> rideTable;
	@FXML
	private TableColumn<Ride, String> tripCol, dateCol, directionCol, rideStatusCol, actionRideCol;
	@FXML
	private TableColumn<Ride, Integer> seatNoCol, seatBookedCol;
	@FXML
	private DatePicker beginDatePicker, endDatePicker;
	@FXML
	private CheckBox toUCCheckBox, fromUCCheckBox, passengerCheckBox, noPassengerCheckBox;

	private Map<Integer, Ride> rides;
	private Map<Integer, Trip> trips;
	private List<RideInstance> rideInstances;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		allTripsToggle.setOnAction(event -> {
			if (allTripsToggle.isSelected()) {
				tripComboBox.getSelectionModel().select(null);
				tripComboBox.setDisable(true);
			} else {
				tripComboBox.setDisable(false);
			}
			loadRideTable();
		});
		tripComboBox.setOnAction(e -> loadRideTable());
		beginDatePicker.setOnAction(e -> loadRideTable());
		endDatePicker.setOnAction(e -> loadRideTable());
		toUCCheckBox.setOnAction(e -> loadRideTable());
		fromUCCheckBox.setOnAction(e -> loadRideTable());
		passengerCheckBox.setOnAction(e -> loadRideTable());
		noPassengerCheckBox.setOnAction(e -> loadRideTable());

		rideTable.setOnMouseClicked(event -> loadPassengerTable());
	}

	@Override
	protected void afterSetRSS() {
		loadTrips();
		loadRideTable();
	}

	private void loadTrips() {
		trips = SQLExecutor.fetchTripsByUser(rss.getUser());
		tripComboBox.getItems().clear();
		tripComboBox.getItems().addAll(trips.values());
	}

	private void loadRideTable() {
		// every time load ride table, needs to clear both tables
		passengerTable.getItems().clear();
		rideTable.getItems().clear();
		rides = SQLExecutor.fetchRidesByUser(rss.getUser());
		List<Ride> filteredRides = getFilteredRides();

		if (filteredRides != null) {
			ObservableList<Ride> stopPointObservableList = FXCollections.observableList(new ArrayList<>(filteredRides));

			tripCol.setCellValueFactory(new PropertyValueFactory<>("alias"));
			dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
			directionCol.setCellFactory(new Callback<TableColumn<Ride, String>, TableCell<Ride, String>>() {
				@Override
				public TableCell<Ride, String> call(TableColumn<Ride, String> param) {
					return new TableCell<Ride, String>() {

						@Override
						public void updateItem(String item, boolean empty) {
							super.updateItem(item, empty);

							setText(null);
							setGraphic(null);
							if (!empty) {
								Ride ride = getTableView().getItems().get(getIndex());
								setText(ride.getTrip().getDirection());
							}

						}
					};
				}
			});
			seatNoCol.setCellValueFactory(new PropertyValueFactory<>("seatNo"));
			seatBookedCol.setCellValueFactory(new PropertyValueFactory<>("seatBooked"));
			rideStatusCol.setCellValueFactory(new PropertyValueFactory<>("rideStatus"));
			actionRideCol.setCellFactory(new Callback<TableColumn<Ride, String>, TableCell<Ride, String>>() {
				@Override
				public TableCell<Ride, String> call(TableColumn<Ride, String> param) {
					TableCell<Ride, String> cell = new TableCell<Ride, String>() {
						Button btn = new Button("Cancel");

						@Override
						public void updateItem(String item, boolean empty) {
							super.updateItem(item, empty);

							if (empty) {
								setText(null);
								setGraphic(null);
							} else {
								Ride ride = getTableView().getItems().get(getIndex());
								btn.setOnAction(e -> clickCancelRideBtn(ride, btn));
								btn.setFont(Font.font(9));

								setGraphic(btn);
								setText(null);
							}
						}
					};
					cell.setAlignment(Pos.CENTER);
					return cell;
				}
			});
			rideTable.setItems(stopPointObservableList);
		}

	}

	private void loadPassengerTable() {
		Ride ride = rideTable.getSelectionModel().getSelectedItem();
		if (ride != null) {
			rideInstances = SQLExecutor.fetchRideInstancesByRide(ride);
			bindRideToInstances(ride);

			if (rideInstances != null) {
				ObservableList<RideInstance> rideInstanceObservableList = FXCollections.observableList(rideInstances);

				addressCol.setCellValueFactory(new PropertyValueFactory<>("fullAddress"));
				timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
				passengerCol.setCellValueFactory(new PropertyValueFactory<>("passengerName"));
				statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
				actionCol.setCellFactory(new Callback<TableColumn<RideInstance, String>, TableCell<RideInstance, String>>() {
					@Override
					public TableCell<RideInstance, String> call(TableColumn<RideInstance, String> param) {
						TableCell<RideInstance, String> cell = new TableCell<RideInstance, String>() {

							@Override
							public void updateItem(String item, boolean empty) {
								super.updateItem(item, empty);

								setGraphic(null);
								setText(null);

								if (!empty) {
									HBox hbox = new HBox();
									Button detailBtn = new Button("Detail");
									Button cancelBtn = new Button("Cancel");

									RideInstance rideInstance = getTableView().getItems().get(getIndex());
									if (!Status.BOOKED.equals(rideInstance.getStatus())) {
										cancelBtn.setText("Cancelled");
										cancelBtn.setDisable(true);
									}

									cancelBtn.setOnAction(event -> clickCancelRIBtn(rideInstance, cancelBtn));

									detailBtn.setFont(Font.font(9));
									cancelBtn.setFont(Font.font(9));
									hbox.getChildren().addAll(detailBtn, cancelBtn);
									hbox.setSpacing(10);
									hbox.setAlignment(Pos.CENTER);

									setGraphic(hbox);
								}
							}
						};
						cell.setAlignment(Pos.CENTER);
						return cell;
					}
				});
				passengerTable.setItems(rideInstanceObservableList);
			}
		}
	}

//	private void deleteRide(Ride ride, Button btn) {
//		try {
//			if (SQLExecutor.deleteRide(ride) == 1) {
//				rss.showInformationDialog("Deletion Succeeded!",
//						"You have deleted this ride.");
//				btn.setText("Deleted");
//				btn.setDisable(true);
//			} else {
//				rss.showErrorDialog("Cancellation Failed!",
//						"Please try again or contact the administrator.");
//			}
//		} catch (SQLiteException e) {
//			// if error code is 1811, which means sql foreign key constraint is violated.
//			String errorMsg;
//			if (e.getResultCode().code == 1811) {
//				errorMsg = "You can only delete a ride if it DOES NOT have any booked or cancelled record.\n" +
//						"(Error code: 1811. Database foreign key constraint has been violated.)\n";
//			} else {
//				errorMsg = "Deletion failed with unknown reasons, please contact administrator.\n";
//			}
//			rss.showErrorDialog("Deletion Failed!", errorMsg);
//		}
//	}

	private void clickCancelRideBtn(Ride ride, Button btn) {
		String comment = showInputDialog();

		if (comment != null && comment.length() <= 20) {
			rss.showErrorDialog("Cancellation Failed!",
					"Please enter at least 20 characters for your reason.");
		} else {
			List<RideInstance> rideInstances = SQLExecutor.fetchRideInstancesByRide(ride);
			if (rideInstances != null) {
				for (RideInstance ri : rideInstances) {
					if (!Status.CANCELLED.equals(ri.getStatus())) {
						ri.setComment(comment);
						SQLExecutor.updateRideByRideInstance(ri, Status.CANCELLED_BY_DRIVER);
						sendNotification(ri);
					}
				}
			}
			if (SQLExecutor.updateRide(ride, Status.CANCELLED) == 1) {
				btn.setDisable(true);
				loadPassengerTable();
				rss.showInformationDialog("Cancellation Succeeded!",
						"You have cancelled this ride.");
				ride.setRideStatus(Status.CANCELLED.toString());
			} else {
				rss.showErrorDialog("Cancellation Failed!",
						"Please try again or contact the administrator.");
			}
		}

	}

	private void clickCancelRIBtn(RideInstance ri, Button btn) {
		String comment = showInputDialog();

		if (comment != null && comment.length() <= 20) {
			rss.showErrorDialog("Cancellation Failed!",
					"Please enter at least 20 characters for your reason.");
		} else {
			ri.setComment(comment);
			if (SQLExecutor.updateRideByRideInstance(ri, Status.CANCELLED_BY_DRIVER) == 1) {
				btn.setDisable(true);
				sendNotification(ri);
				rss.showInformationDialog("Cancellation Succeeded!",
						"You have cancelled this ride.");
				ri.setRideStatus(Status.CANCELLED_BY_DRIVER.toString());
			} else {
				rss.showErrorDialog("Cancellation Failed!",
						"Please try again or contact the administrator.");
			}
		}
	}

	private void bindRideToInstances(Ride ride) {
		Trip trip = trips.get(ride.getTripId());
		if (trip.getStopPoints() == null)
			trip.setStopPoints(SQLExecutor.fetchStopPointsByTrip(trip));
		for (RideInstance rs : rideInstances) {
			rs.setStopPoint(trip.getStopPoints().get(rs.getSpId()));
		}
	}

	private List<Ride> getFilteredRides() {
		if ((!allTripsToggle.isSelected() && tripComboBox.getValue() == null) ||
				(!toUCCheckBox.isSelected() && !fromUCCheckBox.isSelected()) ||
				(!passengerCheckBox.isSelected() && !noPassengerCheckBox.isSelected())) {
			return null;
		}

		List<Ride> filteredRides = new ArrayList<>();

		for (Ride ride : rides.values()) {
			if (!allTripsToggle.isSelected() && !(ride.getTripId().equals(tripComboBox.getValue().getTripId()))) {
				continue;
			}

			LocalDate date = LocalDate.parse(ride.getDate());
			if (beginDatePicker.getValue() != null && date.isBefore(beginDatePicker.getValue())) {
				continue;
			}

			if (endDatePicker.getValue() != null && date.isAfter(endDatePicker.getValue())) {
				continue;
			}

			String direction = trips.get(ride.getTripId()).getDirection();
			if (!(toUCCheckBox.isSelected() && direction.equals("To UC")) &&
					!(fromUCCheckBox.isSelected() && direction.equals("From UC"))) {
				continue;
			}

			if (!(passengerCheckBox.isSelected() && ride.getSeatBooked() != 0) &&
					!(noPassengerCheckBox.isSelected() && ride.getSeatBooked() == 0)) {
				continue;
			}
			filteredRides.add(ride);
		}

		return filteredRides;
	}

	private String showInputDialog() {
		TextInputDialog dialog = new TextInputDialog("");
		dialog.setTitle("Cancel Ride");
		dialog.setHeaderText("Please enter the reason for cancelling this ride.");

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) return result.get();
		return null;
	}

	private void sendNotification(RideInstance ri) {
		Notification no = new Notification();
		no.setRecipient(ri.getPassengerId());
		no.setMessage(String.format("Your ride on %s %s has been cancelled by the driver.",
				ri.getDate(), ri.getStopPoint().getTime()));
		SQLExecutor.addNotification(no);
	}

}
