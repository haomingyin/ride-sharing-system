package models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;
import java.util.Map;

public class Trip {

	private Integer tripId, routeId, day;
	private SimpleStringProperty alias, username, direction;
	private SimpleIntegerProperty carId;
	private LocalDate beginDate, expireDate;

	// properties not existed in database schema
	private Car car;
	private Map<Integer, StopPoint> stopPoints;

	public Trip() {
		this.tripId = null;
		this.alias = new SimpleStringProperty();
		this.username = new SimpleStringProperty();
		this.direction = new SimpleStringProperty();
		this.carId = new SimpleIntegerProperty();
		this.routeId = null;
		this.beginDate = null;
		this.expireDate = null;
		this.day = null;
	}

	@Override
	public String toString() {
		return getAlias();
	}

	public Integer getTripId() {
		return tripId;
	}

	public void setTripId(Integer tripId) {
		this.tripId = tripId;
	}

	public Integer getRouteId() {
		return routeId;
	}

	public void setRouteId(Integer routeId) {
		this.routeId = routeId;
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

	public String getUsername() {
		return username.get();
	}

	public SimpleStringProperty usernameProperty() {
		return username;
	}

	public void setUsername(String username) {
		this.username.set(username);
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

	public int getCarId() {
		return carId.get();
	}

	public SimpleIntegerProperty carIdProperty() {
		return carId;
	}

	public void setCarId(int carId) {
		this.carId.set(carId);
	}

	public LocalDate getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(LocalDate beginDate) {
		this.beginDate = beginDate;
	}

	public LocalDate getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(LocalDate expireDate) {
		this.expireDate = expireDate;
	}

	public Map<Integer, StopPoint> getStopPoints() {
		return stopPoints;
	}

	public void setStopPoints(Map<Integer, StopPoint> stopPoints) {
		this.stopPoints = stopPoints;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public Car getCar() {
		return car;
	}

	public void setCar(Car car) {
		this.car = car;
	}
}
