package controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import models.*;

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
	Text errorText;
	@FXML
	ListView tripListView;
	@FXML
	TextField tripAliasField, tripSPTimeField;
	@FXML
	DatePicker tripStartDatePicker, tripEndDatePicker;
	@FXML
	ComboBox tripCarComboBox, routeComboBox, directionComboBox;
	@FXML
	Button tripSubmitBtn, tripDeleteBtn, setSPTimeBtn;
	@FXML
	GridPane setTimePane, tripDetailPane;
	@FXML
	TableView<StopPoint> SPTable;
	@FXML
	TableColumn<StopPoint, String> timeCol, streetNoCol, streetCol, suburbCol, cityCol;

	private RSS rss;
	private HashMap<Integer, Trip> trips;
	private ArrayList<Trip> tripsList;
	private HashMap<Integer, StopPoint> stopPoints;
	private HashMap<Integer, Route> routes;
	private HashMap<Integer, Integer> routeIdtoIndex;
	private ArrayList<Route> routesList;
	private ObservableList<StopPoint> stopPointObservableList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		trips = new HashMap<>();
		tripsList = new ArrayList<>();
		stopPoints = new HashMap<>();
		routes = new HashMap<>();
		routesList = new ArrayList<>();
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
		routeComboBox.setOnAction(event ->
		{
			if (routeComboBox.getValue() != null) {
				loadStopPoints();
			}
		});
		directionComboBox.getItems().add("To UC");
		directionComboBox.getItems().add("From UC");
		directionComboBox.getSelectionModel().selectFirst();
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
				trip.setRouteId(rs.getInt("routeId"));
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
		// no matter which mode, it always needs to load cars, routes, and sp
		refreshCarComboBox();
		loadRoutes();
		loadStopPoints();
		// no trip to load
		if (tripListView.getItems().size() == 0 || tripAddModeRbtn.isSelected()) {
			addTripMode();
			refreshCarComboBox();
			refreshRouteComboBox();
			loadStopPoints();
		}
		// there are trips to load
		else {
			updateTripMode();
			Trip trip = tripsList.get(tripListView.getSelectionModel().getSelectedIndex());
			tripAliasField.setText(trip.getAlias());
			directionComboBox.setValue(trip.getDirection());
			// set date picker
			tripStartDatePicker.setValue(trip.getBeginDate());
			tripEndDatePicker.setValue(trip.getExpireDate());
			tripCarComboBox.getSelectionModel().select(trip.getPlate());
			routeComboBox.getSelectionModel().select((int) routeIdtoIndex.get(trip.getRouteId()));
		}



	}

	private void addTripMode() {
		SPTable.getItems().clear();
		tripListView.setDisable(true);
		tripAddModeRbtn.setSelected(true);
		tripDeleteBtn.setVisible(false);
		tripSubmitBtn.setText("Add");
		tripAliasField.clear();
		tripStartDatePicker.setValue(LocalDate.now());
		tripEndDatePicker.setValue(LocalDate.now().plusDays(1));
		routeComboBox.setDisable(false);

		// temporary setting as update and deleting features are postponed
		setTimePane.setVisible(true);
		tripDetailPane.setDisable(false);
	}

	private void updateTripMode() {
		tripViewModeRbtn.setSelected(true);
		tripListView.setDisable(false);
		tripSubmitBtn.setText("Update");
		tripDeleteBtn.setVisible(true);
		routeComboBox.setDisable(true);

		// temporary setting as update and deleting features are postponed
		setTimePane.setVisible(false);
		tripDetailPane.setDisable(true);
	}

	/**
	 * loads stop points according to current mode. if it is view mode, than lists
	 * trip_sp. otherwise, list route_sp.
	 *
	 * @param tripId
	 */
	private void fetchStopPoints(Integer tripId, Integer routeId) {
		stopPoints = new HashMap<>();
		String sql;
		boolean isAddMode = tripAddModeRbtn.isSelected();
		try {

			if (isAddMode) {
				sql = String.format("SELECT * " +
						"FROM stop_point JOIN route_sp ON stop_point.spId = route_sp.spId " +
						"WHERE routeId = %d;", routeId);
			} else {
				sql = String.format("SELECT * " +
						"FROM stop_point JOIN trip_sp ON stop_point.spId = trip_sp.spId " +
						"WHERE tripId = %d", tripId);
			}

			ResultSet rs = rss.getSqLiteConnector().executeSQLQuery(sql);
			while (!rs.isClosed() && rs.next()) {
				StopPoint stopPoint = new StopPoint(rs.getInt("spId"),
						rs.getString("streetNo"),
						rs.getString("street"),
						rs.getString("suburb"),
						rs.getString("city"));

				if (!isAddMode) stopPoint.setTime(rs.getString("time"));

				stopPoints.put(stopPoint.getSpId(), stopPoint);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void refreshCarComboBox() {
		tripCarComboBox.getItems().clear();
		tripCarComboBox.getItems().addAll(CarController.fetchCars(rss.getUser(), rss.getSqLiteConnector()).keySet());
	}

	private void loadRoutes() {
		routes = RouteController.fetchRoutes(rss.getUser(), rss.getSqLiteConnector());
		routesList = new ArrayList<>(routes.values());
		routeIdtoIndex = new HashMap<>();
		refreshRouteComboBox();
	}

	private void refreshRouteComboBox() {
		routeComboBox.getItems().clear();
		for (int i = 0; i < routesList.size(); i++) {
			routeComboBox.getItems().add(i, routesList.get(i).getAlias());
			routeIdtoIndex.put(routesList.get(i).getRouteId(), i);
		}
	}

	private void loadStopPoints() {
		// add mode and view mode should be different
		Integer tripId = null;
		Integer routeId = null;
		if (routeComboBox.getValue() != null) {
			routeId = routesList.get(routeComboBox.getSelectionModel().getSelectedIndex()).getRouteId();
		}
		// if is adding trip mode, then tripId keeps as null
		if (!tripAddModeRbtn.isSelected() && tripListView.getSelectionModel().getSelectedItem() != null) {
			tripId = tripsList.get(tripListView.getSelectionModel().getSelectedIndex()).getTripId();
		}
		if (tripId != null || routeId != null) {
			fetchStopPoints(tripId, routeId);
			fillSPTable();
		}
	}

	private void fillSPTable() {
		SPTable.getItems().clear();
		stopPointObservableList = FXCollections.observableList(new ArrayList<StopPoint>(stopPoints.values()));

		timeCol.setCellValueFactory(new PropertyValueFactory<StopPoint, String>("time"));
		streetNoCol.setCellValueFactory(new PropertyValueFactory<StopPoint, String>("streetNo"));
		streetCol.setCellValueFactory(new PropertyValueFactory<StopPoint, String>("street"));
		suburbCol.setCellValueFactory(new PropertyValueFactory<StopPoint, String>("suburb"));
		cityCol.setCellValueFactory(new PropertyValueFactory<StopPoint, String>("city"));
		SPTable.setItems(stopPointObservableList);
	}

	public void clickSetSPTimeBtn() {
		if (SPTable.getSelectionModel().getSelectedItem() != null) {
			StopPoint stopPoint = SPTable.getSelectionModel().getSelectedItem();
			stopPoint.setTime(tripSPTimeField.getText());
		}
	}

	public void clickTripSubmitBtn() {
		try {
			String sql;
			if (isAllFieldsValid()) {
				sql = String.format("INSERT INTO trip " +
								"(alias, username, routeId, direction, plate, beginDate, expireDate) " +
								"VALUES ('%s', '%s', %d, '%s', '%s', '%s', '%s');",
						tripAliasField.getText(),
						rss.getUser().getUsername(),
						routesList.get(routeComboBox.getSelectionModel().getSelectedIndex()).getRouteId(),
						directionComboBox.getValue(),
						tripCarComboBox.getValue(),
						tripStartDatePicker.getValue().toString(),
						tripEndDatePicker.getValue().toString());

				int result = rss.getSqLiteConnector().executeSQLUpdate(sql);
				if (result == 0) {
					errorText.setText("Oops, operation failed. Please try it again.");
					errorText.setVisible(true);
				} else {
					sql = "SELECT last_insert_rowid() AS tripId;";
					ResultSet rs = rss.getSqLiteConnector().executeSQLQuery(sql);
					Integer tripId = rs.getInt("tripId");
					for (StopPoint stopPoint : stopPoints.values()) {
						sql = String.format("INSERT INTO trip_sp (tripId, spId, time) " +
								"VALUES (%d, %d, '%s');",
								tripId,
								stopPoint.getSpId(),
								stopPoint.getTime());
						rss.getSqLiteConnector().executeSQLUpdate(sql);
						errorText.setText("Hooray! Operation succeeded!");
					}
				}
				loadTrips();
				errorText.setVisible(true);
			} else {
				errorText.setText("Something went wrong, check if you filled all fields.");
				errorText.setVisible(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorText.setVisible(true);
		}
	}

	/**
	 * check if all field have been filled.
	 *
	 * @return true if all fields are valid
	 */
	private boolean isAllFieldsValid() {
		boolean result = true;
		if (tripAliasField.getText().equals("") ||
				tripCarComboBox.getValue() == null ||
				routeComboBox.getValue() == null) {
			result = false;
		}
		for (StopPoint stopPoint : stopPoints.values()) {
			if (stopPoint.getTime().equals("")) {
				result = false;
				break;
			}
		}
		return result;
	}
// known bug: in list view, alias cannot be the same!
}
