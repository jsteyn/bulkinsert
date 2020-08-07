package view;

import model.HM_SQLDataTypes;
import model.TableDescription;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Scanner;

/**
 * Since Microsoft seems to be incapable of providing a bulk insert that actually works for their
 * SQL Server I am writing this utility.
 * BULK INSERT is supposed to work with a UNC if your CSV file that you want to insert from is
 * on your local machine and not on the remote SQL Server. However, with my local machine being
 * Linux it is quite understandable that Microsoft cannot comprehend the situation and thus their
 * UNC plainly does not work - it has taken me more time to try all combinations and permutations
 * of host names in the UNC than what it took me to write this program.
 */
public class BULKINSERT {
	private static Logger logger = Logger.getLogger(BULKINSERT.class);

	// Connect to your database.
	// Replace server name, username, and password with your credentials
	public static void main(String[] args) {
		BULKINSERT sqlDatabaseConnection = new BULKINSERT(args);
	}

	BULKINSERT(String[] args) {
		Options options = new Options();
		CommandLineParser parser = new DefaultParser();

		Option t = Option.builder("t").longOpt("table").required(true).hasArg().desc("*Table to be populated").build();
		Option f = Option.builder("f").longOpt("file").required(true).hasArg().desc("*CSV file to populate from").build();
		Option d = Option.builder("d").longOpt("database").required(true).hasArg().desc("*Database").build();
		Option U = Option.builder("U").longOpt("user").required(true).hasArg().desc("*Username").build();
		Option P = Option.builder("P").longOpt("password").required(true).hasArg().desc("*Password").build();
		Option s = Option.builder("s").longOpt("server").required(true).hasArg().desc("*Server").build();
		Option p = Option.builder("p").longOpt("port").required(false).hasArg().desc("Port (default=1433)").build();
		Option D = Option.builder("D").longOpt("delim").required(false).hasArg().desc("Delimiter (default=,)").build();
		Option b = Option.builder("b").longOpt("batch").required(false).hasArg().desc("Batch size (default=100)").build();

		options.addOption(t);
		options.addOption(f);
		options.addOption(d);
		options.addOption(U);
		options.addOption(P);
		options.addOption(s);
		options.addOption(p);
		options.addOption(D);
		options.addOption(b);

		CommandLine cmd;

		try {
			cmd = parser.parse(options, args);
			String table = cmd.getOptionValue("t");
			String file = cmd.getOptionValue("f");
			String database = cmd.getOptionValue("d");
			String user = cmd.getOptionValue("U");
			String password = cmd.getOptionValue("P");
			String server = cmd.getOptionValue("s");
			String port;
			String delim = ",";
			int batch = 100;


			if (cmd.hasOption("b"))
				batch = Integer.valueOf(cmd.getOptionValue("b"));
			if (cmd.hasOption("p")) {
				port = cmd.getOptionValue("p");
			} else {
				port = "1433";
			}
			if (cmd.hasOption("D"))
				delim = cmd.getOptionValue("D");

			String connectionUrl =
						"jdbc:sqlserver://" + server + ":" + port + ";"
								+ "database=" + database + ";"
								+ "user=" + user + ";"
								+ "password=" + password + ";"
								+ "loginTimeout=30;";

			logger.debug(table + "\t" + file);
			logger.debug(connectionUrl);
			logger.debug(delim + "\t" + batch);

			Scanner sc = null;
			try {
					// Retrieve codes used for data types in tables
					Hashtable<String, Integer> tableDescription = TableDescription.getColumnTypes(connectionUrl, table);
					String column_Names = TableDescription.getColumnNames(connectionUrl, table);

					// Connect to the database
					Connection connection = DriverManager.getConnection(connectionUrl);

					// Open CSV file
					sc = new Scanner(new File(file));
					// Read the header line of the CSV file
					String header = sc.nextLine();
					// Get the column names used in the CSV
					String[] columnNames = header.split(delim);
					for (String col:columnNames) {
						logger.debug(col);
					}
					int numberOfValues = columnNames.length;
					Integer[] columnTypes = new Integer[numberOfValues];
					// Convert column names to type codes (all columns in table)
					StringBuilder sb = new StringBuilder("");
					for (int i = 0; i < numberOfValues; i++) {
						String columnName = columnNames[i].replace(" ", "_").replace("-", "");
						logger.debug(columnName + "\t" + tableDescription.get(columnName));
						columnTypes[i] = tableDescription.get(columnName);
						sb.append(columnName);
						sb.append("(");
						sb.append(HM_SQLDataTypes.getType(columnTypes[i]));
						sb.append(")\t");
					}

					// Create a string of ?, for the VALUES part of the SQL INSERT statement
					String[] types = new String[numberOfValues];
					String values = "?";
					for (int i = 1; i < numberOfValues; i++) {
						values += ",?";
					}
					String insertSql = "INSERT INTO " + table + " (" + column_Names + ") VALUES (" + values + ")";
					logger.debug("INSERT QUERY: " + insertSql);
					int count = 0;
					// Create a connection to the server with the INSERT prepared statement
					try (Connection conn = connection; PreparedStatement statement = conn.prepareStatement(insertSql);) {
						while (sc.hasNext()) {
							// Read a line from the CSV file
							String[] tokens = sc.nextLine().split(delim, -1);

							for (int i = 0; i < numberOfValues; i++) {
								String columnName = columnNames[i].replace(" ", "_").replace("-", "");
								if (tokens[i].equals("")) statement.setNull(i + 1, tableDescription.get(columnName));
								else
									switch (HM_SQLDataTypes.getType(tableDescription.get(columnName))) {
										case "NUMERIC":
											statement.setDouble(i + 1, Double.valueOf(tokens[i]));
											break;
										case "INTEGER":
											statement.setInt(i + 1, Integer.valueOf(tokens[i]));
											break;
										case "DATE":
											// 16/05/2020 05:34:00
											SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy HH:mm:ss");
											String dateString = format.format(new Date(0));
											Date date = null;
											try {
												date = format.parse(tokens[i]);
											} catch (java.text.ParseException e) {
												e.printStackTrace();
											}
											java.sql.Date date2 = new java.sql.Date(date.getTime());
											statement.setDate(i + 1, date2);
											break;
										case "FLOAT":
											statement.setFloat(i + 1, Float.valueOf(tokens[i]));
											break;
										case "VARCHAR":
											statement.setString(i + 1, tokens[i]);
											break;
									}
							}
							ResultSet resultSet = null;
							statement.addBatch();
							count++;
							// execute every 100 rows or less
							if (count % batch == 0) {
								logger.debug("Insert Batch");
								statement.executeBatch();
								count = 0;
							}
						}
						if (count > 0) {
							statement.executeBatch();
							logger.debug("Insert last Batch");
						}
						sc.close();
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch(ParseException e){
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("java -cp Bulkinsert.jar view.BULKINSERT\nOptions shown with * are required.", options);
			}

		}
	}
