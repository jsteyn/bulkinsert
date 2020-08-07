package view;

import controller.Globals;
import org.apache.commons.cli.*;

import java.sql.*;
import java.util.HashMap;

public class SQLTableMetadata {
	HashMap<String, Integer> dataTypes = new HashMap<>();

	// Connect to your database.
	// Replace server name, username, and password with your credentials
	public static void main(String[] args) {
		SQLTableMetadata sqlTableMetadata = new SQLTableMetadata(args);
	}

	SQLTableMetadata(String[] args) {
		Options options = new Options();
		CommandLineParser parser = new DefaultParser();

		Option t = Option.builder("t").longOpt("table").required().hasArg().desc("Table to be populated").build();
		Option f = Option.builder("f").longOpt("file").required().hasArg().desc("CSV file to populate from").build();
		Option d = Option.builder("d").longOpt("database").required(true).hasArg().desc("Database").build();
		Option U = Option.builder("U").longOpt("user").required(true).hasArg().desc("Username").build();
		Option P = Option.builder("P").longOpt("password").required(true).hasArg().desc("Password").build();
		Option s = Option.builder("s").longOpt("server").required(false).hasArg().desc("Server").build();
		Option p = Option.builder("p").longOpt("port").required(false).hasArg().desc("Port").build();

		options.addOption(t);
		options.addOption(f);
		options.addOption(d);
		options.addOption(U);
		options.addOption(P);
		options.addOption(s);
		options.addOption(p);

		CommandLine cmd;

		try {
			cmd = parser.parse(options, args);
			String table = cmd.getOptionValue("t");
			String file = cmd.getOptionValue("f");
			String database = cmd.getOptionValue("d");
			String user = cmd.getOptionValue("U");
			String password = cmd.getOptionValue("P");
			String server = cmd.getOptionValue("s");
			String port = cmd.getOptionValue("p");

			System.out.println(table + "\t" + file + "\t" + database + "\t" + user + "\t" + password + "\t" + server + "\t" + port);


			String connectionUrl =
					"jdbc:sqlserver://" + server + ":" + port + ";"
							+ "database=" + database + ";"
							+ "user=" + user + ";"
							+ "password=" + password + ";"
							+ "loginTimeout=30;";

			try {
				Connection connection = DriverManager.getConnection(connectionUrl);

				DatabaseMetaData databaseMetaData = connection.getMetaData();

				ResultSet columns = databaseMetaData.getColumns(null, null, table, null);
				System.out.println(Globals.leftPadString("columnName", 30) + "\tdatatype\tcolumnsize\tdecimaldigits\tisNullable\tis_autoIncrment");

				while (columns.next()) {
					String columnName = columns.getString("COLUMN_NAME");
					String datatype = columns.getString("DATA_TYPE");
					String columnsize = columns.getString("COLUMN_SIZE");
					String decimaldigits = columns.getString("DECIMAL_DIGITS");
					String isNullable = columns.getString("IS_NULLABLE");
					String is_autoIncrment = columns.getString("IS_AUTOINCREMENT");
					//Printing results
					System.out.println(Globals.leftPadString(columnName,40) + "\t" + Globals.leftPadString(datatype, 8) + "\t" +
							Globals.leftPadString(columnsize,10) + "\t" + decimaldigits + "\t" + isNullable + "\t" + is_autoIncrment);
				}


			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ParseException e) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -cp SQLServerThing.jar view.SQLDatabaseConnection", options);
		}
	}
}
