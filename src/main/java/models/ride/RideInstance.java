package models.ride;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import models.position.StopPoint;
import models.User;

public class RideInstance extends Ride {

	private Integer spId;
	private StopPoint stopPoint;
	private User passenger;
	private SimpleStringProperty status, passengerId, fullAddress, passengerName, time, comment;
	private SimpleDoubleProperty price;

	public RideInstance() {
		super();
		this.status = new SimpleStringProperty();
		this.passengerId = new SimpleStringProperty();
		this.price = new SimpleDoubleProperty(0);
		this.fullAddress = new SimpleStringProperty();
		this.passengerName = new SimpleStringProperty();
		this.time = new SimpleStringProperty();
		this.comment = new SimpleStringProperty();
	}

	public Integer getSpId() {
		return spId;
	}

	public void setSpId(Integer spId) {
		this.spId = spId;
	}

	public StopPoint getStopPoint() {
		return stopPoint;
	}

	public void setStopPoint(StopPoint stopPoint) {
		this.stopPoint = (StopPoint) stopPoint;
	}

	public User getPassenger() {
		return passenger;
	}

	public void setPassenger(User passenger) {
		this.passenger = passenger;
	}

	public String getStatus() {
		return status.get();
	}

	public SimpleStringProperty statusProperty() {
		return status;
	}

	public void setStatus(String status) {
		this.status.set(status);
	}

	public String getPassengerId() {
		return passengerId.get();
	}

	public SimpleStringProperty passengerIdProperty() {
		return passengerId;
	}

	public void setPassengerId(String passengerId) {
		this.passengerId.set(passengerId);
	}

	public double getPrice() {
		return price.get();
	}

	public SimpleDoubleProperty priceProperty() {
		return price;
	}

	public void setPrice(double price) {
		this.price.set(price);
	}

	public String getFullAddress() {
		setFullAddress(stopPoint.getFull());
		return fullAddress.get();
	}

	public SimpleStringProperty fullAddressProperty() {
		setFullAddress(stopPoint.getFull());
		return fullAddress;
	}

	public String getPassengerName() {
		setPassengerName(passenger.getName());
		return passengerName.get();
	}

	public SimpleStringProperty passengerNameProperty() {
		setPassengerName(passenger.getName());
		return passengerName;
	}

	public void setFullAddress(String fullAddress) {
		this.fullAddress.set(fullAddress);
	}

	public void setPassengerName(String passengerName) {
		this.passengerName.set(passengerName);
	}

	public String getTime() {
		setTime(stopPoint.getTime());
		return time.get();
	}

	public SimpleStringProperty timeProperty() {
		setTime(stopPoint.getTime());
		return time;
	}

	public void setTime(String time) {
		this.time.set(time);
	}

	public String getComment() {
		return comment.get();
	}

	public SimpleStringProperty commentProperty() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment.set(comment);
	}
}
