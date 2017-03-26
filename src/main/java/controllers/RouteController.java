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
import models.*;

import java.net.URL;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class RouteController implements Initializable {

	private RSS rss;
	private HashMap<Integer, StopPoint> stopPoints;
	private ObservableList<StopPoint>  stopPointObservableList;
	private HashMap<Integer, Route> routes;
	private ArrayList<Route> routeArrayList;
	
	@FXML
	MenuController menuController;
	@FXML
	Parent menuView;
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

	private void loadRoutes () {
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
	
}
