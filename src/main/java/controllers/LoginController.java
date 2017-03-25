package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import models.RSS;
import models.SQLiteConnector;
import models.User;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ResourceBundle;

/**
 * Created by Haoming on 15/03/17.
 */

public class LoginController implements Initializable {

	@FXML
	private PasswordField passwordField, rePasswordField;
	@FXML
	private TextField usernameField, emailField;
	@FXML
	private Text loginPromptText;
	@FXML
	private GridPane signupPane, loginBtnPane;
	@FXML
	private Button submitBtn;

	private Connection conn;
	private SQLiteConnector sqLiteConnector;
	private User user;
	private RSS rss;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		sqLiteConnector = new SQLiteConnector();
		sqLiteConnector.initializeDatabase();
	}

	public void pressLogin() {
		try {
			loginPromptText.setVisible(true);
			if (validLogin()) {
				loginPromptText.setText("Welcome!");
			} else {
				loginPromptText.setText("Oops! Our system cannot match this username and password.");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void pressSignup() {
		loginBtnPane.setVisible(false);
		signupPane.setVisible(true);
		submitBtn.setVisible(true);
	}

	public void pressSubmit() {
		String sql = String.format("SELECT count(username) as cnt " +
				"FROM user " +
				"WHERE username = '%s';", usernameField.getText());
		try {
			ResultSet rs = sqLiteConnector.executeSQLQuery(sql);
			int cnt = rs.getInt("cnt");
			if (usernameField.getText().equals("")) {
				loginPromptText.setText("Seriously? You haven't typed your username.");
			} else if (cnt > 0) {
				loginPromptText.setText(String.format("Uhh... username '%s' has already been registered.",
						usernameField.getText()));
			} else if (passwordField.getText().equals("")) {
				loginPromptText.setText("Excuse me! Could you please type your password =P");
			} else if (!passwordField.getText().equals(rePasswordField.getText())) {
				loginPromptText.setText("Oops, two passwords are not identical.");
			} else {
				sql = String.format("INSERT INTO user (username, password) " +
								"VALUES ('%s', '%s');",
						usernameField.getText(), passwordField.getText());
				int result = sqLiteConnector.executeSQLUpdate(sql);
				if (result == 1) {
					loginPromptText.setText("Hooray~ Welcome to our RRS!");
					pressLogin();
					loginBtnPane.setVisible(true);
					signupPane.setVisible(false);
					submitBtn.setVisible(false);
				} else {
					loginPromptText.setText("Sorry, something went wrong. Please register again.");
				}
				cleanTextFields();
			}
			loginPromptText.setVisible(true);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private boolean validLogin() {
		try {
			String sql = String.format("SELECT * " +
							"FROM user " +
							"WHERE username = '%s' AND password = '%s';",
					usernameField.getText(), passwordField.getText());
			ResultSet rs = sqLiteConnector.executeSQLQuery(sql);
			if (!rs.isClosed()) {
				user = new User();
				user.setUsername(rs.getString("username"));
				user.setPassword(rs.getString("password"));
				user.setEmail(rs.getString("email"));
				user.setFname(rs.getString("fName"));
				user.setLname(rs.getString("lName"));
				user.setPhone(rs.getString("phone"));
				this.rss.setUser(user);
				showCarView();
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	private void cleanTextFields() {
		usernameField.setText("");
		passwordField.setText("");
		rePasswordField.setText("");
		emailField.setText("");
	}

	public void setRSS(RSS rss) {
		this.rss = rss;
		this.rss.setSqLiteConnector(sqLiteConnector);
	}

	public void showCarView() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/CarView.fxml"));
			Parent carView = fxmlLoader.load();
			Scene scene = new Scene(carView);
			CarController carController = fxmlLoader.getController();
			this.rss.setCarController(carController);
			carController.setRSS(this.rss);
			this.rss.getpStage().setScene(scene);
			this.rss.getpStage().show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
