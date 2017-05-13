package models.notification;

import models.Car;
import models.User;
import models.database.SQLExecutor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ExpirationUtility {


	/**
	 * Calculates when is the next notification date.
	 *
	 * @param expireDate
	 * @return the date the next notifications should occurs
	 */
	public static LocalDate getRemindDate(LocalDate expireDate) {
		LocalDate now = LocalDate.now();

		if (expireDate.minusMonths(1).isAfter(now)) {
			return expireDate.minusMonths(1);
		} else if (expireDate.minusWeeks(2).isAfter(now)) {
			return expireDate.minusWeeks(2);
		} else if (expireDate.minusWeeks(1).isAfter(now)) {
			return expireDate.minusWeeks(1);
		} else {
			return expireDate;
		}
	}

	static List<Notification> fetchExpirationNotifications(User user) {
		List<Notification> nos = new ArrayList<>();

		Notification licenceNotification = fetchLicenceExpirationNotification(user);
		if (licenceNotification != null) {
			nos.add(licenceNotification);
		}

		nos.addAll(fetchCarExpirationNotifications(user));
		return nos;
	}

	private static Notification fetchLicenceExpirationNotification(User user) {
		LocalDate remindDate = SQLExecutor.fetchLicenceRemindDate(user);
		if (!remindDate.isAfter(LocalDate.now())) {
			SQLExecutor.updateLicenceRemindDate(user, false);
			Notification notification = new Notification();
			notification.setRecipient(user.getUsername());
			notification.setMessage(String.format("Your driver licence expires on %s.",
					user.getExpireDate().toString()));
			return notification;
		}
		return null;
	}

	private static List<Notification> fetchCarExpirationNotifications(User user) {
		List<Notification> nos = new ArrayList<>();
		List<Car> cars = SQLExecutor.fetchCarsByUser(user);
		for (Car car : cars) {
			LocalDate remindWofDate = SQLExecutor.fetchWofRemindDate(car);
			if (!remindWofDate.isAfter(LocalDate.now())) {
				SQLExecutor.updateWofRemindDate(car, false);
				Notification wofNo = new Notification();
				wofNo.setRecipient(user.getUsername());
				wofNo.setMessage(String.format("The WOF of your car [%s] expires on %s.",
						car.getPlate(), car.getWof()));
				nos.add(wofNo);
			}

			LocalDate remindRegoDate = SQLExecutor.fetchRegoRemindDate(car);
			if (!remindRegoDate.isAfter(LocalDate.now())) {
				SQLExecutor.updateRegoRemindDate(car, false);
				Notification regoNo = new Notification();
				regoNo.setRecipient(user.getUsername());
				regoNo.setMessage(String.format("The registration of your car [%s] expires on %s.",
						car.getPlate(), car.getRegistration()));
				nos.add(regoNo);
			}
		}
		return nos;
	}
}
