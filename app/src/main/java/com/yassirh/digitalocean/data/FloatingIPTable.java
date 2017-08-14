package com.yassirh.digitalocean.data;

public class FloatingIPTable  extends TableHelper {
    public static final String IP_ADDRESS = "ip_address";
    public static final String DROPLET_ID = "droplet_id";
    public static final String REGION_SLUG = "region_slug";

    FloatingIPTable(){
        columns.put(ID, "integer primary key autoincrement");
        columns.put(IP_ADDRESS, "text");
        columns.put(DROPLET_ID, "integer");
        columns.put(REGION_SLUG, "text");
        TABLE_NAME = "floating_ips";
    }
}
