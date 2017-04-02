package models;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.sql.Time;

public class StopPoint {

	private int spId;
	private SimpleStringProperty streetNo, street, suburb, city, time;

	public StopPoint(int spId, String streetNo, String street, String suburb, String city) {
		this.spId = spId;
		this.streetNo = new SimpleStringProperty(streetNo);
		this.street = new SimpleStringProperty(street);
		this.suburb = new SimpleStringProperty(suburb);
		this.city = new SimpleStringProperty(city);
		this.time = new SimpleStringProperty("");
	}

	public StopPoint(int spId, String streetNo, String street, String suburb, String city, String time) {
		this.spId = spId;
		this.streetNo = new SimpleStringProperty(streetNo);
		this.street = new SimpleStringProperty(street);
		this.suburb = new SimpleStringProperty(suburb);
		this.city = new SimpleStringProperty(city);
		this.time = new SimpleStringProperty(time);
	}

	public int getSpId() {
		return spId;
	}

	public void setSpId(int spId) {
		this.spId = spId;
	}

	public String getStreetNo() {
		return streetNo.get();
	}

	public SimpleStringProperty streetNoProperty() {
		return streetNo;
	}

	public void setStreetNo(String streetNo) {
		this.streetNo.set(streetNo);
	}

	public String getStreet() {
		return street.get();
	}

	public SimpleStringProperty streetProperty() {
		return street;
	}

	public void setStreet(String street) {
		this.street.set(street);
	}

	public String getSuburb() {
		return suburb.get();
	}

	public SimpleStringProperty suburbProperty() {
		return suburb;
	}

	public void setSuburb(String suburb) {
		this.suburb.set(suburb);
	}

	public String getCity() {
		return city.get();
	}

	public SimpleStringProperty cityProperty() {
		return city;
	}

	public void setCity(String city) {
		this.city.set(city);
	}

	public String getTime() {
		return time.get();
	}

	public SimpleStringProperty timeProperty() {
		return time;
	}

	public void setTime(String time) {
		this.time.set(time);
	}
}
