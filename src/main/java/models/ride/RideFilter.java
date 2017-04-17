package models.ride;

import models.Trip;

import java.time.LocalDate;

public class RideFilter {

	private LocalDate beginDate, endDate;
	private Boolean toUC, fromUC;
	private String spRequest;

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

	public String getSpRequest() {
		return spRequest;
	}

	public void setSpRequest(String spRequest) {
		this.spRequest = spRequest;
	}
}
