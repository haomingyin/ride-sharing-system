package models;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by Haoming on 5/04/17.
 */
public class SQLiteConnectorTest {

	/** after initialize database, there should be a copy of resource database
	 *
	 * @throws Exception
	 */
	@Test
	public void initializeDatabase() throws Exception {
		SQLiteConnector sqLiteConnector = new SQLiteConnector();
		sqLiteConnector.initializeDatabase();
		File file = new File("rss.db");
		assertTrue(file.exists());
	}

}