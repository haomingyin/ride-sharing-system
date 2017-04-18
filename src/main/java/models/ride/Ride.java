package models.ride;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import models.Trip;

public class Ride {

	private Integer rideId, tripId;
	private SimpleStringProperty alias, date, username;
	private SimpleIntegerProperty seatNo;

	// properties don't belong to ride database schema
	private SimpleIntegerProperty seatBooked, seatLeft;
	private Trip trip;

	public Ride() {
		this.alias = new SimpleStringProperty();
		this.seatNo = new SimpleIntegerProperty(0);
		this.date = new SimpleStringProperty();
		this.username = new SimpleStringProperty();

		this.seatBooked = new SimpleIntegerProperty(0);
		this.seatLeft = new SimpleIntegerProperty(0);
	}

	public void setRide(Ride ride) {
		// only properties in ride database schema will be overwritten
		this.rideId = ride.getRideId();
		this.tripId = ride.getTripId();
		setAlias(ride.getAlias());
		setSeatNo(ride.getSeatNo());
		setDate(ride.getDate());
		setUsername(ride.getUsername());
	}

	@Override
	public String toString() {
		return getAlias();
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

	public Trip getTrip() {
		return trip;
	}

	public void setTrip(Trip trip) {
		this.trip = trip;
	}

	public void setRideId(Integer rideId) {
		this.rideId = rideId;
	}

	public Integer getRideId() {
		return rideId;
	}

	public Integer getTripId() {
		return tripId;
	}

	public void setTripId(Integer tripId) {
		this.tripId = tripId;
	}

	public String getDate() {
		return date.get();
	}

	public SimpleStringProperty dateProperty() {
		return date;
	}

	public void setDate(String date) {
		this.date.set(date);
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

	public int getSeatBooked() {
		return seatBooked.get();
	}

	public SimpleIntegerProperty seatBookedProperty() {
		return seatBooked;
	}

	public void setSeatBooked(int seatBooked) {
		this.seatBooked.set(seatBooked);
	}

	public int getSeatLeft() {
		setSeatLeft(getSeatNo() - getSeatBooked());
		return seatLeft.get();
	}

	public SimpleIntegerProperty seatLeftProperty() {
		setSeatLeft(getSeatNo() - getSeatBooked());
		return seatLeft;
	}

	public void setSeatLeft(int seatLeft) {
		this.seatLeft.set(seatLeft);
	}
}
