package models.notification;

import models.User;
import models.database.SQLExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Notifications extends Observable implements Runnable {

	private User user;
	private boolean terminate;
	private List<Notification> notifications;

	public Notifications(User user) {
		this.user = user;
		this.terminate = false;
	}

	@Override
	public void run() {
		notifications = new ArrayList<>();
		notifications = ExpirationUtility.fetchExpirationNotifications(user);
		if (notifications != null) {
			setChanged();
			notifyObservers(notifications);

			// fetch new notifications after 5 seconds
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				System.out.println("Notifications has been interrupted. " + e.getMessage());
			}
		}

		while (!terminate) {
			notifications = SQLExecutor.fetchNotificationsByUser(user);

			if (notifications != null) {
				notifications.forEach(SQLExecutor::deleteNotification);
				setChanged();
				notifyObservers(notifications);
			}

			// fetch new notifications after 10 seconds
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				System.out.println("Notifications has been interrupted. " + e.getMessage());
				break;
			}
		}
	}

	public void terminate() {
		terminate = true;
	}

	public List<Notification> getNotifications() {
		return notifications;
	}
}
