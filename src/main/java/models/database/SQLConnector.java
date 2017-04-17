package models.database;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class SQLConnector {

	Connection conn;
	// when keepConnect set true, connection will be kept open.
	private boolean keepConnect;

	/**
	 * Sets up a new connection to local SQLite database.
	 * @return a Connection object
	 */
	Connection connect() {
		try {
			// db parameters
			String url = "jdbc:sqlite:rss.db";
			// create a connection to the database
			conn = DriverManager.getConnection(url);
			keepConnect = false;
			return conn;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	/**
	 * Initialize database and connect to it
	 */
	public void initializeDatabase() {
		try {
			File file = new File("rss.db");
			if (!file.exists()) {
				exportDatabaseResource();
				System.out.println("Database initialization succeed!");
			}
			conn = connect();
		} catch (Exception e) {
			System.out.println("Failed to initialize database. Error is:");
			System.out.println(e.getMessage());
		} finally {
			this.closeConnection();
		}

	}

	/**
	 * Closes current database connection
	 */
	void closeConnection() {
		try {
			if (conn != null && !conn.isClosed() && !keepConnect) {
				conn.close();
			} else {
				System.out.println("No connection has been established.");
			}
		} catch (Exception e) {
			System.out.println("Failed to close connection. Error is:");
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Executes a given sql query
	 *
	 * @param sql executes the given sql
	 * @return a result set
	 */
	ResultSet executeSQLQuery(String sql) throws Exception {
		if (conn.isClosed()) connect();
		Statement stmt = conn.createStatement();
		return stmt.executeQuery(sql);
	}

	/**
	 * Update or insert a given sql query
	 *
	 * @param sql executes the given sql
	 * @return a int number showing how many rows affected
	 */
	int executeSQLUpdate(String sql) throws Exception{
		if (conn.isClosed()) connect();
		Statement stmt = conn.createStatement();
		return stmt.executeUpdate(sql);
	}

	/**
	 * Copy pre-defined database file in resource to outside
	 */
	private void exportDatabaseResource() {
		String fileName = "/rss.db";
		try {
			//note that each / is a directory down in the "jar tree" been the jar the root of the tree
			InputStream stream = getClass().getResourceAsStream(fileName);
			Path path = Paths.get("rss.db");
			Files.copy(stream, path, REPLACE_EXISTING);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public boolean isKeepOpen() {
		return keepConnect;
	}

	public void setKeepOpen(boolean keepConnect) {
		this.keepConnect = keepConnect;
	}
}

