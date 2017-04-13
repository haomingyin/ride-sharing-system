package models.database;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Scanner;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class SQLConnector {

	protected Connection conn;

	/**
	 * Sets up a new connection to local SQLite database.
	 * @return a Connection object
	 */
	public Connection connect() {
		try {
			// db parameters
			String url = "jdbc:sqlite:rss.db";
			// create a connection to the database
			conn = DriverManager.getConnection(url);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			return conn;
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
	public void closeConnection() {
		try {
			if (conn != null && !conn.isClosed()) {
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
	 * @param sql
	 * @return a result set
	 */
	public ResultSet executeSQLQuery(String sql) {
		try {
			if (conn.isClosed()) connect();
			Statement stmt = conn.createStatement();
			return stmt.executeQuery(sql);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	/**
	 * Update or insert a given sql query
	 *
	 * @param sql
	 * @return a int number showing how many rows affected
	 */
	public int executeSQLUpdate(String sql) {
		try {
			if (conn.isClosed()) connect();
			Statement stmt = conn.createStatement();
			return stmt.executeUpdate(sql);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return 0;
		}
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
}

