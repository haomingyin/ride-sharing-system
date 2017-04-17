package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import models.Trip;
import models.database.SQLExecutor;
import models.ride.Ride;
import models.ride.RideInstance;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ViewRideController extends Controller implements Initializable {

	@FXML
	private ComboBox<Trip> tripComboBox;
	@FXML
	private ToggleButton allTripsToggle;
	@FXML
	private TableView<RideInstance> passengerTable;
	@FXML
	private TableColumn<RideInstance, String> addressCol, timeCol, passengerCol, statusCol;
	@FXML
	private TableView<Ride> rideTable;
	@FXML
	private TableColumn<Ride, String> tripCol, dateCol, directionCol;
	@FXML
	private TableColumn<Ride, Integer> seatNoCol, seatBookedCol;
	@FXML
	private DatePicker beginDatePicker, endDatePicker;
	@FXML
	private CheckBox toUCCheckBox, fromUCCheckBox, passengerCheckBox, noPassengerCheckBox;
	@FXML
	private GridPane passengerPane;

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

		rideTable.setOnMouseClicked(event -> {
			loadPassengerTable();
		});
	}

	@Override
	protected void afterSetRSS() {
		loadTrips();
		loadRideTable();
	}

	void loadTrips() {
		trips = SQLExecutor.fetchTripsByUser(rss.getUser());
		tripComboBox.getItems().clear();
		tripComboBox.getItems().addAll(trips.values());
	}

	private void loadRideTable() {
		passengerPane.setVisible(false);
		// every time load ride table, needs to clear both tables
		passengerTable.getItems().clear();
		rideTable.getItems().clear();
		rides = SQLExecutor.fetchRidesByUser(rss.getUser());
		List<Ride> filteredRides = getFilteredRides();

		if (filteredRides != null) {
			ObservableList<Ride> stopPointObservableList = FXCollections.observableList(new ArrayList<>(filteredRides));

			tripCol.setCellValueFactory(new PropertyValueFactory<>("alias"));
			dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
			directionCol.setCellValueFactory(new PropertyValueFactory<>("direction"));
			seatNoCol.setCellValueFactory(new PropertyValueFactory<>("seatNo"));
			seatBookedCol.setCellValueFactory(new PropertyValueFactory<>("seatBooked"));
			rideTable.setItems(stopPointObservableList);
		}

	}

	private void loadPassengerTable() {
		Ride ride = rideTable.getSelectionModel().getSelectedItem();
		if (ride != null) {
			if (ride.getSeatBooked() > 0) {
				passengerPane.setVisible(true);
			} else {
				passengerPane.setVisible(false);
			}
			rideInstances = SQLExecutor.fetchRideInstancesByRide(ride);
			bindRideToInstances(ride);

			if (rideInstances != null) {
				ObservableList<RideInstance> rideInstanceObservableList = FXCollections.observableList(rideInstances);

				addressCol.setCellValueFactory(new PropertyValueFactory<>("fullAddress"));
				timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
				passengerCol.setCellValueFactory(new PropertyValueFactory<>("passengerName"));
				statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
				passengerTable.setItems(rideInstanceObservableList);
			}
		} else {
			passengerPane.setVisible(false);
		}
	}

	private void bindRideToInstances(Ride ride) {
		Trip trip = trips.get(ride.getTripId());
		if (trip.getStopPoints() == null)
			trip.setStopPoints(SQLExecutor.fetchStopPointsByTrip(trip));
		for (RideInstance rs : rideInstances) {
			rs.setRide(ride);
			rs.setStopPoint(trip.getStopPoints().get(rs.getSpId()));
		}
	}

	private List<Ride> getFilteredRides() {
		if ((!allTripsToggle.isSelected() && tripComboBox.getValue() == null) ||
				(!toUCCheckBox.isSelected() && !fromUCCheckBox.isSelected()) ||
				(!passengerCheckBox.isSelected() && !noPassengerCheckBox.isSelected())) {return null;}

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

//	private RideFilter getRideFilter() {
//		RideFilter rideFilter = new RideFilter();
//		rideFilter.setAllTrips(allTripsToggle.isSelected());
//		rideFilter.setTrip(tripComboBox.getValue());
//		rideFilter.setBeginDate(beginDatePicker.getValue());
//		rideFilter.setEndDate(endDatePicker.getValue());
//		rideFilter.setToUC(toUCCheckBox.isSelected());
//		rideFilter.setFromUC(fromUCCheckBox.isSelected());
//		rideFilter.setPassenger(passengerCheckBox.isSelected());
//		rideFilter.setPassenger(noPassengerCheckBox.isSelected());
//		return rideFilter;
//	}
}
