package com.yassirh.digitalocean.data;

public class SizeTable extends TableHelper {
	
	public static final String NAME = "name";
	public static final String SLUG = "slug";
	public static final String MEMORY = "memory";
	public static final String CPU = "cpu";
	public static final String DISK = "disk";
	public static final String COST_PER_HOUR = "cost_per_hour";
	public static final String COST_PER_MONTH = "cost_per_month";
	
	public SizeTable(){		
		columns.put(ID, "integer primary key");
		columns.put(NAME, "text");
		columns.put(SLUG, "text");
		columns.put(MEMORY, "integer");
		columns.put(CPU, "integer");
		columns.put(DISK, "integer");
		columns.put(COST_PER_HOUR, "REAL");
		columns.put(COST_PER_MONTH, "REAL");
		TABLE_NAME = "sizes";
	}	
	
}
