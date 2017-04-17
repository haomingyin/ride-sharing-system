package controllers;

import javafx.stage.Stage;

public abstract class Controller {

	protected RSS rss;
	Stage stage;

	public RSS getRSS() {
		return rss;
	}

	void setRSS(RSS rss) {
		this.rss = rss;
		afterSetRSS();
	}

	void setStage(Stage stage) {
		this.stage = stage;
	}

	protected abstract void afterSetRSS();

}
