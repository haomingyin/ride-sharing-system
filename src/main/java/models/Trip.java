package models;

import java.time.LocalDate;
import java.util.HashMap;

public class Trip {

	private int tripId;
	private String alias, username, direction, plate;
	private LocalDate beginDate, expireDate;
	private HashMap<Integer, StopPoint> stopPointHashMap;

	public Trip(int tripId, String alias, String username, String direction, String plate) {
		this.tripId = tripId;
		this.alias = alias;
		this.username = username;
		this.direction = direction;
		this.plate = plate;
		this.beginDate = null;
		this.expireDate = null;
		stopPointHashMap = new HashMap<Integer, StopPoint>();
	}

	public Trip() {
		stopPointHashMap = new HashMap<Integer, StopPoint>();
	}

	public int getTripId() {
		return tripId;
	}

	public void setTripId(int tripId) {
		this.tripId = tripId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public HashMap<Integer, StopPoint> getStopPointHashMap() {
		return stopPointHashMap;
	}

	public void addStopPoint(StopPoint stopPoint) {
		stopPointHashMap.put(stopPoint.getSpId(), stopPoint);
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getPlate() {
		return plate;
	}

	public void setPlate(String plate) {
		this.plate = plate;
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

	public void setStopPointHashMap(HashMap<Integer, StopPoint> stopPointHashMap) {
		this.stopPointHashMap = stopPointHashMap;
	}


}
