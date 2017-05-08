package models.database;

import models.*;
import models.notification.Notification;
import models.position.StopPoint;
import models.ride.Ride;
import models.ride.RideFilter;
import models.ride.RideInstance;
import models.User;
import models.ride.Status;
import org.sqlite.SQLiteException;

import javax.swing.plaf.nimbus.State;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SQLExecutor {

	private static SQLConnector connector;

	private static void connectDB() {
		connector = new SQLConnector();
		connector.connect();
		enableForeignKeys();
	}

	private static void enableForeignKeys() {
		try {
			if (connector != null)
				connector.executeSQLUpdate("PRAGMA foreign_keys = 1;");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void disconnectDB() {
		connector.closeConnection();
	}

	// **************************************************************
	// *
	// *                 Query for 'user'
	// *
	// **************************************************************

	/**
	 * Fetches the number of users who have the given username
	 * @param username username of the user needs to be fetched
	 * @return the number of users, -1 if query fails
	 */
	public static int fetchNumberOfUser(String username) {
		try {
			connectDB();
			String sql = String.format("SELECT count(username) as cnt " +
					"FROM user " +
					"WHERE username = '%s';", username);
			return connector.executeSQLQuery(sql).getInt("cnt");
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			disconnectDB();
		}
	}

	/**
	 * Fetches a user by its username and password
	 * @param username username of the user needs to be fetched
	 * @param password password of the user needs to be fetched
	 * @return a User instance if the user exists, otherwise null
	 */
	public static User fetchUser(String username, String password) {
		try {
			connectDB();
			String sql = "SELECT count(username) as cnt " +
					"FROM user " +
					"WHERE username = ? AND password = ?;";
			PreparedStatement pstmt = connector.conn.prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.setString(2, password);

			if(pstmt.executeQuery().getInt("cnt") == 1) {
				disconnectDB();
				return fetchUser(username);
			}

			disconnectDB();
		} catch (Exception e) {
			e.printStackTrace();
			disconnectDB();
		}
		return null;
	}

	public static User fetchUser(String username) {
		try {
			connectDB();
			User user = new User();
			String sql = "SELECT * FROM user WHERE username = ?;";
			PreparedStatement pstmt = connector.conn.prepareStatement(sql);
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			if (!rs.isClosed()) {
				user.setUsername(rs.getString("username"));
				user.setPassword(rs.getString("password"));
				user.setEmail(rs.getString("email"));
				user.setfName(rs.getString("fName"));
				user.setlName(rs.getString("lName"));
				user.setAddress(rs.getString("address"));
				user.sethPhone(rs.getString("hPhone"));
				user.setmPhone(rs.getString("mPhone"));
				user.setLicenceNo(rs.getString("licenceNo"));
				if (!user.getLicenceNo().equals("")) {
					user.setLicenceType(rs.getString("licenceType"));
					user.setIssueDate(LocalDate.parse(rs.getString("issueDate")));
					user.setExpireDate(LocalDate.parse(rs.getString("expireDate")));
				}
				user.setPhoto(rs.getBytes("photo"));
				return user;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnectDB();
		}
		return null;
	}

	/**
	 * Creates a user into database
	 * @param user the user need to be stored into database
	 * @return 1 if succeed, otherwise 0
	 */
	public static int addUser(User user) {
		try {
			connectDB();
			String sql = "INSERT INTO user (username, password, email, " +
							"fName, lName, address, hPhone, mPhone, licenceNo, licenceType, " +
							"issueDate, expireDate, photo) " +
							"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
			PreparedStatement pstmt = connector.conn.prepareStatement(sql);
			int cnt = 1;
			pstmt.setString(cnt++, user.getUsername());
			pstmt.setString(cnt++, user.getPassword());
			pstmt.setString(cnt++, user.getEmail());
			pstmt.setString(cnt++, user.getfName());
			pstmt.setString(cnt++, user.getlName());
			pstmt.setString(cnt++, user.getAddress());
			pstmt.setString(cnt++, user.gethPhone());
			pstmt.setString(cnt++, user.getmPhone());
			pstmt.setString(cnt++, user.getLicenceNo());
			pstmt.setString(cnt++, user.getLicenceType());
			pstmt.setString(cnt++, user.getIssueDate().toString());
			pstmt.setString(cnt++, user.getExpireDate().toString());
			pstmt.setBytes(cnt, user.getPhoto());

			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			disconnectDB();
		}
	}

	/**
	 * Updates a user into database
	 * @param user the user need to be updated
	 * @return 1 if succeed, otherwise 0
	 */
	public static int updateUser(User user) {
		try {
			connectDB();
			String sql = "UPDATE user " +
					"SET password = ?, email = ?, fName = ?, lName = ?, " +
					"address = ?, hPhone = ?, mPhone = ?, licenceNo = ?, " +
					"licenceType = ?, issueDate = ?, expireDate = ?, photo = ? " +
					"WHERE username = ?;";
			PreparedStatement pstmt = connector.conn.prepareStatement(sql);
			int cnt = 1;
			pstmt.setString(cnt++, user.getPassword());
			pstmt.setString(cnt++, user.getEmail());
			pstmt.setString(cnt++, user.getfName());
			pstmt.setString(cnt++, user.getlName());
			pstmt.setString(cnt++, user.getAddress());
			pstmt.setString(cnt++, user.gethPhone());
			pstmt.setString(cnt++, user.getmPhone());
			pstmt.setString(cnt++, user.getLicenceNo());
			pstmt.setString(cnt++, user.getLicenceType());
			pstmt.setString(cnt++, user.getIssueDate().toString());
			pstmt.setString(cnt++, user.getExpireDate().toString());
			pstmt.setBytes(cnt++, user.getPhoto());
			pstmt.setString(cnt, user.getUsername());
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			disconnectDB();
		}
	}

	// **************************************************************
	// *
	// *                 Query for 'car'
	// *
	// **************************************************************

	/**
	 * Adds a given car to database
	 * @param car a car needs to be stored into database
	 * @return 1 if operation succeed, otherwise 0. However, a specific error code
	 * can also be returned
	 */
	public static int addCar(Car car) {
		try {
			connectDB();
			String sql = "INSERT INTO car " +
					"(plate, username, model, manufacturer, color, year, seatNo, wof, performance, registration) " +
					"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
			PreparedStatement pstmt = connector.conn.prepareStatement(sql);

			int cnt = 1;
			pstmt.setString(cnt++, car.getPlate());
			pstmt.setString(cnt++, car.getUsername());
			pstmt.setString(cnt++, car.getModel());
			pstmt.setString(cnt++, car.getManufacturer());
			pstmt.setString(cnt++, car.getColor());
			pstmt.setInt(cnt++, car.getYear());
			pstmt.setInt(cnt++, car.getSeatNo());
			pstmt.setString(cnt++, car.getWof());
			pstmt.setDouble(cnt++, car.getPerformance());
			pstmt.setString(cnt, car.getRegistration());

			return pstmt.executeUpdate();
		} catch (SQLiteException e) {
			return e.getResultCode().code;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnectDB();
		}
		return 0;
	}

	/**
	 * Updates a given car in database
	 * @param car a car needs to be updated
	 * @return 1 if update succeed, otherwise 0
	 */
	public static int updateCar(Car car) {
		try {
			connectDB();
			String sql = "UPDATE car " +
					"SET model = ?, manufacturer = ?, color = ?, year = ?, seatNo = ?, wof = ?, performance = ?, registration = ? " +
					"WHERE carId = ?;";
			PreparedStatement pstmt = connector.conn.prepareStatement(sql);

			int cnt = 1;
			pstmt.setString(cnt++, car.getModel());
			pstmt.setString(cnt++, car.getManufacturer());
			pstmt.setString(cnt++, car.getColor());
			pstmt.setInt(cnt++, car.getYear());
			pstmt.setInt(cnt++, car.getSeatNo());
			pstmt.setString(cnt++, car.getWof());
			pstmt.setDouble(cnt++, car.getPerformance());
			pstmt.setString(cnt++, car.getRegistration());
			pstmt.setInt(cnt, car.getCarId());

			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			disconnectDB();
		}
	}

	/**
	 * Deletes a given car in database
	 * @param car a car needs to be deleted from database
	 * @return 1 if deletion succeed, otherwise 0
	 */
	public static int deleteCar(Car car) throws SQLiteException {
		connectDB();
		try {
			String sql = String.format("DELETE FROM car WHERE carId = '%s';",
					car.getCarId());
			return connector.executeSQLUpdate(sql);
		} catch (SQLiteException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnectDB();
		}
		return 0;
	}

	/**
	 * Fetches all the cars related to a given user
	 *
	 * @param user user associated with the cars to be fetched
	 * @return a hash map containing car's plate as key, Car instance as value
	 */
	public static List<Car> fetchCarsByUser(User user) {
		List<Car> cars = new ArrayList<>();
		try {
			connectDB();
			String sql = String.format("SELECT * " +
					"FROM car " +
					"WHERE username = '%s';", user.getUsername());
			ResultSet rs = connector.executeSQLQuery(sql);
			while (!rs.isClosed() && rs.next()) {
				Car car = new Car();
				car.setCarId(rs.getInt("carId"));
				car.setPlate(rs.getString("plate"));
				car.setUsername(rs.getString("username"));
				car.setModel(rs.getString("model"));
				car.setManufacturer(rs.getString("manufacturer"));
				car.setColor(rs.getString("color"));
				car.setYear(rs.getInt("year"));
				car.setSeatNo(rs.getInt("seatNo"));
				car.setWof(rs.getString("wof"));
				car.setPerformance(rs.getDouble("performance"));
				car.setRegistration(rs.getString("registration"));

				cars.add(car);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnectDB();
		}
		return cars;
	}

	public static Car fetchCarByCarId(Integer carId) {
		try {
			connectDB();
			String sql = "SELECT * FROM car WHERE carId = ?;";
			PreparedStatement pstmt = connector.conn.prepareStatement(sql);
			pstmt.setInt(1, carId);

			ResultSet rs = pstmt.executeQuery();
			if (!rs.isClosed() && rs.next()) {
				Car car = new Car();
				car.setCarId(rs.getInt("carId"));
				car.setPlate(rs.getString("plate"));
				car.setUsername(rs.getString("username"));
				car.setModel(rs.getString("model"));
				car.setManufacturer(rs.getString("manufacturer"));
				car.setColor(rs.getString("color"));
				car.setYear(rs.getInt("year"));
				car.setSeatNo(rs.getInt("seatNo"));
				car.setWof(rs.getString("wof"));
				car.setPerformance(rs.getDouble("performance"));
				car.setRegistration(rs.getString("registration"));

				return car;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnectDB();
		}
		return null;
	}

	// **************************************************************
	// *
	// *                 Query for 'stop point'
	// *
	// **************************************************************

	public static int updateStopPointDistance(StopPoint stopPoint) {
		try {
			if (stopPoint.getDistance() == -1) return 0;

			connectDB();
			String sql = "UPDATE stop_point SET distance = ? WHERE spId = ?";
			PreparedStatement pstmt = connector.conn.prepareStatement(sql);

			pstmt.setDouble(1, stopPoint.getDistance());
			pstmt.setInt(2, stopPoint.getSpId());

			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnectDB();
		}
		return 0;
	}
	/**
	 * Fetches all stop points whose trimmed address matches the give string
	 * @param query a query string of the address
	 * @param limit a integer of how many records need to be fetched, -1 for all.
	 * @return a HashMap containing spId as keys, StopPoint as values
	 */
	public static List<StopPoint> fetchStopPointsByString(String query, int limit) {
		// add a % wildcard at the end of the trimmed string
		String request = query + "%";
		// replace one or more non alphanumeric character to %
		request = request.toLowerCase().replaceAll("([^a-z0-9]+|(city))+", "%");
		// if request only contains '%', then replace '%' to a empty string
		request = request.equals("%") ? "" : request;
		String sql = String.format("SELECT * FROM stop_point " +
				"WHERE trimmed like '%s' ", request);
		if (limit >= 0) sql += "LIMIT " + String.valueOf(limit) + ";";
		List<StopPoint> stopPoints = new ArrayList<>();
		try {
			connectDB();
			ResultSet rs = connector.executeSQLQuery(sql);
			while (!rs.isClosed() && rs.next()) {
				StopPoint stopPoint = new StopPoint();
				stopPoint.setSpId(rs.getInt("spId"));
				stopPoint.setStreet(rs.getString("street"));
				stopPoint.setStreetNo(rs.getString("streetNo"));
				stopPoint.setSuburb(rs.getString("suburb"));
				stopPoint.setCity(rs.getString("city"));
				stopPoint.setDistance(rs.getDouble("distance"));
				stopPoints.add(stopPoint);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnectDB();
		}
		return stopPoints;
	}

	public static List<StopPoint> fetchStopPointsByRoute(Route route) {
		List<StopPoint> stopPoints = new ArrayList<>();
		try {
			connectDB();
			String sql = "SELECT * " +
					"FROM route_sp JOIN stop_point ON route_sp.spId = stop_point.spId " +
					"WHERE routeId = ?;";

			PreparedStatement pstmt = connector.conn.prepareStatement(sql);
			pstmt.setInt(1, route.getRouteId());

			ResultSet rs = pstmt.executeQuery();
			while (!rs.isClosed() && rs.next()) {
				StopPoint sp = new StopPoint();
				sp.setSpId(rs.getInt("spId"));
				sp.setStreetNo(rs.getString("streetNo"));
				sp.setStreet(rs.getString("street"));
				sp.setSuburb(rs.getString("suburb"));
				sp.setCity(rs.getString("city"));
				sp.setDistance(rs.getDouble("distance"));
				stopPoints.add(sp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnectDB();
		}
		return stopPoints;
	}

	private static StopPoint fetchStopPointByRideIdAndSPId(Integer tripId, Integer spId) {
		try {
			connectDB();
			String sql = "SELECT tsp.time, sp.* " +
					"FROM trip_sp tsp " +
					"LEFT JOIN stop_point sp ON tsp.spId = sp.spId " +
					"WHERE tsp.tripId = ? AND tsp.spId = ?";
			PreparedStatement pstmt = connector.conn.prepareStatement(sql);

			pstmt.setInt(1, tripId);
			pstmt.setInt(2, spId);

			ResultSet rs = pstmt.executeQuery();
			if (!rs.isClosed() && rs.next()) {
				StopPoint stopPoint = new StopPoint();
				stopPoint.setSpId(rs.getInt("spId"));
				stopPoint.setStreetNo(rs.getString("streetNo"));
				stopPoint.setStreet(rs.getString("street"));
				stopPoint.setSuburb(rs.getString("suburb"));
				stopPoint.setCity(rs.getString("city"));
				stopPoint.setTime(rs.getString("time"));
				stopPoint.setDistance(rs.getDouble("distance"));
				return stopPoint;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnectDB();
		}
		return null;
	}

	public static List<StopPoint> fetchStopPointsByTrip(Trip trip) {
		List<StopPoint> stopPoints = new ArrayList<>();
		try {
			connectDB();
			String sql = String.format("SELECT * " +
					"FROM stop_point JOIN trip_sp ON stop_point.spId = trip_sp.spId " +
					"WHERE tripId = %d;", trip.getTripId());

			ResultSet rs = connector.executeSQLQuery(sql);
			while (!rs.isClosed() && rs.next()) {
				StopPoint stopPoint = new StopPoint();
				stopPoint.setSpId(rs.getInt("spId"));
				stopPoint.setStreetNo(rs.getString("streetNo"));
				stopPoint.setStreet(rs.getString("street"));
				stopPoint.setSuburb(rs.getString("suburb"));
				stopPoint.setCity(rs.getString("city"));
				stopPoint.setTime(rs.getString("time"));

				stopPoints.add(stopPoint);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnectDB();
		}
		return stopPoints;
	}

	// **************************************************************
	// *
	// *                 Query for 'route'
	// *
	// **************************************************************

	/**
	 * Fetches all routes associated with a give user
	 * @param user a user associated with routes to be fetched
	 * @return HashMap containing routeId as key, Route as values.
	 */
	public static List<Route> fetchRoutesByUser(User user) {
		List<Route> routes = new ArrayList<>();
		try {
			connectDB();
			String sql = String.format("SELECT * " +
					"FROM route " +
					"WHERE username = '%s';", user.getUsername());
			ResultSet rs = connector.executeSQLQuery(sql);
			while (!rs.isClosed() && rs.next()) {
				Route route = new Route();
				route.setRouteId(rs.getInt("routeId"));
				route.setAlias(rs.getString("alias"));
				route.setUsername(rs.getString("username"));
				routes.add(route);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnectDB();
		}
		return routes;
	}

	public static int addRoute(Route route) {
		try {
			connectDB();
			String sql = "INSERT INTO route (username, alias) VALUES (?, ?);";
			PreparedStatement pstmt = connector.conn.prepareStatement(sql);

			pstmt.setString(1, route.getUsername());
			pstmt.setString(2, route.getAlias());

			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnectDB();
		}
		return 0;
	}

	public static int updateRouteAlias(Route route) {
		try {
			connectDB();
			String sql = "UPDATE route " +
					"SET alias = ? " +
					"WHERE routeId = ?;";
			PreparedStatement pstmt = connector.conn.prepareStatement(sql);

			pstmt.setString(1, route.getAlias());
			pstmt.setInt(2, route.getRouteId());

			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnectDB();
		}
		return 0;
	}

	public static int deleteRoute(Route route) throws SQLiteException{
		try {
			connectDB();
			String sql = "DELETE FROM route WHERE routeId = ?;";
			PreparedStatement pstmt = connector.conn.prepareStatement(sql);
			pstmt.setInt(1, route.getRouteId());
			return pstmt.executeUpdate();
		} catch (SQLiteException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnectDB();
		}
		return 0;
	}

	public static int addStopPointIntoRoute(Route route, StopPoint stopPoint) throws SQLiteException{
		try {
			connectDB();
			// if a route has been instantiate as trips, then can't add more sp
			String sql = "SELECT count(*) AS cnt " +
					"FROM trip WHERE trip.routeid = ?";
			PreparedStatement pstmt = connector.conn.prepareStatement(sql);
			pstmt.setInt(1, route.getRouteId());
			// if a route has been used for a trip sql executor will return -1.
			if (pstmt.executeQuery().getInt("cnt") != 0) {
				return -1;
			}
			// add stop point into route
			sql = "INSERT INTO route_sp (routeId, spId) " +
					"VALUES (?, ?);";
			pstmt = connector.conn.prepareStatement(sql);
			pstmt.setInt(1, route.getRouteId());
			pstmt.setInt(2, stopPoint.getSpId());

			return pstmt.executeUpdate();
		} catch (SQLiteException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnectDB();
		}
		return 0;
	}

	// **************************************************************
	// *
	// *                 Query for 'trip'
	// *
	// **************************************************************

	public static int addTrip(Trip trip) {
		try {
			connectDB();
			String sql = "BEGIN TRANSACTION;";
			connector.executeSQLUpdate(sql);
			sql = "INSERT INTO trip " +
					"(alias, username, routeid, direction, carId, beginDate, expireDate, day) " +
					"VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
			PreparedStatement pstmt = connector.conn.prepareStatement(sql);

			int cnt = 1;
			pstmt.setString(cnt++, trip.getAlias());
			pstmt.setString(cnt++, trip.getUsername());
			pstmt.setInt(cnt++, trip.getRouteId());
			pstmt.setString(cnt++, trip.getDirection());
			pstmt.setInt(cnt++, trip.getCarId());
			pstmt.setString(cnt++, trip.getBeginDate().toString());
			pstmt.setString(cnt++, trip.getExpireDate().toString());
			pstmt.setInt(cnt, trip.getDay());

			pstmt.executeUpdate();
			sql = "SELECT last_insert_rowid() AS result;";
			trip.setTripId(connector.executeSQLQuery(sql).getInt("result"));

			// add stop points into trip_sp table
			for (StopPoint stopPoint : trip.getStopPoints()) {
				sql = String.format("INSERT INTO trip_sp (tripId, spId, time) " +
								"VALUES (%d, %d, '%s');",
						trip.getTripId(),
						stopPoint.getSpId(),
						stopPoint.getTime());
				connector.executeSQLUpdate(sql);
			}
			sql = "COMMIT;";
			connector.executeSQLUpdate(sql);
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnectDB();
		}
		return 0;
	}

	public static int deleteTrip(Trip trip) throws SQLiteException {
		try {
			connectDB();
			String sql = "DELETE FROM trip WHERE tripId = ?";
			PreparedStatement pstmt = connector.conn.prepareStatement(sql);

			pstmt.setInt(1, trip.getTripId());
			return pstmt.executeUpdate();
		} catch (SQLiteException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnectDB();
		}
		return 0;
	}

	public static List<Trip> fetchTripsByUser(User user) {
		List<Trip> trips = new ArrayList<>();
		try {
			connectDB();
			String sql = String.format("SELECT * " +
					"FROM trip " +
					"WHERE username = '%s';", user.getUsername());
			ResultSet rs = connector.executeSQLQuery(sql);
			while (!rs.isClosed() && rs.next()) {
				Trip trip = new Trip();
				trip.setTripId(rs.getInt("tripId"));
				trip.setAlias(rs.getString("alias"));
				trip.setUsername(rs.getString("username"));
				trip.setDirection(rs.getString("direction"));
				trip.setCarId(rs.getInt("carId"));
				trip.setRouteId(rs.getInt("routeId"));
				trip.setBeginDate(LocalDate.parse(rs.getString("beginDate")));
				trip.setExpireDate(LocalDate.parse(rs.getString("expireDate")));
				trip.setDay(rs.getInt("day"));

				trips.add(trip);
			}

			disconnectDB();
			for (Trip trip : trips) {
				trip.setCar(fetchCarByCarId(trip.getCarId()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			disconnectDB();
		}
		return trips;
	}

	private static Trip fetchTripByTripId(Integer tripId) {
		try {
			connectDB();
			String sql = "SELECT * FROM trip WHERE tripId = ?;";
			PreparedStatement pstmt = connector.conn.prepareStatement(sql);
			pstmt.setInt(1, tripId);
			ResultSet rs = pstmt.executeQuery();

			if (!rs.isClosed() && rs.next()) {
				Trip trip = new Trip();
				trip.setTripId(rs.getInt("tripId"));
				trip.setAlias(rs.getString("alias"));
				trip.setUsername(rs.getString("username"));
				trip.setDirection(rs.getString("direction"));
				trip.setCarId(rs.getInt("carId"));
				trip.setRouteId(rs.getInt("routeId"));
				trip.setBeginDate(LocalDate.parse(rs.getString("beginDate")));
				trip.setExpireDate(LocalDate.parse(rs.getString("expireDate")));
				trip.setDay(rs.getInt("day"));

				disconnectDB();

				trip.setCar(fetchCarByCarId(trip.getCarId()));
				return trip;
			}
		} catch (Exception e) {
			e.printStackTrace();
			disconnectDB();
		}
		return null;
	}

	// **************************************************************
	// *
	// *                 Query for 'ride'
	// *
	// **************************************************************

	public static int addRides(List<Ride> rides) {
		try {
			connectDB();
			String sql = "BEGIN TRANSACTION;";
			connector.executeSQLUpdate(sql);
			int result = 0;

			for (Ride ride : rides) {
				sql = "INSERT INTO ride" +
						"(alias, tripId, rideDate, seatNo, username) " +
						"VALUES (?, ?, ?, ?, ?);";
				PreparedStatement pstmt = connector.conn.prepareStatement(sql);

				int cnt = 1;
				pstmt.setString(cnt++, ride.getAlias());
				pstmt.setInt(cnt++, ride.getTripId());
				pstmt.setString(cnt++, ride.getDate());
				pstmt.setInt(cnt++, ride.getSeatNo());
				pstmt.setString(cnt, ride.getUsername());

				result += pstmt.executeUpdate();
			}

			sql = "COMMIT;";
			connector.executeSQLUpdate(sql);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnectDB();
		}
		return -1;
	}

	public static int updateRideStatus(Ride ride, Status status) {
		try {
			connectDB();
			String sql = "UPDATE ride SET status = ? WHERE rideId = ?;";
			PreparedStatement pstmt = connector.conn.prepareStatement(sql);

			pstmt.setString(1, status.toString());
			pstmt.setInt(2, ride.getRideId());

			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnectDB();
		}
		return 0;
	}

	public static int deleteRide(Ride ride) throws SQLiteException {
		try {
			connectDB();
			String sql = "DELETE FROM ride WHERE rideId = ?";
			PreparedStatement pstmt = connector.conn.prepareStatement(sql);

			pstmt.setInt(1, ride.getRideId());

			return pstmt.executeUpdate();
		} catch (SQLiteException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnectDB();
		}
		return 0;
	}

	/**
	 * Fetches rides which are owned by the given user
	 * @param user a user associated with rides to be fetched
	 * @return a HashMap of rides containing rideId as keys, ride as values
	 */
	public static List<Ride> fetchRidesByUser(User user) {
		try {
			connectDB();
			List<Ride> rides = new ArrayList<>();

			String sql = "SELECT * " +
					"FROM all_ride_with_sp_view " +
					"WHERE username = ?" +
					"GROUP BY rideId;";

			PreparedStatement pstmt = connector.conn.prepareStatement(sql);
			pstmt.setString(1, user.getUsername());

			ResultSet rs = pstmt.executeQuery();
			while (!rs.isClosed() && rs.next()) {
				Ride ride = new Ride();
				ride.setRideId(rs.getInt("rideId"));
				ride.setSeatBooked(rs.getInt("seatBooked"));

				rides.add(ride);
			}
			disconnectDB();

			// fill ride and trip detail for each ride
			for (Ride ride : rides) {
				ride.setRide(fetchRideByRideId(ride.getRideId()));
			}
			return rides;

		} catch (Exception e) {
			e.printStackTrace();
			disconnectDB();
		}
		return null;
	}

	/**
	 * This method is mainly for the server to use.
	 * @return all rides that are still active as a list.
	 */
	public static List<Ride> fetchActiveRides() {
		try {
			connectDB();
			List<Ride> rides = new ArrayList<>();
			String sql = String.format("SELECT * FROM ride WHERE status = '%s';", Status.ACTIVE.toString());
			ResultSet rs = connector.executeSQLQuery(sql);
			while (!rs.isClosed() && rs.next()) {
				Ride ride = new Ride();
				ride.setRideId(rs.getInt("rideId"));
				ride.setDate(rs.getString("rideDate"));
				ride.setRideStatus(rs.getString("status"));

				rides.add(ride);
			}
			return rides;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnectDB();
		}
		return null;
	}

	private static Ride fetchRideByRideId(Integer rideId) {
		try {
			connectDB();
			String sql = "SELECT * FROM ride WHERE rideId = ?";
			PreparedStatement pstmt = connector.conn.prepareStatement(sql);
			pstmt.setInt(1, rideId);

			ResultSet rs = pstmt.executeQuery();
			if (!rs.isClosed() && rs.next()) {
				Ride ride = new Ride();
				ride.setRideId(rs.getInt("rideId"));
				ride.setAlias(rs.getString("alias"));
				ride.setTripId(rs.getInt("tripId"));
				ride.setDate(rs.getString("rideDate"));
				ride.setSeatNo(rs.getInt("seatNo"));
				ride.setUsername(rs.getString("username"));
				ride.setRideStatus(rs.getString("status"));

				disconnectDB();

				ride.setTrip(fetchTripByTripId(ride.getTripId()));
				return ride;
			}
		} catch (Exception e) {
			e.printStackTrace();
			disconnectDB();
		}
		return null;
	}

	// **************************************************************
	// *
	// *                 Query for 'ride instance'
	// *
	// **************************************************************

	public static List<RideInstance> fetchRideInstancesByRide(Ride ride) {
		try {
			connectDB();
			List<RideInstance> instances = new ArrayList<>();
			String sql = "SELECT * FROM ride_passenger WHERE rideId = ?";
			PreparedStatement pstmt = connector.conn.prepareStatement(sql);
			pstmt.setInt(1, ride.getRideId());

			ResultSet rs = pstmt.executeQuery();
			while (!rs.isClosed() && rs.next()) {
				RideInstance ri = new RideInstance();
				ri.setPassengerId(rs.getString("username"));
				ri.setRideId(rs.getInt("rideId"));
				ri.setSpId(rs.getInt("spId"));
				ri.setStatus(rs.getString("status"));
				ri.setPrice(rs.getDouble("price"));
				instances.add(ri);
			}
			disconnectDB();

			for (RideInstance ri : instances) {
				ri.setRide(fetchRideByRideId(ri.getRideId()));
				ri.setPassenger(fetchUser(ri.getPassengerId()));
				ri.setStopPoint(fetchStopPointByRideIdAndSPId(ri.getTripId(), ri.getSpId()));
			}
			return instances;
		} catch (Exception e) {
			e.printStackTrace();
			disconnectDB();
		}
		return null;
	}

	public static List<RideInstance> fetchRideInstancesByRideFilter(RideFilter rideFilter) {
		try {
			connectDB();
			List<RideInstance> rideInstances = new ArrayList<>();
			String sql = "SELECT * FROM all_ride_with_sp_view ";

			if (rideFilter.isValid()) {
				sql += "WHERE " + rideFilter.getQuery();
			} else {
				return null;
			}

			PreparedStatement pstmt = connector.conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();

			while (!rs.isClosed() && rs.next()) {
				RideInstance ri = new RideInstance();
				ri.setRideId(rs.getInt("rideId"));
				ri.setTripId(rs.getInt("tripId"));
				ri.setSpId(rs.getInt("spId"));
				ri.setSeatBooked(rs.getInt("seatBooked"));

				StopPoint sp = new StopPoint();
				sp.setSpId(rs.getInt("spId"));
				sp.setStreetNo(rs.getString("streetNo"));
				sp.setStreet(rs.getString("Street"));
				sp.setSuburb(rs.getString("suburb"));
				sp.setCity(rs.getString("city"));
				sp.setTime(rs.getString("time"));
				sp.setDistance(rs.getDouble("distance"));

				ri.setStopPoint(sp);
				rideInstances.add(ri);
			}
			disconnectDB();

			// fill trip and route detail into each ride instance
			for (RideInstance ri : rideInstances) {
				ri.setRide(fetchRideByRideId(ri.getRideId()));
			}

			return rideInstances;
		} catch (Exception e) {
			e.printStackTrace();
			disconnectDB();
		}
		return null;
	}

	public static int addRideInstance(RideInstance rideInstance) throws SQLiteException {
		try {
			connectDB();

			String sql = "INSERT INTO ride_passenger " +
					"(username, rideId, spId, status, price) " +
					"VALUES (?, ?, ?, ?, ?); ";
			PreparedStatement pstmt = connector.conn.prepareStatement(sql);

			int cnt = 1;
			pstmt.setString(cnt++, rideInstance.getPassengerId());
			pstmt.setInt(cnt++, rideInstance.getRideId());
			pstmt.setInt(cnt++, rideInstance.getSpId());
			pstmt.setString(cnt++, "Booked");
			pstmt.setDouble(cnt, rideInstance.getPrice());

			return pstmt.executeUpdate();

		} catch (SQLiteException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnectDB();
		}
		return 0;
	}

	public static List<RideInstance> fetchBookedRideInstanceByUser(User user) {
		try {
			connectDB();
			List<RideInstance> rideInstances = new ArrayList<>();
			String sql = "SELECT * FROM ride_passenger WHERE username = ?";

			PreparedStatement pstmt = connector.conn.prepareStatement(sql);
			pstmt.setString(1, user.getUsername());

			ResultSet rs = pstmt.executeQuery();
			while (!rs.isClosed() && rs.next()) {
				RideInstance ri = new RideInstance();
				ri.setRideId(rs.getInt("rideId"));
				ri.setSpId(rs.getInt("spId"));
				ri.setPrice(rs.getDouble("price"));
				ri.setStatus(rs.getString("status"));
				ri.setComment(rs.getString("comment"));
				ri.setPassengerId(user.getUsername());

				rideInstances.add(ri);
			}
			disconnectDB();

			for (RideInstance ri : rideInstances) {
				ri.setRide(fetchRideByRideId(ri.getRideId()));
				ri.setStopPoint(fetchStopPointByRideIdAndSPId(ri.getTripId(), ri.getSpId()));
			}

			return rideInstances;
		} catch (Exception e) {
			e.printStackTrace();
			disconnectDB();
		}
		return null;
	}

	/**
	 * Updates a give ride instance
	 * @param rideInstance the instance should be updated
	 * @param status the update type, 0 for done, 1 for driver cancel, 2 for
	 *             passenger cancel, 3 for re-book the ride
	 * @return 1 for success, 0 for fail
	 */
	public static int updateRideInstanceStatus(RideInstance rideInstance, Status status) {
		try {
			connectDB();
			String sql = "UPDATE ride_passenger " +
					"SET status = ?, comment = ?, spId = ?, price = ? " +
					"WHERE rideId = ? AND username = ?;";
			PreparedStatement pstmt = connector.conn.prepareStatement(sql);

			pstmt.setString(1, status.toString());
			pstmt.setString(2, rideInstance.getComment());
			pstmt.setInt(3, rideInstance.getSpId());
			pstmt.setDouble(4, rideInstance.getPrice());
			pstmt.setInt(5, rideInstance.getRideId());
			pstmt.setString(6, rideInstance.getPassengerId());

			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnectDB();
		}
		return 0;
	}

	// **************************************************************
	// *
	// *                 Query for 'notification'
	// *
	// **************************************************************

	public static int addNotification(Notification notification) {
		try {
			connectDB();
			String sql = "INSERT INTO notification (recipient, message) " +
					"VALUES (?, ?);";
			PreparedStatement pstmt = connector.conn.prepareStatement(sql);
			pstmt.setString(1, notification.getRecipient());
			pstmt.setString(2, notification.getMessage());
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnectDB();
		}
		return 0;
	}

	public static int deleteNotification(Notification notification) {
		try {
			connectDB();
			String sql = "DELETE FROM notification WHERE nId = ?";
			PreparedStatement pstmt = connector.conn.prepareStatement(sql);
			pstmt.setInt(1, notification.getnId());
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnectDB();
		}
		return 0;
	}

	public static List<Notification> fetchNotificationsByUser(User user) {
		try {
			connectDB();
			List<Notification> notifications = new ArrayList<>();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String sql = "SELECT * FROM notification WHERE recipient = ?;";
			PreparedStatement pstmt = connector.conn.prepareStatement(sql);
			pstmt.setString(1, user.getUsername());

			ResultSet rs = pstmt.executeQuery();
			while (!rs.isClosed() && rs.next()) {
				Notification notification = new Notification();

				notification.setnId(rs.getInt("nId"));
				notification.setMessage(rs.getString("message"));
				notification.setRecipient(rs.getString("recipient"));
				notification.setTimeStamp(LocalDateTime.parse(rs.getString("timeStamp"), formatter));

				notifications.add(notification);
			}
			return notifications;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnectDB();
		}
		return null;
	}

	public static List<String> fetchPassengerIdByCar(Car car) {
		try {
			connectDB();
			List<String> passengers = new ArrayList<>();
			String sql = "SELECT rp.username " +
					"FROM ride_passenger rp " +
					"LEFT JOIN (SELECT  r.*, t.carId FROM ride r LEFT JOIN trip t ON r.tripId = t.tripid) c " +
					"ON rp.rideId = c.rideId " +
					"WHERE carId = ? AND rp.status = ?;";
			PreparedStatement pstmt = connector.conn.prepareStatement(sql);
			pstmt.setInt(1, car.getCarId());
			pstmt.setString(2, Status.BOOKED.toString());

			ResultSet rs = pstmt.executeQuery();
			while (!rs.isClosed() && rs.next()) {
				passengers.add(rs.getString("username"));
			}
			return passengers;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnectDB();
		}
		return null;
	}

}
