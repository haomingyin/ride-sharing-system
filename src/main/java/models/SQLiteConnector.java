package models;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Scanner;
import org.sqlite.JDBC;

public class SQLiteConnector {

	private Connection conn;

	public SQLiteConnector() {
		conn = connect();
	}

	/**
	 * Sets up a new connection to local SQLite database.
	 * @return
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

	public void initializeDatabase() {

		try {
			if (conn.isClosed()) this.connect();
			// can only use input stream to read resource file
			InputStream in = getClass().getResourceAsStream("/tables.txt");
			Scanner sc = new Scanner(in, "UTF-8").useDelimiter(";|\\A");
			Statement stmt = conn.createStatement();
			while (sc.hasNext()) {
				String sql = sc.next() + ";";
				try {
					stmt.execute(sql);
				} catch (Exception e) {
					System.out.println("Failed to create tables while initializing database. Error is:");
					System.out.println(e.getMessage());
				}
			}
			System.out.println("Initialization succeed!");
		} catch (Exception e) {
			System.out.println("Failed to initialize database. Error is:");
			System.out.println(e.getMessage());
		} finally {
			this.closeConnection();
		}

	}

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

	public ResultSet executeSQLQuery(String sql) {
		try {
			if (conn.isClosed()) connect();
			Statement stmt = conn.createStatement();
			ResultSet resultSet = stmt.executeQuery(sql);
			return resultSet;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	public int executeSQLUpdate(String sql) {
		try {
			if (conn.isClosed()) connect();
			Statement stmt = conn.createStatement();
			int affectNo = stmt.executeUpdate(sql);
			return affectNo;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return 0;
		}
	}
}

