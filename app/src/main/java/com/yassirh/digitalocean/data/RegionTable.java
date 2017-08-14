package com.yassirh.digitalocean.data;

public class RegionTable extends TableHelper {
	
	public static final String NAME = "name";
	public static final String REGION_SLUG = "region_slug";
	public static final String FEATURES = "features";
	public static final String AVAILABLE = "available";
	
	
	public RegionTable(){		
		columns.put(REGION_SLUG, "string primary key");
		columns.put(NAME, "text");
		columns.put(FEATURES, "text");
		columns.put(AVAILABLE, "integer");
		TABLE_NAME = "regions";
	}	
	
}
