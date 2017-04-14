package models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;
import java.util.HashMap;

public class Trip {

	private int tripId;
	private Integer routeId;
	private SimpleStringProperty alias, username, direction;
	private SimpleIntegerProperty carId;
	private LocalDate beginDate, expireDate;
	private HashMap<Integer, StopPoint> stopPointHashMap;

	public Trip(int tripId, String alias, String username, String direction, Integer carId) {
		this.tripId = tripId;
		this.alias = new SimpleStringProperty(alias);
		this.username = new SimpleStringProperty(username);
		this.direction = new SimpleStringProperty(direction);
		this.carId = new SimpleIntegerProperty(carId);
		this.routeId = null;
		this.beginDate = null;
		this.expireDate = null;
		this.stopPointHashMap = new HashMap<Integer, StopPoint>();
	}

	public int getTripId() {
		return tripId;
	}

	public void setTripId(int tripId) {
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

	public HashMap<Integer, StopPoint> getStopPointHashMap() {
		return stopPointHashMap;
	}

	public void setStopPointHashMap(HashMap<Integer, StopPoint> stopPointHashMap) {
		this.stopPointHashMap = stopPointHashMap;
	}
}
