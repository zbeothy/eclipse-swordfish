package org.eclipse.swordfish.samples.cxf.domain;

public class Flight {
	public Flight() {
		// TODO Auto-generated constructor stub
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFlightNumber() {
		return flightNumber;
	}
	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}
	private int id;
	private String flightNumber;
public Flight(int id, String flightNumber) {
	this.id = id;
	this.flightNumber = flightNumber;
}
}
