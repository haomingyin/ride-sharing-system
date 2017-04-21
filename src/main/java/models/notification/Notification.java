package models.notification;

import java.time.LocalDateTime;

public class Notification {

	private String recipient;
	private String message;
	private Integer nId;
	private LocalDateTime timeStamp;

	public Notification(String recipient, String message) {
		this.recipient = recipient;
		this.message = message;
	}

	public Notification() {};

	public Integer getnId() {
		return nId;
	}

	public void setnId(Integer nId) {
		this.nId = nId;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(LocalDateTime timeStamp) {
		this.timeStamp = timeStamp;
	}
}
