package steps;

import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.TestUtility;
import models.database.SQLExecutor;
import models.ride.RideFilter;
import models.ride.RideInstance;
import models.ride.Status;
import org.junit.Assert;

import java.util.List;

public class SearchRide {

	private RideFilter rf;
	private String username;

	@Before
	public void setUp() {
		TestUtility.beforeTest();
	}

	@When("^passenger \"([^\"]*)\" search rides$")
	public void passengerSearchRides(String username) throws Throwable {
		rf = new RideFilter();
		rf.setUsername(username);
		this.username = username;
	}

	@Then("^passenger can view all active rides$")
	public void passengerCanViewAllActiveRides() throws Throwable {
		List<RideInstance> ris = SQLExecutor.fetchRideInstancesByRideFilter(rf);
		if (ris != null)
		for (RideInstance ri : ris) {
			Assert.assertEquals(Status.ACTIVE.toString(), ri.getRideStatus());
		}
	}

	@When("^select \"([^\"]*)\"$")
	public void select(String direction) throws Throwable {
		rf.setToUC(direction.equals("To UC"));
		rf.setFromUC(direction.equals("From UC"));
	}

	@Then("^only ride from UC will be displayed$")
	public void onlyRideFromUCWillBeDisplayed() throws Throwable {
		List<RideInstance> ris = SQLExecutor.fetchRideInstancesByRideFilter(rf);
		if (ris != null)
		ris.forEach(ri -> Assert.assertEquals("From UC", ri.getTrip().getDirection()));
	}

	@Then("^no his rides will be displayes$")
	public void noHisRidesWillBeDisplayes() throws Throwable {
		List<RideInstance> ris = SQLExecutor.fetchRideInstancesByRideFilter(rf);
		if (ris != null)
		ris.forEach(ri -> Assert.assertTrue(!ri.getUsername().equals(username)));
	}


}
