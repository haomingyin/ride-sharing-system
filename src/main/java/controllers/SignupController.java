package controllers;

import com.sun.javafx.tk.Toolkit;
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
import org.apache.commons.lang3.text.WordUtils;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * To control sign up page.
 * Created by Haoming on 15/03/17.
 */

public class SignupController extends Controller implements Initializable {

	@FXML
	private Text titleText;
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
	private CheckBox driverCheckBox;
	@FXML
	private DatePicker issueDatePicker, expireDatePicker;
	@FXML
	private GridPane licencePane; // generalPane, ;
	@FXML
	private Button submitBtn, uploadPhotoBtn;

	private enum Mode {SIGNUP, UPDATE_PROFILE};
	private Mode mode = Mode.SIGNUP;
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
				clearDriverFields();
			}
		});
		// add content to comboBoxes
		emailComboBox.getItems().addAll(emailSuffix);
		emailComboBox.getSelectionModel().selectFirst();
		licenceTypeComboBox.getItems().addAll(licenceType);
		licenceTypeComboBox.getSelectionModel().selectLast();

		// initialize file chooser
		/* TODO: validate photo before display it. 13/4/2017 */
		uploadPhotoBtn.setOnAction(event -> {
			FileChooser fileChooser = new FileChooser();
			FileChooser.ExtensionFilter filter =
					new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.gif");
			fileChooser.getExtensionFilters().add(filter);
			photoFile = fileChooser.showOpenDialog(this.stage);
			try {
				if (photoFile != null) photoImage.setImage(new Image(photoFile.toURI().toString()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	protected void afterSetRSS() {}

	public void myProfileMode() {
		mode = Mode.UPDATE_PROFILE;
		titleText.setText("My Profile");
		usernameField.setEditable(false);
		submitBtn.setText("Update");

		// fill all fields
		usernameField.setText(rss.getUser().getUsername());
		passwordField.setText(rss.getUser().getPassword());
		rePasswordField.setText(rss.getUser().getPassword());
		fnameField.setText(rss.getUser().getfName());
		lnameField.setText(rss.getUser().getlName());
		addressField.setText(rss.getUser().getAddress());
		hPhoneField.setText(rss.getUser().gethPhone());
		mPhoneField.setText(rss.getUser().getmPhone());
		String[] tokens = rss.getUser().getEmail().split("@");
		emailField.setText(tokens[0]);
		emailComboBox.getSelectionModel().select("@" + tokens[1]);
		// if current user is a driver
		if (!rss.getUser().getLicenceNo().equals("")) {
			driverCheckBox.setSelected(true);
			driverCheckBox.setVisible(false);
			licencePane.setDisable(false);
			licenceNoField.setText(rss.getUser().getLicenceNo());
			licenceTypeComboBox.getSelectionModel().select(rss.getUser().getLicenceType());
			issueDatePicker.setValue(rss.getUser().getIssueDate());
			expireDatePicker.setValue(rss.getUser().getExpireDate());
		}
		// load photo
		photoImage.setImage(new Image(new ByteArrayInputStream(rss.getUser().getPhoto())));
	}

	private void pressSubmitBtn() {
		changeFieldsFormat();
		if (validateFields()) {
			User user = mode == Mode.UPDATE_PROFILE ? rss.getUser() : new User();
			user.setUsername(usernameField.getText());
			user.setPassword(passwordField.getText());
			user.setAddress(addressField.getText());
			user.setEmail(emailField.getText() + emailComboBox.getValue());
			user.setfName(fnameField.getText());
			user.setlName(lnameField.getText());
			user.sethPhone(hPhoneField.getText());
			user.setmPhone(mPhoneField.getText());
			if (mode == Mode.SIGNUP || photoFile != null)
				user.setPhoto(getByteArrayFromPhoto());
			if (driverCheckBox.isSelected()) {
				user.setLicenceNo(licenceNoField.getText());
				user.setLicenceType(licenceTypeComboBox.getValue());
				user.setIssueDate(issueDatePicker.getValue());
				user.setExpireDate(expireDatePicker.getValue());
			}

			String headMsg, infoMsg;
			if (submitBtn.getText().equals("Submit")) {
				if (SQLExecutor.addUser(user) == 1) {
					headMsg = "Operation Succeeded.";
					infoMsg = "You have successfully signed up with RSS.";
					rss.showInformationDialog(headMsg, infoMsg);
					// close current sign up window
					stage.close();
				} else {
					headMsg = "Operation Failed.";
					infoMsg = "Errors occurred when sign you up.";
					rss.showErrorDialog(headMsg, infoMsg);
				}
			} else {
				if (SQLExecutor.updateUser(user) == 1) {
					headMsg = "Operation Succeeded.";
					infoMsg = "Your profile has been updated.";
					rss.showInformationDialog(headMsg, infoMsg);
					myProfileMode();
				} else {
					headMsg = "Operation Failed.";
					infoMsg = "Errors occurred when update your profile.";
					rss.showErrorDialog(headMsg, infoMsg);
				}
			}
		}
	}

	private byte[] getByteArrayFromPhoto() {
		if (photoFile != null) {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			try {
				byte[] buffer = new byte[1024];
				FileInputStream fileInputStream = new FileInputStream(photoFile);
				for (int len; (len = fileInputStream.read(buffer)) != -1;) {
					byteArrayOutputStream.write(buffer, 0, len);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return byteArrayOutputStream.toByteArray();
		} else {
			return null;
		}
	}
	/**
	 * Clears driver licence section
	 */
	private void clearDriverFields() {
		licenceNoField.clear();
		issueDatePicker.setValue(null);
		issueDatePicker.getEditor().clear();
		expireDatePicker.setValue(null);
		expireDatePicker.getEditor().clear();
	}

	private void changeFieldsFormat() {
		usernameField.setText(usernameField.getText().toLowerCase());
		fnameField.setText(WordUtils.capitalizeFully(fnameField.getText()));
		lnameField.setText(WordUtils.capitalizeFully(lnameField.getText()));
		emailField.setText(emailField.getText().toLowerCase());
		addressField.setText(WordUtils.capitalizeFully(addressField.getText()));
		licenceNoField.setText(licenceNoField.getText().toUpperCase());
	}

	/**
	 * Validates all input fields, if any invalid data is found, pop up a error
	 * dialog showing all error message and return false. Otherwise return true.
	 * @return true if all fields are valid. otherwise false.
	 */
	private boolean validateFields() {
		List<String> errorMsg = new ArrayList<>();

		// check username
		if (!usernameField.getText().matches("[a-zA-Z1-9]{4,7}")) {
			errorMsg.add("Username should only contains 4 to 7 alphanumeric characters.\n");
		}

		// check passwords
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

		// check first name and last name
		if (!fnameField.getText().matches("[a-zA-Z]+") || !lnameField.getText().matches("[a-zA-Z]+")) {
			errorMsg.add("First and last name can only contain alphabetic characters.\n");
		}

		// check email
		if (!emailField.getText().matches("[a-zA-Z1-9_.]+")) {
			errorMsg.add("Email address is empty or contains invalid characters.\n");
		}

		// check residential address
		if (!addressField.getText().matches("[a-zA-Z1-9,. ]+")) {
			errorMsg.add("Address is empty or contains invalid characters.\n");
		}

		// check phone numbers
		String hp = hPhoneField.getText();
		String mp = mPhoneField.getText();
		if (hp.equals("") && mp.equals("")) {
			errorMsg.add("Home phone and mobile phone cannot both be empty.\n");
		} else {
			if (!hp.equals("") && !hp.matches("[0-9]{8,12}")) {
				errorMsg.add("Home phone is invalid.\n");
			}
			if (!mp.equals("") && !mp.matches("[0-9]{8,12}")) {
				errorMsg.add("Mobile phone is invalid.\n");
			}
		}

		// check driver's details
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

		// handle and show error message in dialog
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

		/* TODO: check photo size and format ensure we can display and store it. 13/4/2017 */
	}
}
