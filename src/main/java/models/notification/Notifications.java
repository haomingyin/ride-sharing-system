package models.notification;

import models.User;
import models.database.SQLExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Notifications extends Observable implements Runnable {

	private User user;
	private boolean terminate, pause;
	private List<Notification> notifications;

	public Notifications(User user) {
		this.user = user;
		this.terminate = false;
		this.pause = false;
	}

	@Override
	public void run() {
		notifications = new ArrayList<>();
		while (!terminate) {
			notifications = pause ? null : SQLExecutor.fetchNotificationsByUser(user);

			if (notifications != null) {
				setChanged();
				notifyObservers(notifications);
			}

			// fetch new notifications after 5 seconds
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				System.out.println("Notifications has been interrupted. " + e.getMessage());
				break;
			}
		}
	}

	public void terminate() {
		terminate = true;
	}

	public void pause() {
		this.pause = true;
	}

	public void resume() {
		this.pause = false;
	}

	public List<Notification> getNotifications() {
		return notifications;
	}
}
