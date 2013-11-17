package com.yassirh.digitalocean.data;

public class DomainTable extends TableHelper {
	
	public static final String NAME = "name";
	public static final String TTL = "ttl";
	public static final String LIVE_ZONE_FILE = "live_zone_file";
	public static final String ERROR = "error";
	public static final String ZONE_FILE_WITH_ERROR = "zone_file_with_error";	
	
	public DomainTable(){		
		columns.put(ID, "integer primary key");
		columns.put(NAME, "text");
		columns.put(TTL, "integer");
		columns.put(LIVE_ZONE_FILE, "text");
		columns.put(ERROR, "text");
		columns.put(ZONE_FILE_WITH_ERROR, "text");
		TABLE_NAME = "domains";
	}
	
}
