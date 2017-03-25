package models;

import java.util.HashMap;

public class Route {

	private int routeId;
	private String owner;
	private HashMap<Integer, StopPoint> stopPointHashMap;

	public Route() {
		stopPointHashMap = new HashMap<Integer, StopPoint>();
	}

	public int getRouteId() {
		return routeId;
	}

	public void setRouteId(int routeId) {
		this.routeId = routeId;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public HashMap<Integer, StopPoint> getStopPointHashMap() {
		return stopPointHashMap;
	}

	public void addStopPoint(StopPoint stopPoint) {
		stopPointHashMap.put(stopPoint.getSpId(), stopPoint);
	}
}
