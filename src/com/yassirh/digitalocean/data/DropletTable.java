package com.yassirh.digitalocean.data;

public class DropletTable extends TableHelper {
	
	public static final String NAME = "name";
	public static final String IMAGE_ID = "image_id";
	public static final String SIZE_ID = "size_id";
	public static final String REGION_ID = "region_id";
	public static final String BACKUPS_ACTIVE = "backups_active";
	public static final String IP_ADDRESS = "ip_address";
	public static final String PRIVATE_IP_ADDRESS = "private_ip_address";
	public static final String LOCKED = "locked";
	public static final String STATUS = "status";
	public static final String CREATED_AT = "created_at";
	
	public DropletTable(){		
		columns.put(ID, "integer primary key");
		columns.put(NAME, "text");
		columns.put(IMAGE_ID, "integer");
		columns.put(SIZE_ID, "integer");
		columns.put(REGION_ID, "integer");
		columns.put(BACKUPS_ACTIVE, "integer");
		columns.put(IP_ADDRESS, "integer");
		columns.put(PRIVATE_IP_ADDRESS, "integer");
		columns.put(LOCKED, "integer");
		columns.put(STATUS, "text");
		columns.put(CREATED_AT, "long");
		TABLE_NAME = "droplets";
	}	
	
}
