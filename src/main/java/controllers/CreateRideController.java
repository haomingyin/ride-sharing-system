package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Car;
import models.RecurrenceUtility;
import models.Trip;
import models.database.SQLExecutor;
import models.position.StopPoint;
import models.ride.Ride;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

public class CreateRideController extends Controller implements Initializable {

	@FXML
	private ComboBox<Trip> tripComboBox;
	@FXML
	private ComboBox<Integer> seatComboBox;
	@FXML
	private TextField carField, directionField, beginField, endField;
	@FXML
	private Button submitBtn;
	@FXML
	private TableView<StopPoint> SPTable;
	@FXML
	private TableColumn<StopPoint, String> timeCol, streetNoCol, streetCol, suburbCol, cityCol;
	@FXML
	private CheckBox mondayCheckBox, tuesdayCheckBox, wednesdayCheckBox, thursdayCheckBox,
			fridayCheckBox, saturdayCheckBox, sundayCheckBox;

	private List<StopPoint> stopPoints;
	private List<Trip> trips;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		tripComboBox.setOnAction(event -> {
			if (tripComboBox.getValue() != null) loadTripDetail();
		});
		submitBtn.setOnAction(event -> clickSubmitBtn());
	}

	@Override
	protected void afterSetRSS() {
		loadTrips();
	}

	private void fetchTrips() {
		trips = SQLExecutor.fetchTripsByUser(rss.getUser());
	}

	private void loadTrips() {
		fetchTrips();
		refreshTripComboBox();
		loadTripDetail();
	}

	private void loadTripDetail() {
		Trip trip = tripComboBox.getValue();
		if (trip != null) {
			Car car = SQLExecutor.fetchCarByCarId(trip.getCarId());
			if (car != null) {
				refreshSeatComboBox(car);
				carField.setText(car.getPlate());
			} else {
				carField.setText("null");
			}
			directionField.setText(trip.getDirection());
			beginField.setText(trip.getBeginDate().toString());
			endField.setText(trip.getExpireDate().toString());
			setRecurrenceCheckBox(trip.getDay());
			loadStopPoints();
		}
	}

	private void clearFields() {
		tripComboBox.getSelectionModel().select(null);
		seatComboBox.getSelectionModel().select(null);
		carField.clear();
		directionField.clear();
		beginField.clear();
		endField.clear();
		clearRecurrenceCheckBox();
	}

	private void clearRecurrenceCheckBox() {
		mondayCheckBox.setSelected(false);
		tuesdayCheckBox.setSelected(false);
		wednesdayCheckBox.setSelected(false);
		thursdayCheckBox.setSelected(false);
		fridayCheckBox.setSelected(false);
		saturdayCheckBox.setSelected(false);
		sundayCheckBox.setSelected(false);
	}

	/**
	 * Using recurrence bitmask to set up recurrent check boxes.
	 *
	 * @param recurrence a bitmask represents the recurrence.
	 */
	private void setRecurrenceCheckBox(int recurrence) {
		sundayCheckBox.setSelected((recurrence & (1 << 6)) != 0);
		mondayCheckBox.setSelected((recurrence & 1) != 0);
		tuesdayCheckBox.setSelected((recurrence & (1 << 1)) != 0);
		wednesdayCheckBox.setSelected((recurrence & (1 << 2)) != 0);
		thursdayCheckBox.setSelected((recurrence & (1 << 3)) != 0);
		fridayCheckBox.setSelected((recurrence & (1 << 4)) != 0);
		saturdayCheckBox.setSelected((recurrence & (1 << 5)) != 0);
	}

	private Set<DayOfWeek> parseBitmaskToSet(int bitmask) {
		Set<DayOfWeek> daysOfWeek = new HashSet<>();
		for (int i = 0; i < 7; i++) {
			if ((bitmask & (1 << i)) != 0) {
				daysOfWeek.add(DayOfWeek.of(i + 1));
			}
		}
		return daysOfWeek;
	}

	private void refreshTripComboBox() {
		tripComboBox.getItems().clear();
		tripComboBox.getItems().addAll(trips);
	}

	private void refreshSeatComboBox(Car car) {
		seatComboBox.getItems().clear();
		if (car != null) {
			for (int i = 0; i <= car.getSeatNo(); i++) {
				seatComboBox.getItems().add(i);
			}
			seatComboBox.getSelectionModel().selectFirst();
		}
	}

	private void loadStopPoints() {
		stopPoints = SQLExecutor.fetchStopPointsByTrip(tripComboBox.getValue());
		fillSPTable();
	}

	private void fillSPTable() {
		SPTable.getItems().clear();
		ObservableList<StopPoint> stopPointObservableList = FXCollections.observableList(stopPoints);

		timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
		streetCol.setCellValueFactory(new PropertyValueFactory<>("street"));
		streetNoCol.setCellValueFactory(new PropertyValueFactory<>("streetNo"));
		suburbCol.setCellValueFactory(new PropertyValueFactory<>("suburb"));
		cityCol.setCellValueFactory(new PropertyValueFactory<>("city"));
		SPTable.setItems(stopPointObservableList);
	}

	private void clickSubmitBtn() {
		if (validateFields()) {
			List<Ride> rides = new ArrayList<>();
			Trip trip = tripComboBox.getValue();
			Set<DayOfWeek> recurrence = RecurrenceUtility.parseBitmaskToSet(trip.getDay());
			LocalDate begin = trip.getBeginDate();
			LocalDate end = trip.getExpireDate();

			// matches date with recurrent days
			for (LocalDate now = begin; !now.isAfter(end); now = now.plusDays(1)) {
				// rides before current date and not in recurrence days should not be created.
				if (now.isAfter(LocalDate.now()) && recurrence.contains(now.getDayOfWeek())) {
					Ride ride = new Ride();
					ride.setAlias(trip.getAlias());
					ride.setTripId(trip.getTripId());
					ride.setDate(now.toString());
					ride.setSeatNo(seatComboBox.getValue());
					ride.setUsername(rss.getUser().getUsername());

					rides.add(ride);
				}
			}
			int result = 0;
			if (rides.size() != 0) {
				if ((result = SQLExecutor.addRides(rides)) >= 0) {
					String infoMsg = String.format("%d ride(s) should be created. " +
									"%d ride(s) have been successfully created.\n" +
									"----------------------------------------------------\n" +
									"[%d ride(s) are conflicted with existed ride(s).]\n" +
									"----------------------------------------------------\n",
							rides.size(), result, rides.size() - result);
					rss.showInformationDialog("Operation Succeeded!", infoMsg);
					clearFields();
				}
			}

			if (result < 0) {
				String errorMsg = "Operation failed with unknown reasons, please contact the administrator.";
				rss.showErrorDialog("Operation Failed!", errorMsg);
			}
		}
	}

	private boolean validateFields() {
		List<String> errorMsg = new ArrayList<>();

		if (tripComboBox.getValue() == null) {
			errorMsg.add("Please select a trip to share your rides.\n");
		}

		// handle and show error message in dialog
		if (errorMsg.size() != 0) {
			StringBuilder errorString = new StringBuilder("Operation failed is caused by: \n");
			for (Integer i = 1; i <= errorMsg.size(); i++) {
				errorString.append(i.toString());
				errorString.append(". ");
				errorString.append(errorMsg.get(i - 1));
			}
			String headMsg = "Failed To Share Your Rides.";
			rss.showErrorDialog(headMsg, errorString.toString());
			return false;
		}
		return true;
	}
}
