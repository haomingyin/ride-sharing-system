package models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Ride {

	private int rideId, tripId;
	private SimpleStringProperty alias;
	private SimpleIntegerProperty seatNo;

	public Ride(int rideId, int tripId, String alias, int seatNo) {
		this.rideId = rideId;
		this.tripId = tripId;
		this.alias = new SimpleStringProperty(alias);
		this.seatNo = new SimpleIntegerProperty(seatNo);
	}

	public int getRideId() {
		return rideId;
	}

	public void setRideId(int rideId) {
		this.rideId = rideId;
	}

	public int getTripId() {
		return tripId;
	}

	public void setTripId(int tripId) {
		this.tripId = tripId;
	}

	public String getAlias() {
		return alias.get();
	}

	public SimpleStringProperty aliasProperty() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias.set(alias);
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
