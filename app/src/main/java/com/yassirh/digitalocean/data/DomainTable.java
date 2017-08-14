package com.yassirh.digitalocean.data;

public class DomainTable extends TableHelper {
	
	public static final String NAME = "name";
	public static final String TTL = "ttl";
	public static final String ZONE_FILE = "zone_file";	
	
	public DomainTable(){		
		columns.put(NAME, "text primary key");
		columns.put(TTL, "integer");
		columns.put(ZONE_FILE, "text");
		TABLE_NAME = "domains";
	}
	
}
