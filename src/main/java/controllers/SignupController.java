package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import models.User;
import models.database.SQLExecutor;

import java.io.Externalizable;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Haoming on 15/03/17.
 */

public class SignupController extends Controller implements Initializable {

	@FXML
	private PasswordField passwordField, rePasswordField;
	@FXML
	private ImageView photoImage;
	@FXML
	private TextField usernameField, emailField, fnameField, lnameField,
			addressField, hPhoneField, mPhoneField, licenceNoField;
	@FXML
	private ComboBox<String> emailComboBox, licenceTypeComboBox;
	@FXML
	CheckBox driverCheckBox;
	@FXML
	private DatePicker issueDatePicker, expireDatePicker;
	@FXML
	private GridPane generalPane, licencePane;
	@FXML
	private Button submitBtn, uploadPhotoBtn;

	private File photoFile;
	private String[] emailSuffix = {"@uclive.ac.nz", "@canterbury.ac.nz"};
	private String[] licenceType = {"Learner", "Restricted", "Full", "Full for 2 years+"};

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		submitBtn.setOnAction(e -> pressSubmitBtn());
		driverCheckBox.setOnAction(e -> {
			if (driverCheckBox.isSelected()) {
				licencePane.setDisable(false);
			} else {
				licencePane.setDisable(true);
			}
		});
		// add content to comboBoxes
		emailComboBox.getItems().addAll(emailSuffix);
		emailComboBox.getSelectionModel().selectFirst();
		licenceTypeComboBox.getItems().addAll(licenceType);
		licenceTypeComboBox.getSelectionModel().selectLast();

		// initialize file chooser
		uploadPhotoBtn.setOnAction(event -> {
			FileChooser fileChooser = new FileChooser();
			photoFile = fileChooser.showOpenDialog(this.stage);
			try {
				System.out.println(photoFile.toURI().toString());
				photoImage.setImage(new Image(photoFile.toURI().toString()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	protected void afterSetRSS() {}

	public void pressSubmitBtn() {
		if (validateFields()) {
			System.out.println("valid!");
		}
	}

	private boolean validateFields() {
		List<String> errorMsg = new ArrayList<>();

		if (!usernameField.getText().matches("[a-zA-Z1-9]{4,7}")) {
			errorMsg.add("Username should only contains 4 to 7 alphanumeric characters.\n");
		}

		String pwd = passwordField.getText();
		if (!pwd.matches(".*[a-zA-Z]+.*") || !pwd.matches(".*[0-9]+.*")) {
			errorMsg.add("Password should contain both alphabetic and numeric characters.\n");
		}
		if (pwd.length() > 18 || pwd.length() < 6) {
			errorMsg.add("Password length should be at least 6 and no more than 18 characters.\n");
		}
		if (!pwd.equals(rePasswordField.getText())) {
			errorMsg.add("Two passwords are not identical.\n");
		}

		String fn = fnameField.getText(); String ln = lnameField.getText();
		if (!fn.matches("[a-zA-Z]+") || !ln.matches("[a-zA-Z]+")) {
			errorMsg.add("First / Last name can only contain alphabetic characters.\n");
		}

		if (!emailField.getText().matches("[a-zA-Z1-9_.]+")) {
			errorMsg.add("Email address is empty or contains invalid characters.\n");
		}

		if (!addressField.getText().matches("[a-zA-Z1-9, ]+")) {
			errorMsg.add("Address is empty or contains invalid characters.\n");
		}

		String hp = hPhoneField.getText(); String mp = mPhoneField.getText();
		if (hp.equals("") && hp.equals("")) {
			errorMsg.add("Home phone and mobile phone cannot both be empty.\n");
		} else {
			if (!hp.equals("") && !hp.matches("[0-9]{8,12}")) {
				errorMsg.add("Home phone is invalid.\n");
			}
			if (!mp.equals("") && !mp.matches("[0-9]{8,12}")) {
				errorMsg.add("Mobile phone is invalid.\n");
			}
		}

		if (driverCheckBox.isSelected()) {

			if (!licenceNoField.getText().matches("[a-zA-Z0-9]{7,9}")) {
				errorMsg.add("Driver's licence number is invalid.\n");
			}

			if (issueDatePicker.getValue() == null || expireDatePicker.getValue() == null) {
				errorMsg.add("Please fill issue date and expire date for your driver's licence.\n");
			} else {
				if (issueDatePicker.getValue().isAfter(expireDatePicker.getValue())) {
					errorMsg.add("Expire date should be after issue date.\n");
				}
			}
		}

		if (errorMsg.size() == 0) {
			return true;
		} else {
			String errorString = "Operation failed is caused by: \n";
			for (Integer i = 1; i <= errorMsg.size(); i++) {
				errorString += i.toString() + ". " + errorMsg.get(i - 1);
			}
			rss.showErrorDialog("Sign up failed.", errorString);
			return false;
		}
	}
}
