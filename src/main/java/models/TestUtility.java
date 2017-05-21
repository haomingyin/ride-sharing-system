package models;

import models.database.SQLConnector;

public class TestUtility {

	public static void beforeTest() {
		// always copy-paste the default database from resource
		new SQLConnector().exportDatabaseResource();
	}

}
