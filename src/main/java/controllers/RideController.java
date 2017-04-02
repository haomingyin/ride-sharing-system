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

import java.net.URL;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class RideController implements Initializable {

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

	private RSS rss;
	private HashMap<Integer, StopPoint> stopPoints;
	private HashMap<Integer, Ride> rides;
	private HashMap<Integer, Trip> trips;
	private ObservableList<Ride> rideObservableList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		rideAddModeRbtn.setOnAction(event -> loadRides());
		rideViewModeRbtn.setOnAction(event -> loadRides());
		tripComboBox.setOnAction(event -> {
			if (tripComboBox.getValue() != null){
				loadTripDetail();
			}
		});
	}

	public RSS getRSS() {
		return rss;
	}

	public void setRSS(RSS rss) {
		this.rss = rss;
		menuController.setRSS(rss);
		loadRides();
	}

	public static HashMap<Integer, Ride> fetchRides(User user, SQLiteConnector connector) {
		HashMap<Integer, Ride> rideHashMap = new HashMap<>();
		try {
			String sql = String.format("SELECT * " +
					"FROM ride " +
					"WHERE username = '%s';", user.getUsername());
			ResultSet rs = connector.executeSQLQuery(sql);
			while (!rs.isClosed() && rs.next()) {
				Ride ride = new Ride(rs.getInt("rideId"),
						rs.getInt("tripId"),
						rs.getString("alias"),
						rs.getInt("seatNo"));

				// alias must be unique!!!!
				rideHashMap.put(ride.getTripId(), ride);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rideHashMap;
	}

	private void loadRides() {
		rides = fetchRides(rss.getUser(), rss.getSqLiteConnector());
		refreshRideTable();
		loadRideDetail();
	}

	private void refreshRideTable() {
		rideTable.getItems().clear();
		rideObservableList = FXCollections.observableList(new ArrayList<Ride>(rides.values()));
		rideTable.setItems(rideObservableList);
		rideAliasCol.setCellValueFactory(new PropertyValueFactory<Ride, String>("alias"));
		rideTable.getSelectionModel().selectFirst();
	}

	private void loadRideDetail() {
		loadTrips();
		loadTripDetail();
		if (rideTable.getItems().size() == 0 || rideAddModeRbtn.isSelected()) {
			addRideMode();
			Trip trip = tripComboBox.getValue();
			rideInfoText.setText(String.format("This trip has been scheduled between " +
							"%s and %s, set off %s.",
					trip.getBeginDate().toString(),
					trip.getExpireDate().toString(),
					trip.getDirection()));
		} else {
			updateRideMode();
		}
	}

	private void addRideMode() {
		rideAddModeRbtn.setSelected(true);
		rideDetailPane.setDisable(false);
		rideTable.setDisable(true);
	}

	private void updateRideMode() {
		rideViewModeRbtn.setSelected(true);
		rideDetailPane.setDisable(true);
		rideTable.setDisable(false);

	}

	private void loadTrips() {
		trips = TripController.fetchTrips(rss.getUser(), rss.getSqLiteConnector());
		refreshTripComboBox();
	}

	private void loadTripDetail() {
		refreshSeatComboBox();
		fillSPTable(fetchStopPoints());
	}

	private void refreshTripComboBox() {
		tripComboBox.getItems().clear();
		ObservableList<Trip> trips1 = FXCollections.observableList(new ArrayList<>(trips.values()));
		tripComboBox.setItems(trips1);
		tripComboBox.getSelectionModel().selectFirst();
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

	}

	private void refreshSeatComboBox() {
		seatComboBox.getItems().clear();
		if (tripComboBox.getValue() != null) {
			Trip trip = tripComboBox.getValue();
			Car car = fetchCar(trip.getPlate(), rss.getSqLiteConnector());
			for (int i = 1; i <= car.getSeatNo(); i++) {
				seatComboBox.getItems().add(i);
			}
			seatComboBox.getSelectionModel().selectFirst();
		}

		if (rideViewModeRbtn.isSelected() && rideTable.getSelectionModel().getSelectedItem() != null) {
			seatComboBox.setValue(rideTable.getSelectionModel().getSelectedItem().getSeatNo());
		}

	}

	public static Car fetchCar(String plate, SQLiteConnector connector) {
		Car car = null;
		try {
			String sql = String.format("SELECT * FROM car WHERE plate = '%s';", plate);
			ResultSet rs = connector.executeSQLQuery(sql);
			if (!rs.isClosed() && rs.next()) {
				car = new Car(rs.getString("plate"),
						rs.getString("username"),
						rs.getString("model"),
						rs.getString("manufacturer"),
						rs.getString("color"),
						rs.getInt("year"),
						rs.getInt("seatNo"));
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
			String sql = String.format("SELECT * " +
					"FROM stop_point JOIN trip_sp ON stop_point.spId = trip_sp.spId " +
					"WHERE tripId = %d", tripComboBox.getValue().getTripId());

			ResultSet rs = rss.getSqLiteConnector().executeSQLQuery(sql);
			if (!rs.isClosed() && rs.next()) {
				StopPoint stopPoint = new StopPoint(rs.getInt("spId"),
						rs.getString("streetNo"),
						rs.getString("street"),
						rs.getString("suburb"),
						rs.getString("city"));
				stopPoint.setTime(rs.getString("time"));

				stopPoints.add(stopPoint);
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
}
