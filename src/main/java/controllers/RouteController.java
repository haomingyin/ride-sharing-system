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
import models.*;
import models.database.SQLExecutor;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.sqlite.SQLiteException;

import java.net.URL;
import java.util.*;

public class RouteController extends Controller implements Initializable {


	@FXML
	private MenuController menuController;
	@FXML
	private Parent menuView;
	@FXML
	private Text routeErrorText;
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
	private TableView<StopPoint> SPTable;
	@FXML
	private TableColumn<StopPoint, String> streetNoCol, streetCol, suburbCol, cityCol;

	private enum Mode {UPDATE_MODE, ADD_MODE}
	private Mode mode;

	private Map<Integer, StopPoint> stopPoints;
	private ObservableList<StopPoint> stopPointObservableList;
	private Map<Integer, Route> routes;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
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
		updateModeRbtn.setOnAction(event -> {mode = Mode.UPDATE_MODE; loadRouteDetail();});
		addModeRbtn.setOnAction(event -> {mode = Mode.ADD_MODE; loadRouteDetail();});

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
		routeComboBox.getSelectionModel().selectLast();
	}

	/**
	 * add details to fields and get all related stop points
	 */
	private void loadRouteDetail() {
		if (mode == Mode.ADD_MODE || routeComboBox.getItems().size() == 0) {
			addRouteMode();
		} else {
			updateRouteMode();
			fillSPTable();
		}
	}

	private void addRouteMode() {
		mode = Mode.ADD_MODE;
		addModeRbtn.setSelected(true);

		routeComboBox.setDisable(true);
		SPTable.getItems().clear();
		aliasField.clear();

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

		fillALlFields();
	}

	private void fillALlFields() {
		Route route = routeComboBox.getValue();
		aliasField.setText(route.getAlias());
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
		stopPoints = SQLExecutor.fetchStopPointsByRoute(routeComboBox.getValue());
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
				if ((result = SQLExecutor.addStopPointIntoRoute(routeComboBox.getValue(), sp.get(0))) == 1) {
					fillSPTable();
				} else {
					errorMsg = "Your stop point address is an invalid Christchurch address.\n";
					if (sp.size() > 1) errorMsg = "The entered address is not precise enough.\n";
					rss.showErrorDialog("Operation Failed!", errorMsg);
				}
			} catch (SQLiteException e) {
				if (e.getResultCode().code == 1555) {
					errorMsg = "The entered address is already existed in the route.\n";
					rss.showErrorDialog("Operation Failed!", errorMsg);
				}
			}
		}
	}

	private void clickDeleteBtn () {
		int result = 0;
		String errorMsg;
		try {
			result = SQLExecutor.deleteRoute(routeComboBox.getValue());
			if (result == 1) {
				rss.showInformationDialog("Deletion Succeeded!", "The car has been deleted.");
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

