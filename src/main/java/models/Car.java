package models;

public class Car {

	private String plate, owner, model, manufacturer, color;
	private int year, seatNo;

	public Car(String plate, String owner, String model, String manufacturer, String color, int year, int seatNo) {
		this.plate = plate;
		this.owner = owner;
		this.model = model;
		this.manufacturer = manufacturer;
		this.color = color;
		this.year = year;
		this.seatNo = seatNo;
	}

	public String getPlate() {
		return plate;
	}

	public void setPlate(String plate) {
		this.plate = plate;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getSeatNo() {
		return seatNo;
	}

	public void setSeatNo(int seatNo) {
		this.seatNo = seatNo;
	}
}
