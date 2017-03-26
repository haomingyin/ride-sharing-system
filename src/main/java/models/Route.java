package models;

import java.util.HashMap;

public class Route {

	private int routeId;
	private String alias;
	private HashMap<Integer, StopPoint> stopPoints;

	public Route(int routeId, String alias) {
		this.routeId = routeId;
		this.alias = alias;
		this.stopPoints = new HashMap<>();
	}

	public int getRouteId() {
		return routeId;
	}

	public void setRouteId(int routeId) {
		this.routeId = routeId;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public HashMap<Integer, StopPoint> getStopPoints() {
		return stopPoints;
	}

	public void setStopPoints(HashMap<Integer, StopPoint> stopPoints) {
		this.stopPoints = stopPoints;
	}
}
