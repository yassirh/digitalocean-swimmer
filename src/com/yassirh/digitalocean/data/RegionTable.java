package com.yassirh.digitalocean.data;

public class RegionTable extends TableHelper {
	
	public static final String NAME = "name";
	public static final String SLUG = "slug";
	
	public RegionTable(){		
		columns.put(ID, "integer primary key");
		columns.put(NAME, "text");
		columns.put(SLUG, "text");
		TABLE_NAME = "regions";
	}	
	
}
