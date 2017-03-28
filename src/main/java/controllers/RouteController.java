package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Stop;
import javafx.scene.text.Text;
import models.*;
import org.apache.commons.lang3.text.WordUtils;

import java.net.URL;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.*;

public class RouteController implements Initializable {

	private RSS rss;
	private HashMap<Integer, StopPoint> stopPoints;
	private ObservableList<StopPoint> stopPointObservableList;
	private HashMap<Integer, Route> routes;
	private ArrayList<Route> routeArrayList;

	@FXML
	MenuController menuController;
	@FXML
	Parent menuView;
	@FXML
	Text routeErrorText;
	@FXML
	TextField routeAliasField, routeStreetNoField, routeStreetField, routeSuburbField, routeCityField;
	@FXML
	Button addSPBtn, addRouteBtn;
	@FXML
	ComboBox routeComboBox;
	@FXML
	RadioButton routeAddModeRbtn, routeViewModeRbtn;
	@FXML
	GridPane addRoutePane, addSPPane;
	@FXML
	TableView SPTable;
	@FXML
	TableColumn<StopPoint, String> streetNoCol, streetCol, suburbCol, cityCol;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		stopPoints = new HashMap<>();
		routes = new HashMap<>();
		routeArrayList = new ArrayList<>();
		routeComboBox.setOnAction(event -> {
			if (routeComboBox.getValue() != null) {
				loadRouteDetail();
			}
		});
		routeViewModeRbtn.setOnAction(event -> updateRouteMode());
		routeAddModeRbtn.setOnAction(event -> addRouteMode());
	}

	public RSS getRSS() {
		return rss;
	}

	public void setRSS(RSS rss) {
		this.rss = rss;
		menuController.setRSS(rss);
		loadRoutes();
	}

	private static HashMap<Integer, Route> fetchRoutes(User user, SQLiteConnector connector) {
		HashMap<Integer, Route> routeHashMap = new HashMap<>();
		try {
			String sql = String.format("SELECT * " +
					"FROM route " +
					"WHERE username = '%s';", user.getUsername());
			ResultSet rs = connector.executeSQLQuery(sql);
			while (!rs.isClosed() && rs.next()) {
				Route route = new Route(rs.getInt("routeId"),
						rs.getString("alias"));
				routeHashMap.put(route.getRouteId(), route);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return routeHashMap;
	}

	/**
	 * Refreshes the whole thing, including drop down box, combox, and all fields.
	 */
	private void loadRoutes() {
		routes = fetchRoutes(rss.getUser(), rss.getSqLiteConnector());
		routeArrayList = new ArrayList<>(routes.values());
		refreshComboBox();
		loadRouteDetail();
	}

	private void refreshComboBox() {
		routeComboBox.getItems().clear();
		int cnt = 0;
		for (Route route : routeArrayList) {
			routeComboBox.getItems().add(cnt++, route.getAlias());
		}
		routeComboBox.getSelectionModel().selectFirst();
	}

	/**
	 * add details to fields and get all related stop points
	 */
	private void loadRouteDetail() {
		if (routeComboBox.getValue() == null || routeAddModeRbtn.isSelected()) {
			addRouteMode();
		} else {
			updateRouteMode();
			fillSPTable();
		}
	}

	private void addRouteMode() {
		routeComboBox.setDisable(true);
		SPTable.getItems().clear();
		routeAliasField.clear();
		addSPPane.setVisible(false);
		addRouteBtn.setText("Create");
		routeAddModeRbtn.setSelected(true);
	}

	private void updateRouteMode() {
		routeComboBox.setDisable(false);
		Route route = routeArrayList.get(routeComboBox.getSelectionModel().getSelectedIndex());
		routeAliasField.setText(route.getAlias());
		addSPPane.setVisible(true);
		addRouteBtn.setText("Rename Alias");
		routeViewModeRbtn.setSelected(true);
		fillSPTable();
	}

	/**
	 * IMPORTANT!! add sp details into table view
	 */
	private void fillSPTable() {
		fetchStopPoints();
		SPTable.getItems().clear();
		stopPointObservableList = FXCollections.observableList(new ArrayList<StopPoint>(stopPoints.values()));

		streetNoCol.setCellValueFactory(new PropertyValueFactory<StopPoint, String>("streetNo"));
		streetCol.setCellValueFactory(new PropertyValueFactory<StopPoint, String>("street"));
		suburbCol.setCellValueFactory(new PropertyValueFactory<StopPoint, String>("suburb"));
		cityCol.setCellValueFactory(new PropertyValueFactory<StopPoint, String>("city"));
		SPTable.setItems(stopPointObservableList);
	}


	private void fetchStopPoints() {
		stopPoints = new HashMap<>();
		try {
			String sql = String.format("SELECT * " +
					"FROM route_sp JOIN stop_point ON route_sp.spId = stop_point.spId " +
					"WHERE routeId = %d;", routeArrayList.get(routeComboBox.getSelectionModel().getSelectedIndex()).getRouteId());
			ResultSet rs = rss.getSqLiteConnector().executeSQLQuery(sql);
			while (!rs.isClosed() && rs.next()) {
				StopPoint sp = new StopPoint(rs.getInt("spId"),
						rs.getString("streetNo"),
						rs.getString("street"),
						rs.getString("suburb"),
						rs.getString("city"));
				stopPoints.put(sp.getSpId(), sp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void clickAddRouteBtn() {
		String sql;
		Integer routeId = null;
		Integer index = null; // current selected item index in combo box
		try {
			if (addRouteBtn.getText().equals("Create")) {
				sql = String.format("INSERT INTO route (username, alias) " +
								"VALUES ('%s', '%s');",
						rss.getUser().getUsername(),
						routeAliasField.getText());
			} else {
				routeId = routeArrayList.get(routeComboBox.getSelectionModel().getSelectedIndex()).getRouteId();
				sql = String.format("UPDATE route " +
								"SET alias = '%s' " +
								"WHERE routeId = %d;",
						routeAliasField.getText(),
						routeId);
			}

			int result = this.rss.getSqLiteConnector().executeSQLUpdate(sql);
			if (result == 0) {
				routeErrorText.setText("Oops, operation failed. Please try it again.");
			} else {
				routeErrorText.setText("Hooray! Operation succeeded!");
				if (addRouteBtn.getText().equals("Create")) {
					routeComboBox.getSelectionModel().selectLast();
				}
			}
			routeErrorText.setVisible(true);
			// after refresh screen, remove current sp deatails to previous selected route info.
			loadRoutes();
			if (routeId != null) index = getComboBoxIndexByRouteId(routeId);
			if (index != null) {
				routeComboBox.getSelectionModel().select((int) index);
			} else {
				routeComboBox.getSelectionModel().selectLast();
			}
			loadRouteDetail();
			updateRouteMode();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void clickAddSPBtn() {
		String sql;
		Integer spId, routeId;
		try {
			// just in case we are going to 'Update' btn in future
			if (addSPBtn.getText().equals("Add")) {
				spId = addSPToDatabase();
				routeId = routeArrayList.get(routeComboBox.getSelectionModel().getSelectedIndex()).getRouteId();
				// associate spId with routeId
				sql = String.format("INSERT INTO route_sp (routeId, spId) " +
						"VALUES " +
						"(%d, %d);", routeId, spId);
				int result = this.rss.getSqLiteConnector().executeSQLUpdate(sql);
				if (result == 0) {
					routeErrorText.setText("Oops, operation failed. Please try it again.");
				} else {
					routeErrorText.setText("Hooray! Operation succeeded!");
					loadRoutes();
					if (addRouteBtn.getText().equals("Create")) {
						routeComboBox.getSelectionModel().selectLast();
					}
					loadRouteDetail();
					// clean sp details
					routeStreetNoField.clear();
					routeStreetField.clear();
					routeSuburbField.clear();
					routeCityField.setText("Christchurch");
					// set current comboBox to current routeId
					Integer index = getComboBoxIndexByRouteId(routeId);
					if (index != null)
					routeComboBox.getSelectionModel().select((int) index);
				}
				routeErrorText.setVisible(true);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds the current SP fields info into database no matter if it is already
	 * in database, this will return the associated spId to identify the SP.
	 *
	 * @return spId in Integer format
	 */
	private Integer addSPToDatabase() {
		String sql;
		Integer spId = null;
		try {
			// find if the address is already in database
			// if the address is NOT existed, then add into our system.
			spId = fetchSPId();
			if (spId == null) {
				sql = String.format("INSERT INTO stop_point " +
								"(streetNo, street, suburb, city, trimmed) " +
								"VALUES " +
								"('%s', '%s', '%s', '%s', '%s');",
						routeStreetNoField.getText().replaceAll("[^a-zA-Z0-9]", "").toUpperCase(),
						WordUtils.capitalizeFully(routeStreetField.getText().replace("[^a-zA-Z0-9 ]", "")),
						WordUtils.capitalizeFully(routeSuburbField.getText().replace("[^a-zA-Z0-9 ]", "")),
						WordUtils.capitalizeFully(routeCityField.getText().replace("[^a-zA-Z0-9 ]", "")),
						getTrimmedSP());

				int result = this.rss.getSqLiteConnector().executeSQLUpdate(sql);
				if (result == 0) {
					routeErrorText.setText("Oops, operation failed. Please try it again.");
				} else {
					routeErrorText.setText("Hooray! Operation succeeded!");
					loadRouteDetail();
				}
				routeErrorText.setVisible(true);
			}
			spId = fetchSPId();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return spId;
		}
	}

	private Integer fetchSPId() {
		Integer spId = null;
		try {
			String sql = String.format("SELECT spId " +
							"FROM stop_point " +
							"WHERE trimmed = '%s';",
					getTrimmedSP());
			ResultSet rs = rss.getSqLiteConnector().executeSQLQuery(sql);
			// if the address is already existed
			if (!rs.isClosed() && rs.next()) {
				spId = rs.getInt("spId");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return spId;
		}
	}

	private String getTrimmedSP() {
		String trimmedSP = (routeStreetNoField.getText() +
				routeStreetField.getText() +
				routeSuburbField.getText() +
				routeCityField.getText()).replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
		return trimmedSP;
	}

	private Integer getComboBoxIndexByRouteId(Integer routeId) {
		Integer index = null;
		for (int i = 0; i < routeArrayList.size(); i++) {
			if (routeArrayList.get(i).getRouteId() == routeId) {
				return i;
			}
		}
		return index;
	}
}

// Known bug:
// when two route have the same alias, switch between these routes cannot activate
// the selection (combo box) listener, as the showing values are the same, thus
// listener thinks there is no changes.
