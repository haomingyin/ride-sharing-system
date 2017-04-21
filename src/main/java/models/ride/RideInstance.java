package models.ride;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import models.position.StopPoint;
import models.User;

public class RideInstance extends Ride {

	private Integer spId;
	private StopPoint stopPoint;
	private User passenger;
	private SimpleStringProperty status, passengerId, comment;
	private SimpleDoubleProperty price;

	public RideInstance() {
		super();
		this.status = new SimpleStringProperty(Status.NULL.toString());
		this.passengerId = new SimpleStringProperty();
		this.price = new SimpleDoubleProperty(0);
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

	public String getComment() {
		return comment.get();
	}

	public SimpleStringProperty commentProperty() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment.set(comment);
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
}
