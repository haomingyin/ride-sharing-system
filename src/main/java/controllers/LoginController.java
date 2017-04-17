package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import models.User;
import models.database.SQLExecutor;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Haoming on 15/03/17.
 */

public class LoginController extends Controller implements Initializable {

	@FXML
	private PasswordField passwordField, rePasswordField;
	@FXML
	private TextField usernameField;
	@FXML
	private Text loginPromptText;
	@FXML
	private GridPane signupPane, loginBtnPane;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	public void pressLoginBtn() {
		try {
			loginPromptText.setVisible(true);
			if (validLogin()) {
				loginPromptText.setText("Welcome!");
				this.rss.showCarView();
			} else {
				loginPromptText.setText("Oops! Our system cannot match this username and password.");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void pressSignupBtn() {
		rss.showSignUpView();
	}


	private boolean validLogin() {
		User user = SQLExecutor.fetchUser(usernameField.getText(), passwordField.getText());
		if (user != null) {
			this.rss.setUser(user);
			return true;
		}
		return false;
	}

	private void cleanTextFields() {
		usernameField.setText("");
		passwordField.setText("");
		rePasswordField.setText("");
	}

	@Override
	protected void afterSetRSS() {}

}
