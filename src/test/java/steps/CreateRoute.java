package steps;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.Route;
import models.TestUtility;
import models.User;
import models.database.SQLExecutor;
import models.position.StopPoint;
import org.junit.Assert;

public class CreateRoute {

	private User user;
	private int result;
	private String alias;

	@Before
	public void setUp() {
		TestUtility.beforeTest();
	}

	@Given("^user \"([^\"]*)\"$")
	public void user(String username) throws Throwable {
		user = new User();
		user.setUsername(username);
	}

	@When("^user create route with alias \"([^\"]*)\"$")
	public void user_create_route_with_alias(String alias) throws Throwable {
		Route route = new Route();
		this.alias = alias;
		route.setAlias(alias);
		route.setUsername(user.getUsername());
		result = SQLExecutor.addRoute(route);
	}

	@Then("^route will be created$")
	public void route_will_be_created() throws Throwable {
		Assert.assertEquals(1, result);
	}

	@When("^user search and add \"([^\"]*)\"$")
	public void user_search_and_add(String address) throws Throwable {
		StopPoint sp = SQLExecutor.fetchStopPointsByString(address, 1).get(0);
		Route route;
		result = 0;
		for (Route r : SQLExecutor.fetchRoutesByUser(user)) {
			if (r.getAlias().equals(alias)) {
				route = r;
				result = SQLExecutor.addStopPointIntoRoute(route, sp);
				break;
			}
		}

	}

	@Then("^stop point will be added into route$")
	public void stop_point_will_be_added_into_route() throws Throwable {
		Assert.assertEquals(1, result);
	}
}
