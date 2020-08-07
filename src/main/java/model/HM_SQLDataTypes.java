package model;

import org.apache.log4j.Logger;
import java.util.Hashtable;

public class HM_SQLDataTypes {
	static Logger logger = Logger.getLogger(HM_SQLDataTypes.class);
	private final static Hashtable<Integer, String> dataTypes = new Hashtable<>() {{
		put(2, "NUMERIC");
		put(4, "INTEGER");
		put(8, "FLOAT");
		put(12, "VARCHAR");
		put(93, "DATE");
	}};

	HM_SQLDataTypes() {
		super();

	}

	public static String getType(int type) {
		return dataTypes.get(type);
	}


}
