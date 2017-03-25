package models;

import controllers.*;
import javafx.stage.Stage;

public class RSS {

	private User user;
	private Stage pStage;
	private MenuController menuController;
	private CarController carController;
	private SQLiteConnector sqLiteConnector;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Stage getpStage() {
		return pStage;
	}

	public void setpStage(Stage pStage) {
		this.pStage = pStage;
	}

	public MenuController getMenuController() {
		return menuController;
	}

	public void setMenuController(MenuController menuController) {
		this.menuController = menuController;
	}

	public CarController getCarController() {
		return carController;
	}

	public void setCarController(CarController carController) {
		this.carController = carController;
	}

	public SQLiteConnector getSqLiteConnector() {
		return sqLiteConnector;
	}

	public void setSqLiteConnector(SQLiteConnector sqLiteConnector) {
		this.sqLiteConnector = sqLiteConnector;
	}
}
