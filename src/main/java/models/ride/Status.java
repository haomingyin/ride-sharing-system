package models.ride;

/** Enums for ride and ride instance status
 * Created by Haoming on 20/04/17.
 */
public enum Status {

	BOOKED("Booked"),
	CANCELLED_BY_DRIVER("Driver Cancelled"),
	CANCELLED_BY_PASSENGER("Passenger Cancelled"),
	DONE("Done"),
	CANCELLED("Cancelled"),
	ACTIVE("Active");

	private String status;

	Status(final String text) {
		this.status = text;
	}

	@Override
	public String toString() {
		return status;
	}

	public boolean equals(String other) {
		String test = other.toLowerCase();

		if ((test.equals("driver cancelled") || test.equals("passenger cancelled"))
				&& status.toLowerCase().equals("cancelled")) {
			return true;
		}

		return status.toLowerCase().equals(test);
	}

}
