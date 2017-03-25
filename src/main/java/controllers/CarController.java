package controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import models.Car;
import models.RSS;
import models.User;

import java.net.URL;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.StringJoiner;

public class CarController implements Initializable {

	private RSS rss;
	private HashMap<String, Car> cars;
	private String defaultComboBoxText = "Click to add a new car.";

	@FXML
	Parent menuView;
	@FXML
	MenuController menuController;

	@FXML
	ComboBox carComboBox;
	@FXML
	Text carErrorText;
	@FXML
	TextField carPlateField, carManuField, carModelField, carColorField, carYearField, carSeatField;
	@FXML
	Button carSubmitBtn, carDeleteBtn;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		cars = new HashMap<>();
		addComboBoxListener();
	}

	public void setRSS(RSS rss) {
		this.rss = rss;
		loadCars();
	}

	public void addComboBoxListener() {
		try {
			carErrorText.setVisible(false);
			carComboBox.setOnAction(event -> loadTextFields());
		} catch (Exception e) {
			System.out.println(e.getCause());
		}
	}

	public void loadCars() {
		try {
			String sql = String.format("SELECT * " +
					"FROM car " +
					"WHERE owner = '%s';", this.rss.getUser().getUsername());
			ResultSet rs = this.rss.getSqLiteConnector().executeSQLQuery(sql);
			cars = new HashMap<>();
			while (!rs.isClosed() && rs.next()) {
				Car car = new Car(rs.getString("plate"),
						rs.getString("owner"),
						rs.getString("model"),
						rs.getString("manufacturer"),
						rs.getString("color"),
						rs.getInt("year"),
						rs.getInt("seatNo"));
				cars.put(car.getPlate(), car);
			}
			refreshCarCombobox();
			loadTextFields();
		} catch (Exception e) {
			System.out.println(e.getCause());
		}
	}

	private void refreshCarCombobox() {
		carComboBox.getItems().clear();
		carComboBox.getItems().addAll(cars.keySet());
		carComboBox.getItems().addAll(defaultComboBoxText);
		carComboBox.getSelectionModel().selectFirst();
	}

	private void loadTextFields() {
		if (carComboBox.getSelectionModel().getSelectedItem().toString().equals(defaultComboBoxText)) {
			addCarMode();
		} else {
			updateCarMode();
			Car car = cars.get(carComboBox.getSelectionModel().getSelectedItem().toString());
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
		if (carSubmitBtn.getText() == "Update") {
			sql = String.format("UPDATE car " +
							"SET model = '%s', " +
							"manufacturer = '%s', " +
							"color = '%s', " +
							"year = %s, " +
							"seatNo = %s " +
							"WHERE plate = '%s';",
					carModelField.getText(),
					carManuField.getText(),
					carColorField.getText(),
					carYearField.getText(),
					carSeatField.getText(),
					carPlateField.getText());
		} else {
			sql = String.format("INSERT INTO car " +
							"(plate, owner, model, manufacturer, color, year, seatNo) " +
							"VALUES ('%s', '%s', '%s', '%s', '%s', %s, %s);",
					carPlateField.getText(),
					this.rss.getUser().getUsername(),
					carModelField.getText(),
					carManuField.getText(),
					carColorField.getText(),
					carYearField.getText(),
					carSeatField.getText());
		}
		try {
			int result = this.rss.getSqLiteConnector().executeSQLUpdate(sql);
			if (result == 0) {
				carErrorText.setText("Oops, Operation failed. Please try it again.");
			} else {
				carErrorText.setText("Horray! Operation succeed!");
				loadCars();
			}
			carErrorText.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void clickCarDeleteBtn() {
		String sql = String.format("DELETE FROM car WHERE plate = '%s';",
				carPlateField.getText());
		try {
			int result = this.rss.getSqLiteConnector().executeSQLUpdate(sql);
			if (result == 0) {
				carErrorText.setText("Oops, Deletion failed. Please try it again.");
			} else {
				loadCars();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
