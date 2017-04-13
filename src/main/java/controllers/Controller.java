package controllers;

import javafx.stage.Stage;

public abstract class Controller {

	protected RSS rss;
	protected Stage stage;

	public RSS getRSS() {
		return rss;
	}

	public void setRSS(RSS rss) {
		this.rss = rss;
		afterSetRSS();
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	protected abstract void afterSetRSS();

}
