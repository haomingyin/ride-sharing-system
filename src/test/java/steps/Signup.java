package steps;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.TestUtility;
import models.User;
import models.database.SQLExecutor;
import org.junit.Assert;

import java.time.LocalDate;

public class Signup {

	private User user;
	private int result;

	@Before
	public void setUp() {
		TestUtility.beforeTest();
	}


	@Given("^user \"([^\"]*)\" with password \"([^\"]*)\"$")
	public void user_with_password(String username, String password) throws Throwable {
		user = new User();
		user.setUsername(username);
		user.setPassword(password);
	}

	@When("^sign up \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\"$")
	public void sign_up(String email, String fname, String lname, String address, String mphone, String licence, String licenceType, String issue, String expire) throws Throwable {
		user.setEmail(email);
		user.setfName(fname);
		user.setlName(lname);
		user.setAddress(address);
		user.setmPhone(mphone);
		user.setLicenceNo(licence);
		user.setLicenceType(licenceType);
		user.setIssueDate(LocalDate.parse(issue));
		user.setExpireDate(LocalDate.parse(expire));
		result = SQLExecutor.addUser(user);
	}

	@Then("^user data should be recorded$")
	public void user_data_should_be_recorded() throws Throwable {
		Assert.assertEquals(1, result);
	}

	@Then("^licence remind date should be one month before expiration date$")
	public void licence_remind_date_should_be_one_month_before_expiration_date() throws Throwable {
		String expected = user.getExpireDate().minusMonths(1).toString();
		String actual = SQLExecutor.fetchLicenceRemindDate(user).toString();
		Assert.assertEquals(expected, actual);
	}

}
