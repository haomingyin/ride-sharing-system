package server;

import models.database.SQLExecutor;
import models.ride.Ride;
import models.ride.RideInstance;
import models.ride.Status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * The server runs as a background thread to check all rides if they are expired.
 * If any ride or its instance is expired, then sever will mark it ad 'Done'.
 * Author: Haoming
 * Date: April 4th, 2017
 */
public class RSSServer implements Runnable {

	@SuppressWarnings("InfiniteLoopStatement")
	@Override
	public void run() {
		while (true) {
			List<Ride> activeRides = SQLExecutor.fetchActiveRides();
			if (activeRides != null) {
				for (Ride ride : activeRides) {
					LocalDate rideDate = ride.getLocalDate();
					// if an active ride's date is before now, then update it and all
					// of its instances.
					if (rideDate.isBefore(LocalDate.now())) {
						SQLExecutor.updateRideStatus(ride, Status.DONE);
						updateRideInstanceStatus(ride);
					} else if (rideDate.isEqual(LocalDate.now())) {
						updateRideInstanceStatus(ride);
					}
				}
			}

			try {
				// wait to 1 minute to continue the loop
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
		}
	}


	private void updateRideInstanceStatus(Ride ride) {
		List<RideInstance> rideInstances = SQLExecutor.fetchRideInstancesByRide(ride);
		if (rideInstances != null) {
			for (RideInstance ri : rideInstances) {
				LocalDate rideDate = ride.getLocalDate();
				if (rideDate.isBefore(LocalDate.now())
						|| (rideDate.isEqual(LocalDate.now())
						&& ri.getStopPoint().getLocalTime().isAfter(LocalTime.now()))) {
					if (Status.BOOKED.equals(ri.getStatus())) {
						SQLExecutor.updateRideInstanceStatus(ri, Status.DONE);
					}
				}
			}
		}
	}
}
