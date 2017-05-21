package steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.User;
import models.database.SQLExecutor;
import org.junit.Assert;

public class UserLogin {

	private String username;
	private User user;

	@Given("^user \"([^\"]*)\" is logging to RSS$")
	public void userIsLoggingToRSS(String username) throws Throwable {
		this.username = username;
	}

	@When("^user enter \"([^\"]*)\"$")
	public void userEnter(String password) throws Throwable {
		user = SQLExecutor.fetchUser(username, password);
	}

	@Then("^login succeeds$")
	public void loginSucceeds() throws Throwable {
		Assert.assertTrue(user != null);
		Assert.assertEquals(username, user.getUsername());
	}

	@Then("^login fails$")
	public void loginFails() throws Throwable {
		Assert.assertTrue(user == null);
	}
}
