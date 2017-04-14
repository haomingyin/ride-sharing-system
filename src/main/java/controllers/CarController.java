package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import models.*;
import models.database.*;
import org.apache.commons.lang3.text.WordUtils;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class CarController extends Controller implements Initializable {

	private HashMap<String, Car> cars;

	@FXML
	private Parent menuView;
	@FXML
	private MenuController menuController;

	@FXML
	private ComboBox<Car> carComboBox;
	@FXML
	private ComboBox<Integer> yearComboBox, seatNoComboBox;
	@FXML
	private TextField plateField, manuField, modelField, colorField, performanceField;
	@FXML
	private DatePicker wofDatePicker;
	@FXML
	private Button submitBtn, deleteBtn, addBtn;

	private enum Mode {ADD_MODE, UPDATE_MODE};
	private Mode mode;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		cars = new HashMap<>();

		// add all possible years, default is 2017
		for (int i = 1970; i <= 2017; i++) {
			yearComboBox.getItems().add(i);
		}
		yearComboBox.getSelectionModel().selectLast();

		// add all possible seatNo, default is 4
		for (int i = 1; i <= 9; i++) {
			seatNoComboBox.getItems().add(i);
		}
		seatNoComboBox.getSelectionModel().select((Integer) 4);

		// set action for car combo box
		carComboBox.setOnAction(e -> {
			if (carComboBox.getValue() != null) {
				loadTextFields();
			}
		});

		// set action for submit & delete button
		submitBtn.setOnAction(e -> clickSubmitBtn());
		deleteBtn.setOnAction(e -> clickDeleteBtn());
		addBtn.setOnAction(e -> addCarMode());

	}

	@Override
	protected void afterSetRSS() {
		menuController.setRSS(rss);
		loadCars();
	}

	private void fetchCars() {
		cars = SQLExecutor.fetchCarsByUser(this.rss.getUser());
	}

	public void loadCars() {
		fetchCars();
		refreshCarComboBox();
		loadTextFields();
	}

	private void refreshCarComboBox() {
		carComboBox.getItems().clear();
		carComboBox.getItems().addAll(cars.values());
		carComboBox.getSelectionModel().selectFirst();
	}

	private void loadTextFields() {
		if (carComboBox.getValue() == null) {
			addCarMode();
		} else {
			updateCarMode();
			Car car = carComboBox.getValue();
			plateField.setText(car.getPlate());
			manuField.setText(car.getManufacturer());
			modelField.setText(car.getModel());
			colorField.setText(car.getColor());
			yearComboBox.setValue(car.getYear());
			seatNoComboBox.setValue(car.getSeatNo());
			performanceField.setText(String.valueOf(car.getPerformance()));
			wofDatePicker.setValue(LocalDate.parse(car.getWof()));
		}
	}

	private void addCarMode() {
		mode = Mode.ADD_MODE;
		carComboBox.getSelectionModel().select(null);
		plateField.setEditable(true);
		deleteBtn.setVisible(false);
		plateField.clear();
		manuField.clear();
		modelField.clear();
		colorField.clear();
		yearComboBox.getSelectionModel().selectLast();
		seatNoComboBox.getSelectionModel().selectLast();
		wofDatePicker.setValue(null);
		performanceField.clear();
		submitBtn.setText("Add");
	}

	private void updateCarMode() {
		mode = Mode.UPDATE_MODE;
		deleteBtn.setVisible(true);
		plateField.setEditable(false);
		submitBtn.setText("Update");
	}

	/* TODO: check if the performance is changed, then notify the price changes to related passengers. 4/14/2017*/
	public void clickSubmitBtn() {
		if (validateFields()) {
			String sql;

			Car car = mode == Mode.ADD_MODE ? new Car() : carComboBox.getValue();
			car.setPlate(plateField.getText().toUpperCase());
			car.setUsername(rss.getUser().getUsername());
			car.setModel(modelField.getText().toUpperCase());
			car.setManufacturer(manuField.getText().toUpperCase());
			car.setColor(WordUtils.capitalizeFully(colorField.getText()));
			car.setYear(yearComboBox.getValue());
			car.setSeatNo(seatNoComboBox.getValue());
			car.setPerformance(Double.valueOf(performanceField.getText()));
			car.setWof(wofDatePicker.getValue().toString());

			int resultCode;
			if (mode == Mode.UPDATE_MODE) {
				if ((resultCode = SQLExecutor.updateCar(car)) == 1) {
					rss.showInformationDialog("Update Succeeded!", "The car has been updated.");
				}
			} else {
				if ((resultCode = SQLExecutor.addCar(car)) == 1) {
					rss.showInformationDialog("Operation Succeeded!", "The car has been added into your account.");
					loadCars();
				} else if (resultCode == 2067) {
					rss.showErrorDialog("Operation Failed!", "The car already existed in our system.");
				}
			}

		}
	}

	public void clickDeleteBtn() {
		int result = SQLExecutor.deleteCar(carComboBox.getValue());
		if (result == 1) {
			rss.showInformationDialog("Deletion Succeeded!", "The car has been deleted.");
			loadCars();
		}
		// if error code is 1811, which means sql foreign key constraint is violated.
		else {
			String errorMsg;
			if (result == 1811) {
				errorMsg = "Before you delete the car, you must delete all trips associated with the car.\n" +
						"(Error code: 1811. Database foreign key constraint has been violated.)\n";
			} else {
				errorMsg = "Deletion failed with unknown reason, please contact administrator.\n";
			}
			rss.showErrorDialog("Deletion Failed!", errorMsg);
		}
	}

	private boolean validateFields() {
		List<String> errorMsg = new ArrayList<>();

		if (!plateField.getText().matches("[a-zA-Z0-9]{1,6}")) {
			errorMsg.add("Valid plates can only contain up to 6 alphanumeric characters.\n");
		}

		if (!manuField.getText().matches("[a-zA-Z]+")) {
			errorMsg.add("Manufacturer cannot be left empty and can only contain alphabetic characters.\n");
		}

		if (!modelField.getText().matches("[a-zA-Z0-9]+")) {
			errorMsg.add("Model cannot be left empty and can only contain alphanumeric characters.\n");
		}

		if (!colorField.getText().matches("[a-zA-Z]+")) {
			errorMsg.add("Color cannot be left empty and can only contain alphabetic characters.\n");
		}

		if (wofDatePicker.getValue() == null) {
			errorMsg.add("WOF expire date cannot be left empty\n");
		} else {
			if (wofDatePicker.getValue().isBefore(LocalDate.now())) {
				errorMsg.add("A car with expired WOF is not allowed to be used in our system.\n");
			}
		}

		if (!performanceField.getText().matches("^[0-9]+([.]{1}[0-9]*)?$")) {
			errorMsg.add("Performance should be a real number.\n");
		} else if (Double.valueOf(performanceField.getText()) == 0) {
			errorMsg.add("Performance should be greater than 0.\n");
		}

		// handle and show error message in dialog
		if (errorMsg.size() == 0) {
			return true;
		} else {
			String errorString = "Operation failed is caused by: \n";
			for (Integer i = 1; i <= errorMsg.size(); i++) {
				errorString += i.toString() + ". " + errorMsg.get(i - 1);
			}
			String headMsg = mode == Mode.ADD_MODE ? "Failed to add the car." : "Failed to update the car.";
			rss.showErrorDialog(headMsg, errorString);
			return false;
		}
	}
}
