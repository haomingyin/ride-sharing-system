package controllers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import org.sqlite.JDBC;

public class SQLiteController {

	/**
	 * Connect to a RSS database
	 */
	public void connect() {
		Connection conn = null;
		try {
			// db parameters
			String url = "jdbc:sqlite:rss.db";
			// create a connection to the database
			conn = DriverManager.getConnection(url);
			System.out.println("Connection to SQLite has been established.");
			createTables(conn);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			System.out.println("Done");
		}
	}

	private void createTables(Connection conn) throws Exception{
		InputStream in = getClass().getResourceAsStream("/tables.txt");
		Scanner sc = new Scanner(in, "UTF-8").useDelimiter(";|\\A");
		Statement stmt = conn.createStatement();
		while (sc.hasNext()) {
			String sql = sc.next() + ";";
			try {
				stmt.execute(sql);
				System.out.println("good");
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}




}

