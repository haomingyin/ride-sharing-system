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
import java.util.HashMap;
import java.util.ResourceBundle;

public class CarController extends Controller implements Initializable {

	private HashMap<String, Car> cars;
	private String defaultComboBoxText = "Click to add a new car.";

	@FXML
	Parent menuView;
	@FXML
	MenuController menuController;

	@FXML
	ComboBox<String> carComboBox;
	@FXML
	Text carErrorText;
	@FXML
	TextField carPlateField, carManuField, carModelField, carColorField, carYearField, carSeatField;
	@FXML
	Button carSubmitBtn, carDeleteBtn;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		cars = new HashMap<>();

	}

	@Override
	protected void afterSetRSS() {
		menuController.setRSS(rss);
		loadCars();
	}

	public void selectedComboBox() {
		if (carComboBox.getValue() != null) {
			carErrorText.setVisible(false);
			loadTextFields();
		}
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
		carComboBox.getItems().addAll(cars.keySet());
		carComboBox.getItems().addAll(defaultComboBoxText);
		carComboBox.getSelectionModel().selectFirst();
	}

	private void loadTextFields() {
		if (carComboBox.getValue() == null || carComboBox.getValue().equals(defaultComboBoxText)) {
			addCarMode();
		} else {
			updateCarMode();
			Car car = cars.get(carComboBox.getValue());
			carPlateField.setText(car.getPlate());
			carManuField.setText(car.getManufacturer());
			carModelField.setText(car.getModel());
			carColorField.setText(car.getColor());
			carYearField.setText(String.valueOf(car.getYear()));
			carSeatField.setText(String.valueOf(car.getSeatNo()));
		}
	}

	private void addCarMode() {
		carPlateField.setDisable(false);
		carDeleteBtn.setVisible(false);
		carPlateField.clear();
		carManuField.clear();
		carModelField.clear();
		carColorField.clear();
		carYearField.clear();
		carSeatField.clear();
		carSubmitBtn.setText("Add");
	}

	private void updateCarMode() {
		carDeleteBtn.setVisible(true);
		carPlateField.setDisable(true);
		carSubmitBtn.setText("Update");
	}

	public void clickCarSubmitBtn() {
		String sql;
		int result = 0;

		Car car = new Car();
		car.setPlate(carPlateField.getText().toUpperCase());
		car.setUsername(rss.getUser().getUsername());
		car.setModel(carModelField.getText().toUpperCase());
		car.setManufacturer(carManuField.getText().toUpperCase());
		car.setColor(WordUtils.capitalizeFully(carColorField.getText()));
		car.setYear(Integer.valueOf(carYearField.getText()));
		car.setSeatNo(Integer.valueOf(carSeatField.getText()));

		if (carSubmitBtn.getText() == "Update") {
			result = SQLExecutor.updateCar(car);
		} else {
			result = SQLExecutor.addCar(car);
		}

		if (result == 0) {
			carErrorText.setText("Oops, operation failed. Please try it again.");
		} else {
			carErrorText.setText("Hooray! Operation succeeded!");
			loadCars();
		}
		carErrorText.setVisible(true);
	}

	public void clickCarDeleteBtn() {
		Car car = new Car();
		car.setPlate(carPlateField.getText());

		int result = SQLExecutor.deleteCar(car);
		if (result == 0) {
			carErrorText.setText("Oops, deletion failed. Please try it again.");
		} else {
			carErrorText.setText("Hooray! Operation succeeded!");
			loadCars();
		}
		carErrorText.setVisible(true);
	}
}
