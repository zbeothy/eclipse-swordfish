package org.eclipse.swordfish.samples.cxf;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.eclipse.swordfish.samples.cxf.domain.Flight;
import org.eclipse.swordfish.samples.cxf.domain.Passenger;
import org.eclipse.swordfish.samples.cxf.domain.Reservation;

@WebService
public interface BookingService {
	public int createReservation(@WebParam(name = "passengers")List<Passenger> passengers, @WebParam(name = "flight")Flight flight);
	public Reservation findReservation(@WebParam(name = "reservationId")int reservationId);
}
