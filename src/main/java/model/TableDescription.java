package model;
// -t deaths -f /home/jannetta/TURING/data/coronavirus-deaths.tsv -s localhost -p 1434 -U sa -P Your_password123 -d GovData -D "\t"
// -t cases -f /home/jannetta/TURING/data/coronavirus-cases.tsv -s localhost -p 1434 -U sa -P Your_password123 -d GovData -D "\t"

import org.apache.log4j.Logger;

import java.sql.*;
import java.util.Hashtable;

public class TableDescription {
	private static Logger logger = Logger.getLogger(TableDescription.class);
	/**
	 * Return a HashMap for the tableName provided as a parameter
	 * where the key is the column name and the data type is the value as a String
	 *
	 * @param tableName
	 * @return
	 */
	public static Hashtable<String, Integer> getColumnTypes(String connectionURL, String tableName) {
		Hashtable<String, Integer> types = new Hashtable<>();
		try {
			Connection connection = DriverManager.getConnection(connectionURL);

			DatabaseMetaData databaseMetaData = connection.getMetaData();

			ResultSet columns = databaseMetaData.getColumns(null, null, tableName, null);

			while (columns.next()) {
				Integer dataType_id = Integer.valueOf(columns.getString("DATA_TYPE"));
				String dataType = HM_SQLDataTypes.getType(dataType_id);
				logger.debug(columns.getString("COLUMN_NAME")
						+ "\t" + dataType_id
						+ "\t" + HM_SQLDataTypes.getType(dataType_id));
				types.put(columns.getString("COLUMN_NAME"), dataType_id);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return types;
	}

	public static String getColumnNames(String connectionURL, String tableName) {

		String colnames = null;
		StringBuilder sb = new StringBuilder();
		try {
			Connection connection = DriverManager.getConnection(connectionURL);

			DatabaseMetaData databaseMetaData = connection.getMetaData();

			ResultSet columns = databaseMetaData.getColumns(null, null, tableName, null);

			while (columns.next()) {

				sb.append(columns.getString("COLUMN_NAME") + " ");

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sb.toString().trim().replace(" ", ", ");
	}

}
