package models.database;

import models.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class SQLExecutor {

	private static SQLConnector connector;

	private static void connectDB() {
		connector = new SQLConnector();
		connector.connect();
	}

	private static void disconnectDB() {
		connector.closeConnection();
	}

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
			User user = new User();
			String sql = String.format("SELECT * " +
							"FROM user " +
							"WHERE username = '%s' AND password = '%s';",
					username, password);
			ResultSet rs = connector.executeSQLQuery(sql);
			if (!rs.isClosed()) {
				user.setUsername(rs.getString("username"));
				user.setPassword(rs.getString("password"));
				user.setEmail(rs.getString("email"));
				user.setfName(rs.getString("fName"));
				user.setlName(rs.getString("lName"));
				user.setmPhone(rs.getString("phone"));
				return user;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			disconnectDB();
		}
	}

	/**
	 * Creates a user into database
	 * @param user the user need to be stored into database
	 * @return 1 if succeed, otherwise 0
	 */
	public static int addUser(User user) {
		try {
			connectDB();
			String sql = String.format("INSERT INTO user (username, password) " +
							"VALUES ('%s', '%s');",
					user.getUsername(), user.getPassword());
			return connector.executeSQLUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			disconnectDB();
		}
	}

	/**
	 * Fetches all the cars related to a given user
	 *
	 * @param user user associated with the cars to be fetched
	 * @return a hash map containing car's plate as key, Car instance as value
	 */
	public static HashMap<String, Car> fetchCarsByUser(User user) {
		connectDB();
		HashMap<String, Car> carsHashMap = new HashMap<>();
		try {
			String sql = String.format("SELECT * " +
					"FROM car " +
					"WHERE username = '%s';", user.getUsername());
			ResultSet rs = connector.executeSQLQuery(sql);
			while (!rs.isClosed() && rs.next()) {
				Car car = new Car(rs.getString("plate"),
						rs.getString("username"),
						rs.getString("model"),
						rs.getString("manufacturer"),
						rs.getString("color"),
						rs.getInt("year"),
						rs.getInt("seatNo"));
				carsHashMap.put(car.getPlate(), car);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnectDB();
		}
		return carsHashMap;
	}

	/**
	 * Updates a given car in database
	 * @param car a car needs to be updated
	 * @return 1 if update succeed, otherwise 0
	 */
	public static int updateCar(Car car) {
		connectDB();
		String sql = "UPDATE car " +
				"SET model = ?, manufacturer = ?, color = ?, year = ?, seatNo = ? " +
				"WHERE plate = ?;";
		try {
			PreparedStatement pstmt = connector.conn.prepareStatement(sql);

			pstmt.setString(1, car.getModel());
			pstmt.setString(2, car.getManufacturer());
			pstmt.setString(3, car.getColor());
			pstmt.setInt(4, car.getYear());
			pstmt.setInt(5, car.getSeatNo());

			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			disconnectDB();
		}
	}

	/**
	 * Adds a given car to database
	 * @param car a car needs to be stored into database
	 * @return 1 if operation succeed, otherwise 0
	 */
	public static int addCar(Car car) {
		connectDB();
		String sql = "INSERT INTO car " +
				"(plate, username, model, manufacturer, color, year, seatNo) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?);";
		try {
			PreparedStatement pstmt = connector.conn.prepareStatement(sql);

			pstmt.setString(1, car.getPlate());
			pstmt.setString(2, car.getUsername());
			pstmt.setString(3, car.getModel());
			pstmt.setString(4, car.getManufacturer());
			pstmt.setString(5, car.getColor());
			pstmt.setInt(6, car.getYear());
			pstmt.setInt(7, car.getSeatNo());

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
	public static int deleteCar(Car car) {
		connectDB();
		try {
			String sql = String.format("DELETE FROM car WHERE plate = '%s';",
					car.getPlate());
			return connector.executeSQLUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			disconnectDB();
		}
	}

	/**
	 * Fetches all stop points whose trimmed address matches the give string
	 * @param trimmed a trimmed string of the address
	 * @param limit a integer of how many records need to be fetched, -1 for all.
	 * @return a HashMap containing spId as keys, StopPoint as values
	 */
	public static Map<Integer, StopPoint> fetchStopPointsByTrimmed(String trimmed, int limit) {
		// add a % wildcard at the end of the trimmed string
		String request = trimmed + "%";
		// replace one or more non alphanumeric character to %
		request = request.replaceAll("[^a-zA-Z0-9]+", "%").toLowerCase();
		String sql = String.format("SELECT * FROM stop_point " +
				"WHERE trimmed like '%s' ", request);
		if (limit >= 0) sql += "LIMIT " + String.valueOf(limit) + ";";
		Map<Integer, StopPoint> stopPoints = new HashMap<>();
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
				stopPoint.setTime(rs.getString("time"));
				stopPoints.put(stopPoint.getSpId(), stopPoint);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnectDB();
		}
		return stopPoints;
	}
	/**
	 * Fetches all routes associated with a give user
	 * @param user a user associated with routes to be fetched
	 * @return HashMap containing routeId as key, Route as values.
	 */
	public static HashMap<Integer, Route> fetchRoutesByUser(User user) {
		HashMap<Integer, Route> routeHashMap = new HashMap<>();
		try {
			String sql = String.format("SELECT * " +
					"FROM route " +
					"WHERE username = '%s';", user.getUsername());
			ResultSet rs = connector.executeSQLQuery(sql);
			while (!rs.isClosed() && rs.next()) {
				Route route = new Route(rs.getInt("routeId"),
						rs.getString("alias"));
				routeHashMap.put(route.getRouteId(), route);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return routeHashMap;
	}

	/**
	 * Fetches rides which are owned by the given user
	 * @param user a user associated with rides to be fetched
	 * @return a HashMap of rides containing rideId as keys, ride as values
	 */
	public static HashMap<Integer, Ride> fetchRidesByUser(User user) {
		HashMap<Integer, Ride> rideHashMap = new HashMap<>();
		try {
			connectDB();
			String sql = String.format("SELECT * " +
					"FROM ride " +
					"WHERE username = '%s';", user.getUsername());
			ResultSet rs = connector.executeSQLQuery(sql);
			while (!rs.isClosed() && rs.next()) {
				Ride ride = new Ride(rs.getInt("rideId"),
						rs.getInt("tripId"),
						rs.getString("alias"),
						rs.getInt("seatNo"));

				// alias must be unique!!!!
				rideHashMap.put(ride.getRideId(), ride);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			disconnectDB();
		}
		return rideHashMap;
	}
}
