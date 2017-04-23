package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import models.Car;
import models.Route;
import models.position.StopPoint;
import models.Trip;
import models.database.SQLExecutor;
import org.sqlite.SQLiteException;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class TripController extends Controller implements Initializable {

	@FXML
	private MenuController menuController;
	@FXML
	private RadioButton updateModeRbtn, addModeRbtn;
	@FXML
	private TextField aliasField;
	@FXML
	private DatePicker startDatePicker, endDatePicker;
	@FXML
	private ComboBox<Trip> tripComboBox;
	@FXML
	private ComboBox<Car> carComboBox;
	@FXML
	private ComboBox<Route> routeComboBox;
	@FXML
	private ComboBox<String> directionComboBox, timeIndicatorComboBox, hourComboBox, minuteComboBox;
	@FXML
	private CheckBox mondayCheckBox, tuesdayCheckBox, wednesdayCheckBox, thursdayCheckBox,
	fridayCheckBox, saturdayCheckBox, sundayCheckBox;
	@FXML
	private Button submitBtn, deleteBtn, setSPTimeBtn;
	@FXML
	private GridPane setTimePane, recurrencePane;
	@FXML
	private TableView<StopPoint> SPTable;
	@FXML
	private TableColumn<StopPoint, String> timeCol, streetNoCol, streetCol, suburbCol, cityCol;

	private List<Trip> trips;
	private List<StopPoint> stopPoints;
	private List<Route> routes;
	private List<Car> cars;

	private enum Mode {ADD_MODE, UPDATE_MODE}
	private Mode mode;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		trips = new ArrayList<>();
		stopPoints = new ArrayList<>();
		routes = new ArrayList<>();
		cars = new ArrayList<>();

		setSPTimeBtn.setOnAction(event -> clickSetSPTimeBtn());
		submitBtn.setOnAction(event -> clickSubmitBtn());
		deleteBtn.setOnAction(event -> clickDeleteBtn());
		addModeRbtn.setOnAction(event -> {mode = Mode.ADD_MODE; loadTrips();});
		updateModeRbtn.setOnAction(event -> {mode = Mode.UPDATE_MODE; loadTrips();});
		routeComboBox.setOnAction(event -> {if (routeComboBox.getValue() != null) loadStopPoints();});
		tripComboBox.setOnAction(event -> {if (tripComboBox.getValue() != null) loadTripDetail();});

		directionComboBox.getItems().add("To UC");
		directionComboBox.getItems().add("From UC");
		directionComboBox.getSelectionModel().selectFirst();

		// set time combo box
		for (int i = 0; i <= 11; i++) {
			hourComboBox.getItems().add(String.format("%02d", i));
		}
		for (int i =0; i <= 59; i++) {
			minuteComboBox.getItems().add(String.format("%02d", i));
		}
		timeIndicatorComboBox.getItems().addAll("AM", "PM");
	}

	@Override
	protected void afterSetRSS() {
		menuController.setRSS(rss);
		loadTrips();
	}

	private void fetchTrips() {
		trips = SQLExecutor.fetchTripsByUser(rss.getUser());
	}

	private void fetchRoutes() {
		routes = SQLExecutor.fetchRoutesByUser(rss.getUser());
	}

	private void fetchCars() {
		cars = SQLExecutor.fetchCarsByUser(rss.getUser());
	}

	/**
	 * loads stop points according to current mode. if it is view mode, than lists
	 * trip_sp. otherwise, list route_sp.
	 */
	private void fetchStopPoints() {
		if (mode == Mode.ADD_MODE && routeComboBox.getValue() != null) {
			stopPoints = SQLExecutor.fetchStopPointsByRoute(routeComboBox.getValue());
		} else if (mode == Mode.UPDATE_MODE && tripComboBox.getValue() != null){
			stopPoints = SQLExecutor.fetchStopPointsByTrip(tripComboBox.getValue());
		}
	}

	private void loadTrips() {
		fetchTrips();
		refreshTripsComboBox();
		loadTripDetail();
	}

	private void loadTripDetail() {
		// no matter which mode, it always needs to load cars, routes
		loadCars();
		loadRoutes();
		// no trip to load
		if (tripComboBox.getValue() == null || mode == Mode.ADD_MODE) {
			loadStopPoints();
			addTripMode();
		}
		// there are trips to load
		else {
			updateTripMode();
			loadStopPoints();
			Trip trip = tripComboBox.getValue();
			directionComboBox.setValue(trip.getDirection());
			// set date picker
			startDatePicker.setValue(trip.getBeginDate());
			endDatePicker.setValue(trip.getExpireDate());
			carComboBox.getSelectionModel().select(getCarByCarId(trip.getCarId()));
			routeComboBox.getSelectionModel().select(getRouteByRouteId(trip.getRouteId()));
			setRecurrenceCheckBox(trip.getDay());
		}
	}

	private Car getCarByCarId(int carId) {
		for (Car car : cars) {
			if (carId == car.getCarId()) return car;
		}
		return null;
	}

	private Route getRouteByRouteId(int routeId) {
		for (Route route : routes) {
			if (routeId == route.getRouteId()) return route;
		}
		return null;
	}

	private void loadRoutes() {
		fetchRoutes();
		refreshRouteComboBox();
	}

	private void loadCars() {
		fetchCars();
		refreshCarComboBox();
	}

	private void loadStopPoints() {
		fetchStopPoints();
		fillSPTable();
	}

	private void refreshTripsComboBox() {
		tripComboBox.getItems().clear();
		tripComboBox.getItems().addAll(trips);
		tripComboBox.getSelectionModel().selectLast();
	}

	private void refreshRouteComboBox() {
		routeComboBox.getItems().clear();
		routeComboBox.getItems().addAll(routes);
	}

	private void refreshCarComboBox() {
		carComboBox.getItems().clear();
		carComboBox.getItems().addAll(cars);
	}

	private void addTripMode() {
		mode = Mode.ADD_MODE;
		addModeRbtn.setSelected(true);
		deleteBtn.setVisible(false);
		submitBtn.setDisable(false);

		SPTable.getItems().clear();
		clearRecurrenceCheckBox();

		tripComboBox.setVisible(false);
		aliasField.setVisible(true);

		aliasField.clear();
		startDatePicker.setValue(LocalDate.now());
		endDatePicker.setValue(LocalDate.now().plusDays(1));

		// temporary setting as update and deleting features are postponed
		setTimePane.setVisible(true);
		recurrencePane.setDisable(false);
		directionComboBox.setDisable(false);
		carComboBox.setDisable(false);
		routeComboBox.setDisable(false);
		startDatePicker.setDisable(false);
		endDatePicker.setDisable(false);
	}

	private void updateTripMode() {
		mode = Mode.UPDATE_MODE;
		updateModeRbtn.setSelected(true);
		deleteBtn.setVisible(true);
		submitBtn.setDisable(true);

		tripComboBox.setVisible(true);
		aliasField.setVisible(false);

		// temporary setting as update and deleting features are postponed
		setTimePane.setVisible(false);
		recurrencePane.setDisable(true);
		directionComboBox.setDisable(true);
		carComboBox.setDisable(true);
		routeComboBox.setDisable(true);
		startDatePicker.setDisable(true);
		endDatePicker.setDisable(true);
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

	private void fillSPTable() {
		SPTable.getItems().clear();
		ObservableList<StopPoint> stopPointObservableList = FXCollections.observableList(stopPoints);

		timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
		streetNoCol.setCellValueFactory(new PropertyValueFactory<>("streetNo"));
		streetCol.setCellValueFactory(new PropertyValueFactory<>("street"));
		suburbCol.setCellValueFactory(new PropertyValueFactory<>("suburb"));
		cityCol.setCellValueFactory(new PropertyValueFactory<>("city"));
		SPTable.setItems(stopPointObservableList);
	}

	private void clickSetSPTimeBtn() {
		if (SPTable.getSelectionModel().getSelectedItem() != null) {
			StopPoint stopPoint = SPTable.getSelectionModel().getSelectedItem();
			String time = String.format("%s:%s %s",
					hourComboBox.getValue(),
					minuteComboBox.getValue(),
					timeIndicatorComboBox.getValue());
			if (validateArrivingTime(stopPoint)) {
				stopPoint.setTime(time);
			} else {
				rss.showErrorDialog("Failed To Add Arriving Time!",
						"Arriving time should be differ from each other by at least 5 minutes.");
			}
		}
	}

	/**
	 * check if the arriving time is differ from each other at least 5 minutes.
	 * @return true if time is valid.
	 */
	private boolean validateArrivingTime(StopPoint currentSP) {
		try {
			int time = Integer.valueOf(hourComboBox.getValue()) * 60;
			time += Integer.valueOf(minuteComboBox.getValue());
			time += timeIndicatorComboBox.getValue().equals("PM") ? 12 * 60 : 0;
			for (StopPoint stopPoint : stopPoints) {
				if (stopPoint.getTime() != null && !stopPoint.getTime().equals("") && stopPoint != currentSP) {
					String[] spTimeString = stopPoint.getTime().split("[ :]+");
					int spTime = Integer.valueOf(spTimeString[0]) * 60;
					spTime += Integer.valueOf(spTimeString[1]);
					spTime += spTimeString[2].equals("PM") ? 12 * 60 : 0;

					if (time <= spTime + 5 && time >= spTime - 5) {
						return false;
					}
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private void clickSubmitBtn() {
		if (validateFields() && mode == Mode.ADD_MODE) {
			int result;

			Trip trip = new Trip();
			trip.setAlias(aliasField.getText());
			trip.setUsername(rss.getUser().getUsername());
			trip.setRouteId(routeComboBox.getValue().getRouteId());
			trip.setDirection(directionComboBox.getValue());
			trip.setCarId(carComboBox.getValue().getCarId());
			trip.setBeginDate(startDatePicker.getValue());
			trip.setExpireDate(endDatePicker.getValue());
			trip.setDay(getRecurrence());
			trip.setStopPoints(stopPoints);

			result = SQLExecutor.addTrip(trip);

			if (result == 1) {
				rss.showInformationDialog("Operation Succeeded!", "The trip has been created.");
				loadTrips();
			}
		}
	}

	private void clickDeleteBtn() {
		int result;
		String errorMsg;
		try {
			result = SQLExecutor.deleteTrip(tripComboBox.getValue());
			if (result == 1) {
				rss.showInformationDialog("Deletion Succeeded!", "The trip has been deleted.");
				loadTrips();
			} else {
				errorMsg = "Deletion failed with unknown reason, please contact administrator.\n";
				rss.showErrorDialog("Deletion Failed!", errorMsg);
			}
		} catch (SQLiteException e) {
			// if error code is 1811, which means sql foreign key constraint is violated.
			if (e.getResultCode().code == 1811) {
				errorMsg = "Before delete the trip, you must delete all rides associated with this trip.\n" +
						"(Error code: 1811. Database foreign key constraint has been violated.)\n";
			} else {
				errorMsg = "Deletion failed with unknown reasons, please contact administrator.\n";
			}
			rss.showErrorDialog("Deletion Failed!", errorMsg);
		}
	}

	/**
	 * Using bitmask to flag all recurrence of a week.
	 * @return bitmask int
	 */
	private int getRecurrence() {
		int recurrence = 0;
		if (mondayCheckBox.isSelected()) recurrence |= 1;
		if (tuesdayCheckBox.isSelected()) recurrence |= 1 << 1;
		if (wednesdayCheckBox.isSelected()) recurrence |= 1 << 2;
		if (thursdayCheckBox.isSelected()) recurrence |= 1 << 3;
		if (fridayCheckBox.isSelected()) recurrence |= 1 << 4;
		if (saturdayCheckBox.isSelected()) recurrence |= 1 << 5;
		if (sundayCheckBox.isSelected()) recurrence |= 1 << 6;

		return recurrence;
	}

	/**
	 * Using recurrence bitmask to set up recurrent check boxes.
	 * @param recurrence a bitmask represents the recurrence.
	 */
	private void setRecurrenceCheckBox(int recurrence) {
		mondayCheckBox.setSelected((recurrence & 1) != 0);
		tuesdayCheckBox.setSelected((recurrence & (1 << 1)) != 0);
		wednesdayCheckBox.setSelected((recurrence & (1 << 2)) != 0);
		thursdayCheckBox.setSelected((recurrence & (1 << 3)) != 0);
		fridayCheckBox.setSelected((recurrence & (1 << 4)) != 0);
		saturdayCheckBox.setSelected((recurrence & (1 << 5)) != 0);
		sundayCheckBox.setSelected((recurrence & (1 << 6)) != 0);
	}

	/**
	 * check if all field have been filled.
	 *
	 * @return true if all fields are valid
	 */
	private boolean validateFields() {
		List<String> errorMsg = new ArrayList<>();

		if (aliasField.getText().equals(""))
			errorMsg.add("Alias cannot be left empty.\n");

		if (carComboBox.getValue() == null)
			errorMsg.add("A car should be assigned to the trip.\n");

		if (routeComboBox.getValue() == null)
			errorMsg.add("A route should be assigned to the trip.\n");

		for (StopPoint stopPoint : stopPoints) {
			if (stopPoint.getTime() == null || stopPoint.getTime().equals(""))
				errorMsg.add("You need to set an arriving time for '" + stopPoint.toString() + "'.\n");
		}

		/* TODO: check during the recurring period, if user doesn't select any day. */
		// consistency check
		// WOF & registration cannot be expired before the end date of recurring trip
		Car car = carComboBox.getValue();
		if (endDatePicker.getValue() != null && startDatePicker.getValue() != null && car != null) {
			if (endDatePicker.getValue().isBefore(startDatePicker.getValue())) {
				errorMsg.add("Trip end date should be after start date.\n");
			}
			if (LocalDate.parse(car.getWof()).isBefore(endDatePicker.getValue()) ||
			LocalDate.parse(car.getRegistration()).isBefore(endDatePicker.getValue())) {
				errorMsg.add(String.format("The expiration date of a recurring trip cannot occur after the expiry date of car's WOF and registration.\n" +
								"------------------------------------------------\n" +
								"Details for car [%s]:\n" +
								"   a. WOF expire date is %s.\n" +
								"   b. Registration expire date is %s.\n" +
								"------------------------------------------------\n",
						car.getPlate(), car.getWof(), car.getRegistration()));
			}
		}

		// handle and show error message in dialog
		if (errorMsg.size() != 0) {
			StringBuilder errorString = new StringBuilder("Operation failed is caused by: \n");
			for (Integer i = 1; i <= errorMsg.size(); i++) {
				errorString.append(i.toString());
				errorString.append(". ");
				errorString.append(errorMsg.get(i - 1));
			}
			String headMsg = mode == Mode.ADD_MODE ? "Failed to add the trip." : "Failed to update the trip.";
			rss.showErrorDialog(headMsg, errorString.toString());
			return false;
		}
		return true;
	}
}
