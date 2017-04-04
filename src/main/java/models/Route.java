package models;

import javafx.beans.property.SimpleStringProperty;

import java.util.HashMap;

public class Route {

	private int routeId;
	private SimpleStringProperty alias;
	private HashMap<Integer, StopPoint> stopPoints;

	public Route(int routeId, String alias) {
		this.routeId = routeId;
		this.alias = new SimpleStringProperty(alias);
		this.stopPoints = new HashMap<>();
	}

	public int getRouteId() {
		return routeId;
	}

	public void setRouteId(int routeId) {
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

	public HashMap<Integer, StopPoint> getStopPoints() {
		return stopPoints;
	}

	public void setStopPoints(HashMap<Integer, StopPoint> stopPoints) {
		this.stopPoints = stopPoints;
	}
}
