package models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.HashMap;

public class Route {

	private SimpleIntegerProperty routeId;
	private SimpleStringProperty alias, username;
	private HashMap<Integer, StopPoint> stopPoints;

	public Route(Integer routeId, String alias) {
		this.routeId = new SimpleIntegerProperty(routeId);
		this.alias = new SimpleStringProperty(alias);
		this.username = new SimpleStringProperty();
		this.stopPoints = new HashMap<>();
	}

	public Route() {
		this.routeId = new SimpleIntegerProperty();
		this.alias = new SimpleStringProperty();
		this.stopPoints = new HashMap<>();
		this.username = new SimpleStringProperty();
	}

	@Override
	public String toString() {
		return getAlias().toString();
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

	public HashMap<Integer, StopPoint> getStopPoints() {
		return stopPoints;
	}

	public void setStopPoints(HashMap<Integer, StopPoint> stopPoints) {
		this.stopPoints = stopPoints;
	}

	public int getRouteId() {
		return routeId.get();
	}

	public SimpleIntegerProperty routeIdProperty() {
		return routeId;
	}

	public void setRouteId(int routeId) {
		this.routeId.set(routeId);
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
}
