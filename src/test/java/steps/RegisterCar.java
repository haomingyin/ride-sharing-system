package steps;

import cucumber.api.PendingException;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.Car;
import models.TestUtility;
import models.User;
import models.database.SQLExecutor;
import org.junit.Assert;

import java.time.LocalDate;

public class RegisterCar {

	private Car car;
	private String username;
	private int result;

	@Before
	public void setUp() {
		TestUtility.beforeTest();
	}

	@Given("^\"([^\"]*)\" input car \"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\", (\\d+), (\\d+), \"([^\"]*)\", \"([^\"]*)\" ,(\\d+)$")
	public void inputCar(String username, String plate, String man, String model, String color, int year, int seatNo, String wof, String reg, double performance) throws Throwable {
		car = new Car();
		car.setUsername(username);
		car.setPlate(plate);
		car.setManufacturer(man);
		car.setModel(model);
		car.setColor(color);
		car.setYear(year);
		car.setSeatNo(seatNo);
		car.setWof(wof);
		car.setRegistration(reg);
		car.setPerformance(performance);
	}

	@When("^the user register the car$")
	public void theUserRegisterTheCar() throws Throwable {
		result = SQLExecutor.addCar(car);
	}

	@Then("^the car should be added into DB$")
	public void theCarShouldBeAddedIntoDB() throws Throwable {
		Assert.assertEquals(1, result);
	}

	@Then("^the car should not be added into DB$")
	public void theCarShouldNotBeAddedIntoDB() throws Throwable {
		Assert.assertEquals(2067, result);
	}

	@Then("^wof remind date should be one month before$")
	public void wof_remind_date_should_be_one_month_before() throws Throwable {
		User user = new User();
		user.setUsername(username);

		for (Car c : SQLExecutor.fetchCarsByUser(user)) {
			if (c.getPlate().equals(car.getPlate())) {
				String expected = LocalDate.parse(car.getWof()).minusMonths(1).toString();
				Assert.assertEquals(expected, SQLExecutor.fetchWofRemindDate(c).toString());
			}
		}
	}

	@Then("^reg remind date should be one month before$")
	public void reg_remind_date_should_be_one_month_before() throws Throwable {
		User user = new User();
		user.setUsername(username);

		for (Car c : SQLExecutor.fetchCarsByUser(user)) {
			if (c.getPlate().equals(car.getPlate())) {
				String expected = LocalDate.parse(car.getRegistration()).minusMonths(1).toString();
				Assert.assertEquals(expected, SQLExecutor.fetchRegoRemindDate(c).toString());
			}
		}
	}
}
