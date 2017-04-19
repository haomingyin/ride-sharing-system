package models.position;

public class Position {

	private Double lat, lng, distance;

	public Position(){};

	public Position(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}

	public static Position getUniPosition() {
		Position uni = new Position(-43.5235375d, 172.5839233d);
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

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}
}
