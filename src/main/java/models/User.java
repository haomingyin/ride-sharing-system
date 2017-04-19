package models;

import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;

public class User {

	private String username, password, email, fName, lName, hPhone, mPhone, address, licenceNo, licenceType;
	private LocalDate issueDate, expireDate;
	private byte[] photo;

	// properties don't belong to database schema
	private SimpleStringProperty name;

	public User() {
		this.name = new SimpleStringProperty();
	}

	public void setUser(User user) {
		this.username = user.getUsername();
		this.password = user.getPassword();
		this.email = user.getEmail();
		this.fName = user.getfName();
		this.lName = user.getlName();
		this.hPhone = user.gethPhone();
		this.mPhone = user.getmPhone();
		this.address = user.getAddress();
		this.licenceNo = user.getLicenceNo();
		this.licenceType = user.getLicenceType();
		this.issueDate = user.getIssueDate();
		this.expireDate = user.getExpireDate();
		this.photo = user.getPhoto();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		if (email == null) return "@uclive.ac.nz";
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getfName() {
		if (fName == null) return "";
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public String getlName() {
		if (lName == null) return "";
		return lName;
	}

	public void setlName(String lName) {
		this.lName = lName;
	}

	public String gethPhone() {
		if (hPhone == null) return "";
		return hPhone;
	}

	public void sethPhone(String hPhone) {
		this.hPhone = hPhone;
	}

	public String getmPhone() {
		if (mPhone == null) return "";
		return mPhone;
	}

	public void setmPhone(String mPhone) {
		this.mPhone = mPhone;
	}

	public String getAddress() {
		if (address == null) return "";
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLicenceNo() {
		if (licenceNo == null) return "";
		return licenceNo;
	}

	public void setLicenceNo(String licenceNo) {
		this.licenceNo = licenceNo;
	}

	public String getLicenceType() {
		if (licenceType == null) return "";
		return licenceType;
	}

	public void setLicenceType(String licenceType) {
		this.licenceType = licenceType;
	}

	public LocalDate getIssueDate() {
		if (issueDate == null) return LocalDate.of(1901,1,1);
		return issueDate;
	}

	public void setIssueDate(LocalDate issueDate) {
		this.issueDate = issueDate;
	}

	public LocalDate getExpireDate() {
		if (expireDate == null) return LocalDate.of(1901,1,1);
		return expireDate;
	}

	public void setExpireDate(LocalDate expireDate) {
		this.expireDate = expireDate;
	}

	public byte[] getPhoto() {
		if (photo == null) return new byte[] {};
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	public String getName() {
		setName(getfName() + " " + getlName());
		return name.get();
	}

	public SimpleStringProperty nameProperty() {
		setName(getfName() + " " + getlName());
		return name;
	}

	private void setName(String name) {
		this.name.set(name);
	}
}
