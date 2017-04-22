package models.position;

import javafx.beans.property.SimpleDoubleProperty;

public class Position {

	private Double lat, lng;
	private SimpleDoubleProperty distance;

	public Position() {
		this.distance = new SimpleDoubleProperty(-1);
	}

	public static Position getUniPosition() {
		Position uni = new Position();
		uni.setLat(-43.5235375d);
		uni.setLng(172.5839233d);
		uni.setDistance(0d);
		return uni;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	public double getDistance() {
		return distance.get();
	}

	public SimpleDoubleProperty distanceProperty() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance.set(distance);
	}
}
