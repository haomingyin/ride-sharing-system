package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import models.Route;
import models.database.SQLExecutor;
import models.position.StopPoint;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.sqlite.SQLiteException;

import java.net.URL;
import java.util.*;

public class RouteController extends Controller implements Initializable {


	@FXML
	private MenuController menuController;
	@FXML
	private TextField aliasField, addressField;
	@FXML
	private Button addSPBtn, addRouteBtn, deleteBtn;
	@FXML
	private ComboBox<Route> routeComboBox;
	@FXML
	private RadioButton addModeRbtn, updateModeRbtn;
	@FXML
	private GridPane addRoutePane, addSPPane, routesPane;
	@FXML
	private WebView webView;
	@FXML
	private TableView<StopPoint> SPTable;
	@FXML
	private TableColumn<StopPoint, Double> distanceCol;
	@FXML
	private TableColumn<StopPoint, String> streetNoCol, streetCol, suburbCol, cityCol;

	private enum Mode {UPDATE_MODE, ADD_MODE}
	private Mode mode;
	private MapHandler mapHandler;

	private Map<Integer, StopPoint> stopPoints;
	private Map<Integer, Route> routes;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		mapHandler = new MapHandler(webView.getEngine());
		stopPoints = new HashMap<>();
		routes = new HashMap<>();

		routeComboBox.setOnAction(event -> {
			if (routeComboBox.getValue() != null) {
				loadRouteDetail();
			}
		});

		addRouteBtn.setOnAction(event -> clickAddRouteBtn());
		addSPBtn.setOnAction(event -> clickAddSPBtn());
		deleteBtn.setOnAction(event -> clickDeleteBtn());
		updateModeRbtn.setOnAction(event -> {mode = Mode.UPDATE_MODE; loadRouteDetail(); mapHandler.clearMarkers();});
		addModeRbtn.setOnAction(event -> {mode = Mode.ADD_MODE; loadRouteDetail(); mapHandler.clearMarkers();});

		// autocomplete address finder
		TextFields.bindAutoCompletion(addressField, new Callback<AutoCompletionBinding.ISuggestionRequest, Collection<StopPoint>>() {
			public Collection<StopPoint> call(AutoCompletionBinding.ISuggestionRequest param) {
				return SQLExecutor.fetchStopPointsByString(param.getUserText(), 6).values();
			}
		}).setPrefWidth(315);

	}

	@Override
	protected void afterSetRSS() {
		menuController.setRSS(rss);
		loadRoutes();
	}

	private void fetchRoutes() {
		routes = SQLExecutor.fetchRoutesByUser(rss.getUser());
	}

	/**
	 * Refreshes the whole thing, including drop down box, combox, and all fields.
	 */
	private void loadRoutes() {
		fetchRoutes();
		refreshComboBox();
		loadRouteDetail();
	}

	private void refreshComboBox() {
		routeComboBox.getItems().clear();
		ObservableList<Route> routeObservableList = FXCollections.observableList(new ArrayList<>(routes.values()));
		routeComboBox.setItems(routeObservableList);
	}

	/**
	 * add details to fields and get all related stop points
	 */
	private void loadRouteDetail() {
		if (mode == Mode.ADD_MODE || routeComboBox.getItems().size() == 0) {
			addRouteMode();
		} else {
			updateRouteMode();
			fillALlFields();
		}
	}

	private void addRouteMode() {
		mode = Mode.ADD_MODE;
		addModeRbtn.setSelected(true);

		routeComboBox.setDisable(true);
		SPTable.getItems().clear();
		aliasField.clear();

		addRoutePane.setVisible(true);
		routesPane.setDisable(true);
		addSPPane.setVisible(false);
		addRouteBtn.setText("Create");
	}

	private void updateRouteMode() {
		mode = Mode.UPDATE_MODE;
		updateModeRbtn.setSelected(true);

		routeComboBox.setDisable(false);

		routesPane.setDisable(false);
		addSPPane.setVisible(true);
		addRouteBtn.setText("Rename Alias");

		if (routeComboBox.getValue() == null) {
			addSPPane.setVisible(false);
			addRoutePane.setVisible(false);
			deleteBtn.setDisable(true);
		} else {
			addSPPane.setVisible(true);
			addRoutePane.setVisible(true);
			deleteBtn.setDisable(false);
		}
		SPTable.getItems().clear();
	}

	private void fillALlFields() {
		Route route = routeComboBox.getValue();
		if (route != null) {
			aliasField.setText(route.getAlias());
			fillSPTable();
		}
	}

	/**
	 * IMPORTANT!! add sp details into table view
	 */
	private void fillSPTable() {
		stopPoints = SQLExecutor.fetchStopPointsByRoute(routeComboBox.getValue());
		if (stopPoints != null) {
			mapHandler.drawMarkers(new ArrayList<>(stopPoints.values()));
			SPTable.getItems().clear();
			ObservableList<StopPoint> stopPointObservableList =
					FXCollections.observableList(new ArrayList<>(stopPoints.values()));

			streetNoCol.setCellValueFactory(new PropertyValueFactory<>("streetNo"));
			streetCol.setCellValueFactory(new PropertyValueFactory<>("street"));
			suburbCol.setCellValueFactory(new PropertyValueFactory<>("suburb"));
			cityCol.setCellValueFactory(new PropertyValueFactory<>("city"));
			distanceCol.setCellValueFactory(new PropertyValueFactory<>("distance"));
			SPTable.setItems(stopPointObservableList);
		}
	}

	private void clickAddRouteBtn() {
		Route route = mode == Mode.ADD_MODE ? new Route() : routeComboBox.getValue();
		route.setAlias(aliasField.getText());

		int result;
		String infoMsg = "";
		if (mode == Mode.ADD_MODE) {
			route.setUsername(rss.getUser().getUsername());
			if ((result = SQLExecutor.addRoute(route)) == 1)
				infoMsg = "A route with alias '" + route.getAlias() + "' has been created.";

		} else {
			if ((result = SQLExecutor.updateRouteAlias(route)) == 1)
				infoMsg = "The alias of this route has been updated.";
		}

		if (result == 0) {
			rss.showErrorDialog("Operation failed.",
					"Operation failed with unknown reason. Please contact administrator.");
		} else {
			rss.showInformationDialog("Operation succeeded!", infoMsg);
			loadRoutes();
			updateRouteMode();
		}
	}

	private void clickAddSPBtn() {
		String address = addressField.getText().toLowerCase().replaceAll("([^a-z0-9]+|(city))+", "");
		List<StopPoint> sp = new ArrayList<>(SQLExecutor.fetchStopPointsByString(address, 2).values());

		int result = 0;
		String errorMsg;
		if (sp.size() == 1) {
			try {
				Double distance = mapHandler.getDistance(sp.get(0));
				if (distance != null) {
					sp.get(0).setDistance(distance);
					result = SQLExecutor.updateStopPointDistance(sp.get(0));
				}

				if (result != 1) {
					errorMsg = "Failed to connect to Google server for retrieving distance. Please try again.";
					rss.showWarningDialog("Operation Failed!", errorMsg);
				} else {

					if ((result = SQLExecutor.addStopPointIntoRoute(routeComboBox.getValue(), sp.get(0))) == 1) {
						fillSPTable();
					} else {
						// if a route has been used for a trip sql executor will return -1.
						if (result == -1) {
							errorMsg = "A route that has been used for trips cannot add any new stop point.\n";
						} else {
							errorMsg = "Your stop point address is an invalid Christchurch address.\n";
						}
						rss.showErrorDialog("Operation Failed!", errorMsg);
					}
				}
			} catch (SQLiteException e) {
				if (e.getResultCode().code == 1555) {
					errorMsg = "The entered address is already existed in the route.\n";
					rss.showErrorDialog("Operation Failed!", errorMsg);
				}
			}
		} else {
			errorMsg = "The entered address is not precise enough.\n";
			rss.showErrorDialog("Operation Failed!", errorMsg);
		}
	}

	private void clickDeleteBtn () {
		int result;
		String errorMsg;
		try {
			result = SQLExecutor.deleteRoute(routeComboBox.getValue());
			if (result == 1) {
				rss.showInformationDialog("Deletion Succeeded!", "The route has been deleted.");
				loadRoutes();
			} else {
				errorMsg = "Deletion failed with unknown reason, please contact administrator.\n";
				rss.showErrorDialog("Deletion Failed!", errorMsg);
			}
		} catch (SQLiteException e) {
			// if error code is 1811, which means sql foreign key constraint is violated.
			if (e.getResultCode().code == 1811) {
				errorMsg = "Before delete the route, you must delete all trips associated with this route.\n" +
						"(Error code: 1811. Database foreign key constraint has been violated.)\n";
			} else {
				errorMsg = "Deletion failed with unknown reason, please contact administrator.\n";
			}
			rss.showErrorDialog("Deletion Failed!", errorMsg);
		}
	}
}

