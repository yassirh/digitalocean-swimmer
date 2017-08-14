package com.yassirh.digitalocean.data;

public class SizeTable extends TableHelper {
	
	public static final String SIZE_SLUG = "size_slug";
	public static final String MEMORY = "memory";
	public static final String CPU = "cpu";
	public static final String DISK = "disk";
	public static final String TRANSFER = "transfer";
	public static final String COST_PER_HOUR = "cost_per_hour";
	public static final String COST_PER_MONTH = "cost_per_month";
	
	public SizeTable(){		
		columns.put(SIZE_SLUG, "string primary key");
		columns.put(MEMORY, "integer");
		columns.put(CPU, "integer");
		columns.put(DISK, "integer");
		columns.put(TRANSFER, "integer");
		columns.put(COST_PER_HOUR, "REAL");
		columns.put(COST_PER_MONTH, "REAL");
		TABLE_NAME = "sizes";
	}	
	
}
