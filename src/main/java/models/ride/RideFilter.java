package models.ride;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RideFilter {

	private LocalDate beginDate, endDate;
	private Boolean toUC, fromUC;
	private String spRequest, username;

	public RideFilter() {
		toUC = false;
		fromUC = false;
	}

	public String getQuery() {
		List<String> rawList = new ArrayList<>();
		rawList.add(getBeginDate());
		rawList.add(getEndDate());
		rawList.add(getDirection());
		rawList.add(getSpRequest());
		rawList.add(getUsername());
		rawList.add(getRideStatus());

		List<String> noNullList = new ArrayList<>();

		for (String string : rawList) {
			if (string != null) {
				noNullList.add(string);
			}
		}
		if (noNullList.isEmpty()) return null;

		return String.join(" AND ", noNullList);
	}

	public Boolean isValid() {
		// if both toUC and fromUC are false, then no need to query
		return (toUC || fromUC);
	}

	private String getUsername() {
		return username != null ? "username != '" + username + "'" : null;
	}
	private String getBeginDate() {
		return beginDate != null ? "rideDate >= '" + beginDate.toString() + "'" : null;
	}

	private String getEndDate() {
		return endDate != null ? "rideDate <= '" + endDate.toString() + "'": null;
	}

	private String getDirection() {
		if (toUC && fromUC) {
			return null;
		} else if (toUC) {
			return "direction = 'To UC'";
		} else if (fromUC) {
			return "direction = 'From UC'";
		}
		return null;
	}

	private String getSpRequest() {
		if (spRequest == null) {
			return "trimmed LIKE '%'";
		} else {
			String request = "%" + spRequest + "%";
			request = request.toLowerCase().replaceAll("[^a-z0-9+]", "%");
			return "trimmed LIKE '" + request + "'";
		}
	}

	private String getRideStatus() {
		return "status = 'Active'";
	}

	public void setBeginDate(LocalDate beginDate) {
		this.beginDate = beginDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public void setToUC(Boolean toUC) {
		this.toUC = toUC;
	}

	public void setFromUC(Boolean fromUC) {
		this.fromUC = fromUC;
	}

	public void setSpRequest(String spRequest) {
		this.spRequest = spRequest;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
