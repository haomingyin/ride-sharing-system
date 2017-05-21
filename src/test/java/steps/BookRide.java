package steps;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.TestUtility;
import models.User;
import models.database.SQLExecutor;
import models.ride.RideFilter;
import models.ride.RideInstance;
import org.junit.Assert;

import java.util.List;

public class BookRide {

	private RideFilter rf;
	private RideInstance ri;
	private String username;
	private User user;

	@Before
	public void setUp() {
		TestUtility.beforeTest();
	}

	@Given("^passenger \"([^\"]*)\"$")
	public void passenger(String username) throws Throwable {
		rf = new RideFilter();
		rf.setUsername(username);
		this.username = username;
		user = new User();
		user.setUsername(username);
	}

	@When("^searches a ride \"([^\"]*)\" to \"([^\"]*)\"$")
	public void searchesARideTo(String direction, String address) throws Throwable {
		rf.setToUC(direction.equals("To UC"));
		rf.setFromUC(direction.equals("From UC"));
		rf.setSpRequest(address);
	}

	@When("^there are available seats$")
	public void thereAreAvailableSeats() throws Throwable {
		List<RideInstance> lis = SQLExecutor.fetchRideInstancesByRideFilter(rf);
		if (lis != null) {
			ri = lis.get(0);
			Assert.assertTrue(ri.getSeatLeft() > 0);
		}
	}

	@Then("^user can book seat$")
	public void userCanBookSeat() throws Throwable {
		if (ri != null) {
			ri.setPassengerId(username);
			Assert.assertEquals(1, SQLExecutor.addRideInstance(ri));
		}
	}

	@Then("^seat no will decrement by one$")
	public void seatNoWillDecrementBy() throws Throwable {
		List<RideInstance> lis = SQLExecutor.fetchRideInstancesByRideFilter(rf);
		if (lis != null) {
			lis.forEach(r -> {
				if (r.getRideId().equals(ri.getRideId())) {
					// new ride instance has one less seat available
					Assert.assertTrue(ri.getSeatLeft() - 1 == r.getSeatLeft());
				}
			});
		}
	}

	@Then("^passenger can view the booked ride$")
	public void passengerCanViewTheBookedRide() throws Throwable {
		List<RideInstance> ris = SQLExecutor.fetchBookedRideInstanceByUser(user);
		boolean foundRide = false;
		if (ris != null) {
			for (RideInstance r : ris){
				if (r.getRideId().equals(ri.getRideId())) {
					foundRide = true;
				}
			}
		}
		Assert.assertTrue(foundRide);
	}
}
