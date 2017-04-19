package models.position;

import javafx.beans.property.SimpleStringProperty;

public class StopPoint extends Position {

	private int spId;
	private SimpleStringProperty streetNo, street, suburb, city, time, full;

	public StopPoint() {
		this.spId = -1;
		this.streetNo = new SimpleStringProperty();
		this.street = new SimpleStringProperty();
		this.suburb = new SimpleStringProperty();
		this.city = new SimpleStringProperty();
		this.time = new SimpleStringProperty();
		this.full = new SimpleStringProperty();
	}
	@Override
	public String toString() {
		return String.format("%s %s, %s, %s", streetNo.get(), street.get(), suburb.get(), city.get());
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

	public String getFull() {
		setFull();
		return full.get();
	}

	public SimpleStringProperty fullProperty() {
		setFull();
		return full;
	}

	private void setFull() {
		this.full.set(toString());
	}
}
