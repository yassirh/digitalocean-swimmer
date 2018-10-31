package com.yassirh.digitalocean.data;

public class ImageTable extends TableHelper {
	
	public static final String NAME = "name";
	public static final String DISTRIBUTION = "distribution";
	public static final String SLUG = "slug";
    public static final String IS_IN_USE = "is_in_use";
	public static final String PUBLIC = "public";
    public static final String REGIONS = "regions";
    public static final String MINDISKSIZE = "min_disk_size";

    public ImageTable(){		
		columns.put(ID, "integer primary key");
		columns.put(NAME, "text");
		columns.put(DISTRIBUTION, "text");
		columns.put(SLUG, "text");
        columns.put(IS_IN_USE, "integer");
		columns.put(PUBLIC, "integer");
		columns.put(REGIONS, "text");
		columns.put(MINDISKSIZE, "integer");
		TABLE_NAME = "images";
	}	
	
}
