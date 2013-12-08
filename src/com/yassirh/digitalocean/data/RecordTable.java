package com.yassirh.digitalocean.data;

public class RecordTable extends TableHelper {
	
	public static final String NAME = "name";
	public static final String RECORD_TYPE = "record_type";
	public static final String DOMAIN_ID = "domain_id";
	public static final String DATA = "data";	
	
	public RecordTable(){		
		columns.put(ID, "integer primary key");
		columns.put(NAME, "text");
		columns.put(DOMAIN_ID, "integer");
		columns.put(DATA, "text");
		columns.put(RECORD_TYPE, "record_type");
		TABLE_NAME = "records";
	}
}
