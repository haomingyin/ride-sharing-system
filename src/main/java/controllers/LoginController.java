package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import models.User;
import models.database.SQLExecutor;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for login view.
 * Created by Haoming on 15/03/17.
 */

public class LoginController extends Controller implements Initializable {

	@FXML
	private PasswordField passwordField;
	@FXML
	private TextField usernameField;
	@FXML
	private Text loginPromptText;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	public void pressLoginBtn() {
		try {
			loginPromptText.setVisible(true);
			if (!validLogin()) {
				loginPromptText.setText("Oops! Our system cannot match this username and password.");
			}
		} catch (Exception e) {
			System.out.println("Error occurred when log the user in.");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void pressSignupBtn() {
		rss.showSignUpView();
	}


	private boolean validLogin() {
		User user = SQLExecutor.fetchUser(usernameField.getText(), passwordField.getText());
		if (user != null) {
			rss.setUser(user);
			rss.login();
			return true;
		}
		return false;
	}

	@Override
	protected void afterSetRSS() {}

}
