package controller;

import java.io.*;
import java.util.Properties;

public class Globals {
	static private Properties properties = null;
	static private String lastDir = "";


	/**
	 * Load properties from system.properties file
	 */
	public static void loadProperties() {
		properties = new Properties();
		try {
			File f = new File("system.properties");
			if (!(f.exists())) {
				OutputStream out = new FileOutputStream(f);
			}
			InputStream is = new FileInputStream(f);
			properties.load(is);
			lastDir = getLastDir();
			if (lastDir == null) {
				lastDir = "~";
				setLastDir(lastDir);
			}
			properties.setProperty("lastdir", lastDir);

			// Try loading properties from the file (if found)
			properties.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set the specified property with the specified value
	 *
	 * @param property The property to set
	 * @param value    The value to set the property to
	 */
	public static void setProperty(String property, String value) {
		properties.setProperty(property, value);
		File f = new File("system.properties");
		try {
			OutputStream out = new FileOutputStream(f);
			properties.store(out, "This is an optional header comment string");
			out.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Get the specified property and return its value
	 *
	 * @param property property to return
	 * @return property
	 */
	public static String getProperty(String property) {
		if (properties == null) {
			loadProperties();
		}
		return properties.getProperty(property);
	}

	/**
	 * Get the last directory that something was saved to or opened from
	 *
	 * @return lastDir
	 */
	public static String getLastDir() {
		return getProperty("lastdir");
	}

	/**
	 * Set the value of lastDir
	 *
	 * @param lastDir
	 */
	public static void setLastDir(String lastDir) {
		setProperty("lastdir", lastDir);
	}

	/**
	 * Left pad a string
	 * @param str
	 * @param length
	 * @return
	 */
	public static String leftPadString(String str, int length) {
		StringBuilder sb = new StringBuilder();
		for (
				int i = 0;
				i < length; i++) {
			sb.append(' ');
		}
		return sb.substring(str.length()) + str;
	}


}
