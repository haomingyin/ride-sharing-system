package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.StringConverter;
import models.*;
import models.database.SQLConnector;
import models.database.SQLExecutor;

import java.net.URL;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class RideController extends Controller implements Initializable {

	@FXML
	Parent menuView;
	@FXML
	MenuController menuController;
	@FXML
	RadioButton rideViewModeRbtn, rideAddModeRbtn;
	@FXML
	TableView<Ride> rideTable;
	@FXML
	TableColumn<Ride, String> rideAliasCol;
	@FXML
	Text errorText;
	@FXML
	TextField aliasField;
	@FXML
	GridPane rideDetailPane;
	@FXML
	ComboBox<Trip> tripComboBox;
	@FXML
	ComboBox<Integer> seatComboBox;
	@FXML
	Button submitBtn, deleteBtn;
	@FXML
	Text rideInfoText;
	@FXML
	TableView<StopPoint> SPTable;
	@FXML
	TableColumn<StopPoint, String> timeCol, streetNoCol, streetCol, suburbCol, cityCol;

	private HashMap<Integer, StopPoint> stopPoints;
	private HashMap<Integer, Ride> rides;
	private HashMap<Integer, Trip> trips;
	private ObservableList<Ride> rideObservableList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		rideAddModeRbtn.setOnAction(event -> loadRideDetail());
		rideViewModeRbtn.setOnAction(event -> loadRideDetail());
		tripComboBox.setOnAction(event -> {
			if (tripComboBox.getValue() != null){
				loadTripDetail();
			}
		});
		rideTable.getSelectionModel().selectedItemProperty().addListener(event -> loadRideDetail());
	}

	@Override
	protected void afterSetRSS() {
		menuController.setRSS(rss);
		loadRides();
	}

	private void fetchRides() {
		rides = SQLExecutor.fetchRidesByUser(rss.getUser());
	}

	private void loadRides() {
		fetchRides();
		refreshRideTable();
		loadRideDetail();
	}

	private void refreshRideTable() {
		rideTable.getItems().clear();
		rideObservableList = FXCollections.observableList(new ArrayList<>(rides.values()));
		rideTable.setItems(rideObservableList);
		rideAliasCol.setCellValueFactory(new PropertyValueFactory<>("alias"));
		rideTable.getSelectionModel().selectFirst();
	}

	private void loadRideDetail() {
		loadTrips();
		loadTripDetail();
		if (rideTable.getItems().size() == 0 || rideAddModeRbtn.isSelected()) {
			addRideMode();
		} else {
			aliasField.setText(rideTable.getSelectionModel().getSelectedItem().getAlias());
			updateRideMode();
			tripComboBox.setValue(trips.get(rideTable.getSelectionModel().getSelectedItem().getTripId()));
		}
	}

	private void addRideMode() {
		rideAddModeRbtn.setSelected(true);
		rideDetailPane.setDisable(false);
		rideTable.setDisable(true);
		aliasField.setText("");
	}

	private void updateRideMode() {
		rideViewModeRbtn.setSelected(true);
		rideDetailPane.setDisable(true);
		rideTable.setDisable(false);

	}

	private void loadTrips() {
		trips = TripController.fetchTrips(rss.getUser(), rss.getSqlConnector());
		refreshTripComboBox();
	}

	private void loadTripDetail() {
		refreshSeatComboBox();
		fillSPTable(fetchStopPoints());
		Trip trip = tripComboBox.getValue();
		if (trip != null) {
			rideInfoText.setText(String.format("This trip has been scheduled between " +
							"%s and %s, depart %s.",
					trip.getBeginDate().toString(),
					trip.getExpireDate().toString(),
					trip.getDirection()));
		}
	}

	private void refreshTripComboBox() {
		tripComboBox.getItems().clear();
		ObservableList<Trip> trips1 = FXCollections.observableList(new ArrayList<>(trips.values()));
		tripComboBox.setItems(trips1);
		tripComboBox.setCellFactory(new Callback<ListView<Trip>, ListCell<Trip>>() {
			@Override
			public ListCell<Trip> call(ListView<Trip> param) {
				return new ListCell<Trip>() {
					@Override
					public void updateItem(Trip trip, boolean empty) {
						super.updateItem(trip, empty);
						if (!empty) {
							setText(trip.getAlias());
							setGraphic(null);
						} else {
							setText(null);
						}
					}
				};
			}
		});
		tripComboBox.setConverter(new StringConverter<Trip>() {
			@Override
			public String toString(Trip object) {
				if (object == null) {
					return null;
				} else {
					return object.getAlias();
				}
			}

			@Override
			public Trip fromString(String string) {
				return null;
			}
		});
		tripComboBox.getSelectionModel().selectFirst();
	}

	private void refreshSeatComboBox() {
		seatComboBox.getItems().clear();
		if (tripComboBox.getValue() != null) {
			Trip trip = tripComboBox.getValue();
			Car car = fetchCar(trip.getCarId(), rss.getSqlConnector());
			for (int i = 0; i <= car.getSeatNo(); i++) {
				seatComboBox.getItems().add(i);
			}
			seatComboBox.getSelectionModel().selectFirst();
		}

		if (rideViewModeRbtn.isSelected() && rideTable.getSelectionModel().getSelectedItem() != null) {
			seatComboBox.setValue(rideTable.getSelectionModel().getSelectedItem().getSeatNo());
		}

	}

	public static Car fetchCar(Integer carId, SQLConnector connector) {
		Car car = null;
		try {
			String sql = String.format("SELECT * FROM car WHERE carId = '%s';", carId);
			ResultSet rs = connector.executeSQLQuery(sql);
			if (!rs.isClosed() && rs.next()) {
				car = new Car();
				car.setCarId(rs.getInt("carId"));
				car.setPlate(rs.getString("plate"));
				car.setUsername(rs.getString("username"));
				car.setModel(rs.getString("model"));
				car.setManufacturer(rs.getString("manufacturer"));
				car.setColor(rs.getString("color"));
				car.setYear(rs.getInt("year"));
				car.setSeatNo(rs.getInt("seatNo"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return car;
		}
	}

	private ArrayList<StopPoint> fetchStopPoints() {
		ArrayList<StopPoint> stopPoints = new ArrayList<>();
		try {
			if (tripComboBox.getValue() != null) {
				String sql = String.format("SELECT * " +
						"FROM stop_point JOIN trip_sp ON stop_point.spId = trip_sp.spId " +
						"WHERE tripId = %d", tripComboBox.getValue().getTripId());

				ResultSet rs = rss.getSqlConnector().executeSQLQuery(sql);
				if (!rs.isClosed() && rs.next()) {
					StopPoint stopPoint = new StopPoint(rs.getInt("spId"),
							rs.getString("streetNo"),
							rs.getString("street"),
							rs.getString("suburb"),
							rs.getString("city"));
					stopPoint.setTime(rs.getString("time"));

					stopPoints.add(stopPoint);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return stopPoints;
		}
	}

	private void fillSPTable(ArrayList<StopPoint> stopPoints) {
		SPTable.getItems().clear();
		ObservableList<StopPoint> stopPointObservableList = FXCollections.observableList(stopPoints);

		timeCol.setCellValueFactory(new PropertyValueFactory<StopPoint, String>("time"));
		streetNoCol.setCellValueFactory(new PropertyValueFactory<StopPoint, String>("streetNo"));
		streetCol.setCellValueFactory(new PropertyValueFactory<StopPoint, String>("street"));
		suburbCol.setCellValueFactory(new PropertyValueFactory<StopPoint, String>("suburb"));
		cityCol.setCellValueFactory(new PropertyValueFactory<StopPoint, String>("city"));
		SPTable.setItems(stopPointObservableList);
	}

	public void clickSubmitBtn() {
		try {
			if (isAllFieldsValid()) {
				String sql = String.format("INSERT INTO ride (alias, tripId, seatNo, username) " +
								"VALUES ('%s', %d, %d, '%s');",
						aliasField.getText(),
						tripComboBox.getValue().getTripId(),
						seatComboBox.getValue(),
						rss.getUser().getUsername());
				int result = rss.getSqlConnector().executeSQLUpdate(sql);
				if (result == 1) {
					errorText.setText("Hooray! Operation succeeded!");
					loadRides();
				} else {
					errorText.setText("Oops, operation failed. Please try it again.");
				}
			} else {
				errorText.setText("Something went wrong, check if you filled all fields.");
			}
			errorText.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isAllFieldsValid() {
		boolean isValid = true;
		if (aliasField.getText().equals("") || tripComboBox.getValue() == null || seatComboBox.getValue() == null) {
			isValid = false;
		}
		return isValid;
	}
}
