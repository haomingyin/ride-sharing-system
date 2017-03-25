package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import models.Car;
import models.RSS;
import models.User;

import java.awt.*;
import java.awt.TextField;
import java.net.URL;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.ResourceBundle;

public class CarController implements Initializable {

	private RSS rss;
	private HashMap<String, Car> cars;

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
	Button carSubmitBtn;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	public void setRSS(RSS rss) {
		this.rss = rss;
	}

	public void loadCars() {
		String sql = String.format("SELECT * " +
				"FROM car " +
				"WHERE owner = '%s';", this.rss.getUser().getUsername());
		try {
			ResultSet rs = this.rss.getSqLiteConnector().executeSQLQuery(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
