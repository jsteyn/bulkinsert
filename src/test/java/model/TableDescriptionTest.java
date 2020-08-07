package model;

import org.junit.jupiter.api.Test;

import java.util.Enumeration;
import java.util.Hashtable;

class TableDescriptionTest {

	@Test
	void getColumnTypes() {
		Hashtable<String, Integer> table = new Hashtable<>();
		table.put("Area_name", 12);
		table.put("Area_code", 12);
		table.put("Area_type", 12);
		table.put("Specimen_date", 12);
		String server = "localhost";
		int port = 1433;
		String database = "dbname";
		String user = "sa";
		String password = "password.";
		String connectionUrl =
				"jdbc:sqlserver://" + server + ":" + port + ";"
						+ "database=" + database + ";"
						+ "user=" + user + ";"
						+ "password=" + password + ";"
						+ "loginTimeout=30;";
		Hashtable<String, Integer> types = new Hashtable<>();
		types = TableDescription.getColumnTypes(connectionUrl, "cases");
		Enumeration<String> keys = types.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			System.out.println(key + "\t" + types.get(key));
		}
		// TODO Add proper test

	}

	@Test
	void getColumnNames() {
		String server = "localhost";
		int port = 1433;
		String database = "dbname";
		String user = "sa";
		String password = "password.";
		String connectionUrl =
				"jdbc:sqlserver://" + server + ":" + port + ";"
						+ "database=" + database + ";"
						+ "user=" + user + ";"
						+ "password=" + password + ";"
						+ "loginTimeout=30;";

		System.out.println("Header: " + TableDescription.getColumnNames(connectionUrl, "cases"));
		// TODO Add proper test
	}
}