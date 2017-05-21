package steps;

import controllers.BookedRideController;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.TestUtility;
import models.User;
import models.database.SQLExecutor;
import models.notification.Notification;
import models.ride.RideFilter;
import models.ride.RideInstance;
import models.ride.Status;
import org.junit.Assert;

import java.util.List;

public class CancelRide {

	private RideInstance ri;
	private User driver, passenger;

	@Before
	public void setUp() {
		TestUtility.beforeTest();
	}

	@Given("^passenger \"([^\"]*)\" books ride \"([^\"]*)\" to \"([^\"]*)\"$")
	public void passengerBooksRideTo(String username, String direction, String address) throws Throwable {
		passenger = new User();
		passenger.setUsername(username);

		RideFilter rf = new RideFilter();
		rf.setUsername(username);
		rf.setToUC(direction.equals("To UC"));
		rf.setFromUC(direction.equals("From UC"));
		rf.setSpRequest(address);
		List<RideInstance> lis = SQLExecutor.fetchRideInstancesByRideFilter(rf);

		if (lis != null) {
			ri = lis.get(0);
			driver = new User();
			driver.setUsername(ri.getUsername());
			ri.setPassengerId(username);
			SQLExecutor.addRideInstance(ri);
		}
	}

	@When("^passenger cancelled the ride$")
	public void passengerCancelledTheRide() throws Throwable {
		List<RideInstance> ris = SQLExecutor.fetchBookedRideInstanceByUser(passenger);
		System.out.println(ris);
		if (ris != null) {
			ris.forEach(r -> {
				if (r.getRideId().equals(ri.getRideId())) {
					r.setComment("test");
					Assert.assertEquals(1, SQLExecutor.updateRideInstanceStatus(ri, Status.CANCELLED_BY_PASSENGER));
					new BookedRideController().sendNotification(ri);
				}
			});
		}
	}

	@Then("^the driver should receive a notification$")
	public void theDriverShouldReceiveANotification() throws Throwable {
		List<Notification> nos = SQLExecutor.fetchNotificationsByUser(driver);
		Assert.assertEquals(1, nos.size());
	}

}
