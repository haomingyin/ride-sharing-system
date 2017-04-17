package controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import models.StopPoint;

import java.net.URL;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by hyi25 on 3/04/17.
 */
public class BookRideController implements Initializable{

	@FXML
	MenuController menuController;
	@FXML
	Parent menuView;

	@FXML
	RadioButton viewModeRbtn, addModeRbtn;
	@FXML
	CheckBox toUniCheckbox, fromUniCheckbox;
	@FXML
	TextField streetNoField, streetField, suburbField, cityField;
	@FXML
	Button searchBtn, bookBtn;
	@FXML
	Text errorText;
	@FXML
	TableView<StopPoint> SPTable;
	@FXML
	TableView<RideTableBean> rideTable;
	@FXML
	TableColumn<RideTableBean, String> directionCol, timeCol, usernameCol, plateCol;
	@FXML
	TableColumn<RideTableBean, Integer>seatNoCol;
	@FXML
	TableColumn<StopPoint, String> streetNoCol, streetCol, suburbCol, cityCol;

	private RSS rss;
	private List<StopPoint> stopPoints;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		addModeRbtn.setOnAction(event -> loadRides());
		viewModeRbtn.setOnAction(event -> loadRides());
		searchBtn.setOnAction(event -> loadRides());
		SPTable.getSelectionModel().selectedItemProperty().addListener(event -> {
			if (SPTable.getSelectionModel().getSelectedItem() != null)
			fillRideTable(SPTable.getSelectionModel().getSelectedItem().getSpId());
		});
		toUniCheckbox.setOnAction(event -> {
			if (SPTable.getSelectionModel().getSelectedItem() != null)
				fillRideTable(SPTable.getSelectionModel().getSelectedItem().getSpId());
		});
		fromUniCheckbox.setOnAction(event -> {
			if (SPTable.getSelectionModel().getSelectedItem() != null)
				fillRideTable(SPTable.getSelectionModel().getSelectedItem().getSpId());
		});
		bookBtn.setOnAction(event -> clickBookBtn());
	}

	public RSS getRSS() {
		return rss;
	}

	public void setRSS(RSS rss) {
		this.rss = rss;
		menuController.setRSS(this.rss);
		loadRides();
	}

	private void loadRides() {
		rideTable.getItems().clear();
		loadSP();
		if (SPTable.getItems().size() != 0) {
			SPTable.getSelectionModel().selectFirst();
		}
		if (viewModeRbtn.isSelected()) {
			viewMode();
		} else {
			bookMode();
		}
	}

	private void loadSP() {
		fetchAllSPRelatedToRides(getSPSearchLikeClause());
		fillSPTable();
	}

	private void bookMode() {
		addModeRbtn.setSelected(true);
		errorText.setVisible(false);
		bookBtn.setText("Book Ride");
	}

	private void viewMode() {
		viewModeRbtn.setSelected(true);
		errorText.setVisible(false);
		bookBtn.setText("Cancel Ride");
	}

	/**
	 * Gets all text from all SP fields, and add % at the end, so SQLite can do
	 * ambiguous search.
	 * @return a string of 'like' clause
	 */
	private String getSPSearchLikeClause() {
		String result = "%";
		if (!streetNoField.getText().equals("")) {
			result += streetNoField.getText().replace("[^a-zA-Z0-9 ]", "").toLowerCase() + "%";
		}
		if (!streetField.getText().equals("")) {
			result += streetField.getText().replace("[^a-zA-Z0-9 ]", "").toLowerCase() + "%";
		}
		if (!suburbField.getText().equals("")) {
			result += suburbField.getText().replace("[^a-zA-Z0-9 ]", "").toLowerCase() + "%";
		}
		if (!cityField.getText().equals("")) {
			result += cityField.getText().replace("[^a-zA-Z0-9 ]", "").toLowerCase() + "%";
		}
		return result;
	}

	/**
	 * fetch all stop points that have been linked to at least one ride.
	 */
	private void fetchAllSPRelatedToRides(String likeClause) {
		try {
			stopPoints = new ArrayList<>();
			String sql;
			if (addModeRbtn.isSelected()) {
				sql = String.format("SELECT * " +
						"FROM ride_sp_view " +
						"WHERE trimmed LIKE '%s';", likeClause);
			} else {
				sql = String.format("SELECT * " +
						"FROM ride_passenger r LEFT JOIN stop_point sp ON r.spId = sp.spId " +
						"WHERE r.username = '%s' AND sp.trimmed LIKE '%s' " +
						"GROUP BY sp.spId;", rss.getUser().getUsername(), likeClause);
			}
			ResultSet rs = rss.getSqlConnector().executeSQLQuery(sql);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void fillSPTable(){
		SPTable.getItems().clear();
		ObservableList<StopPoint> stopPointObservableList = FXCollections.observableList(stopPoints);

		streetNoCol.setCellValueFactory(new PropertyValueFactory<StopPoint, String>("streetNo"));
		streetCol.setCellValueFactory(new PropertyValueFactory<StopPoint, String>("street"));
		suburbCol.setCellValueFactory(new PropertyValueFactory<StopPoint, String>("suburb"));
		cityCol.setCellValueFactory(new PropertyValueFactory<StopPoint, String>("city"));
		SPTable.setItems(stopPointObservableList);
	}

	private void fillRideTable(Integer spId) {
		rideTable.getItems().clear();
		ArrayList<RideTableBean> rideTableBeans = new ArrayList<>();
		for (RideTableBean rideTableBean : fetchRidesBySPId(spId)) {
			if (toUniCheckbox.isSelected() && rideTableBean.getDirection().equals("To UC")) {
				rideTableBeans.add(rideTableBean);
			}
			if (fromUniCheckbox.isSelected() && rideTableBean.getDirection().equals("From UC")) {
				rideTableBeans.add(rideTableBean);
			}
		}
		ObservableList<RideTableBean> rideTableBeanObservableList = FXCollections.observableList(rideTableBeans);

		directionCol.setCellValueFactory(new PropertyValueFactory<RideTableBean, String>("direction"));
		timeCol.setCellValueFactory(new PropertyValueFactory<RideTableBean, String>("time"));
		seatNoCol.setCellValueFactory(new PropertyValueFactory<RideTableBean, Integer>("seatNo"));
		usernameCol.setCellValueFactory(new PropertyValueFactory<RideTableBean, String>("username"));
		plateCol.setCellValueFactory(new PropertyValueFactory<RideTableBean, String>("plate"));
		rideTable.setItems(rideTableBeanObservableList);
	}

	private List<RideTableBean> fetchRidesBySPId(Integer spId){
		List<RideTableBean> rideTableBeans = new ArrayList<>();
		try {
			String sql;
			if (spId != null) {
				if (addModeRbtn.isSelected()) {
					sql = String.format("SELECT * FROM book_ride_view WHERE spId = %d;", spId);
				} else {
					sql = String.format("SELECT * FROM ride_passenger p "+
							"LEFT JOIN book_ride_passenger_view v " +
							"ON p.rideId = v.rideId AND p.spId = v.spId AND p.username = v.passenger " +
							"WHERE v.spId = %d AND passenger = '%s';", spId, rss.getUser().getUsername());
				}
				ResultSet rs = rss.getSqlConnector().executeSQLQuery(sql);
				while (!rs.isClosed() && rs.next()) {
					RideTableBean rideTableBean = new RideTableBean(
							rs.getInt("rideId"),
							rs.getInt("tripId"),
							rs.getInt("spId"),
							rs.getString("direction"),
							rs.getString("time"),
							rs.getInt("seatNo"),
							rs.getString("username"),
							rs.getString("plate")
					);
					rideTableBeans.add(rideTableBean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return rideTableBeans;
		}
	}

	public void clickBookBtn() {
		try {
			if (rideTable.getSelectionModel().getSelectedItem() != null) {
				String sql;
				if (addModeRbtn.isSelected()) {
					sql = String.format("INSERT INTO ride_passenger " +
									"(username, rideId, spId) " +
									"VALUES ('%s', %d, %d);",
							rss.getUser().getUsername(),
							rideTable.getSelectionModel().getSelectedItem().getRideId(),
							SPTable.getSelectionModel().getSelectedItem().getSpId());
				} else {
					sql = String.format("DELETE FROM ride_passenger " +
									"WHERE username = '%s' AND rideId = %d AND spId = %d;",
							rss.getUser().getUsername(),
							rideTable.getSelectionModel().getSelectedItem().getRideId(),
							SPTable.getSelectionModel().getSelectedItem().getSpId());
				}
				int result = rss.getSqlConnector().executeSQLUpdate(sql);
				if (result == 0) {
					errorText.setText("You may have already booked/cancelled this ride.");
				} else {
					errorText.setText("You have successfully booked/cancelled this ride.");
					loadRides();
				}
			} else {
				errorText.setText("Please select a ride in the right side pane.");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			errorText.setText("You may have already booked/cancelled this ride.");
		} finally {
			errorText.setVisible(true);
		}
	}

	public class RideTableBean {
		private SimpleIntegerProperty rideId, tripId, spId, seatNo;
		private SimpleStringProperty direction, time, username, plate;

		public RideTableBean(Integer rideId, Integer tripId, Integer spId,
		                     String direction, String time, Integer seatNo,
		                     String username, String plate) {
			this.rideId = new SimpleIntegerProperty(rideId);
			this.tripId = new SimpleIntegerProperty(tripId);
			this.spId = new SimpleIntegerProperty(spId);
			this.seatNo = new SimpleIntegerProperty(seatNo);
			this.direction = new SimpleStringProperty(direction);
			this.time = new SimpleStringProperty(time);
			this.username = new SimpleStringProperty(username);
			this.plate = new SimpleStringProperty(plate);
		}

		public int getRideId() {
			return rideId.get();
		}

		public SimpleIntegerProperty rideIdProperty() {
			return rideId;
		}

		public void setRideId(int rideId) {
			this.rideId.set(rideId);
		}

		public int getTripId() {
			return tripId.get();
		}

		public SimpleIntegerProperty tripIdProperty() {
			return tripId;
		}

		public void setTripId(int tripId) {
			this.tripId.set(tripId);
		}

		public int getSpId() {
			return spId.get();
		}

		public SimpleIntegerProperty spIdProperty() {
			return spId;
		}

		public void setSpId(int spId) {
			this.spId.set(spId);
		}

		public int getSeatNo() {
			return seatNo.get();
		}

		public SimpleIntegerProperty seatNoProperty() {
			return seatNo;
		}

		public void setSeatNo(int seatNo) {
			this.seatNo.set(seatNo);
		}

		public String getDirection() {
			return direction.get();
		}

		public SimpleStringProperty directionProperty() {
			return direction;
		}

		public void setDirection(String direction) {
			this.direction.set(direction);
		}

		public String getTime() {
			return time.get();
		}

		public SimpleStringProperty timeProperty() {
			return time;
		}

		public void setTime(String time) {
			this.time.set(time);
		}

		public String getUsername() {
			return username.get();
		}

		public SimpleStringProperty usernameProperty() {
			return username;
		}

		public void setUsername(String username) {
			this.username.set(username);
		}

		public String getPlate() {
			return plate.get();
		}

		public SimpleStringProperty plateProperty() {
			return plate;
		}

		public void setPlate(String plate) {
			this.plate.set(plate);
		}
	}
}
