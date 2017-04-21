package models.ride;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import models.Trip;
import models.User;

import java.time.LocalDate;

public class Ride {

	private Integer rideId, tripId;
	private SimpleStringProperty alias, date, username, rideStatus;
	private SimpleIntegerProperty seatNo;

	// properties don't belong to ride database schema
	private SimpleIntegerProperty seatBooked, seatLeft;
	private Trip trip;
	private User driver;

	public Ride() {
		this.alias = new SimpleStringProperty();
		this.seatNo = new SimpleIntegerProperty(0);
		this.date = new SimpleStringProperty();
		this.username = new SimpleStringProperty();
		this.rideStatus = new SimpleStringProperty(Status.NULL.toString());

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
		setRideStatus(ride.getRideStatus());
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

	public LocalDate getLocalDate() {
		return LocalDate.parse(date.get());
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

	public String getRideStatus() {
		return rideStatus.get();
	}

	public SimpleStringProperty rideStatusProperty() {
		return rideStatus;
	}

	public void setRideStatus(String rideStatus) {
		this.rideStatus.set(rideStatus);
	}

	public User getDriver() {
		return driver;
	}

	public void setDriver(User driver) {
		this.driver = driver;
	}
}
