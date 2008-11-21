package org.eclipse.swordfish.samples.cxf.domain;

import java.util.List;

public class Reservation {
	private int id;
	private List<Passenger> passengers;
	public Reservation() {
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<Passenger> getPassengers() {
		return passengers;
	}
	public void setPassengers(List<Passenger> passengers) {
		this.passengers = passengers;
	}
	public Flight getFlight() {
		return flight;
	}
	public void setFlight(Flight flight) {
		this.flight = flight;
	}
	private Flight flight;
}
