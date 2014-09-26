package com.yassirh.digitalocean.data;

public class RecordTable extends TableHelper {
	
	public static final String NAME = "name";
	public static final String DOMAIN_NAME = "domain_name";
	public static final String RECORD_TYPE = "record_type";
	public static final String DATA = "data";
	public static final String PORT = "port";
	public static final String PRIORITY = "priority";
	public static final String WEIGHT = "weight";
	
	public RecordTable(){		
		columns.put(ID, "integer primary key");
		columns.put(DOMAIN_NAME, "text");
		columns.put(NAME, "text");
		columns.put(DATA, "text");
		columns.put(RECORD_TYPE, "text");
		columns.put(PORT, "integer");
		columns.put(PRIORITY, "integer");
		columns.put(WEIGHT, "integer");
		TABLE_NAME = "records";
	}
}
