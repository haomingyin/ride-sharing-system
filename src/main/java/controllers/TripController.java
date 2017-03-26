package controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.SQLiteConnector;
import models.StopPoint;
import models.Trip;
import models.User;

import java.net.URL;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class TripController implements Initializable {

	@FXML
	Parent menuView;
	@FXML
	MenuController menuController;
	@FXML
	RadioButton tripViewModeRbtn, tripAddModeRbtn;
	@FXML
	ListView tripListView;
	@FXML
	TextField tripAliasField, tripStreetNoField, tripStreetField, tripSuburbField, tripCityField, tripTimeField;
	@FXML
	DatePicker tripStartDatePicker, tripEndDatePicker;
	@FXML
	ComboBox tripCarComboBox;
	@FXML
	Button tripSubmitBtn, tripDeleteBtn, tripAddSPBtn;
	@FXML
	TableView tripSPTable;
	@FXML
	TableColumn tripSPTimeCol, tripSPCol;

	private RSS rss;
	private HashMap<Integer, Trip> trips;
	private ArrayList<Trip> tripsList;
	private HashMap<Integer, StopPoint> stopPoints;
	private ArrayList<StopPoint> stopPointsList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		trips = new HashMap<>();
		tripsList = new ArrayList<>();
		stopPoints = new HashMap<>();
		tripListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue observable, Object oldValue, Object newValue) {
				if (tripListView.getSelectionModel().getSelectedIndex() >= 0) {
					loadTripDetail();
					loadStopPoints();
				}
			}
		});
		tripAddModeRbtn.setOnAction(event -> loadTrips());
		tripViewModeRbtn.setOnAction(event -> loadTrips());
	}


	public RSS getRSS() {
		return rss;
	}

	public void setRSS(RSS rss) {
		this.rss = rss;
		menuController.setRSS(rss);
		loadTrips();
	}

	public static HashMap<Integer, Trip> fetchTrips(User user, SQLiteConnector connector) {
		HashMap<Integer, Trip> tripHashMap = new HashMap<>();
		try {
			String sql = String.format("SELECT * " +
					"FROM trip " +
					"WHERE username = '%s';", user.getUsername());
			ResultSet rs = connector.executeSQLQuery(sql);
			while (!rs.isClosed() && rs.next()) {
				Trip trip = new Trip(rs.getInt("tripId"),
						rs.getString("alias"),
						rs.getString("username"),
						rs.getString("direction"),
						rs.getString("plate"));

				trip.setBeginDate(LocalDate.parse(rs.getString("beginDate")));
				trip.setExpireDate(LocalDate.parse(rs.getString("expireDate")));
				// alias must be unique!!!!
				tripHashMap.put(trip.getTripId(), trip);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tripHashMap;
	}

	private void loadTrips() {
		trips = fetchTrips(this.rss.getUser(), this.rss.getSqLiteConnector());
		tripsList = new ArrayList<>(trips.values());
		refreshViewList();
		loadTripDetail();
	}

	private void refreshViewList() {
		tripListView.getItems().clear();
		for (Trip trip : tripsList) {
			tripListView.getItems().add(trip.getAlias());
		}
		tripListView.getSelectionModel().selectFirst();
	}

	private void loadTripDetail() {
		if (tripListView.getItems().size() == 0 || tripAddModeRbtn.isSelected()) {
			addTripMode();
			refreshCarComboBox();
		} else {
			updateTripMode();
			Trip trip = tripsList.get(tripListView.getSelectionModel().getSelectedIndex());
			tripAliasField.setText(trip.getAlias());
			tripStartDatePicker.setValue(trip.getBeginDate());
			tripEndDatePicker.setValue(trip.getExpireDate());
			refreshCarComboBox();
			tripCarComboBox.getSelectionModel().select(trip.getPlate());
		}
	}

	private void addTripMode() {
		tripAddModeRbtn.setSelected(true);
		tripDeleteBtn.setVisible(false);
		tripSubmitBtn.setText("Add");
		tripAliasField.clear();
		tripStartDatePicker.setValue(LocalDate.now());
		tripEndDatePicker.setValue(LocalDate.now().plusDays(1));
	}

	private void updateTripMode() {
		tripViewModeRbtn.setSelected(true);
		tripSubmitBtn.setText("Update");
		tripDeleteBtn.setVisible(true);
	}

	private static HashMap<Integer, StopPoint> fetchStopPoint(Integer tripId, SQLiteConnector connector) {
		HashMap<Integer, StopPoint> stopPointsHashMap = new HashMap<>();
		try {
			String sql = String.format("SELECT * " +
					"FROM stop_point join trip_sp on stop_point.spId = trip_sp.spId " +
					"WHERE tripId = %d;", tripId);
			ResultSet rs = connector.executeSQLQuery(sql);
			while (!rs.isClosed() && rs.next()) {
				StopPoint stopPoint = new StopPoint(rs.getInt("spId"),
						rs.getString("streetNo"),
						rs.getString("street"),
						rs.getString("suburb"),
						rs.getString("city"),
						rs.getString("time"));
				// alias must be unique!!!!
				stopPointsHashMap.put(stopPoint.getSpId(), stopPoint);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stopPointsHashMap;
	}

	private void refreshCarComboBox() {
		tripCarComboBox.getItems().clear();
		tripCarComboBox.getItems().addAll(CarController.fetchCars(rss.getUser(), rss.getSqLiteConnector()).keySet());
	}
	private void loadStopPoints() {

		Integer tripId = tripsList.get(tripListView.getSelectionModel().getSelectedIndex()).getTripId();
		stopPoints = fetchStopPoint(tripId, rss.getSqLiteConnector());
		stopPointsList = new ArrayList<StopPoint>(stopPoints.values());
	}

}
