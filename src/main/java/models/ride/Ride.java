package models.ride;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import models.Trip;

import java.util.ArrayList;
import java.util.List;

public class Ride {

	private Integer rideId, tripId;
	private SimpleStringProperty alias, date, username;
	private SimpleIntegerProperty seatNo;

	// properties don't belong to ride database schema
	private SimpleIntegerProperty seatBooked;
	private SimpleStringProperty direction;
	private Trip trip;

	public Ride() {
		this.rideId = null;
		this.tripId = null;
		this.alias = new SimpleStringProperty();
		this.seatNo = new SimpleIntegerProperty();
		this.date = new SimpleStringProperty();
		this.username = new SimpleStringProperty();

		this.trip = null;
		this.direction = new SimpleStringProperty();
		this.seatBooked = new SimpleIntegerProperty();
	}

	public void setRide(Ride ride) {
		this.rideId = ride.getRideId();
		this.tripId = ride.getTripId();
		setAlias(ride.getAlias());
		setSeatNo(ride.getSeatNo());
		setDate(ride.getDate());
		setUsername(ride.getUsername());

		this.trip = ride.getTrip();
		setDirection(ride.getDirection());
		setSeatBooked(ride.getSeatBooked());
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

	public String getDirection() {
		return direction.get();
	}

	public SimpleStringProperty directionProperty() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction.set(direction);
	}
}
