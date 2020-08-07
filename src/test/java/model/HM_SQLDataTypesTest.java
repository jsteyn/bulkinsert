package model;

import static org.junit.jupiter.api.Assertions.*;

class HM_SQLDataTypesTest {

	@org.junit.jupiter.api.Test
	void getType() {
		assertEquals("NUMERIC",HM_SQLDataTypes.getType(2));
		assertEquals("INTEGER", HM_SQLDataTypes.getType(4));
		assertEquals("FLOAT", HM_SQLDataTypes.getType(8));
		assertEquals("VARCHAR", HM_SQLDataTypes.getType(12));
		assertEquals("DATE", HM_SQLDataTypes.getType(93));
	}
}