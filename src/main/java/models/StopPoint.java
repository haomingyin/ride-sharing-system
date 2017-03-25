package models;

import java.sql.Time;

public class StopPoint {

	private int spId, streetNo;
	private String street, suburb;
	private Time time;

	public StopPoint(int spId, int streetNo, String street, String suburb) {
		this.spId = spId;
		this.streetNo = streetNo;
		this.street = street;
		this.suburb = suburb;
	}

	public int getSpId() {
		return spId;
	}

	public void setSpId(int spId) {
		this.spId = spId;
	}

	public int getStreetNo() {
		return streetNo;
	}

	public void setStreetNo(int streetNo) {
		this.streetNo = streetNo;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getSuburb() {
		return suburb;
	}

	public void setSuburb(String suburb) {
		this.suburb = suburb;
	}

	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
	}
}
