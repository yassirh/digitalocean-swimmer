package com.yassirh.digitalocean.data;

public class ImageTable extends TableHelper {
	
	public static final String NAME = "name";
	public static final String DISTRIBUTION = "distribution";
	public static final String SLUG = "slug";
	public static final String PUBLIC = "public";
	
	public ImageTable(){		
		columns.put(ID, "integer primary key");
		columns.put(NAME, "text");
		columns.put(DISTRIBUTION, "text");
		columns.put(SLUG, "text");
		columns.put(PUBLIC, "integer");
		TABLE_NAME = "images";
	}	
	
}
