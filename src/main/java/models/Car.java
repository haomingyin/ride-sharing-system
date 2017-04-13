package models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Car {

	private SimpleStringProperty plate, username, model, manufacturer, color;
	private SimpleIntegerProperty carId, year, seatNo;

	public Car(String plate, String username, String model, String manufacturer, String color, int year, int seatNo) {
		this.carId = new SimpleIntegerProperty();
		this.plate = new SimpleStringProperty(plate);
		this.username = new SimpleStringProperty(username);
		this.model = new SimpleStringProperty(model);
		this.manufacturer = new SimpleStringProperty(manufacturer);
		this.color = new SimpleStringProperty(color);
		this.year = new SimpleIntegerProperty(year);
		this.seatNo = new SimpleIntegerProperty(seatNo);
	}

	public Car() {
		this.carId = new SimpleIntegerProperty();
		this.plate = new SimpleStringProperty();
		this.username = new SimpleStringProperty();
		this.model = new SimpleStringProperty();
		this.manufacturer = new SimpleStringProperty();
		this.color = new SimpleStringProperty();
		this.year = new SimpleIntegerProperty();
		this.seatNo = new SimpleIntegerProperty();
	}

	public int getCarId() {
		return carId.get();
	}

	public SimpleIntegerProperty carIdProperty() {
		return carId;
	}

	public void setCarId(int carId) {
		this.carId.set(carId);
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

	public String getUsername() {
		return username.get();
	}

	public SimpleStringProperty usernameProperty() {
		return username;
	}

	public void setUsername(String username) {
		this.username.set(username);
	}

	public String getModel() {
		return model.get();
	}

	public SimpleStringProperty modelProperty() {
		return model;
	}

	public void setModel(String model) {
		this.model.set(model);
	}

	public String getManufacturer() {
		return manufacturer.get();
	}

	public SimpleStringProperty manufacturerProperty() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer.set(manufacturer);
	}

	public String getColor() {
		return color.get();
	}

	public SimpleStringProperty colorProperty() {
		return color;
	}

	public void setColor(String color) {
		this.color.set(color);
	}

	public int getYear() {
		return year.get();
	}

	public SimpleIntegerProperty yearProperty() {
		return year;
	}

	public void setYear(int year) {
		this.year.set(year);
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
}
