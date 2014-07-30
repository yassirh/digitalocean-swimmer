package com.yassirh.digitalocean.data;

public class NetworkTable extends TableHelper {
	
	public static final String IP_ADDRESS = "ip_address";
	public static final String GATEWAY = "gatway";
	public static final String TYPE = "type";
	public static final String CIDR = "cidr";
	public static final String NETMASK = "netmask";
	public static final String DROPLET_ID = "droplet_id";
		
	public NetworkTable(){
		columns.put(ID, "integer primary key autoincrement");
		columns.put(IP_ADDRESS, "text");
		columns.put(GATEWAY, "text");
		columns.put(TYPE, "text");
		columns.put(CIDR, "text");
		columns.put(NETMASK, "text");
		columns.put(DROPLET_ID, "integer");
		TABLE_NAME = "networks";
	}
	
}
