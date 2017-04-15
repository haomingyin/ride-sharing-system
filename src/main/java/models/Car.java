package models;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class Car {

	private SimpleStringProperty plate, username, model, manufacturer, color, wof, registration;
	private SimpleIntegerProperty carId, year, seatNo;
	private SimpleDoubleProperty performance;

	public Car() {
		this.carId = new SimpleIntegerProperty();
		this.plate = new SimpleStringProperty();
		this.username = new SimpleStringProperty();
		this.model = new SimpleStringProperty();
		this.manufacturer = new SimpleStringProperty();
		this.color = new SimpleStringProperty();
		this.year = new SimpleIntegerProperty();
		this.seatNo = new SimpleIntegerProperty();
		this.wof = new SimpleStringProperty();
		this.performance = new SimpleDoubleProperty();
		this.registration = new SimpleStringProperty();
	}

	@Override
	public String toString() {
		return getPlate();
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

	public String getWof() {
		return wof.get();
	}

	public SimpleStringProperty wofProperty() {
		return wof;
	}

	public void setWof(String wof) {
		this.wof.set(wof);
	}

	public double getPerformance() {
		return performance.get();
	}

	public SimpleDoubleProperty performanceProperty() {
		return performance;
	}

	public void setPerformance(double performance) {
		this.performance.set(performance);
	}

	public String getRegistration() {
		return registration.get();
	}

	public SimpleStringProperty registrationProperty() {
		return registration;
	}

	public void setRegistration(String registration) {
		this.registration.set(registration);
	}
}
