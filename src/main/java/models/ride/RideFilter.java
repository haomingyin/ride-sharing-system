package models.ride;

import models.Trip;

import java.time.LocalDate;

public class RideFilter {

	private Integer tripId;
	private LocalDate beginDate, endDate;
	private Boolean allTrips, toUC, fromUC, passenger, noPassenger;
	private String spRequest;
	private Trip trip;

	public RideFilter() {}

	public Integer getTripId() {
		return tripId;
	}

	public void setTripId(Integer tripId) {
		this.tripId = tripId;
	}

	public LocalDate getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(LocalDate beginDate) {
		this.beginDate = beginDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public Boolean getAllTrips() {
		return allTrips;
	}

	public void setAllTrips(Boolean allTrips) {
		this.allTrips = allTrips;
	}

	public Boolean getToUC() {
		return toUC;
	}

	public void setToUC(Boolean toUC) {
		this.toUC = toUC;
	}

	public Boolean getFromUC() {
		return fromUC;
	}

	public void setFromUC(Boolean fromUC) {
		this.fromUC = fromUC;
	}

	public Boolean getPassenger() {
		return passenger;
	}

	public void setPassenger(Boolean passenger) {
		this.passenger = passenger;
	}

	public Boolean getNoPassenger() {
		return noPassenger;
	}

	public void setNoPassenger(Boolean noPassenger) {
		this.noPassenger = noPassenger;
	}

	public String getSpRequest() {
		return spRequest;
	}

	public void setSpRequest(String spRequest) {
		this.spRequest = spRequest;
	}

	public Trip getTrip() {
		return trip;
	}

	public void setTrip(Trip trip) {
		this.trip = trip;
	}
}
